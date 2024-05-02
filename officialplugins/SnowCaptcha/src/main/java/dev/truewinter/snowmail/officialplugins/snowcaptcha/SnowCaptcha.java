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

        CustomElementInput initScript = new CustomElementInput();
        initScript.setType("script");
        initScript.setInnerHtml("""
                (() => {
                    function removeBorder() {
                        document.getElementById('snowmail-snowcaptcha').style.border = '';
                    }
                    
                    function addBorder() {
                        document.getElementById('snowmail-snowcaptcha').style.border = '1px solid var(--mantine-color-error)';
                    }

                    window.smSnowCaptchaCallback = (status) => {
                        if (status === 'LOADED') {
                            const respField = document.querySelector('#snowmail-snowcaptcha [name="snowcaptcha"]');
                            window.registerSnowMailInput('snowcaptcha', {
                                getValue: () => respField.value,
                                reset: () => SnowCaptcha.reset(respField.parentElement.getElementsByClassName('snowcaptcha')[0]),
                                validate: () => {
                                    if (!respField.value) {
                                        addBorder();
                                        return false;
                                    }
                                    
                                    return true;
                                }
                            });
                            
                            return;
                        }
                        
                        if (status === 'RESET') {
                            removeBorder();
                            return;
                        }
                        
                        if (status !== 'SOLVED') {
                            addBorder();
                        } else {
                            removeBorder();
                        }
                    };
                })();
        """);

        ScriptInput scriptInput = new ScriptInput();
        scriptInput.setAsync(true);
        scriptInput.setSrc("%metadata:snowcaptcha-src%");
        scriptInput.getCustomAttributes().put("data-host", "%metadata:snowcaptcha-host%");
        scriptInput.getCustomAttributes().put("data-sitekey", "%metadata:snowcaptcha-sitekey%");
        scriptInput.getCustomAttributes().put("data-callback", "smSnowCaptchaCallback");

        if (!responseField.isValid() || !renderContainer.isValid() || !scriptInput.isValid()) {
            throw new IllegalStateException("Failed to create SnowCaptcha inputs");
        }

        Input.MultipleInputs inputs = new Input.MultipleInputs();
        inputs.setCustomDisplayName("SnowCaptcha");
        inputs.getCustomAttributes().put("id", "snowmail-snowcaptcha");
        inputs.getCustomAttributes().put("style", "width: fit-content;");
        inputs.getInputs().add(responseField);
        inputs.getInputs().add(renderContainer);
        inputs.getInputs().add(initScript);
        inputs.getInputs().add(scriptInput);

        getApi().registerInput(inputs);
    }

    @Override
    protected void onUnload() {
        getLogger().info("Unloaded plugin");
    }
}