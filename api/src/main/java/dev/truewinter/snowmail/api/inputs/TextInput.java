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

    /**
     * @return The input type, by default {@link TextInputTypes#TEXT}
     */
    public TextInputTypes getType() {
        return type;
    }

    /**
     * {@link #getType()}
     */
    public void setType(TextInputTypes type) {
        this.type = type;
    }

    /**
     * @return The pattern, if it exists
     */
    @Nullable
    @NullableField
    public String getPattern() {
        return pattern;
    }

    /**
     * {@link #getPattern()}
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return The error that will be shown if the input does not match the pattern
     */
    @Nullable
    @NullableField
    public String getPatternError() {
        return patternError;
    }

    /**
     * {@link #getPatternError()}
     */
    public void setPatternError(String patternError) {
        this.patternError = patternError;
    }
}
