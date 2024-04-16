package dev.truewinter.snowmail.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import dev.truewinter.snowmail.Config;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Database {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final AccountDatabase accountDatabase;
    private final FormDatabase formDatabase;

    public Database() throws JsonProcessingException {
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder()
                        .automatic(true)
                        .conventions(List.of(Conventions.SET_PRIVATE_FIELDS_CONVENTION))
                        .build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyConnectionString(new ConnectionString(Config.getInstance().getMongoDb()))
                .build();
        this.mongoClient = MongoClients.create(settings);
        this.database = mongoClient.getDatabase("snowmail");

        this.accountDatabase = new AccountDatabase(this.database);
        this.formDatabase = new FormDatabase(this.database);
    }

    public AccountDatabase getAccountDatabase() {
        return accountDatabase;
    }

    public void destroy() {
        mongoClient.close();
    }
}
