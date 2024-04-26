package dev.truewinter.snowmail.officialplugins.snowcaptcha;

import dev.truewinter.snowmail.api.inputs.CustomElementInput;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.inputs.ScriptInput;
import dev.truewinter.snowmail.api.inputs.TextInput;
import dev.truewinter.snowmail.api.plugin.SnowMailPlugin;

@SuppressWarnings("unused")
public class SnowCaptcha extends SnowMailPlugin {
    @Override
    protected void onLoad() {
        getLogger().info("Loaded plugin");
        try {
            registerListeners(this, new Listener(getLogger()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        TextInput responseField = new TextInput();
        responseField.setType(TextInput.TextInputTypes.HIDDEN);
        responseField.setName("snowcaptcha");
        responseField.setLabel("SnowCaptcha");
        responseField.setIgnoredOnClient(true);

        CustomElementInput renderContainer = new CustomElementInput();
        renderContainer.setType("div");
        renderContainer.getCustomAttributes().put("class", "snowcaptcha");

        // TODO: Change URLs
        ScriptInput scriptInput = new ScriptInput();
        scriptInput.setAsync(true);
        scriptInput.setSrc("https://snowcaptcha-cdn.binaryfrost.net/captcha.js");
        scriptInput.getCustomAttributes().put("data-host", "https://snowcaptcha.binaryfrost.net");
        scriptInput.getCustomAttributes().put("data-sitekey", "%metadata:snowcaptcha-sitekey%");

        if (!responseField.isValid() || !renderContainer.isValid() || !scriptInput.isValid()) {
            throw new IllegalStateException("Failed to create SnowCaptcha inputs");
        }

        Input.MultipleInputs inputs = new Input.MultipleInputs();
        inputs.setCustomDisplayName("SnowCaptcha");
        inputs.getInputs().add(responseField);
        inputs.getInputs().add(renderContainer);
        inputs.getInputs().add(scriptInput);

        getApi().registerInput(inputs);
    }

    @Override
    protected void onUnload() {
        getLogger().info("Unloaded plugin");
    }
}