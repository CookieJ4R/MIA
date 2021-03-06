package mia.core;

import java.util.Arrays;
import java.util.List;

/***
 * This class loads all the core modules as well as the extension modules
 */
public class Mia implements IMiaCommandBusNode{

    private static MiaCommandBus commandBus;
    private static MiaDataStorage dataStorage;
    private static MiaMQTTHandler MQTTHandler;
    private static MiaLogger logger;
    private static MiaWebserver webserver;
    private static MiaShutdownManager shutdownManager;
    private static MiaConfig config;

    public Mia() {

        //Initialize the Config first so other parts can use config values
        config = new MiaConfig();

        logger = new MiaLogger();
        shutdownManager = new MiaShutdownManager();
        commandBus = new MiaCommandBus();
        dataStorage = new MiaDataStorage();
        MQTTHandler = new MiaMQTTHandler(getConfig().getProperty("mqttBrokerIP"));
        //webserver = new MiaWebserver(getConfig().getProperty("webserverIP"), getConfig().getProperty("webserverPort"));

        addShutdownHooks();

        getCommandBus().register(this, Arrays.asList("mqtt:core"));

        logger.logInfo("Test");
        logger.logWarning("Test");
        logger.logError("Test", false);
    }

    /***
     * Register all shutdown hooks
     */
    private void addShutdownHooks(){
        //Shut down logger last so the shutdown process of other modules can be logged
        getShutdownManager().addShutdownableNode(getLogger(),10);

        getShutdownManager().addShutdownableNode(getMQTTHandler(), 0);
        getShutdownManager().addShutdownableNode(getDataStorage(),0);
        //getShutdownManager().addShutdownableNode(getWebserver(),0);
    }

    /***
     * Get the CommandBus instance
     * @return the CommandBus instance
     */
    public static MiaCommandBus getCommandBus(){
        return commandBus;
    }
    /***
     * Get the DataStorage instance
     * @return the DataStorage instance
     */
    public static MiaDataStorage getDataStorage(){
        return dataStorage;
    }
    /***
     * Get the MQTTHandler instance
     * @return the MQTTHandler instance
     */
    public static MiaMQTTHandler getMQTTHandler(){
        return MQTTHandler;
    }
    /***
     * Get the Logger instance
     * @return the Logger instance
     */
    public static MiaLogger getLogger(){
        return logger;
    }
    /***
     * Get the ShutdownManager instance
     * @return the ShutdownManager instance
     */
    public static MiaShutdownManager getShutdownManager(){
        return shutdownManager;
    }
    /***
     * Get the Webserver instance
     * @return the Webserver instance
     */
    public static MiaWebserver getWebserver(){
        return webserver;
    }
    /***
     * Get the Config instance
     * @return the Config instance
     */
    public static MiaConfig getConfig(){
        return config;
    }

    @Override
    public void receive(String cmd, List<String> data) {
        getLogger().logInfo("CORE: '" + data.get(0) + "' command received");
        if(data.get(0).equals("shutdown"))
            getShutdownManager().shutdownSystem();
        if(data.get(0).equals("testLog"))
            getLogger().logInfo("Testing log");
    }
}
