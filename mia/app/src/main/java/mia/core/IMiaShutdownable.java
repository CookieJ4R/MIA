package mia.core;

/***
 * This interface needs to be implemented on every node that needs to be closed/shutdowned before the program terminates
 */
public interface IMiaShutdownable {

    /**
     * This method gets called once the program begins to shut down
     */
    void shutdown();

}
