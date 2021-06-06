package mia.isaac;

import mia.core.Mia;
import mia.core.MiaTimedEvent;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * This class loads all scripts and stores them via their callID
 */
public class IsaacScriptLoader {

    private final HashMap<String, IsaacScript> loadedScripts = new HashMap<>();

    private final String scriptPath = "../scripts";

    private final String META_SCRIPT_TYPE= "ISAAC-script-type=";
    private final String META_CALL_ID = "call-id:";
    private final String META_EXEC_TIME = "exec-time:";

    private String META_TIMED_IDENTIFIER = "TIMED";

    /***
     * Loads all scripts and stores them in loadedScripts with their callIDs as keys
     * If the script is a timed script it will be automatically scheduled in the ScheduledEventHandler
     */
    public void loadScripts(){
        Mia.getLogger().logInfo("Loading scripts from: " + scriptPath);
        File scriptFolder = new File(scriptPath);
        scriptFolder.mkdir();
        File[] scripts = scriptFolder.listFiles();

        for (File file : scripts) {
            try {
                IsaacScript script = createScriptFromFile(file);
                if (script instanceof IsaacTimedScript) {
                    Mia.getScheduledEventHandler().scheduleTimedEvent(MiaTimedEvent.createScriptEvent(script.getScriptCallID()), ((IsaacTimedScript) script).getExecutionTime());
                }
                loadedScripts.put(script.getScriptCallID(), script);
            } catch (IsaacScriptSyntaxException e) {
                Mia.getLogger().logError("Script syntax error detected", false);
                e.printStackTrace();
            }
        }
    }

    /***
     * Creates an IsaacScript from a passed File
     * @param scriptFile the script file that will be parsed to an IsaacScript
     * @return the generated IsaacScript (if the metaData is set to "TIMED", an IsaacTimedScript is returned instead)
     * @throws IsaacScriptSyntaxException
     */
    private IsaacScript createScriptFromFile(File scriptFile) throws IsaacScriptSyntaxException {
        String metaData = null;
        List<String> tempLineList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))){
            while(br.ready()){
                String line = br.readLine().trim();
                if(line.startsWith("<")){ metaData = line; continue;}
                tempLineList.add(line);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        IsaacScript script;
        if(getMetaDataPart(metaData, META_SCRIPT_TYPE).equals(META_TIMED_IDENTIFIER)) {
            LocalTime time = LocalTime.parse(getMetaDataPart(metaData, META_EXEC_TIME));
            script = new IsaacTimedScript(LocalDateTime.of(LocalDate.now(), time));
        }
        else
            script = new IsaacScript();

        script.setScriptCallID(getMetaDataPart(metaData, META_CALL_ID));
        String[] lines = new String[tempLineList.size()];
        script.setScriptLines(tempLineList.toArray(lines));
        return script;
    }

    /***
     * Returns the value of a specific part of the passed comma-separated meta-data
     * @param metaDataComplete the complete meta-data to get a specific part of
     * @param metaDataName the meta-data part to retrieve (comparable to a key of a map)
     * @return the value retrieved from the meta-data
     * @throws IsaacScriptSyntaxException
     */
    private String getMetaDataPart(String metaDataComplete, String metaDataName) throws IsaacScriptSyntaxException {
        try {
            return metaDataComplete.split(metaDataName)[1].split("[,>]")[0].trim();
        } catch (Exception e){
            throw new IsaacScriptSyntaxException();
        }
    }

    /***
     * Returns a script by looking it up via its callID
     * @param callID the callID to lookup
     * @return the script associated with the passed callID
     */
    public IsaacScript getScript(String callID) {
        return loadedScripts.get(callID);
    }

    public boolean isScriptLoaded(String callID){return loadedScripts.containsKey(callID);}
}
