package dev.truewinter.snowmail;

import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.plugin.SnowMailAPI;

public class API implements SnowMailAPI {
    @Override
    public void registerInput(Input input) {
        CustomInputRegistry.getInstance().addInput(input);
    }
}
