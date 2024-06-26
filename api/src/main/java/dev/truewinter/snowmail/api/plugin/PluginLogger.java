package dev.truewinter.snowmail.api.plugin;

import dev.truewinter.PluginManager.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PluginLogger extends Logger {
    private final org.slf4j.Logger logger;
    private static final Map<Logger.LogEvents, String> logStrings = Map.ofEntries(
            Map.entry(Logger.LogEvents.PLUGIN_LOADED, "Plugin %s loaded"),
            Map.entry(Logger.LogEvents.PLUGIN_UNLOADED, "Plugin %s unloaded"),
            Map.entry(Logger.LogEvents.PLUGIN_LOADING_ERROR, "Failed to load plugin %s"),
            Map.entry(Logger.LogEvents.PLUGIN_UNLOADING_ERROR, "Failed to unload plugin %s"),
            Map.entry(Logger.LogEvents.UNKNOWN_PLUGIN_ERROR, "Unknown plugin %s"),
            Map.entry(Logger.LogEvents.ALL_PLUGINS_LOADED_FAILED_CALL_ERROR, "Failed to call onAllPluginsLoaded() method for plugin %s"),
            Map.entry(Logger.LogEvents.EVENT_DISPATCH_CALL_ERROR, "Failed to dispatch event to plugin %s")
    );

    protected PluginLogger(String pluginName) {
        super(pluginName);
        logger = L4jLogger.getLogger();
    }

    @Override
    public final void info(String s) {
        logger.info(format(s));
    }

    @Override
    public void warn(String s) {
        logger.warn(format(s));
    }

    @Override
    public void warn(String s, Throwable t) {
        logger.warn(format(s), t);
    }

    @Override
    public final void error(String s) {
        logger.error(format(s));
    }

    @Override
    public final void error(String s, Throwable t) {
        logger.error(format(s), t);
    }

    private String format(String s) {
        return String.format("[%s] %s", getPluginName(), s);
    }

    private static String getLogString(PluginManagerLog log) {
        String logString = logStrings.get(log.getEvent());

        if (logString == null) {
            logString = log.getEvent().name() + " log from plugin %s";
        }

        return logString;
    }

    /**
     * @hidden
     */
    protected static void handlePluginManagerLog(Logger.PluginManagerLog log) {
        String logString = String.format(getLogString(log), log.getPluginName());

        if (log.getException() != null) {
            L4jLogger.getLogger().error(String.format(logString, log.getPluginName()), log.getException());
        } else  {
            L4jLogger.getLogger().info(String.format(logString, log.getPluginName()));
        }
    }

    public static class L4jLogger {
        private static org.slf4j.Logger logger;

        protected static org.slf4j.Logger getLogger() {
            if (logger == null) {
                logger = LoggerFactory.getLogger(SnowMailPlugin.class);
            }

            return logger;
        }
    }
}
