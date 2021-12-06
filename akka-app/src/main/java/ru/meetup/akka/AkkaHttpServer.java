package ru.meetup.akka;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
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
import java.util.HashMap;

public class AkkaHttpServer extends AllDirectives {

    private final static Logger LOG = LoggerFactory.getLogger(AkkaHttpServer.class);

    private final String gravatarUrl;
    private final int gravatarSize;

    public AkkaHttpServer(Config config) {
        gravatarUrl = config.getString("gravatar-url");
        gravatarSize = config.getInt("image-size");
    }

    public static void main(String[] args) {
        Kamon.init();

        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "meetup");
        final Http http = Http.get(system);
        var app = new AkkaHttpServer(ConfigFactory.load());
        http.newServerAt("0.0.0.0", 8080).bind(app.createRoute());
    }

    private Route createRoute() {
        return concat(
                path("gravatar", () ->
                        get(() -> parameter("name", name -> complete(createPageWithGravatar(name))))
                )
        );
    }

    private HttpResponse getResponse(String body) {
        return HttpResponse.create()
                .withEntity(HttpEntities.create(ContentTypes.TEXT_HTML_UTF8, body));
    }

    private String createGravatarUrl(String hash) {
        return gravatarUrl + "/monster/" + hash + "?size=" + gravatarSize;
    }

    private HttpResponse createPageWithGravatar(String name) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        try {
            HashMap<String, String> dataModel = new HashMap<>();
            dataModel.put("name", name);
            dataModel.put("imageUrl", createGravatarUrl(DigestUtils.md5Hex(name)));
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
