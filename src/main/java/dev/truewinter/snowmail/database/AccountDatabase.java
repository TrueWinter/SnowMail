package dev.truewinter.snowmail.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import dev.truewinter.snowmail.pojo.objects.Account;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDatabase {
    private MongoDatabase database;

    protected AccountDatabase(MongoDatabase database) throws JsonProcessingException {
        this.database = database;

        /*
            For the account database, there's no need to check if exists.
            The initialization should run whenever no accounts exist.
         */
        MongoCollection<Document> collection = getCollection();
        if (collection.countDocuments() == 0) {
            IndexOptions indexOptions = new IndexOptions().unique(true);
            collection.createIndex(Indexes.ascending("username"), indexOptions);
            createOrEditAccount(new Account("admin", "snowmail", false));
        }
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("accounts");
    }

    public void createOrEditAccount(Account account) throws JsonProcessingException {
        Document document = Document.parse(JsonMapper.builder()
                .configure(MapperFeature.USE_ANNOTATIONS, false)
                .build()
                .writeValueAsString(account));

        Optional<Account> account1 = getAccount(account.getUsername());
        if (account1.isPresent()) {
            Document searchQuery = new Document();
            searchQuery.put("username", account.getUsername());

            Document updateDocument = new Document();
            updateDocument.put("$set", document);

            getCollection().findOneAndUpdate(searchQuery, updateDocument);
        } else {
            getCollection().insertOne(document);
        }
    }

    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        getCollection().find(Account.class).forEach(accounts::add);
        return accounts;
    }

    public Optional<Account> getAccount(String username) {
        Document searchQuery = new Document();
        searchQuery.put("username", username);

        Account result = getCollection().find(searchQuery, Account.class).first();
        if (result != null) {
            return Optional.of(result);
        }

        return Optional.empty();
    }

    public Optional<Account> getAccountIfPasswordIsCorrect(String username, String password) {
        Optional<Account> account = getAccount(username);

        if (account.isPresent() && Account.isCorrectPassword(password, account.get())) {
            return account;
        }

        return Optional.empty();
    }

    public void deleteAccount(String username) {
        Document searchQuery = new Document();
        searchQuery.put("username", username);
        getCollection().findOneAndDelete(searchQuery);
    }
}
