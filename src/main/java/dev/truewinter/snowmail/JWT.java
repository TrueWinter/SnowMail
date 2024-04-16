package dev.truewinter.snowmail;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public record JWT(String username) {
    private static final int EXPIRES_IN = 24 * 60 * 60;

    public static JWT create(String username) {
        return new JWT(username);
    }

    public String toJwt() {
        return com.auth0.jwt.JWT.create()
                .withExpiresAt(Instant.ofEpochSecond((System.currentTimeMillis() / 1000) + EXPIRES_IN))
                .withClaim("username", username)
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
            return new JWT(username);
        } catch (Exception ignored) {
            return null;
        }
    }
}
