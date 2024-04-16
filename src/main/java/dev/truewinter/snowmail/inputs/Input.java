package dev.truewinter.snowmail.inputs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO: Check if this way of including the inputType works when deserializing
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
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
            "metadata": {
                "displayName": "Email", // name displayed in the form editor
                "addedBy": "SnowMailInputPlugin", // plugin that added the input
                "custom": {} // optional, plugins can use this to store arbitrary metadata
            }, // optional, only exists for plugin-added inputs and will not be sent to client
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

    private boolean ignoreOnClient;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private InputMetadata metadata;
    private Map<String, String> customAttributes = new HashMap<>();

    @JsonProperty("inputType")
    public abstract String getInputType();

    public boolean isIgnoredOnClient() {
        return ignoreOnClient;
    }

    public void setIgnoredOnClient(boolean ignoreOnClient) {
        this.ignoreOnClient = ignoreOnClient;
    }

    @Nullable
    public InputMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(InputMetadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public static class InputMetadata {
        private String displayName;
        private String addedBy;
        private List<String> custom = new ArrayList<>();

        public InputMetadata() {}

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAddedBy() {
            return addedBy;
        }

        public void setAddedBy(String addedBy) {
            this.addedBy = addedBy;
        }

        public List<String> getCustom() {
            return custom;
        }
    }

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

        public MultipleInputs() {}

        public LinkedList<Input> getInputs() {
            return inputs;
        }

        @Override
        public String getInputType() {
            return INPUT_TYPE;
        }
    }
}
