package dev.truewinter.snowmail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Util {
    // https://stackoverflow.com/a/15954821
    public static String getInstallPath() {
        Path relative = Paths.get("");
        return relative.toAbsolutePath().toString();
    }

    // https://stackoverflow.com/a/50381020
    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    @Contract("null -> true; !null -> false")
    public static boolean isBlank(@Nullable String string) {
        if (string == null) return true;
        return string.isBlank();
    }

    public static JsonNode requestToJson(Context ctx) throws JsonProcessingException {
        return new ObjectMapper().readTree(ctx.body());
    }
}
