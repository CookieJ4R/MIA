package mia.core;

import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;

import java.time.LocalDateTime;

/***
 * This class represents an event that can be automatically triggered at a specific time
 */
public abstract class MiaTimedEvent implements Mappable {

    @Id
    private NitriteId eventDatabaseID;

    private LocalDateTime execDateTime;

    private TimedEventTriggerRate triggerRate;

    /***
     * Get the eventDatabaseID - probably only used in the implementations of the mappable methods in the subclasses
     * @return the eventDatabaseID of the TimedEvent
     */
    public NitriteId getEventDatabaseID() {
        return eventDatabaseID;
    }

    /***
     * Set the eventDatabaseID - probably only used in the implementations of the mappable methods in the subclasses
     * @param eventDatabaseID the new eventDatabaseID
     */
    public void setEventDatabaseID(NitriteId eventDatabaseID) {
        this.eventDatabaseID = eventDatabaseID;
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

    @Override
    public Document write(NitriteMapper mapper) {
        Document document = new Document();
        document.put("eventDatabaseID", document.getId());
        document.put("triggerRate", triggerRate);
        document.put("execDateTime", execDateTime);

        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            eventDatabaseID = document.getId();
            triggerRate = ((TimedEventTriggerRate) document.get("triggerRate"));
            execDateTime = ((LocalDateTime) document.get("execDateTime"));
        }
    }
}

