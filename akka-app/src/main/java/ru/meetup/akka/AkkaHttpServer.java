package ru.meetup.akka;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.StatusReply;
import akka.util.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import kamon.Kamon;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.CompletionStage;

public class AkkaHttpServer extends AllDirectives {

    private final static Logger LOG = LoggerFactory.getLogger(AkkaHttpServer.class);

    private final String gravatarUrl;
    private final int gravatarSize;

    private ActorRef<GravatarActor.Command> gravatarActor;
    private Scheduler scheduler;

    public AkkaHttpServer(Config config) {
        gravatarUrl = config.getString("gravatar-url");
        gravatarSize = config.getInt("image-size");
    }

    public static void main(String[] args) {
        Kamon.init();
        Config config = ConfigFactory.load();
        var app = new AkkaHttpServer(ConfigFactory.load());
        int port = config.getInt("server-port");
        String host = config.getString("server-host");
        ActorSystem.create(createGuardian(app, host, port), "meetup", config);
    }

    public static Behavior<Void> createGuardian(AkkaHttpServer app, String host, int port) {
        return Behaviors.setup(context -> {
            app.spawnActors(context).initScheduler(context.getSystem().scheduler());
            final akka.actor.ActorSystem classicSystem = Adapter.toClassic(context.getSystem());
            Http.get(classicSystem).newServerAt(host, port)
                    .bind(app.createRoute())
                    .whenComplete((binding, failure) -> {
                        if (failure == null) {
                            classicSystem.log().info("HTTP server now listening at port {}", port);
                        } else {
                            classicSystem.log().error(failure, "Failed to bind HTTP server, terminating.");
                            classicSystem.terminate();
                        }
                    });
            return Behaviors.empty();
        });
    }

    private static HttpResponse getResponse(String body) {
        return HttpResponse.create()
                .withEntity(HttpEntities.create(ContentTypes.TEXT_HTML_UTF8, body));
    }

    private AkkaHttpServer spawnActors(ActorContext<Void> context) {
        this.gravatarActor = context.spawn(GravatarActor.create(), "gravatar");
        return this;
    }

    private AkkaHttpServer initScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    private Route createRoute() {
        return concat(
                path("gravatar", () ->
                        get(() -> parameter("name", name -> onSuccess(
                                getGravatar(name), gravatar -> complete(createPageWithGravatar(name, gravatar)))
                        ))
                )
        );
    }

    private String createGravatarUrl(String hash) {
        return gravatarUrl + "/monster/" + hash + "?size=" + gravatarSize;
    }

    private CompletionStage<ByteString> getGravatar(String name) {
        var requestUrl = createGravatarUrl(DigestUtils.md5Hex(name));
        CompletionStage<StatusReply<ByteString>> gravatar = AskPattern.ask(
                gravatarActor,
                replyTo -> new GravatarActor.GetGravatar(requestUrl, replyTo),
                Duration.ofSeconds(5),
                scheduler
        );
        return gravatar.thenApply(StatusReply::getValue);
    }

    private HttpResponse createPageWithGravatar(String name, ByteString gravatar) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        try {
            HashMap<String, String> dataModel = new HashMap<>();
            dataModel.put("name", name);
            dataModel.put("gravatar", gravatar.utf8String());
            Template template = cfg.getTemplate("page.ftl");
            StringWriter stringWriter = new StringWriter();
            template.process(dataModel, stringWriter);
            return getResponse(stringWriter.toString());
        } catch (IOException | TemplateException e) {
            LOG.error("Error generate page", e);
        }
        return getResponse("Internal Server error");
    }
}
