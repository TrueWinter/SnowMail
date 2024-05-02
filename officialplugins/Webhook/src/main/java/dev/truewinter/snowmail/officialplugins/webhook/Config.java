package dev.truewinter.snowmail.officialplugins.webhook;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.truewinter.PluginManager.Logger;

import java.util.List;

public class Config {
    private final String url;
    private boolean saveEventEnabled;
    private boolean submitEventEnabled;

    protected Config(Logger logger, YamlDocument yamlDocument) {
        this.url = yamlDocument.getString("url");
        if (this.url.equals("https://example.com")) {
            logger.warn("URL is still set to the default, disabling events");
            return;
        }

        List<String> events = yamlDocument.getStringList("events");
        this.saveEventEnabled = events.contains("save");
        this.submitEventEnabled = events.contains("submit");
    }

    public String getUrl() {
        return url;
    }

    public boolean isSaveEventEnabled() {
        return saveEventEnabled;
    }

    public boolean isSubmitEventEnabled() {
        return submitEventEnabled;
    }
}
