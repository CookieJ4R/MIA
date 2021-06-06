package mia.core;

import mia.isaac.Isaac;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/***
 * This class handles scheduled execution of MiaTimedEvents.
 * Most of the time these will probably be scripts.
 * However, in the future this class may also play a role in implementing timer, alarms and a calendar
 */
public class MiaScheduledEventHandler implements IMiaShutdownable{

    private final Map<LocalDateTime, List<MiaTimedEvent>> scheduledEvents = new HashMap<>();

    public Timer eventExecutionTimer;

    public MiaScheduledEventHandler(){
        init();
    }

    /***
     * Initializes the ScheduledEventHandler
     */
    private void init(){
        eventExecutionTimer = new Timer();

        //Schedule all events stored in the database
        Mia.getDataStorage().getAllEvents().forEach((event) -> {
            if(event.getExecDateTime().isAfter(LocalDateTime.now())){
                scheduleTimedEvent(event, event.getExecDateTime());
                Mia.getLogger().logInfo("Scheduled event from database for: " + event.getExecDateTime());
            }
            else{
                Mia.getLogger().logWarning("Scheduled execution time in database has already passed...calculating new one...");
                LocalDateTime nextExecTime = event.getExecDateTime();
                do{
                    nextExecTime = getNextExecDateTime(nextExecTime, event.getTriggerRate());
                }while(nextExecTime != null && nextExecTime.isBefore(LocalDateTime.now()));
                if(nextExecTime != null) {
                    Mia.getLogger().logInfo("Calculation finished...Now scheduled event from database for: " + nextExecTime);
                    scheduleTimedEvent(event, nextExecTime);
                }
            }
        });

        eventExecutionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDateTime dateTimeToLookup = LocalDateTime.now().withSecond(0).withNano(0);
                if(scheduledEvents.containsKey(dateTimeToLookup)){
                    List<MiaTimedEvent> events = scheduledEvents.get(dateTimeToLookup);
                    events.forEach((event) -> handleEvent(event));
                    scheduledEvents.remove(dateTimeToLookup);
                }
            }
        }, (60-LocalTime.now().getSecond()) * 1000, 60000);
    }

    /***
     * Internal method for handling an occurred event
     * @param event the event to handle
     */
    private void handleEvent(MiaTimedEvent event){

        if(event instanceof MiaTimedScriptEvent){
            MiaTimedScriptEvent scriptEvent = (MiaTimedScriptEvent)event;
            Mia.getCommandBus().emit("isaac", "run", scriptEvent.getScriptCallID());
        }

        //Schedule next execution
        LocalDateTime nextExecTime;
        nextExecTime = getNextExecDateTime(event.getExecDateTime(), event.getTriggerRate());
        if(nextExecTime == null) return;
        event.setExecDateTime(nextExecTime);
        if(event.getEventDatabaseID() != null && Mia.getDataStorage().containsEvent(event))
            Mia.getDataStorage().updateEvent(event);
        else
            Mia.getDataStorage().storeEvent(event);
        scheduleTimedEvent(event, nextExecTime);
        Mia.getLogger().logInfo("Scheduled next event trigger for: " + nextExecTime);
    }

    /***
     * Calculate the next LocalDateTime the event will trigger based on the passed triggerRate
     * @param oldExecTime the last time the script got executed (probably close to the current time, since
     *                    this methode gets called on script execution)
     * @param triggerRate the TimedEventTriggerRate specifying if and how often a TimedEvent will trigger
     * @return
     */
    private LocalDateTime getNextExecDateTime(LocalDateTime oldExecTime, TimedEventTriggerRate triggerRate){
        LocalDateTime nextExecTime = null;
        switch (triggerRate){
            case MINUTELY -> nextExecTime = oldExecTime.plusMinutes(1);
            case HOURLY -> nextExecTime = oldExecTime.plusHours(1);
            case DAILY -> nextExecTime = oldExecTime.plusDays(1);
            case WEEKLY -> nextExecTime = oldExecTime.plusWeeks(1);
            case MONTHLY -> nextExecTime = oldExecTime.plusMonths(1);
            case YEARLY -> nextExecTime = oldExecTime.plusYears(1);
        }
        return  nextExecTime;
    }

    /***
     * Places an event in the scheduledEvent hashmap
     * @param event the event to insert
     * @param execTime the time the event will execute
     */
    public void scheduleTimedEvent(MiaTimedEvent event, LocalDateTime execTime){
        LocalDateTime shortenedTime = execTime.withNano(0);
        event.setExecDateTime(shortenedTime);
        if(scheduledEvents.containsKey(shortenedTime)){
            scheduledEvents.get(shortenedTime).add(event);
        }
        else{
            List<MiaTimedEvent> events = new ArrayList<>();
            events.add(event);
            scheduledEvents.put(shortenedTime, events);
        }
        Mia.getLogger().logInfo("Scheduled event for: " + execTime);
    }

    @Override
    public void shutdown() {
        eventExecutionTimer.cancel();
    }
}
