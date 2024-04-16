package dev.truewinter.snowmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.truewinter.snowmail.database.Database;

public class SnowMail {
    public static void main(String[] args) throws JsonProcessingException {
        /*
            API should return array of objects defining the form inputs. See example in Input class.

            Allow plugins to add inputs which can then be added to a form through the form editor. When a form
            submission is received, SnowMail will pass this to all plugins to allow them to reject invalid submissions.
            Possibly send an event to plugins when a form starts using an input the plugin created to allow for initial
            setup (e.g. creating a captcha API key). The same should be done when the form stops using the input.
            A plugin should be able to register multiple inputs which can be added/removed all at once through the
            form editor. Preferably, this should only appear as one input in the form editor. Possibly achieve this
            by creating a new type of input which can contain multiple inputs.

            Also allow plugins to add metadata (not returned to client) to forms.

            SnowMail won't be sending emails itself. Instead, a plugin can register as the mail sender and all
            accepted submissions will be sent to the plugin. A simple, minimally configurable plugin should be made
            as an official plugin to allow users to easily get started with SnowMail.

            TODO: Find way to handle migrations for form inputs.

            Database schema (MongoDB):
            - users
                - username (string, index)
                - password (string)
            - forms
                - id (uuid, index) // or leave the ID creation to Mongo's _id field?
                - name (string)
                - email (string)
                - metadata (json)
                - inputs (json)
         */

        // Initialize config to ensure all required options are set
        Config.getInstance();

        Database database = new Database();
        WebServer webServer = new WebServer(database);
        webServer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            webServer.shutdown();
            database.destroy();
            // TODO: Close Mongo connection
        }));
    }
}
