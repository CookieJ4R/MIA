package mia.core;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;

import java.time.LocalDateTime;

/***
 * A subclass of MiaTimedEvents for Scripts that need or want to be called at a specific time
 */
public class MiaTimedScriptEvent extends MiaTimedEvent implements Mappable {

    private String scriptCallID;

    public MiaTimedScriptEvent(){}

    /***
     * Get the scriptCallID of the event
     * @return the scriptCallID of the event
     */
    public String getScriptCallID() {
        return scriptCallID;
    }

    /***
     * Set the scriptCallID of the event
     * @param scriptCallID the new scriptCallID of the event
     */
    public void setScriptCallID(String scriptCallID) {
        this.scriptCallID = scriptCallID;
    }

    /***
     * Creates a new MiaTimedScriptEvent
     * @param scriptCallID the scriptID of the script the event will trigger
     * @param triggerRate wenn the event will trigger
     * @return the new event as MiaTimedScriptEvent
     */
    public static MiaTimedScriptEvent createScriptEvent(String scriptCallID, TimedEventTriggerRate triggerRate){
        MiaTimedScriptEvent miaTimedScriptEvent = new MiaTimedScriptEvent();
        miaTimedScriptEvent.setScriptCallID(scriptCallID);
        miaTimedScriptEvent.setTriggerRate(triggerRate);
        return miaTimedScriptEvent;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = new Document();
        document.put("eventDatabaseID", document.getId());
        document.put("triggerRate", getTriggerRate());
        document.put("execDateTime", getExecDateTime());
        document.put("scriptCallID", getScriptCallID());
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setEventDatabaseID(document.getId());
            setTriggerRate((TimedEventTriggerRate) document.get("triggerRate"));
            setExecDateTime((LocalDateTime) document.get("execDateTime"));
            setScriptCallID((String) document.get("scriptCallID"));
        }
    }

}
