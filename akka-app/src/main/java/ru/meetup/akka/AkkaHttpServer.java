package ru.meetup.akka;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import kamon.Kamon;
import org.apache.commons.codec.digest.DigestUtils;

// TODO добавить конфиг и взаимодействие с https://github.com/amouat/dnmonster
public class AkkaHttpServer extends AllDirectives {

    public static void main(String[] args) {
        Kamon.init();

        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "meetup");
        final Http http = Http.get(system);
        var app = new AkkaHttpServer();
        http.newServerAt("0.0.0.0", 8080).bind(app.createRoute());
    }

    private Route createRoute() {
        return concat(
                path("gravatar", () ->
                        get(() -> parameter("name", name -> complete(getImage(name))))
                )
        );
    }

    private String getImage(String name) {
        // TODO формирование страницы с картинкой
        return DigestUtils.md5Hex(name);
    }
}
