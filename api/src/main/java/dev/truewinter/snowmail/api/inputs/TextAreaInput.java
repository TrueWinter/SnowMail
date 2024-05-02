package dev.truewinter.snowmail.api.inputs;

@SuppressWarnings("unused")
public class TextAreaInput extends AbstractTextInput {
    public static final String INPUT_TYPE = "TEXTAREA";

    private int rows;

    public TextAreaInput() {
        super.setInputType(INPUT_TYPE);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
