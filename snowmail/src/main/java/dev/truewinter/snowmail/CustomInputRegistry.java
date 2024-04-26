package dev.truewinter.snowmail;

import dev.truewinter.snowmail.api.inputs.Input;

import java.util.ArrayList;
import java.util.List;

public class CustomInputRegistry {
    private static CustomInputRegistry customInputRegistry;
    private final List<Input> inputs = new ArrayList<>();

    private CustomInputRegistry() {}

    public static CustomInputRegistry getInstance() {
        if (customInputRegistry == null) {
            customInputRegistry = new CustomInputRegistry();
        }

        return customInputRegistry;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public void addInput(Input input) {
        inputs.add(input);
    }
}
