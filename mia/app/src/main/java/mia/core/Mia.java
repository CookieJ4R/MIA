package mia.core;

public class Mia {

    private static MiaCommandBus commandBus;
    private static MiaDataStorage dataStorage;
    private static MiaMQTTHandler MQTTHandler;

    public Mia() {
        commandBus = new MiaCommandBus();
        dataStorage = new MiaDataStorage();
        MQTTHandler = new MiaMQTTHandler();


        new MiaWebserver().init("192.168.2.101", 80);
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

}
