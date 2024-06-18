package dev.truewinter.snowmail;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.truewinter.snowmail.api.Util;
import dev.truewinter.snowmail.api.pojo.objects.Account;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

public record JWT(@Nullable String username, @NotNull Account.AccountRole role, @NotNull List<String> forms) {
    private static final int EXPIRES_IN = 24 * 60 * 60;

    public static JWT create(Account account) {
        return new JWT(account.getUsername(), account.getRole(), account.getForms());
    }

    public String toJwt() {
        return com.auth0.jwt.JWT.create()
                .withExpiresAt(Instant.ofEpochSecond((System.currentTimeMillis() / 1000) + EXPIRES_IN))
                .withClaim("username", username)
                .withClaim("role", role.toString())
                .withClaim("forms", forms)
                .sign(Algorithm.HMAC256(Config.getInstance().getSecret()));
    }

    @Nullable
    public static JWT fromJwt(Context ctx) {
        String header = ctx.header("Authorization");
        if (Util.isBlank(header)) {
            return null;
        }

        String[] headerParts = header.split(" ");
        if (headerParts.length != 2 || !headerParts[0].equalsIgnoreCase("Bearer")) {
            return null;
        }

        try {
            String jwtString = headerParts[1];
            JWTVerifier verifier = com.auth0.jwt.JWT.require(Algorithm.HMAC256(Config.getInstance().getSecret()))
                    .build();
            DecodedJWT jwt = verifier.verify(jwtString);
            String username = jwt.getClaim("username").asString();
            Account.AccountRole role = Account.AccountRole.valueOf(jwt.getClaim("role").asString());
            List<String> forms = jwt.getClaim("forms").asList(String.class);
            return new JWT(username, role, forms);
        } catch (Exception ignored) {
            return null;
        }
    }
}
