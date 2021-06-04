package mia.core;

/***
 * This class represents an event that can be automatically triggered at a specific time
 */

public class MiaTimedEvent {

    private TimedEventType type;
    private String[] data;

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
     * Creates a new MiaTimedEvent of type TimedEventType.SCRIPT
     * @param data the data the event will contain
     * @return the newly generated event
     */
    public static MiaTimedEvent createScriptEvent(String... data){
        MiaTimedEvent miaTimedEvent = new MiaTimedEvent();
        miaTimedEvent.data = data;
        miaTimedEvent.type = TimedEventType.SCRIPT;
        return miaTimedEvent;
    }

}

/***
 * This enum contains all TimedEventTypes
 */
enum TimedEventType{
    SCRIPT
}
