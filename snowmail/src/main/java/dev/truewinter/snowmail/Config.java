package dev.truewinter.snowmail;

import dev.truewinter.snowmail.api.Util;
import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static Config config;
    private final String secret;
    private final String mongodb;
    private final int port;
    private final String emailFrom;
    private final String smtpHost;
    private final int smtpPort;
    private final SMTP_ENCRYPTION smtpEncryption;
    private final String smtpUser;
    private final String smtpPassword;
    private final int smtpTimeout;

    private Config() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.secret = dotenv.get("APP_SECRET");
        this.mongodb = dotenv.get("MONGO_DB");
        this.port = Integer.parseInt(dotenv.get("PORT", "8025"));
        this.emailFrom = dotenv.get("EMAIL_FROM");
        this.smtpHost = dotenv.get("SMTP_HOST");
        this.smtpPort = Integer.parseInt(dotenv.get("SMTP_PORT", "25"));
        this.smtpEncryption = SMTP_ENCRYPTION.valueOf(dotenv.get("SMTP_ENCRYPTION", SMTP_ENCRYPTION.NONE.name()));
        this.smtpUser = dotenv.get("SMTP_USER");
        this.smtpPassword = dotenv.get("SMTP_PASSWORD");
        this.smtpTimeout = Integer.parseInt(dotenv.get("SMTP_TIMEOUT", "15000"));

        if (Util.isBlank(this.secret, this.mongodb, emailFrom, smtpHost)) {
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

    public String getEmailFrom() {
        return emailFrom;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public SMTP_ENCRYPTION getSmtpEncryption() {
        return smtpEncryption;
    }

    public String getSmtpUser() {
        return smtpUser;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public int getSmtpTimeout() {
        return smtpTimeout;
    }

    public enum SMTP_ENCRYPTION {
        NONE,
        SSL,
        TLS
    }
}
