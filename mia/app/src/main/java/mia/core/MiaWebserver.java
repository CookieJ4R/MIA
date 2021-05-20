package mia.core;

import io.javalin.Javalin;

public class MiaWebserver {

    public MiaWebserver(){
        Javalin server = Javalin.create().start("192.168.2.101", 8080);
        server.get("/test", (ctx) -> {
            ctx.result("Test running");
        });
    }

}
