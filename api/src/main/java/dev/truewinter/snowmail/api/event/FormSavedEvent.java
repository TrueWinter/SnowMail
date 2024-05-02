package dev.truewinter.snowmail.api.event;

import dev.truewinter.PluginManager.Event;
import dev.truewinter.snowmail.api.pojo.objects.Form;

public class FormSavedEvent extends Event {
    private final Form form;

    public FormSavedEvent(Form form) {
        this.form = form;
    }

    public Form getForm() {
        return form;
    }
}
