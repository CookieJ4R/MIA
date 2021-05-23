package mia.core;

import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;

public final class MiaDataStorage {

    private MiaDataStorage(){}

    private static MiaDataStorage instance = new MiaDataStorage();
    public static MiaDataStorage getInstance() {return instance;}

    private Nitrite db = Nitrite.builder()
            .compressed()
            .filePath("../miaDatabase.db")
            .openOrCreate();

    private final String tokenCollection = "tokens";

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

}
