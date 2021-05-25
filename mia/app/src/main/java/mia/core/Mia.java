package mia.core;

import java.util.Arrays;
import java.util.List;

public class Mia implements IMiaShutdownable, IMiaCommandBusNode{

    private static MiaCommandBus commandBus;
    private static MiaDataStorage dataStorage;
    private static MiaMQTTHandler MQTTHandler;
    private static MiaLogger logger;
    private static MiaWebserver webserver;

    public Mia() {

        commandBus = new MiaCommandBus();
        dataStorage = new MiaDataStorage();
        MQTTHandler = new MiaMQTTHandler();
        //webserver = new MiaWebserver();
        logger = new MiaLogger();

        getCommandBus().register(this, Arrays.asList("mqtt:core"));

        logger.logInfo("Test");
        logger.logWarning("Test");
        logger.logError("Test");
    }

    public static MiaCommandBus getCommandBus(){
        return commandBus;
    }

    public static MiaDataStorage getDataStorage(){
        return dataStorage;
    }

    public static MiaMQTTHandler getMQTTHandler(){
        return MQTTHandler;
    }

    public static MiaLogger getLogger(){
        return logger;
    }

    public void shutdown(){
        getDataStorage().shutdown();
        getMQTTHandler().shutdown();
        getLogger().shutdown();
    }

    @Override
    public void receive(String cmd, List<String> data) {
        if(data.get(0).equals("shutdown"))
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    System.out.println("Shutting down ...");
                    shutdown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }).start();
        if(data.get(0).equals("testLog"))
            getLogger().logInfo("Testing log");
    }
}
