package mia.isaac;

import java.time.LocalDateTime;

/***
 * A slighly more advanced version of IsaacScript.
 * A IsaacTimedScript can be scheduled to be executet at a specific time by registering it in the TimedExecutor.
 */
public class IsaacTimedScript extends IsaacScript{

    private LocalDateTime executionDateTime;

    public IsaacTimedScript(LocalDateTime executionDateTime){
        this.executionDateTime = executionDateTime;
    }

    /***
     * Get the execution time of the script
     * @return the execution time of the script
     */
    public LocalDateTime getExecutionTime() {
        return executionDateTime;
    }
    /***
     * Set the execution time of the script
     */
    public void setExecutionTime(LocalDateTime executionDateTime) {
        this.executionDateTime = executionDateTime;
    }
}
