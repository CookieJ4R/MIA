package mia.core;

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
        eventExecutionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDateTime dateTimeToLookup = LocalDateTime.now().withSecond(0).withNano(0);
                if(scheduledEvents.containsKey(dateTimeToLookup)){
                    List<MiaTimedEvent> events = scheduledEvents.get(dateTimeToLookup);
                    events.forEach((event) -> handleEvent(event));
                }
            }
        }, (60-LocalTime.now().getSecond()) * 1000, 60000);
    }

    /***
     * Internal method for handling an occurred event
     * @param event the event to handle
     */
    private void handleEvent(MiaTimedEvent event){
        switch (event.getType()) {
            case SCRIPT -> Mia.getCommandBus().emit("isaac", "run", event.getData());
        }
    }

    /***
     * Places an event in the scheduledEvent hashmap
     * @param event the event to insert
     * @param execTime the time the event will execute
     */
    public void scheduleTimedEvent(MiaTimedEvent event, LocalDateTime execTime){
        LocalDateTime shortenedTime = execTime.withNano(0);
        if(scheduledEvents.containsKey(shortenedTime)){
            scheduledEvents.get(shortenedTime).add(event);
        }
        else{
            List<MiaTimedEvent> events = new ArrayList<>();
            events.add(event);
            scheduledEvents.put(shortenedTime, events);
        }
    }

    @Override
    public void shutdown() {
        eventExecutionTimer.cancel();
    }
}
