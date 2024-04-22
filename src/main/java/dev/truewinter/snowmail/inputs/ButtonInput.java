package dev.truewinter.snowmail.inputs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.truewinter.snowmail.Util;

@SuppressWarnings("unused")
public class ButtonInput extends Input.StylableInput {
    public static final String INPUT_TYPE = "BUTTON";

    public enum ButtonInputTypes {
        BUTTON,
        RESET,
        SUBMIT;

        @JsonValue
        public String toLowerCase() {
            return toString().toLowerCase();
        }
    }

    private ButtonInputTypes type = ButtonInputTypes.BUTTON;
    private String text;

    public ButtonInput() {
        super.setInputType(INPUT_TYPE);
    }

    public ButtonInputTypes getType() {
        return type;
    }

    public void setType(ButtonInputTypes type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(text);
    }
}
