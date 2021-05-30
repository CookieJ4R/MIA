package mia.core;

import io.javalin.Javalin;

/***
 * This class provides a WebServer which handles frontend and web requests.
 */
public final class MiaWebserver implements IMiaShutdownable{

    private Javalin server;

    //Standard-Values can be overwritten by overloading the constructor
    private String hostIP = "127.0.0.1";
    private int port = 80;

    public MiaWebserver(){
        init();
    }

    public MiaWebserver(String hostIP, String ip){
        this.hostIP = hostIP;
        this.port = Integer.parseInt(ip);
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
