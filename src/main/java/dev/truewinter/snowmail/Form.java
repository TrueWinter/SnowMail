package dev.truewinter.snowmail;

import dev.truewinter.snowmail.inputs.Input;

import java.util.HashMap;
import java.util.LinkedList;

public class Form {
    private String name;
    private String email;
    private HashMap<String, String> metadata;
    private LinkedList<Input> inputs;

    public Form() {}
    public Form(String name, String email, HashMap<String, String> metadata, LinkedList<Input> inputs) {
        this.name = name;
        this.email = email;
        this.metadata = metadata;
        this.inputs = inputs;
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
}
