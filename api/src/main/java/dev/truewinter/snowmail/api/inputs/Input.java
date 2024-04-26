package dev.truewinter.snowmail.api.inputs;

import com.fasterxml.jackson.annotation.*;
import dev.truewinter.snowmail.api.NullableField;
import dev.truewinter.snowmail.api.pojo.Views;
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
    @JsonView(Views.DashboardFull.class)
    // Key for React
    private String rKey = UUID.randomUUID().toString();
    @JsonView(Views.DashboardFull.class)
    private String customDisplayName;
    private Map<String, String> customAttributes = new HashMap<>();

    public String getInputType() {
        return inputType;
    }

    protected void setInputType(String inputType) {
        this.inputType = inputType;
    }

    @Nullable
    @NullableField
    public String getCustomDisplayName() {
        return customDisplayName;
    }

    public void setCustomDisplayName(String customDisplayName) {
        this.customDisplayName = customDisplayName;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    @JsonIgnore
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isValid();

    @SuppressWarnings("SpellCheckingInspection")
    public static abstract class StylableInput extends Input {
        private boolean unstyled;
        private Map<String, String> cssStyles = new HashMap<>();
        private List<String> cssClasses = new ArrayList<>();

        public boolean isUnstyled() {
            return unstyled;
        }

        public void setUnstyled(boolean unstyled) {
            this.unstyled = unstyled;
        }

        public Map<String, String> getCssStyles() {
            return cssStyles;
        }

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
