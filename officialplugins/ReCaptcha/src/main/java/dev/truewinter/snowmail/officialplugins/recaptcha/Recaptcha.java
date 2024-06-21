package dev.truewinter.snowmail.officialplugins.recaptcha;

import dev.truewinter.snowmail.api.inputs.CustomElementInput;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.inputs.ScriptInput;
import dev.truewinter.snowmail.api.inputs.TextInput;
import dev.truewinter.snowmail.api.plugin.SnowMailPlugin;

import java.util.LinkedList;

public class Recaptcha extends SnowMailPlugin {
    @Override
    protected void onLoad() {
        try {
            registerListeners(this, new Listener(getLogger()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Input.MultipleInputs inputs = createClientInputs();
        inputs.getSettings().addAll(createSettingsForm());
        getApi().registerInput(inputs);

        getLogger().info("Loaded plugin");
    }

    @Override
    protected void onUnload() {
        getLogger().info("Unloaded plugin");
    }

    private Input.MultipleInputs createClientInputs() {
        TextInput responseField = new TextInput();
        responseField.setType(TextInput.TextInputTypes.HIDDEN);
        responseField.setName("g-recaptcha-response");
        responseField.setLabel("Captcha");
        responseField.setIgnoredOnClient(true);

        CustomElementInput renderContainer = new CustomElementInput();
        renderContainer.setType("div");
        renderContainer.getCustomAttributes().put("id", "snowmail-recaptcha");

        CustomElementInput initScript = new CustomElementInput();
        initScript.setType("script");
        initScript.setInnerHtml("""
            (() => {
                function removeBorder() {
                    document.getElementById('snowmail-recaptcha').style.border = '';
                }
                
                function addBorder() {
                    document.getElementById('snowmail-recaptcha').style.border = '1px solid var(--mantine-color-error)';
                }
                
                function callback() {
                    removeBorder();
                }
    
                window.smReCaptchaLoaded = () => {
                    const widgetId = grecaptcha.render('snowmail-recaptcha', {
                        sitekey: '%metadata:recaptcha-sitekey%',
                        callback: callback,
                        'expired-callback': addBorder,
                        'error-callback': addBorder
                    });
                    
                    window.registerSnowMailInput('g-recaptcha-response', {
                        getValue: () => grecaptcha.getResponse(widgetId),
                        reset: () => grecaptcha.reset(widgetId),
                        validate: () => {
                            if (!grecaptcha.getResponse(widgetId)) {
                                addBorder();
                                return false;
                            }
                            
                            return true;
                        }
                    });
                };
            })();
        """);

        ScriptInput scriptInput = new ScriptInput();
        scriptInput.setAsync(true);
        scriptInput.setSrc("https://www.google.com/recaptcha/api.js?onload=smReCaptchaLoaded&render=explicit");

        if (!responseField.isValid() || !renderContainer.isValid() || !scriptInput.isValid()) {
            throw new IllegalStateException("Failed to create SnowCaptcha inputs");
        }

        Input.MultipleInputs inputs = new Input.MultipleInputs();
        inputs.setCustomDisplayName("ReCaptcha (v2 checkbox)");
        inputs.getCustomAttributes().put("style", "width:fit-content");
        inputs.getInputs().add(responseField);
        inputs.getInputs().add(renderContainer);
        inputs.getInputs().add(initScript);
        inputs.getInputs().add(scriptInput);

        return inputs;
    }

    private LinkedList<Input> createSettingsForm() {
        LinkedList<Input> inputs = new LinkedList<>();

        TextInput sitekey = new TextInput();
        sitekey.setLabel("Site Key");
        sitekey.setName("recaptcha-sitekey");
        inputs.add(sitekey);

        TextInput secret = new TextInput();
        secret.setLabel("Secret Key");
        secret.setName("recaptcha-secret");
        inputs.add(secret);

        for (Input input : inputs) {
            if (input instanceof TextInput) {
                ((TextInput) input).setRequired(true);
            }
        }

        return inputs;
    }
}
