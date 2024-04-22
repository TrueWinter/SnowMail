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
            /*LinkedList<Form> forms = new LinkedList<>();

            Form form1 = new Form(new ObjectId("661eca4d752415374a2d0ffa"), "Test Form", "email@example.com", new HashMap<>(), new LinkedList<>());

            HashMap<String, String> form2Metadata = new HashMap<>();
            form2Metadata.put("test", "1234");
            Form form2 = new Form("Test Form 2", "email2@example.com", form2Metadata, new LinkedList<>());

            Form form3 = new Form("Test Form 3", "email3@example.com", new HashMap<>(), new LinkedList<>());

            forms.add(form1);
            forms.add(form2);
            forms.add(form3);

            ctx.json(new Response(forms, Views.DashboardSummary.class));*/
            ctx.json(new Response(database.getFormDatabase().getForms(), Views.DashboardSummary.class));
        });

        server.post("/api/forms", ctx -> {
            Form form = ctx.bodyAsClass(Form.class);
            if (!form.isValid()) {
                throw new BadRequestResponse();
            }

            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("id", database.getFormDatabase().createForm(form));
            ctx.json(node);
        });

        server.put("/api/forms/{id}", ctx -> {
            String id = ctx.pathParam("id");
            if (!database.getFormDatabase().formExists(id)) {
                throw new NotFoundResponse();
            }

            Form form = ctx.bodyAsClass(Form.class);
            if (!form.isValid()) {
                throw new BadRequestResponse();
            }

            database.getFormDatabase().editForm(form);
            ctx.status(200);
        });

        server.get("/api/forms/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            ctx.json(new Response(form.get(), Views.DashboardFull.class));
        });

        server.delete("/api/forms/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            database.getFormDatabase().deleteForm(id);
            ctx.status(200);
        });
    }

    public void shutdown() {
        if (this.server != null) {
            this.server.stop();
        }
    }
}
