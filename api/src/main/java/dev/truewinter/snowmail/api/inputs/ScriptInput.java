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

    /**
     * @return The script "src" attribute
     */
    public String getSrc() {
        return src;
    }

    /**
     * {@link #getSrc()}
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * @return Whether this script should be deferred
     */
    public boolean isDefer() {
        return defer;
    }

    /**
     * {@link #isDefer()}
     */
    public void setDefer(boolean defer) {
        this.defer = defer;
    }

    /**
     * @return Whether this script should be loaded asynchronously
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * {@link #isAsync()}
     */
    public void setAsync(boolean async) {
        this.async = async;
    }

    /**
     * @return Whether this script is a module
     */
    public boolean isModule() {
        return module;
    }

    /**
     * {@link #isModule()}
     */
    public void setModule(boolean module) {
        this.module = module;
    }

    @Override
    public boolean isValid() {
        return !Util.isBlank(src);
    }
}
