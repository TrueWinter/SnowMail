package dev.truewinter.snowmail.inputs;

import com.fasterxml.jackson.annotation.*;
import dev.truewinter.snowmail.NullableField;
import dev.truewinter.snowmail.pojo.Views;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.*;

// TODO: Check if this way of including the inputType works when deserializing
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
    // TODO: Generate TypeScript types
    /*
        Example:
        {
            "inputType": "TEXT", // enum, one of TEXT, TEXTAREA, SCRIPT, BUTTON; more will be added in the future
            // ignoreOnClient: if true, it will not be rendered by SnowMail in the browser;
            // useful for fields that are required server-side after being added by client-side JS (e.g. captcha)
            "ignoreOnClient": false,
            "customDisplayName": "SnowMailInputPlugin-Email", // optional, name displayed in the form editor for plugin-added inputs
            "label": "Email",
            "id": "sm-123456", // used to link the field and label, a random ID is generated if one is not provided
            "name": "email",
            "type": "email",
            "required": true,
            "placeholder": "name@example.com", // optional
            "maxlength": 255, // optional
            "pattern": "/^.+$/", // optional
            "unstyled": false, // if true, don't apply default styles
            "cssStyles": {
                "border": "1px solid grey"
            }, // optional
            "cssClasses: [
                "form-input"
            ], //optional
            "customAttributes": {
                "autocomplete": "off",
                "data-custom-id": "1234"
            } //optional
        }

        The data in the object will differ depending on the input_type.
     */

    private String inputType;
    @BsonIgnore
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
