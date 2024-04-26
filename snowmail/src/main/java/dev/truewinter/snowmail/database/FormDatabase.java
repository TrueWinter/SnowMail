package dev.truewinter.snowmail.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import io.javalin.http.NotFoundResponse;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormDatabase {
    private final MongoDatabase database;

    public FormDatabase(MongoDatabase database) {
        this.database = database;
    }

    private MongoCollection<Document> getCollection() {
        return database.getCollection("forms");
    }

    private Form documentToForm(Document document) throws JsonProcessingException {
        String id = document.getObjectId("_id").toHexString();

        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(document.toJson());
        node.remove("_id");
        node.put("id", id);

        return JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build()
                .treeToValue(node, Form.class);
    }

    public Optional<Form> getForm(String id) throws JsonProcessingException {
        if (!ObjectId.isValid(id)) return Optional.empty();
        Document searchQuery = new Document();
        searchQuery.put("_id", new ObjectId(id));

        Document result = getCollection().find(searchQuery).first();
        if (result != null) {
            return Optional.of(documentToForm(result));
        }

        return Optional.empty();
    }

    public List<Form> getForms() throws JsonProcessingException {
        List<Form> forms = new ArrayList<>();

        for (Document document : getCollection().find()) {
            forms.add(documentToForm(document));
        }

        return forms;
    }

    public boolean formExists(String id) throws JsonProcessingException {
        return getForm(id).isPresent();
    }

    public String createForm(Form form) throws JsonProcessingException {
        Document document = Document.parse(new ObjectMapper().writeValueAsString(form));
        return getCollection().insertOne(document).getInsertedId().asObjectId().getValue().toHexString();
    }

    public void editForm(Form form) throws JsonProcessingException {
        Optional<Form> form1 = getForm(form.getId().toHexString());
        if (form1.isEmpty()) {
            throw new NotFoundResponse();
        }

        Document searchQuery = new Document();
        searchQuery.put("_id", form.getId());

        Document document = Document.parse(new ObjectMapper().writeValueAsString(form));
        Document updateDocument = new Document();
        updateDocument.put("$set", document);

        getCollection().findOneAndUpdate(searchQuery, updateDocument);
    }

    public void deleteForm(String id) {
        if (!ObjectId.isValid(id)) return;
        Document searchQuery = new Document();
        searchQuery.put("_id", new ObjectId(id));
        getCollection().findOneAndDelete(searchQuery);
    }
}
