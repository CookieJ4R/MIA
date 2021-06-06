package mia.core;

import mia.isaac.Isaac;

import java.time.LocalDateTime;
import java.util.*;

/***
 * This class loads all the core modules as well as the extension modules
 */
public class Mia implements IMiaCommandBusNode{

    private static MiaCommandBus commandBus;
    private static MiaDataStorage dataStorage;
    private static MiaMQTTHandler MQTTHandler;
    private static MiaScheduledEventHandler miaScheduledEventHandler;
    private static MiaLogger logger;
    private static MiaWebserver webserver;
    private static MiaShutdownManager shutdownManager;
    private static MiaConfig config;

    private List<IMiaExtensionModule> modules = new ArrayList<>();

    public Mia() {

        //Initialize the Config first so other parts can use config values
        config = new MiaConfig();

        logger = new MiaLogger();
        getLogger().logInfo("Starting Mia...");
        shutdownManager = new MiaShutdownManager();
        commandBus = new MiaCommandBus();
        dataStorage = new MiaDataStorage();
        miaScheduledEventHandler = new MiaScheduledEventHandler();
        MQTTHandler = new MiaMQTTHandler(getConfig().getProperty("mqttBrokerIP"));
        webserver = new MiaWebserver(getConfig().getProperty("webserverIP"), getConfig().getProperty("webserverPort"));

        registerExtensionModules();
        initExtensionModules();

        getCommandBus().register(this, Arrays.asList("mqtt:core", "core"));


        addShutdownHooks();

        getCommandBus().emit("core", "testLog");

        //getScheduledEventHandler().scheduleTimedEvent(MiaTimedScriptEvent.createScriptEvent("testscript", TimedEventTriggerRate.ONCE), LocalDateTime.now().plusMinutes(1).withNano(0).withSecond(0));
        //MiaTimedScriptEvent mia = MiaTimedScriptEvent.createScriptEvent("testscript", TimedEventTriggerRate.MINUTELY);
        //LocalDateTime now = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        //getScheduledEventHandler().scheduleTimedEvent(mia, now);
    }

    /***
     * Register all shutdown hooks
     */
    private void addShutdownHooks(){
        getLogger().logInfo("Adding shutdown hooks...");
        //Shut down logger last so the shutdown process of other modules can be logged
        getShutdownManager().addShutdownableNode(getLogger(),10);

        getShutdownManager().addShutdownableNode(getScheduledEventHandler(), 0);
        getShutdownManager().addShutdownableNode(getMQTTHandler(), 0);
        getShutdownManager().addShutdownableNode(getDataStorage(),0);
        getShutdownManager().addShutdownableNode(getWebserver(),0);
        modules.forEach(IMiaExtensionModule::addShutdownHooks);
    }

    /***
     * In this method all extension modules get registered
     */
    private void registerExtensionModules(){
        registerModule(new Isaac());
    }

    /***
     * This method calls init() on every registered extension module
     */
    private void initExtensionModules(){
        getLogger().logInfo("Initializing extension modules...");
        modules.forEach(IMiaExtensionModule::initModule);
    }

    /***
     * A simple wrapper method to provide nicer looking registration of modules
     * @param module the modul to register
     */
    private void registerModule(IMiaExtensionModule module){
        modules.add(module);
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

    /***
     * Get the ScheduledEventHandler instance
     * @return the ScheduledEventHandler instance
     */
    public static MiaScheduledEventHandler getScheduledEventHandler() {
        return miaScheduledEventHandler;
    }

    @Override
    public void receive(String cmd, List<String> data) {
        getLogger().logInfo("CORE: '" + cmd + "' command received");
        switch (cmd) {
            case "shutdown" -> getShutdownManager().shutdownSystem();
            case "testLog" -> {
                getLogger().logInfo("Testing log");
                getLogger().logWarning("Testing log");
                getLogger().logError("Testing log", false);
            }
        }
    }
}
