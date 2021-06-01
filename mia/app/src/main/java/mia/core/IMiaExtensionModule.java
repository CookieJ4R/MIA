package mia.core;

/***
 * This interface needs to be implemented in every extension module that want to be registered in the Mia.java file
 */
public interface IMiaExtensionModule {

    /***
     * This gets called for every module thats registered in the Mia File to initialize the module
     */
    void initModule();

    /***
     * This get called during addShutdownHooks() of the core program to add module specific shutdown hooks
      */
    void addShutdownHooks();

}
