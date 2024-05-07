package dev.truewinter.snowmail.api.event;

import dev.truewinter.PluginManager.Event;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import org.jetbrains.annotations.ApiStatus;

/**
 * This event is fired after a form is saved
 */
public class FormSavedEvent extends Event {
    private final Form form;

    @ApiStatus.Internal
    public FormSavedEvent(Form form) {
        this.form = form;
    }

    /**
     * @return The form that was saved
     */
    public Form getForm() {
        return form;
    }
}
