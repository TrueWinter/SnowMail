package dev.truewinter.snowmail.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class FormDatabase {
    private final MongoDatabase database;

    public FormDatabase(MongoDatabase database) {
        this.database = database;
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("forms");
    }
}
