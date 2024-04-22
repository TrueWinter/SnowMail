package dev.truewinter.snowmail.inputs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.truewinter.snowmail.Util;

@SuppressWarnings("unused")
public class CustomElementInput extends Input {
    public static final String INPUT_TYPE = "CUSTOM";

    private String type = "div";
    private String innerHtml = "";

    public CustomElementInput () {
        super.setInputType(INPUT_TYPE);
    }

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
