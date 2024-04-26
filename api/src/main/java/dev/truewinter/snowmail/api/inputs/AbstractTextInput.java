package dev.truewinter.snowmail.api.inputs;

import dev.truewinter.snowmail.api.NullableField;
import dev.truewinter.snowmail.api.Util;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractTextInput extends Input.StylableInput {
    protected String label;
    protected String name;
    protected boolean required;
    protected String placeholder;
    protected int maxLength;
    private boolean includeInEmail;
    private boolean ignoreOnClient;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Nullable
    @NullableField
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isIncludedInEmail() {
        return includeInEmail;
    }

    public void setIncludedInEmail(boolean includeInEmail) {
        this.includeInEmail = includeInEmail;
    }

    public boolean isIgnoredOnClient() {
        return ignoreOnClient;
    }

    public void setIgnoredOnClient(boolean ignoreOnClient) {
        this.ignoreOnClient = ignoreOnClient;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(label) && !Util.isBlank(name);
    }
}
