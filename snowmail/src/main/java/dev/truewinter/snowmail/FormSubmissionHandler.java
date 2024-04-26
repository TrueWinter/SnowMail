package dev.truewinter.snowmail;

import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.event.FormSubmissionEvent;
import dev.truewinter.snowmail.api.inputs.AbstractTextInput;
import dev.truewinter.snowmail.api.inputs.Input;
import dev.truewinter.snowmail.api.inputs.TextAreaInput;
import dev.truewinter.snowmail.api.plugin.SnowMailPluginManager;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

public class FormSubmissionHandler {
    public static void handle(Form form, HashMap<String, FormSubmissionInput> submission) {
        FormSubmissionEvent event = SnowMailPluginManager.getInstance().getPluginManager()
                .fireEvent(new FormSubmissionEvent(form, submission));

        if (!event.isCancelled()) {
            try {
                email(form, submission);
            } catch (UnsupportedEncodingException | MessagingException e) {
                Logger.getInstance().getLogger().error("Failed to send email", e);
                throw new InternalServerErrorResponse("Failed to send email, please try again later");
            }
        } else if (!Util.isBlank(event.getError())) {
            throw new BadRequestResponse(event.getError());
        }
    }

    private static String format(Form form, HashMap<String, FormSubmissionInput> submission) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Input input : form.getInputs()) {
            if (!(input instanceof AbstractTextInput i)) continue;
            String label = i.getLabel();
            FormSubmissionInput value = submission.get(i.getName());

            if (value != null) {
                stringBuilder.append(label).append(": ");

                if (input instanceof TextAreaInput) {
                    stringBuilder.append("<br />");
                }

                stringBuilder.append(value.value()).append("<br />");
            }
        }

        return stringBuilder.toString();
    }

    private static void email(Form form, HashMap<String, FormSubmissionInput> submission) throws MessagingException, UnsupportedEncodingException {
        Config config = Config.getInstance();

        Properties properties = new Properties();
        int timeout = config.getSmtpTimeout();
        properties.put("mail.smtp.connectiontimeout", timeout);
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.smtp.writetimeout", timeout);

        properties.put("mail.smtp.host", config.getSmtpHost());
        properties.put("mail.smtp.port", config.getSmtpPort());


        switch (config.getSmtpEncryption()) {
            case SSL:
                properties.put("mail.smtp.socketFactory.port", config.getSmtpPort());
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                break;
            case TLS:
                properties.put("mail.smtp.starttls.enable", true);
                break;
        }

        Session session;
        if (!Util.isBlank(config.getSmtpUser())) {
            properties.put("mail.smtp.auth", true);
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(config.getSmtpUser(), config.getSmtpPassword());
                }
            });
        } else {
            session = Session.getInstance(properties);
        }

        Message message = new MimeMessage(session);

        Address from;
        if (submission.containsKey("name")) {
            from = new InternetAddress(config.getEmailFrom(), submission.get("name").value());
        } else {
            from = new InternetAddress(config.getEmailFrom());
        }

        message.setFrom(from);
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(form.getEmail()));

        if (submission.containsKey("email")) {
            message.setReplyTo(new Address[]{new InternetAddress(submission.get("email").value())});
        }

        message.setSubject(String.format("Message sent from %s website (%s)", form.getName(), getId()));
        message.setContent(format(form, submission), "text/html; charset=utf-8");
        Transport.send(message);
    }

    private static String getId() {
        int time = (int) (System.currentTimeMillis() / 1000);
        return Integer.toHexString(time);
    }
}
