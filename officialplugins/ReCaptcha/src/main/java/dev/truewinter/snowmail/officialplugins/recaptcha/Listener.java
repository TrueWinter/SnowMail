package dev.truewinter.snowmail.officialplugins.recaptcha;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.truewinter.PluginManager.EventHandler;
import dev.truewinter.PluginManager.Logger;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.event.FormSubmissionEvent;
import dev.truewinter.snowmail.api.pojo.objects.Form;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Listener implements dev.truewinter.PluginManager.Listener {
    private final Logger logger;

    protected Listener(Logger logger) {
        this.logger = logger;
    }

    @EventHandler
    public void onFormSubmission(FormSubmissionEvent e) {
        if (!Form.recursivelyGetInputs(e.getForm().getInputs()).containsKey("g-recaptcha-response")) return;

        HashMap<String, String> metadata = e.getForm().getMetadata();
        if (!metadata.containsKey("recaptcha-sitekey") || !metadata.containsKey("recaptcha-secret")) {
            logger.warn("Metadata keys recaptcha-sitekey, and recaptcha-secret must be set, skipping captcha validation");
            return;
        }

        FormSubmissionInput token = e.getSubmission().get("g-recaptcha-response");
        if (token == null || Util.isBlank(token.value())) {
            e.setError("Captcha is required");
            return;
        }

        try {
            URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            String body = String.format("secret=%s&response=%s", metadata.get("recaptcha-secret"), token.value());
            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
            }

            HashMap<String, Object> response = new ObjectMapper()
                    .readValue(connection.getInputStream().readAllBytes(), new TypeReference<>() {});
            boolean valid = (boolean) response.get("success");
            if (!valid) {
                e.setError("Invalid captcha token");
            }

            connection.disconnect();
        } catch (Exception ex) {
            logger.error("Failed to validate captcha token", ex);
            e.setError("Failed to validate captcha");
        }
    }
}
