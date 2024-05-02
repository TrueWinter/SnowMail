package dev.truewinter.snowmail.officialplugins.webhook;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.truewinter.snowmail.api.plugin.SnowMailPlugin;

@SuppressWarnings("unused")
public class Webhook extends SnowMailPlugin {
    @Override
    protected void onLoad() {
        try {
            copyDefaultConfig();
            Config config = new Config(getLogger(), YamlDocument.create(getConfig()));
            registerListeners(this, new Listener(getLogger(), config));
        } catch (Exception e) {
            getLogger().error("Failed to load plugin", e);
            return;
        }

        getLogger().info("Plugin loaded");
    }

    @Override
    protected void onUnload() {
        getLogger().info("Plugin unloaded");
    }
}
