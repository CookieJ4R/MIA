package mia.core;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***
 * This class provides access to a simple nitrite database.
 */
public final class MiaDataStorage implements IMiaShutdownable, Serializable {
    private Nitrite db;

    private final String tokenCollection = "tokens";

    public MiaDataStorage(){
        db = Nitrite.builder()
                .compressed()
                .registerModule(new JavaTimeModule())
                .filePath("../miaDatabase.db")
                .openOrCreate();
    }

    /***
     * Store an event with its executing date in the database
     * Will call storeScriptEvent() when the passed event is a script event
     * @param event the event to be stored
     */
    public void storeEvent(MiaTimedEvent event){
        if(event instanceof MiaTimedScriptEvent){
            storeScriptEvent((MiaTimedScriptEvent) event);
        }else {
            ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
            timedEventStorage.insert(event);
        }
    }

    /***
     * Intern method that gets called when a MiaTimedScriptEvent is passed to storeEvent()
     * @param event the event to be stored
     */
    private void storeScriptEvent(MiaTimedScriptEvent event){
        ObjectRepository<MiaTimedScriptEvent> timedScriptEventStorage = db.getRepository(MiaTimedScriptEvent.class);
        timedScriptEventStorage.insert(event);
    }


    /***
     * Updates an event in the corresponding collection
     * @param event the event to update
     */
    public void updateEvent(MiaTimedEvent event) {
        if(event instanceof MiaTimedScriptEvent){
            ObjectRepository<MiaTimedScriptEvent> scriptTimedEventStorage = db.getRepository(MiaTimedScriptEvent.class);
            scriptTimedEventStorage.update(ObjectFilters.eq("_id", event.getEventDatabaseID()), (MiaTimedScriptEvent) event);
        }else {
            ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
            timedEventStorage.update(ObjectFilters.eq("_id", event.getEventDatabaseID()), event);
        }
    }

    /***
     * Checks if a event collection contains the passed event
     * @param event the event to check for
     * @return if a event collection contains the element
     */
    public boolean containsEvent(MiaTimedEvent event) {
        boolean containsEvent = false;
        if(event instanceof MiaTimedScriptEvent){
            ObjectRepository<MiaTimedScriptEvent> scriptTimedEventStorage = db.getRepository(MiaTimedScriptEvent.class);
            org.dizitart.no2.objects.Cursor<MiaTimedScriptEvent> scriptCursor = scriptTimedEventStorage.find(ObjectFilters.eq("_id", event.getEventDatabaseID()));
            if(scriptCursor.totalCount() > 0) containsEvent = true;
        }else {
            ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
            org.dizitart.no2.objects.Cursor<MiaTimedEvent> cursor = timedEventStorage.find(ObjectFilters.eq("_id", event.getEventDatabaseID()));
            if(cursor.totalCount() > 0) containsEvent = true;
        }
        return containsEvent;
    }

    /***
     * Gets all events for a specific LocalDateTime from all timedEventCollections
     * @param dateTime the LocalDateTime to look up
     * @return a list of MiaTimedEvents for the passed dateTime
     */
    public List<MiaTimedEvent> getEventsForDateTime(LocalDateTime dateTime){
        List<MiaTimedEvent> allEventsForDateTime = new ArrayList<>();
        ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedEvent> cursor = timedEventStorage.find(ObjectFilters.eq("execDateTime", dateTime));

        ObjectRepository<MiaTimedScriptEvent> scriptTimedEventStorage = db.getRepository(MiaTimedScriptEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedScriptEvent> cursorScriptEvents = scriptTimedEventStorage.find(ObjectFilters.eq("execDateTime", dateTime));

        allEventsForDateTime.addAll(cursor.toList());
        allEventsForDateTime.addAll(cursorScriptEvents.toList());
        return allEventsForDateTime;
    }

    /***
     * Gets all events from all timedEventCollections
     * @return a list of all MiaTimedEvents stored in the database
     */
    public List<MiaTimedEvent> getAllEvents(){
        List<MiaTimedEvent> allEvents = new ArrayList<>();

        ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedEvent> cursor = timedEventStorage.find();

        ObjectRepository<MiaTimedScriptEvent> scriptTimedEventStorage = db.getRepository(MiaTimedScriptEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedScriptEvent> cursorScriptEvents = scriptTimedEventStorage.find();

        allEvents.addAll(cursor.toList());
        allEvents.addAll(cursorScriptEvents.toList());
        return allEvents;
    }

    /***
     * Stores a Key-Value token in the database.
     * @param tokenName the name of the token
     * @param tokenValue the value of the token
     */
    public void storeToken(String tokenName, String tokenValue){
        NitriteCollection collection = db.getCollection(tokenCollection);
        Cursor c = collection.find(Filters.eq("tokenName", tokenName));
        if(c.idSet().size() > 0) {
            for (Document doc : c) {
                doc.put("tokenValue", tokenValue);
                collection.update(doc);
                break;
            }
        }else {
            Document document = Document.createDocument("tokenName", tokenName).put("tokenValue", tokenValue);
            collection.insert(document);
        }
        collection.close();
    }

    /***
     * Retrieve a token from the database.
     * @param tokenName the token name to retrieve
     * @return the tokenValue that belongs to the passed tokenName
     */
    public String getToken(String tokenName){
        NitriteCollection collection = db.getCollection(tokenCollection);
        Cursor c = collection.find(Filters.eq("tokenName", tokenName));
        String tokenValue = "";
        for (Document doc : c) {
            tokenValue = doc.get("tokenValue", String.class);
            break;
        }
        collection.close();
        return tokenValue;
    }

    /***
     * Gets the amount of documents in a collection
     * @param collectionName the name of the collection
     * @return the amount of documents in the collection
     */
    public int getDocumentCountForCollection(String collectionName){
        NitriteCollection collection = db.getCollection(collectionName);
        int entries = collection.find().idSet().size();
        collection.close();
        return entries;
    }

    public void shutdown(){
        db.close();
    }

}
