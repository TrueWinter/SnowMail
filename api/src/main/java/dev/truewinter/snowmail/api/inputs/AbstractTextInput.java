package dev.truewinter.snowmail.api.inputs;

import dev.truewinter.snowmail.api.NullableField;
import dev.truewinter.snowmail.api.Util;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractTextInput extends Input.StylableInput {
    protected String label;
    protected String description;
    protected String name;
    protected boolean required;
    protected String placeholder;
    protected int maxLength;
    private boolean includeInEmail;
    private boolean ignoreOnClient;

    /**
     * @return The input label
     */
    public String getLabel() {
        return label;
    }

    /**
     * {@link #getLabel()}
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return The input description
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@link #getDescription()}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The input name
     */
    public String getName() {
        return name;
    }

    /**
     * {@link #getName()}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Whether this input is required or not
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * {@link #isRequired()}
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * @return The input placeholder, if it exists
     */
    @Nullable
    @NullableField
    public String getPlaceholder() {
        return placeholder;
    }

    /**
     * {@link #getPlaceholder()}
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * @apiNote If the maximum length is 0, you should treat the length as unlimited
     * @return The maximum length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * {@link #getMaxLength()}
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return Whether this input should be included in the email or not
     */
    public boolean isIncludedInEmail() {
        return includeInEmail;
    }

    /**
     * {@link #isIncludedInEmail()}
     */
    public void setIncludedInEmail(boolean includeInEmail) {
        this.includeInEmail = includeInEmail;
    }

    /**
     * @return Whether this input should be ignored by the client-side script
     */
    public boolean isIgnoredOnClient() {
        return ignoreOnClient;
    }

    /**
     * {@link #isIgnoredOnClient()}
     */
    public void setIgnoredOnClient(boolean ignoreOnClient) {
        this.ignoreOnClient = ignoreOnClient;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(label) && !Util.isBlank(name);
    }
}
