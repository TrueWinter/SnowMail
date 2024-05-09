package dev.truewinter.snowmail.api.pojo.objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.inputs.AbstractTextInput;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.pojo.Views;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    /**
     * @return The form ID
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * @return The form name
     */
    public String getName() {
        return name;
    }

    /**
     * {@link #getName()}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The email form submissions are sent to
     */
    public String getEmail() {
        return email;
    }

    /**
     * {@link #getEmail()}
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Form metadata
     */
    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    /**
     * @return The LinkedList of inputs
     */
    public LinkedList<Input> getInputs() {
        return inputs;
    }

    /**
     * Returns all the {@link AbstractTextInput}s from the Input list
     * @return A HashMap, keyed by the input name
     */
    public static HashMap<String, AbstractTextInput> recursivelyGetInputs(List<Input> inputs) {
        HashMap<String, AbstractTextInput> out = new HashMap<>();

        for (Input input : inputs) {
            if (input instanceof AbstractTextInput textInput) {
                out.put(((AbstractTextInput) input).getName(), textInput);
            }

            if (input instanceof Input.MultipleInputs) {
                out.putAll(recursivelyGetInputs(((Input.MultipleInputs) input).getInputs()));
            }
        }

        return out;
    }

    /**
     * @apiNote You should call this method before attempting to save the form
     * @return Whether this form is valid
     */
    @JsonIgnore
    public boolean isValid() {
        if (Util.isBlank(name) || Util.isBlank(email)) return false;
        if (!email.matches("^\\S+@\\S+$")) return false;

        for (Input input : inputs) {
            if (!input.isValid()) return false;
        }

        for (String key : metadata.keySet()) {
            if (!key.matches("^[0-9a-zA-Z\\-_]+$")) return false;
        }

        return true;
    }
}
