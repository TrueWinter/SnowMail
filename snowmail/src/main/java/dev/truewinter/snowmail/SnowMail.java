package dev.truewinter.snowmail;

import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.plugin.SnowMailPluginManager;
import dev.truewinter.snowmail.database.Database;

public class SnowMail {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getInstance();

        // Initialize config to ensure all required options are set
        logger.getLogger().info("Loading config");
        Config.getInstance();

        logger.getLogger().info("Connecting to database");
        Database database = new Database();

        logger.getLogger().info("Starting web server");
        WebServer webServer = new WebServer(database);
        webServer.start();

        logger.getLogger().info("Loading plugins");
        SnowMailPluginManager pluginManager = SnowMailPluginManager.getInstance(new API(database));
        pluginManager.getPluginManager().loadPlugins(Util.getPluginJars());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.getLogger().info("Shutting down");
            pluginManager.getPluginManager().handleShutdown();
            webServer.shutdown();
            database.destroy();
        }));
    }
}
