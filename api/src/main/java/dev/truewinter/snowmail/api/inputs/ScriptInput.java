package dev.truewinter.snowmail.api.inputs;

import dev.truewinter.snowmail.api.Util;

@SuppressWarnings("unused")
public class ScriptInput extends Input {
    public static final String INPUT_TYPE = "SCRIPT";

    private String src;
    private boolean defer;
    private boolean async;
    private boolean module;

    public ScriptInput() {
        super.setInputType(INPUT_TYPE);
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public boolean isDefer() {
        return defer;
    }

    public void setDefer(boolean defer) {
        this.defer = defer;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public boolean isModule() {
        return module;
    }

    public void setModule(boolean module) {
        this.module = module;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(src);
    }
}
