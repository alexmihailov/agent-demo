package ru.meetup.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.pattern.StatusReply;
import akka.util.ByteString;

import java.util.concurrent.CompletionStage;

public final class GravatarActor {

    private GravatarActor() {
    }

    public interface Command {}

    public static class GetGravatar implements Command {
        private final String url;
        private final ActorRef<StatusReply<ByteString>> replyTo;

        public GetGravatar(String url, ActorRef<StatusReply<ByteString>> replyTo) {
            this.url = url;
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

    public static Behavior<Command> create() {
        return Behaviors.setup(GravatarActor::gravatarActor);
    }

    private static Behavior<Command> gravatarActor(ActorContext<Command> context) {
        return Behaviors.receive(Command.class)
                .onMessage(GetGravatar.class , cmd -> onGetGravatar(context, cmd))
                .onMessage(FoundedGravatar.class, GravatarActor::onFoundedGravatar)
                .build();
    }

    private static Behavior<Command> onGetGravatar(ActorContext<Command> context, GetGravatar cmd) {
        CompletionStage<ByteString> resFuture = Http.get(context.getSystem())
                .singleRequest(HttpRequest.create(cmd.url))
                .thenCompose(response -> Unmarshaller.entityToByteString()
                        .unmarshal(response.entity(), context.getSystem()))
                .thenApply(ByteString::encodeBase64);
        context.pipeToSelf(resFuture, (result, exception) -> new FoundedGravatar(result, exception, cmd));
        return Behaviors.same();
    }

    private static Behavior<Command> onFoundedGravatar(FoundedGravatar cmd) {
        var reply = cmd.toReply();
        cmd.getReplyTo().tell(reply);
        return Behaviors.same();
    }
}
