package mia.isaac;

/***
 * This class represents a simple Isaac Script that can only be called by explicitly calling runScript with its callID
 */
public class IsaacScript {
    private String scriptCallID;
    private String[] scriptLines;

    /***
     * Set the callID of the script
     */
    public void setScriptCallID(String scriptCallID) {
        this.scriptCallID = scriptCallID;
    }
    /***
     * Set the scriptLines of the script
     */
    public void setScriptLines(String[] scriptLines) {
        this.scriptLines = scriptLines;
    }
    /***
     * Get the callID of the script
     * @return the callID of the script
     */
    public String getScriptCallID() {
        return scriptCallID;
    }
    /***
     * Get the scriptLines of the script
     * @return the scriptLines of the script
     */
    public String[] getScriptLines() {
        return scriptLines;
    }
}
