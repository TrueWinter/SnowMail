package dev.truewinter.snowmail.api.inputs;

import com.fasterxml.jackson.annotation.JsonValue;
import dev.truewinter.snowmail.api.NullableField;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TextInput extends AbstractTextInput {
    public static final String INPUT_TYPE = "TEXT";

    public enum TextInputTypes {
        TEXT,
        EMAIL,
        NUMBER,
        PASSWORD,
        TEL,
        URL,
        HIDDEN;

        @JsonValue
        public String toLowerCase() {
            return toString().toLowerCase();
        }
    }

    private TextInputTypes type = TextInputTypes.TEXT;
    private String pattern;
    private String patternError;

    public TextInput() {
        super.setInputType(INPUT_TYPE);
    }

    public TextInputTypes getType() {
        return type;
    }

    public void setType(TextInputTypes type) {
        this.type = type;
    }

    @Nullable
    @NullableField
    public String getPattern() {
        return pattern;
    }

    @Nullable
    @NullableField
    public String getPatternError() {
        return patternError;
    }

    public void setPattern(String pattern, String error) {
        this.pattern = pattern;
        this.patternError = error;
    }
}
