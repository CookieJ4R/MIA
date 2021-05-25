package mia.core;

import io.javalin.Javalin;

/***
 * This class provides a WebServer which handles frontend and web requests.
 */
public final class MiaWebserver implements IMiaShutdownable{

    private Javalin server;

    private String hostIP = "192.168.2.101";
    private int port = 80;


    public void MiaWebserver(){
        init();
    }

    /***
     * Initializes the WebServer
     */
    private void init(){
        server = Javalin.create().start(hostIP, port);
        server.get("/test", (ctx) ->
                ctx.result("Test running"));
    }

    @Override
    public void shutdown() {
        server.stop();
    }
}
