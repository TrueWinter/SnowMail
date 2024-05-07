package dev.truewinter.snowmail.api.inputs;

import com.fasterxml.jackson.annotation.*;
import dev.truewinter.snowmail.api.NullableField;
import dev.truewinter.snowmail.api.pojo.Views;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "inputType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextInput.class, name = TextInput.INPUT_TYPE),
        @JsonSubTypes.Type(value = TextAreaInput.class, name = TextAreaInput.INPUT_TYPE),
        @JsonSubTypes.Type(value = ScriptInput.class, name = ScriptInput.INPUT_TYPE),
        @JsonSubTypes.Type(value = ButtonInput.class, name = ButtonInput.INPUT_TYPE),
        @JsonSubTypes.Type(value = Input.MultipleInputs.class, name = Input.MultipleInputs.INPUT_TYPE),
        @JsonSubTypes.Type(value = CustomElementInput.class, name = CustomElementInput.INPUT_TYPE),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings({"unused", "FieldMayBeFinal"})
public abstract class Input {
    private String inputType;
    @JsonView({Views.DashboardFull.class, Views.Public.class})
    // Key for React
    private String rKey = UUID.randomUUID().toString();
    @JsonView(Views.DashboardFull.class)
    private String customDisplayName;
    private Map<String, String> customAttributes = new HashMap<>();

    /**
     * @apiNote You probably shouldn't be using this directly. Instead, use the instanceof keyword.
     * @return The input type
     */
    public String getInputType() {
        return inputType;
    }

    @ApiStatus.Internal
    protected void setInputType(String inputType) {
        this.inputType = inputType;
    }

    /**
     * @return The custom display name
     */
    @Nullable
    @NullableField
    public String getCustomDisplayName() {
        return customDisplayName;
    }

    /**
     * {@link #getCustomDisplayName()}
     */
    public void setCustomDisplayName(String customDisplayName) {
        this.customDisplayName = customDisplayName;
    }

    /**
     * @return A Map of custom attributes
     */
    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    /**
     * @apiNote You should call this method on every input you create
     * @return Whether this input is valid
     */
    @JsonIgnore
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isValid();

    @SuppressWarnings("SpellCheckingInspection")
    public static abstract class StylableInput extends Input {
        private boolean unstyled;
        private Map<String, String> cssStyles = new HashMap<>();
        private List<String> cssClasses = new ArrayList<>();

        /**
         * @return Whether the input is unstyled
         */
        public boolean isUnstyled() {
            return unstyled;
        }

        /**
         * {@link #isUnstyled()}
         */
        public void setUnstyled(boolean unstyled) {
            this.unstyled = unstyled;
        }

        /**
         * @return A Map of CSS styles
         */
        public Map<String, String> getCssStyles() {
            return cssStyles;
        }

        /**
         * @return A List of CSS classes
         */
        public List<String> getCssClasses() {
            return cssClasses;
        }
    }

    public static class MultipleInputs extends Input {
        public static final String INPUT_TYPE = "MULTIPLE";
        private LinkedList<Input> inputs = new LinkedList<>();

        public MultipleInputs() {
            super.setInputType(INPUT_TYPE);
        }

        /**
         * @return The LinkedList of inputs
         */
        public LinkedList<Input> getInputs() {
            return inputs;
        }

        @Override
        public boolean isValid() {
            for (Input input : inputs) {
                if (!input.isValid()) return false;
            }

            return true;
        }
    }
}
