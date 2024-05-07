package dev.truewinter.snowmail.api.inputs;

import com.fasterxml.jackson.annotation.JsonValue;
import dev.truewinter.snowmail.api.Util;

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

    /**
     * @return The button type, by default {@link ButtonInputTypes#BUTTON}
     */
    public ButtonInputTypes getType() {
        return type;
    }

    /**
     * {@link #getType()}
     */
    public void setType(ButtonInputTypes type) {
        this.type = type;
    }

    /**
     * @return The button text
     */
    public String getText() {
        return text;
    }

    /**
     * {@link #getText()}
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(text);
    }
}
