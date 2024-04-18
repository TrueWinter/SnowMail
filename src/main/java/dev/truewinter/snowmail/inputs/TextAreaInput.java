package dev.truewinter.snowmail.inputs;

@SuppressWarnings("unused")
public class TextAreaInput extends AbstractTextInput {
    public static final String INPUT_TYPE = "TEXTAREA";

    private String cols;
    private String rows;

    public TextAreaInput() {
        super.setInputType(INPUT_TYPE);
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }
}
