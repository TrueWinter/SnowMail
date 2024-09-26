package dev.truewinter.snowmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import dev.truewinter.snowmail.api.FormSubmissionInput;
import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.event.FormSavedEvent;
import dev.truewinter.snowmail.api.plugin.SnowMailPluginManager;
import dev.truewinter.snowmail.database.Database;
import dev.truewinter.snowmail.api.inputs.AbstractTextInput;
import dev.truewinter.snowmail.api.inputs.TextInput;
import dev.truewinter.snowmail.api.pojo.Response;
import dev.truewinter.snowmail.api.pojo.objects.Account;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import dev.truewinter.snowmail.api.pojo.Views;
import dev.truewinter.snowmail.pebble.PebbleExtension;
import io.javalin.Javalin;
import io.javalin.http.*;
import io.javalin.rendering.template.JavalinPebble;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;

public class WebServer extends Thread {
    private Javalin server;
    private final Database database;

    public WebServer(Database database) {
        this.database = database;
    }

    @Override
    public void run() {
        PebbleEngine.Builder pebbleEngine = new PebbleEngine.Builder();
        Loader<String> loader = new ClasspathLoader();
        loader.setPrefix("web");
        loader.setSuffix(".peb");
        pebbleEngine.loader(loader);
        pebbleEngine.extension(new PebbleExtension());

        this.server = Javalin.create(config -> {
            config.showJavalinBanner = false;
            // In development, the dist folder is empty which prevents it from being included as a resource
            if (getClass().getResource("/web/dist") != null) {
                config.staticFiles.add(s -> {
                    s.hostedPath = "/assets";
                    s.directory = "/web/dist";
                    // Javalin ignores the cache headers in the before handler if the header exists here
                    s.headers.remove(Header.CACHE_CONTROL);
                });
            }
            config.fileRenderer(new JavalinPebble(pebbleEngine.build()));
            config.spaRoot.addHandler("/", ctx -> {
                HashMap<String, HashMap<String, Object>> data = new HashMap<>(){{
                    put("sso", new HashMap<>());
                }};

                if (Config.getInstance().isSsoEnabled()) {
                    HashMap<String, Object> ssoData = data.get("sso");
                    ssoData.put("enabled", Config.getInstance().isSsoEnabled());
                    ssoData.put("forceRedirect", Config.getInstance().isSsoForceRedirect());
                }

                ctx.render("index", data);
            });
            config.http.prefer405over404 = true;
        });
        server.start(Config.getInstance().getPort());

        server.before(ctx -> {
            ctx.header("X-Robots-Tag", "noindex");

            String path = ctx.path();
            if (path.startsWith("/assets/") && !path.equals("/assets/main.js")) {
                ctx.header(Header.CACHE_CONTROL, "public, max-age=604800, immutable");
            } else {
                ctx.header(Header.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                ctx.header(Header.EXPIRES, "0");
            }

            if (path.startsWith("/public-api")) {
                ctx.header("Access-Control-Allow-Origin", "*");
                return;
            }

            if (path.startsWith("/login/sso") && !Config.getInstance().isSsoEnabled()) {
                ctx.redirect("/login");
                return;
            }

            JWT jwt = JWT.fromJwt(ctx);

            if (path.equals("/login") || path.equals("/api/login")) {
                if (jwt != null) {
                    ctx.redirect("/");
                }

                return;
            }

            if (jwt == null && path.startsWith("/api")) {
                throw new UnauthorizedResponse();
            }
        });

        server.get("/login/sso", ctx -> {
            String proto = Util.stringOrDefault(ctx.header("X-Forwarded-Proto"), ctx.scheme());
            String host = Util.stringOrDefault(ctx.header("X-Forwarded-Host"), ctx.host());
            URI callback = new URI(String.format("%s://%s/login/sso/callback", proto, host));

            ClientID clientID = new ClientID(Config.getInstance().getSsoClientId());
            com.nimbusds.oauth2.sdk.id.State state = new com.nimbusds.oauth2.sdk.id.State();
            Nonce nonce = new Nonce();

            String[] saltAndEncr = Util.encrypt(String.format("%d.%s", System.currentTimeMillis() + 5 * 60 * 1000,
                            state.getValue()), Config.getInstance().getSecret());
            ctx.cookie("snowmail-sso", String.join(".", saltAndEncr));

            AuthenticationRequest request = new AuthenticationRequest.Builder(
                    new ResponseType("code"),
                    new Scope("openid", "profile", "snowmail"),
                    clientID,
                    callback)
                    .endpointURI(new URI(Config.getInstance().getSsoAuthUrl()))
                    .state(state)
                    .nonce(nonce)
                    .build();

            ctx.redirect(request.toURI().toString());
        });

        server.get("/login/sso/callback", ctx -> {
            String ssoCookie = ctx.cookie("snowmail-sso");
            if (Util.isBlank(ssoCookie)) {
                throw new BadRequestResponse("SSO cookie is not set. Please try logging in again");
            }

            AuthenticationResponse response = AuthenticationResponseParser.parse(new URI(ctx.fullUrl()));
            org.slf4j.Logger logger = Logger.getInstance().getLogger();

            String[] ssoCookieParts = ssoCookie.split("\\.");
            String salt = ssoCookieParts[0];
            String encr = ssoCookieParts[1];

            String[] decryptedSsoCookie = Util.decrypt(encr, salt, Config.getInstance().getSecret()).split("\\.");
            long expiry = Long.parseLong(decryptedSsoCookie[0]);
            String state = decryptedSsoCookie[1];
            ctx.removeCookie("snowmail-sso");

            if (!response.getState().equals(new com.nimbusds.oauth2.sdk.id.State(state))) {
                throw new BadRequestResponse("Invalid state");
            }

            if (expiry <= System.currentTimeMillis()) {
                throw new BadRequestResponse("SSO cookie expired. Please try logging in again");
            }

            if (response instanceof AuthenticationErrorResponse) {
                ErrorObject errorObject = response.toErrorResponse().getErrorObject();
                logger.error("SSO Authentication Error: " + errorObject.getDescription());
                throw new InternalServerErrorResponse();
            }

            Config config = Config.getInstance();
            String proto = Util.stringOrDefault(ctx.header("X-Forwarded-Proto"), ctx.scheme());
            String host = Util.stringOrDefault(ctx.header("X-Forwarded-Host"), ctx.host());
            URI callback = new URI(String.format("%s://%s/login/sso/callback", proto, host));
            AuthorizationCode code = response.toSuccessResponse().getAuthorizationCode();
            AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);
            ClientID clientID = new ClientID(config.getSsoClientId());
            Secret clientSecret = new Secret(config.getSsoClientSecret());
            ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
            URI tokenEndpoint = new URI(config.getSsoTokenURL());

            TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);
            TokenResponse tokenResponse = OIDCTokenResponseParser.parse(request.toHTTPRequest().send());

            if (!tokenResponse.indicatesSuccess()) {
                ErrorObject errorResponse = tokenResponse.toErrorResponse().getErrorObject();
                logger.error("SSO Authentication Error: " + errorResponse.getDescription());
                throw new InternalServerErrorResponse();
            }

            OIDCTokenResponse successResponse = (OIDCTokenResponse) tokenResponse.toSuccessResponse();
            AccessToken accessToken = successResponse.getOIDCTokens().getAccessToken();

            URI userInfoEndpoint = new URI(config.getSsoUserInfoUrl());
            HTTPResponse httpResponse = new UserInfoRequest(userInfoEndpoint, accessToken)
                    .toHTTPRequest().send();
            UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);

            if (!userInfoResponse.indicatesSuccess()) {
                ErrorObject errorResponse = userInfoResponse.toErrorResponse().getErrorObject();
                logger.error("SSO Authentication Error: " + errorResponse.getDescription());
                throw new InternalServerErrorResponse();
            }

            UserInfo userInfo = userInfoResponse.toSuccessResponse().getUserInfo();
            JsonNode claim = new ObjectMapper().valueToTree(userInfo.getClaim("snowmail"));
            if (!claim.hasNonNull("role") || !claim.hasNonNull("forms") ||
                    !claim.get("forms").getNodeType().equals(JsonNodeType.ARRAY)) {
                logger.error("UserInfo request failed: Missing role and/or forms claim, or received invalid data type");
                throw new BadRequestResponse();
            }

            Account.AccountRole role = Account.AccountRole.valueOf(claim.get("role").asText());
            ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<String>>() {});
            List<String> forms = reader.readValue(claim.get("forms"));
            JWT jwt = JWT.create(role, forms);

            HashMap<String, String> data = new HashMap<>();
            data.put("jwt", jwt.toJwt());
            ctx.render("sso", data);
        });

        server.get("/login/sso/logout", ctx -> {
            String logoutUrl = Config.getInstance().getSsoLogoutUrl();
            ctx.redirect(Util.isBlank(logoutUrl) ? "/login" : logoutUrl);
        });

        server.post("/api/login", ctx -> {
            if (Config.getInstance().isSsoEnabled() && Config.getInstance().isSsoForceRedirect()) {
                throw new BadRequestResponse("Local accounts are disabled if forced SSO redirects are enabled");
            }

            JsonNode data = requestToJson(ctx);

            if (!data.hasNonNull("username") || !data.hasNonNull("password")) {
                throw new UnauthorizedResponse("Invalid username or password");
            }

            String username = data.get("username").asText();
            String password = data.get("password").asText();

            database.getAccountDatabase().getAccountIfPasswordIsCorrect(username, password).ifPresentOrElse(a -> {
                ObjectNode node = new ObjectMapper().createObjectNode();
                node.put("token", JWT.create(a).toJwt());
                ctx.json(node);
            }, () -> {
                throw new UnauthorizedResponse("Invalid username or password");
            });
        });

        server.get("/api/accounts", ctx -> {
            if (!Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                throw new ForbiddenResponse();
            }

            ctx.json(new Response(database.getAccountDatabase().getAccounts(), Views.DashboardFull.class));
        });

        server.post("/api/accounts", ctx -> {
            if (!Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                throw new ForbiddenResponse();
            }

            JsonNode body = requestToJson(ctx);
            if (!body.hasNonNull("username") || !body.hasNonNull("password") ||
                    !body.hasNonNull("confirm-password")) {
                throw new BadRequestResponse("All fields are required");
            }

            String username = body.get("username").asText();
            String password = body.get("password").asText();
            String confirmPassword = body.get("confirm-password").asText();
            Account.AccountRole role = Account.AccountRole.valueOf(body.get("role").asText());
            ArrayList<String> forms = new ObjectMapper().readerForListOf(String.class).readValue(body.get("forms"));

            if (!password.equals(confirmPassword)) {
                throw new BadRequestResponse("Passwords do not match");
            }

            if (database.getAccountDatabase().getAccount(username).isPresent()) {
                throw new BadRequestResponse("An account with that username already exists");
            }

            Account account = new Account(username, password, false, role, forms);
            database.getAccountDatabase().createOrEditAccount(account);

            ctx.status(200);
        });

        server.get("/api/accounts/{username}", ctx -> {
            JWT jwt = Objects.requireNonNull(JWT.fromJwt(ctx));
            if (!jwt.role().equals(Account.AccountRole.ADMIN) && !ctx.pathParam("username").equals(jwt.username())) {
                throw new ForbiddenResponse();
            }

            Optional<Account> account = database.getAccountDatabase().getAccount(ctx.pathParam("username"));
            account.ifPresentOrElse(a -> ctx.json(new Response(a, Views.DashboardFull.class)), () -> {
                throw new NotFoundResponse("Account not found");
            });
        });

        server.put("/api/accounts/{username}", ctx -> {
            JWT jwt = Objects.requireNonNull(JWT.fromJwt(ctx));
            if (!jwt.role().equals(Account.AccountRole.ADMIN) && !ctx.pathParam("username").equals(jwt.username())) {
                throw new ForbiddenResponse();
            }

            JsonNode body = requestToJson(ctx);
            if (!body.hasNonNull("password") || !body.hasNonNull("confirm-password")) {
                throw new BadRequestResponse("All fields are required");
            }

            Optional<Account> account = database.getAccountDatabase().getAccount(ctx.pathParam("username"));
            account.ifPresentOrElse(a -> {
                try {
                    String password = body.get("password").asText();
                    String confirmPassword = body.get("confirm-password").asText();

                    if (!password.isBlank()) {
                        if (!password.equals(confirmPassword)) {
                            throw new BadRequestResponse("Passwords do not match");
                        }

                        a.setPassword(password, false);
                    }

                    if (Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                        Account.AccountRole role = Account.AccountRole.valueOf(body.get("role").asText());
                        ArrayList<String> forms = new ObjectMapper().readerForListOf(String.class)
                                .readValue(body.get("forms"));

                        a.setRole(role);
                        a.getForms().clear();
                        a.getForms().addAll(forms);
                    }

                    database.getAccountDatabase().createOrEditAccount(a);
                } catch (IOException e) {
                    throw new InternalServerErrorResponse(e.getMessage());
                }

                ctx.status(200);
            }, () -> {
                throw new NotFoundResponse("Account not found");
            });
        });

        server.delete("/api/accounts/{username}", ctx -> {
            if (!Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                throw new ForbiddenResponse();
            }

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
            JWT jwt = Objects.requireNonNull(JWT.fromJwt(ctx));
            List<Form> forms = database.getFormDatabase().getForms();
            List<Form> permittedForms = jwt.role().equals(Account.AccountRole.ADMIN) ? forms :
                    forms.stream().filter(form -> jwt.forms().contains(form.getId().toHexString())).toList();
            ctx.json(new Response(permittedForms, Views.DashboardSummary.class));
        });

        server.post("/api/forms", ctx -> {
            if (!Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                throw new ForbiddenResponse();
            }

            Form form = ctx.bodyAsClass(Form.class);
            if (!form.isValid()) {
                throw new BadRequestResponse();
            }

            ObjectNode node = new ObjectMapper().createObjectNode();
            node.put("id", database.getFormDatabase().createForm(form));
            ctx.json(node);
        });

        server.get("/api/forms/{id}", ctx -> {
            JWT jwt = Objects.requireNonNull(JWT.fromJwt(ctx));
            if (!jwt.role().equals(Account.AccountRole.ADMIN) && !jwt.forms().contains(ctx.pathParam("id"))) {
                throw new ForbiddenResponse();
            }

            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            ctx.json(new Response(form.get(), Views.DashboardFull.class));
        });

        server.put("/api/forms/{id}", ctx -> {
            JWT jwt = Objects.requireNonNull(JWT.fromJwt(ctx));
            if (!jwt.role().equals(Account.AccountRole.ADMIN) && !jwt.forms().contains(ctx.pathParam("id"))) {
                throw new ForbiddenResponse();
            }

            String id = ctx.pathParam("id");
            if (!database.getFormDatabase().formExists(id)) {
                throw new NotFoundResponse();
            }

            Form form = ctx.bodyAsClass(Form.class);
            if (!form.isValid()) {
                throw new BadRequestResponse();
            }

            database.getFormDatabase().editForm(form);
            SnowMailPluginManager.getInstance().getPluginManager().fireEvent(new FormSavedEvent(form));
            ctx.status(200);
        });

        server.delete("/api/forms/{id}", ctx -> {
            if (!Objects.requireNonNull(JWT.fromJwt(ctx)).role().equals(Account.AccountRole.ADMIN)) {
                throw new ForbiddenResponse();
            }

            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            database.getFormDatabase().deleteForm(id);
            ctx.status(200);
        });

        server.get("/api/custom-inputs", ctx -> {
            ctx.json(new Response(CustomInputRegistry.getInstance(), Views.DashboardFull.class));
        });

        server.get("/public-api/forms/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            ctx.json(new Response(form.get(), Views.Public.class));
        });

        server.options("/public-api/forms/{id}", ctx -> {
            ctx.status(204)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept");
        });

        server.post("/public-api/forms/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Optional<Form> form = database.getFormDatabase().getForm(id);
            if (form.isEmpty()) {
                throw new NotFoundResponse();
            }

            HashMap<String, AbstractTextInput> formInputs = Form.recursivelyGetInputs(form.get().getInputs());
            HashMap<String, FormSubmissionInput> submission = new HashMap<>();
            try {
                HashMap<String, Object> body = ctx.bodyAsClass(HashMap.class.getGenericSuperclass());
                for (String s : body.keySet()) {
                    if (formInputs.containsKey(s)) {
                        submission.put(s, new FormSubmissionInput(formInputs.get(s), (String) body.get(s)));
                    }
                }
            } catch (ClassCastException ignored) {
                throw new BadRequestResponse("All fields must be strings");
            }

            for (AbstractTextInput input : formInputs.values()) {
                String label = input.getLabel();
                FormSubmissionInput value = submission.get(input.getName());

                if (input.isRequired() && (value == null || Util.isBlank(value.value()))) {
                    throw new BadRequestResponse(label + " is required");
                }

                if (value != null && !Util.isBlank(value.value())) {
                    int maxLength = input.getMaxLength();
                    if (maxLength > 0 && value.value().length() > maxLength) {
                        throw new BadRequestResponse(label + " is over maximum length of " + maxLength);
                    }

                    if (input instanceof TextInput) {
                        String pattern = ((TextInput) input).getPattern();
                        if (!Util.isBlank(pattern) && !Pattern.compile(pattern).matcher(value.value()).find()) {
                            String patternError = ((TextInput) input).getPatternError();
                            if (Util.isBlank(patternError)) {
                                patternError = "Value does not match required pattern";
                            }
                            throw new BadRequestResponse(patternError);
                        }
                    }
                }
            }

            try {
                FormSubmissionHandler.handle(form.get(), submission);
                ctx.status(200);
            } catch (Exception e) {
                throw new BadRequestResponse(e.getMessage());
            }
        });
    }

    public void shutdown() {
        if (this.server != null) {
            this.server.stop();
        }
    }

    private JsonNode requestToJson(Context ctx) throws JsonProcessingException {
        return new ObjectMapper().readTree(ctx.body());
    }
}
