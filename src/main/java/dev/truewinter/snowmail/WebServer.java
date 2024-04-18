package dev.truewinter.snowmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.truewinter.snowmail.database.Database;
import dev.truewinter.snowmail.inputs.*;
import dev.truewinter.snowmail.pojo.Response;
import dev.truewinter.snowmail.pojo.objects.Account;
import dev.truewinter.snowmail.pojo.objects.Form;
import dev.truewinter.snowmail.pojo.Views;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

public class WebServer extends Thread {
    private Javalin server;
    private Database database;

    public WebServer(Database database) {
        this.database = database;
    }

    @Override
    public void run() {
        this.server = Javalin.create(config -> {
            config.showJavalinBanner = false;
            // In development, the dist folder is empty which prevents it from being included as a resource
            if (getClass().getResource("/web/dist") != null) {
                config.staticFiles.add("/web/dist");
            }
            config.spaRoot.addFile("/", "/web/index.html");
            config.http.prefer405over404 = true;
        });
        server.start(Config.getInstance().getPort());

        server.before(ctx -> {
            JWT jwt = JWT.fromJwt(ctx);

            if (ctx.path().startsWith("/public-api")) {
                return;
            }

            if (ctx.path().equals("/login") || ctx.path().equals("/api/login")) {
                if (jwt != null) {
                    ctx.redirect("/");
                }

                return;
            }

            if (jwt == null && ctx.path().startsWith("/api")) {
                throw new UnauthorizedResponse();
            }
        });

        server.post("/api/login", ctx -> {
            JsonNode data = Util.requestToJson(ctx);

            if (!data.hasNonNull("username") || !data.hasNonNull("password")) {
                throw new UnauthorizedResponse("Invalid username or password");
            }

            String username = data.get("username").asText();
            String password = data.get("password").asText();

            database.getAccountDatabase().getAccountIfPasswordIsCorrect(username, password).ifPresentOrElse(a -> {
                ObjectNode node = new ObjectMapper().createObjectNode();
                node.put("token", JWT.create(username).toJwt());
                ctx.json(node);
            }, () -> {
                throw new UnauthorizedResponse("Invalid username or password");
            });
        });

        server.get("/api/accounts", ctx -> {
            ctx.json(new Response(database.getAccountDatabase().getAccounts(), Views.DashboardFull.class));
        });

        server.post("/api/accounts", ctx -> {
            JsonNode body = Util.requestToJson(ctx);
            if (!body.hasNonNull("username") || !body.hasNonNull("password") ||
                    !body.hasNonNull("confirm-password")) {
                throw new BadRequestResponse("All fields are required");
            }

            String username = body.get("username").asText();
            String password = body.get("password").asText();
            String confirmPassword = body.get("confirm-password").asText();

            if (!password.equals(confirmPassword)) {
                throw new BadRequestResponse("Passwords do not match");
            }

            if (database.getAccountDatabase().getAccount(username).isPresent()) {
                throw new BadRequestResponse("An account with that username already exists");
            }

            Account account = new Account(username, password, false);
            database.getAccountDatabase().createOrEditAccount(account);

            ctx.status(200);
        });

        server.get("/api/accounts/{username}", ctx -> {
            Optional<Account> account = database.getAccountDatabase().getAccount(ctx.pathParam("username"));
            account.ifPresentOrElse(a -> ctx.json(new Response(a, Views.DashboardFull.class)), () -> {
                throw new NotFoundResponse("Account not found");
            });
        });

        server.patch("/api/accounts/{username}", ctx -> {
            JsonNode body = Util.requestToJson(ctx);
            if (!body.hasNonNull("password") || !body.hasNonNull("confirm-password")) {
                throw new BadRequestResponse("All fields are required");
            }

            Optional<Account> account = database.getAccountDatabase().getAccount(ctx.pathParam("username"));
            account.ifPresentOrElse(a -> {
                String password = body.get("password").asText();
                String confirmPassword = body.get("confirm-password").asText();

                if (!password.equals(confirmPassword)) {
                    throw new BadRequestResponse("Passwords do not match");
                }

                a.setPassword(password, false);
                try {
                    database.getAccountDatabase().createOrEditAccount(a);
                } catch (JsonProcessingException e) {
                    throw new InternalServerErrorResponse(e.getMessage());
                }

                ctx.status(200);
            }, () -> {
                throw new NotFoundResponse("Account not found");
            });
        });

        server.delete("/api/accounts/{username}", ctx -> {
            Optional<Account> account = database.getAccountDatabase().getAccount(ctx.pathParam("username"));
            account.ifPresentOrElse(a -> {
                database.getAccountDatabase().deleteAccount(a.getUsername());
                ctx.status(200);
            }, () -> {
                throw new NotFoundResponse("Account does not exist");
            });
        });

        server.get("/api/is-logged-in", ctx -> {
            ctx.status(200);
        });

        server.get("/api/forms", ctx -> {
            LinkedList<Form> forms = new LinkedList<>();

            Form form1 = new Form(new ObjectId("661eca4d752415374a2d0ffa"), "Test Form", "email@example.com", new HashMap<>(), new LinkedList<>());

            HashMap<String, String> form2Metadata = new HashMap<>();
            form2Metadata.put("test", "1234");
            Form form2 = new Form("Test Form 2", "email2@example.com", form2Metadata, new LinkedList<>());

            Form form3 = new Form("Test Form 3", "email3@example.com", new HashMap<>(), new LinkedList<>());

            forms.add(form1);
            forms.add(form2);
            forms.add(form3);

            ctx.json(new Response(forms, Views.DashboardSummary.class));
        });

        server.get("/api/forms/661eca4d752415374a2d0ffa", ctx -> {
            LinkedList<Input> inputs = new LinkedList<>();

            TextInput name = new TextInput();
            name.setLabel("Name");
            name.setName("name");
            name.setMaxLength(60);
            name.setRequired(true);
            name.setIncludedInEmail(true);
            name.getCustomAttributes().put("test", "1234");
            name.getCustomAttributes().put("testing", "5678");
            inputs.add(name);

            TextInput email = new TextInput();
            email.setLabel("Email");
            email.setName("email");
            email.setType(TextInput.TextInputTypes.EMAIL);
            email.setMaxLength(60);
            email.setIncludedInEmail(false);
            email.setPattern("/^.*@.*$/", "Please enter a valid email address");
            email.setRequired(true);
            inputs.add(email);

            TextInput customDisplayName = new TextInput();
            customDisplayName.setName("custom");
            customDisplayName.setCustomDisplayName("Custom Display Name");
            customDisplayName.setIgnoredOnClient(true);
            inputs.add(customDisplayName);

            ButtonInput button = new ButtonInput();
            button.setText("Submit");
            button.setType(ButtonInput.ButtonInputTypes.SUBMIT);
            inputs.add(button);

            Form form = new Form("Form A", "a@example.com", new HashMap<>(), inputs);
            ctx.json(new Response(form, Views.DashboardFull.class));
        });

        server.get("/api/forms/123402", ctx -> {
            LinkedList<Input> inputs = new LinkedList<>();

            Input.MultipleInputs snowcaptcha = new Input.MultipleInputs();
            LinkedList<Input> snowcaptchaInputs = snowcaptcha.getInputs();

            TextInput snowcaptchaResp = new TextInput();
            snowcaptchaResp.setLabel("SnowCaptcha");
            snowcaptchaResp.setName("snowcaptcha");
            snowcaptchaResp.setType(TextInput.TextInputTypes.HIDDEN);
            snowcaptchaResp.setRequired(true);
            snowcaptchaResp.setIgnoredOnClient(true);
            snowcaptchaInputs.add(snowcaptchaResp);

            ScriptInput snowcaptchaScript = new ScriptInput();
            snowcaptchaScript.setSrc("https://snowcaptcha-cdn.binaryfrost.net/captcha.js");
            snowcaptchaScript.setAsync(true);
            Map<String, String> customAttribs = snowcaptchaScript.getCustomAttributes();
            customAttribs.put("data-sitekey", "test1234");
            customAttribs.put("data-host", "https://snowcaptcha.binaryfrost.net");
            snowcaptchaInputs.add(snowcaptchaScript);

            inputs.add(snowcaptcha);

            ButtonInput button = new ButtonInput();
            button.setText("Submit");
            button.setType(ButtonInput.ButtonInputTypes.SUBMIT);
            inputs.add(button);

            HashMap<String, String> metadata = new HashMap<>();
            metadata.put("snowcaptcha-secret", "secret4321");
            Form form = new Form("Form B", "b@example.com", metadata, inputs);
            ctx.json(new Response(form, Views.DashboardFull.class));
        });

        server.get("/api/forms/123403", ctx -> {
            LinkedList<Input> inputs = new LinkedList<>();

            Input.MultipleInputs snowcaptcha = new Input.MultipleInputs();
            LinkedList<Input> snowcaptchaInputs = snowcaptcha.getInputs();

            TextInput snowcaptchaResp = new TextInput();
            snowcaptchaResp.setLabel("SnowCaptcha");
            snowcaptchaResp.setName("snowcaptcha");
            snowcaptchaResp.setType(TextInput.TextInputTypes.HIDDEN);
            snowcaptchaResp.setRequired(true);
            snowcaptchaResp.setIgnoredOnClient(true);
            snowcaptchaInputs.add(snowcaptchaResp);

            CustomElementInput snowcaptchaScript = new CustomElementInput();
            snowcaptchaScript.setType("script");
            Map<String, String> customAttribs = snowcaptchaScript.getCustomAttributes();
            customAttribs.put("id", "snowcaptcha-lazy");
            customAttribs.put("data-sitekey", "test1234");
            customAttribs.put("data-host", "https://snowcaptcha.binaryfrost.net");
            customAttribs.put("async", "true");
            customAttribs.put("data-src", "https://snowcaptcha-cdn.binaryfrost.net/captcha.js");
            snowcaptchaInputs.add(snowcaptchaScript);

            CustomElementInput snowcaptchaLoadingScript = new CustomElementInput();
            snowcaptchaLoadingScript.setType("script");
            snowcaptchaLoadingScript.setInnerHtml("(function(){" +
                    "const e = document.getElementById('snowcaptcha-lazy');" +
                    "e.src = e.dataset.src;" +
                    "}())");
            snowcaptchaInputs.add(snowcaptchaLoadingScript);

            inputs.add(snowcaptcha);

            ButtonInput button = new ButtonInput();
            button.setText("Submit");
            button.setType(ButtonInput.ButtonInputTypes.SUBMIT);
            inputs.add(button);

            HashMap<String, String> metadata = new HashMap<>();
            metadata.put("snowcaptcha-secret", "secret4321");
            Form form = new Form("Form B", "b@example.com", metadata, inputs);
            ctx.json(new Response(form, Views.DashboardFull.class));
        });
    }

    public void shutdown() {
        if (this.server != null) {
            this.server.stop();
        }
    }
}
