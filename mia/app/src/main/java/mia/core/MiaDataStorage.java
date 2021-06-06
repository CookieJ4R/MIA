package mia.core;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;
import org.dizitart.no2.mapper.JacksonMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Indices;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/***
 * This class provides access to a simple nitrite database.
 */
public final class MiaDataStorage implements IMiaShutdownable, Serializable {
    private Nitrite db;

    private final String tokenCollection = "tokens";

    public MiaDataStorage(){
        JacksonMapper nitriteMapper = new JacksonMapper();
        nitriteMapper.getObjectMapper().registerModule(new JavaTimeModule());
        db = Nitrite.builder()
                .compressed()
                .nitriteMapper(nitriteMapper)
                .filePath("../miaDatabase.db")
                .openOrCreate();
    }

    public void storeEvent(MiaTimedEvent event){
        ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
        timedEventStorage.insert(event);
    }

    public List<MiaTimedEvent> getEventsForDateTime(LocalDateTime dateTime){
        ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedEvent> cursor = timedEventStorage.find(ObjectFilters.eq("execDateTime", dateTime));
        return cursor.toList();
    }

    public List<MiaTimedEvent> getAllEvents(){
        ObjectRepository<MiaTimedEvent> timedEventStorage = db.getRepository(MiaTimedEvent.class);
        org.dizitart.no2.objects.Cursor<MiaTimedEvent> cursor = timedEventStorage.find();
        return cursor.toList();
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
