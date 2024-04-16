package dev.truewinter.snowmail;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static Config config;
    private final String secret;
    private final String mongodb;
    private final int port;

    private Config() {
        Dotenv dotenv = Dotenv.load();
        this.secret = dotenv.get("APP_SECRET");
        this.mongodb = dotenv.get("MONGO_DB");
        this.port = Integer.parseInt(dotenv.get("PORT", "8025"));

        if (Util.isBlank(this.secret) || Util.isBlank(this.mongodb)) {
            Logger.getInstance().getLogger().error("Missing required configuration option(s)");
            System.exit(1);
        }
    }

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }

        return config;
    }

    public String getSecret() {
        return secret;
    }

    public String getMongoDb() {
        return mongodb;
    }

    public int getPort() {
        return port;
    }
}
