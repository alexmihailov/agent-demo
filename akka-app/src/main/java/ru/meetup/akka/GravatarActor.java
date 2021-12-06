package ru.meetup.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.pattern.StatusReply;
import akka.util.ByteString;
import com.typesafe.config.Config;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class GravatarActor extends AbstractBehavior<GravatarActor.Command> {

    private final Map<String, ByteString> localCache = new HashMap<>();

    private final String gravatarUrl;
    private final int gravatarSize;

    private GravatarActor(ActorContext<Command> context, Config config) {
        super(context);

        this.gravatarUrl = config.getString("gravatar-url");
        this.gravatarSize = config.getInt("image-size");
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetGravatar.class , cmd -> onGetGravatar(getContext(), cmd))
                .onMessage(FoundedGravatar.class, this::onFoundedGravatar)
                .build();
    }

    public interface Command {}

    public static class GetGravatar implements Command {
        private final String name;
        private final ActorRef<StatusReply<ByteString>> replyTo;

        public GetGravatar(String name, ActorRef<StatusReply<ByteString>> replyTo) {
            this.name = name;
            this.replyTo = replyTo;
        }
    }

    private static class FoundedGravatar implements Command {
        private final ByteString result;
        private final Throwable e;
        private final GetGravatar cmd;

        public FoundedGravatar(ByteString result, Throwable e, GetGravatar cmd) {
            this.result = result;
            this.e = e;
            this.cmd = cmd;
        }

        public ActorRef<StatusReply<ByteString>> getReplyTo() {
            return cmd.replyTo;
        }

        public StatusReply<ByteString> toReply() {
            StatusReply<ByteString> reply;
            if (e != null) {
                reply = StatusReply.error(e);
            } else {
                reply = StatusReply.success(result);
            }
            return reply;
        }
    }

    public static Behavior<Command> create(Config config) {
        return Behaviors.setup(ctx -> new GravatarActor(ctx, config));
    }

    private String createGravatarUrl(String hash) {
        return gravatarUrl + "/monster/" + hash + "?size=" + gravatarSize;
    }

    private Behavior<Command> onGetGravatar(ActorContext<Command> context, GetGravatar cmd) {
        CompletionStage<ByteString> resFuture;
        if (localCache.containsKey(cmd.name)) {
            resFuture = CompletableFuture.completedFuture(localCache.get(cmd.name));
        } else {
            var requestUrl = createGravatarUrl(DigestUtils.md5Hex(cmd.name));
            resFuture = Http.get(context.getSystem())
                    .singleRequest(HttpRequest.create(requestUrl))
                    .thenCompose(response -> Unmarshaller.entityToByteString()
                            .unmarshal(response.entity(), context.getSystem()))
                    .thenApply(ByteString::encodeBase64);
        }
        context.pipeToSelf(resFuture, (result, exception) -> new FoundedGravatar(result, exception, cmd));
        return Behaviors.same();
    }

    private Behavior<Command> onFoundedGravatar(FoundedGravatar cmd) {
        var reply = cmd.toReply();
        if (!localCache.containsKey(cmd.cmd.name) && reply.isSuccess()) {
            localCache.put(cmd.cmd.name, reply.getValue());
        }
        cmd.getReplyTo().tell(reply);
        return Behaviors.same();
    }
}
