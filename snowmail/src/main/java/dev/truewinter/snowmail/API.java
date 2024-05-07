package dev.truewinter.snowmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.plugin.SnowMailAPI;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import dev.truewinter.snowmail.database.Database;

import java.util.List;
import java.util.Optional;

public class API implements SnowMailAPI {
    private Database database;

    public API(Database database) {
        this.database = database;
    }

    @Override
    public void registerInput(Input input) {
        CustomInputRegistry.getInstance().addInput(input);
    }

    @Override
    public List<Form> getForms() throws JsonProcessingException {
        return database.getFormDatabase().getForms();
    }

    @Override
    public Optional<Form> getForm(String id) throws JsonProcessingException {
        return database.getFormDatabase().getForm(id);
    }

    @Override
    public void createForm(Form form) throws JsonProcessingException {
        database.getFormDatabase().createForm(form);
    }

    @Override
    public void editForm(Form form) throws JsonProcessingException {
        database.getFormDatabase().editForm(form);
    }
}
