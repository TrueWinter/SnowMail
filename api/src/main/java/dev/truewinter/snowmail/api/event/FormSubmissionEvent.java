package dev.truewinter.snowmail.api.event;

import dev.truewinter.PluginManager.CancellableEvent;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class FormSubmissionEvent extends CancellableEvent {
    private final Form form;
    private final HashMap<String, FormSubmissionInput> submission;
    private String error;

    public FormSubmissionEvent(Form form, HashMap<String, FormSubmissionInput> submission) {
        this.form = form;
        this.submission = submission;
    }

    public Form getForm() {
        return form;
    }

    public HashMap<String, FormSubmissionInput> getSubmission() {
        return submission;
    }

    @Nullable
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
        setCancelled(true);
    }
}
