package mia.core;

import org.dizitart.no2.Document;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/***
 * This class represents an event that can be automatically triggered at a specific time
 */
public class MiaTimedEvent implements Mappable {

    private long empId;

    private LocalDateTime execDateTime;
    private TimedEventType type;
    private String[] data;

    private TimedEventTriggerRate triggerRate;

    /***
     * Get the type of the event
     * @return the type of the event as TimedEventType
     */
    public TimedEventType getType() {
        return type;
    }

    /***
     * Get the data of the event.
     * The data contains everything that will be needed by the instance this event will be passed to by the ScheduledEventHandles handleEvent() method
     * @return the data of the event as String[]
     */
    public String[] getData() {
        return data;
    }

    /***
     * Get the trigger rate of the event.
     * @return the trigger rate of the event as TimedEventTriggerRate
     */
    public TimedEventTriggerRate getTriggerRate() {
        return triggerRate;
    }

    /***
     * Set the trigger rate of the event.
     * @param triggerRate the new triggerRate of the event
     */
    public void setTriggerRate(TimedEventTriggerRate triggerRate) {
        this.triggerRate = triggerRate;
    }

    /***
     * Get the execDateTime of the event.
     * @return the execDateTime as LocalDateTime
     */
    public LocalDateTime getExecDateTime() {
        return execDateTime;
    }

    /***
     * Set the execDateTime of the event.
     * @param execDateTime the new execDateTime.
     */
    public void setExecDateTime(LocalDateTime execDateTime) {
        this.execDateTime = execDateTime;
    }

    /***
     * Creates a new MiaTimedEvent of type TimedEventType.SCRIPT
     * @param data the data the event will contain
     * @return the newly generated event
     */
    public static MiaTimedEvent createScriptEvent(TimedEventTriggerRate triggerRate, String... data){
        MiaTimedEvent miaTimedEvent = new MiaTimedEvent();
        miaTimedEvent.triggerRate = triggerRate;
        miaTimedEvent.data = data;
        miaTimedEvent.type = TimedEventType.SCRIPT;
        return miaTimedEvent;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = new Document();
        document.put("empId", empId);
        document.put("type", type);
        document.put("triggerRate", triggerRate);
        document.put("execDateTime", execDateTime);
        document.put("data", data);

        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            empId = ((long) document.get("empId"));
            type = ((TimedEventType) document.get("type"));
            triggerRate = ((TimedEventTriggerRate) document.get("triggerRate"));
            execDateTime = ((LocalDateTime) document.get("execDateTime"));
            data = ((String[]) document.get("data"));
        }
    }
}

/***
 * This enum contains all TimedEventTypes
 */
enum TimedEventType{
    SCRIPT
}

