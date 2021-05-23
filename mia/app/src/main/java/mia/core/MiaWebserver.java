package mia.core;

import io.javalin.Javalin;

/***
 * This class provides a WebServer which handles frontend and web requests.
 */
public class MiaWebserver {

    /***
     * Initializes the WebServer
     * @param hostIP ip the WebServer runs on
     * @param port port the WebServer runs on
     */
    public void init(String hostIP, int port){
        Javalin server = Javalin.create().start(hostIP, port);
        server.get("/test", (ctx) ->
            ctx.result("Test running"));
    }

}
