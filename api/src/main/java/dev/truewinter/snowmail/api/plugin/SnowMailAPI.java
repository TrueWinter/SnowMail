package dev.truewinter.snowmail.api.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.pojo.objects.Form;

import java.util.List;
import java.util.Optional;

public interface SnowMailAPI {
    /**
     * Registers a new input that can be used from the SnowMail dashboard.
     * @param input The input, preferably with the {@link Input#setCustomDisplayName(String)} set
     */
    void registerInput(Input input);

    /**
     * @return A List of forms
     */
    List<Form> getForms() throws JsonProcessingException;

    /**
     * Gets a form by its ID
     * @param id Form ID
     * @return The form
     */
    Optional<Form> getForm(String id) throws JsonProcessingException;

    /**
     * Creates a form
     * @param form The Form
     */
    void createForm(Form form) throws JsonProcessingException;

    /**
     * Edits a form
     * @param form The form
     */
    void editForm(Form form) throws JsonProcessingException;
}
