package dev.truewinter.snowmail.officialplugins.snowcaptcha;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.truewinter.PluginManager.EventHandler;
import dev.truewinter.PluginManager.Logger;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.event.FormSubmissionEvent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
        if (!metadata.containsKey("snowcaptcha-sitekey") || !metadata.containsKey("snowcaptcha-secret") || !metadata.containsKey("snowcaptcha-host")) {
            logger.warn("Metadata keys snowcaptcha-sitekey, snowcaptcha-secret, and snowcaptcha-host must be set, skipping captcha validation");
            return;
        }

        FormSubmissionInput token = e.getSubmission().get("snowcaptcha");
        if (token == null) {
            e.setError("Captcha is required");
            return;
        }

        try {
            URL url = new URL(getValidateURL(metadata.get("snowcaptcha-host")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            HashMap<String, String> body = new HashMap<>();
            body.put("token", token.value());
            body.put("sitekey", metadata.get("snowcaptcha-sitekey"));
            body.put("secretkey", metadata.get("snowcaptcha-secret"));

            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(new ObjectMapper().writeValueAsBytes(body));
            }

            HashMap<String, Object> response = new ObjectMapper()
                    .readValue(connection.getInputStream().readAllBytes(), new TypeReference<>() {});
            boolean valid = (boolean) response.get("valid");
            if (!valid) {
                e.setError("Invalid captcha token");
            }

            connection.disconnect();
        } catch (Exception ex) {
            logger.error("Failed to validate captcha token", ex);
            e.setError("Failed to validate captcha");
        }
    }

    private String getValidateURL(String host) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(host);
        if (!host.endsWith("/")) {
            stringBuilder.append("/");
        }
        stringBuilder.append("validate-token");
        return stringBuilder.toString();
    }
}
