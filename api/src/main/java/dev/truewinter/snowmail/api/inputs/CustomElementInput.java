package dev.truewinter.snowmail.api.inputs;

import dev.truewinter.snowmail.api.Util;

@SuppressWarnings("unused")
public class CustomElementInput extends Input {
    public static final String INPUT_TYPE = "CUSTOM";

    private String type = "div";
    private String innerHtml = "";

    public CustomElementInput () {
        super.setInputType(INPUT_TYPE);
    }

    /**
     * @return The custom element type, by default "div"
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInnerHtml() {
        return innerHtml;
    }

    public void setInnerHtml(String innerHtml) {
        this.innerHtml = innerHtml;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(type);
    }
}
