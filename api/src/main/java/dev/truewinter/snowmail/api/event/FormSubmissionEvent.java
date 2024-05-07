package dev.truewinter.snowmail.api.event;

import dev.truewinter.PluginManager.CancellableEvent;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * This event is called when a form submission is received
 */
public class FormSubmissionEvent extends CancellableEvent {
    private final Form form;
    private final HashMap<String, FormSubmissionInput> submission;
    private String error;

    @ApiStatus.Internal
    public FormSubmissionEvent(Form form, HashMap<String, FormSubmissionInput> submission) {
        this.form = form;
        this.submission = submission;
    }

    /**
     * @return The form
     */
    public Form getForm() {
        return form;
    }

    /**
     * @return Form submissions
     */
    public HashMap<String, FormSubmissionInput> getSubmission() {
        return submission;
    }

    /**
     * @return The error, if it exists
     */
    @Nullable
    public String getError() {
        return error;
    }

    /**
     * Sets an error and cancels the event
     * @param error The error message
     */
    public void setError(@NotNull String error) {
        this.error = error;
        setCancelled(true);
    }
}
