package dev.truewinter.snowmail.officialplugins.snowcaptcha;

import dev.truewinter.PluginManager.EventHandler;
import dev.truewinter.PluginManager.Logger;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.event.FormSubmissionEvent;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Listener implements dev.truewinter.PluginManager.Listener {
    private final Logger logger;

    protected Listener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler
    public void onFormSubmission(FormSubmissionEvent e) {
        HashMap<String, String> metadata = e.getForm().getMetadata();
        if (!metadata.containsKey("snowcaptcha-sitekey") || !metadata.containsKey("snowcaptcha-secret")) {
            logger.warn("Metadata keys snowcaptcha-sitekey and snowcaptcha-secret must be set, skipping captcha validation");
            return;
        }

        FormSubmissionInput token = e.getSubmission().get("snowcaptcha");
        if (token == null) {
            e.setError("Captcha is required");
            return;
        }

        if (!token.value().equals("token1234")) {
            e.setError("Invalid captcha token");
            return;
        }

        System.out.println("valid captcha token");
    }
}
