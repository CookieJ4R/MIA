package mia.isaac;

import java.time.LocalTime;

/***
 * A slighly more advanced version of IsaacScript.
 * A IsaacTimedScript can be scheduled to be executet at a specific time by registering it in the TimedExecutor.
 */
public class IsaacTimedScript extends IsaacScript{

    private LocalTime executionTime;

    public IsaacTimedScript(LocalTime executionTime){
        this.executionTime = executionTime;
    }

    /***
     * Get the execution time of the script
     * @return the execution time of the script
     */
    public LocalTime getExecutionTime() {
        return executionTime;
    }
    /***
     * Set the execution time of the script
     */
    public void setExecutionTime(LocalTime executionTime) {
        this.executionTime = executionTime;
    }
}
