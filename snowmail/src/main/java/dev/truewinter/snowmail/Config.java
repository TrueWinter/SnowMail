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
    private final boolean ssoEnabled;
    private final boolean ssoForceRedirect;
    private final String ssoClientId;
    private final String ssoClientSecret;
    private final String ssoAuthUrl;
    private final String ssoTokenURL;
    private final String ssoUserInfoUrl;
    private final String ssoLogoutUrl;

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
        this.ssoEnabled = dotenv.get("SSO_ENABLED", "false").equals("true");
        this.ssoForceRedirect = dotenv.get("SSO_FORCE_REDIRECT", "false").equals("true");
        this.ssoClientId = dotenv.get("SSO_CLIENT_ID");
        this.ssoClientSecret = dotenv.get("SSO_CLIENT_SECRET");
        this.ssoAuthUrl = dotenv.get("SSO_AUTH_URL");
        this.ssoTokenURL = dotenv.get("SSO_TOKEN_URL");
        this.ssoUserInfoUrl = dotenv.get("SSO_USERINFO_URL");
        this.ssoLogoutUrl = dotenv.get("SSO_LOGOUT_URL");

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

    public boolean isSsoEnabled() {
        return ssoEnabled;
    }

    public boolean isSsoForceRedirect() {
        return ssoForceRedirect;
    }

    public String getSsoClientId() {
        return ssoClientId;
    }

    public String getSsoClientSecret() {
        return ssoClientSecret;
    }

    public String getSsoAuthUrl() {
        return ssoAuthUrl;
    }

    public String getSsoTokenURL() {
        return ssoTokenURL;
    }

    public String getSsoUserInfoUrl() {
        return ssoUserInfoUrl;
    }

    public String getSsoLogoutUrl() {
        return ssoLogoutUrl;
    }

    public enum SMTP_ENCRYPTION {
        NONE,
        SSL,
        TLS
    }
}
