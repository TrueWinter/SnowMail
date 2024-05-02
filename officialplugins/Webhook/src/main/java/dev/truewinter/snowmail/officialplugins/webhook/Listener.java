package dev.truewinter.snowmail.officialplugins.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.truewinter.PluginManager.EventHandler;
import dev.truewinter.PluginManager.Logger;
import dev.truewinter.snowmail.api.event.FormSavedEvent;
import dev.truewinter.snowmail.api.event.FormSubmissionEvent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@SuppressWarnings("unused")
public class Listener implements dev.truewinter.PluginManager.Listener {
    private final Logger logger;
    private final Config config;

    protected Listener(Logger logger, Config config) {
        this.logger = logger;
        this.config = config;
    }

    @EventHandler
    public void onFormSave(FormSavedEvent e) {
        if (!config.isSaveEventEnabled()) return;
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "save");
        data.put("form", e.getForm());
        send(data);
    }

    @EventHandler
    public void onFormSubmit(FormSubmissionEvent e) {
        if (!config.isSubmitEventEnabled()) return;
        if (e.isCancelled()) return;
        HashMap<String, Object> data = new HashMap<>();
        data.put("type", "submit");
        data.put("values", e.getSubmission());
        send(data);
    }

    private void send(Object object) {
        try {
            URL url = new URL(config.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            connection.connect();
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(new ObjectMapper().writeValueAsBytes(object));
            }

            int status = connection.getResponseCode();
            if (status >= 400) {
                connection.disconnect();
                throw new Exception("Received error response code " + status + " from webhook server");
            }

            connection.disconnect();
        } catch (Exception ex) {
            logger.error("Failed to fire webhook event", ex);
        }
    }
}
