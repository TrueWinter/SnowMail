package dev.truewinter.snowmail.api.plugin;

import dev.truewinter.PluginManager.PluginManager;
import dev.truewinter.PluginManager.Logger;

import java.util.HashMap;

public class SnowMailPluginManager {
    private static SnowMailPluginManager snowMailPluginManager;
    private final PluginManager<SnowMailAPI> pluginManager;
    private final HashMap<String, Logger> logger = new HashMap<>();
    private final SnowMailAPI api;

    private SnowMailPluginManager(SnowMailAPI api) {
        pluginManager = new PluginManager<>(getClass().getClassLoader(), PluginLogger::handlePluginManagerLog);
        this.api = api;
    }

    public static SnowMailPluginManager getInstance(SnowMailAPI api) {
        if (snowMailPluginManager == null) {
            snowMailPluginManager = new SnowMailPluginManager(api);
        }

        return snowMailPluginManager;
    }

    public static SnowMailPluginManager getInstance() {
        if (snowMailPluginManager == null) {
            throw new RuntimeException("PluginManager has not yet been initialized");
        }

        return snowMailPluginManager;
    }

    public PluginManager<SnowMailAPI> getPluginManager() {
        return pluginManager;
    }

    protected SnowMailAPI getApi() {
        return api;
    }

    protected Logger getLogger(String pluginName) {
        if (!logger.containsKey(pluginName)) {
            logger.put(pluginName, new PluginLogger(pluginName));
        }

        return logger.get(pluginName);
    }
}
