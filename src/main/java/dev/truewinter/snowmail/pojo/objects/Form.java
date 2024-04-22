package dev.truewinter.snowmail.pojo.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.truewinter.snowmail.Util;
import dev.truewinter.snowmail.inputs.Input;
import dev.truewinter.snowmail.pojo.Views;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.LinkedList;

public class Form {
    @BsonId
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @JsonView(Views.DashboardSummary.class)
    private String name;
    @JsonView(Views.DashboardSummary.class)
    private String email;
    @JsonView(Views.DashboardFull.class)
    private HashMap<String, String> metadata = new HashMap<>();
    @JsonView({Views.DashboardFull.class, Views.Public.class})
    private LinkedList<Input> inputs;

    public Form() {}
    public Form(String name, String email, HashMap<String, String> metadata, LinkedList<Input> inputs) {
        this(ObjectId.get(), name, email, metadata, inputs);
    }
    public Form(ObjectId id, String name, String email, HashMap<String, String> metadata, LinkedList<Input> inputs) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.metadata = metadata;
        this.inputs = inputs;
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public LinkedList<Input> getInputs() {
        return inputs;
    }

    @JsonIgnore
    public boolean isValid() {
        if (Util.isBlank(name) || Util.isBlank(email)) return false;
        if (!email.matches("^\\S+@\\S+$")) return false;

        for (Input input : inputs) {
            if (!input.isValid()) return false;
        }

        return true;
    }
}
