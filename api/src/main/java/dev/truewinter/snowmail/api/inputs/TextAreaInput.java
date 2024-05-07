package dev.truewinter.snowmail.api.inputs;

@SuppressWarnings("unused")
public class TextAreaInput extends AbstractTextInput {
    public static final String INPUT_TYPE = "TEXTAREA";

    private int rows;

    public TextAreaInput() {
        super.setInputType(INPUT_TYPE);
    }

    /**
     * @apiNote If the number of rows is 0, use the browser default
     * @return The number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * {@link #getRows()}
     */
    public void setRows(int rows) {
        this.rows = rows;
    }
}
