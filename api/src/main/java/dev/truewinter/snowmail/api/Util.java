package dev.truewinter.snowmail.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;

public class Util {
    // https://stackoverflow.com/a/15954821
    @ApiStatus.Internal
    public static String getInstallPath() {
        Path relative = Paths.get("");
        return relative.toAbsolutePath().toString();
    }

    // https://stackoverflow.com/a/50381020
    @ApiStatus.Internal
    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    /**
     * This method checks if the string is null before checking if it is blank to avoid NullPointerExceptions
     * @param string The string to check
     * @return Whether this string is blank
     */
    @Contract("null -> true; !null -> false")
    public static boolean isBlank(@Nullable String string) {
        if (string == null) return true;
        return string.isBlank();
    }

    /**
     * {@link #isBlank(String)}
     */
    public static boolean isBlank(String... string) {
        for (String s : string) {
            if (isBlank(s)) return true;
        }

        return false;
    }

    @ApiStatus.Internal
    public static String pojoToJson(Object object, Class<?> view) throws JsonProcessingException {
        return new ObjectMapper().writerWithView(view).writeValueAsString(object);
    }

    @ApiStatus.Internal
    public static List<File> getPluginJars() throws Exception {
        List<File> pluginJars = new ArrayList<>();

        File[] files = Path.of(getInstallPath(), "plugins").toFile()
                .listFiles((dir, name) -> name.endsWith(".jar"));

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    pluginJars.add(file);
                }
            }
        }

        return pluginJars;
    }

    public static String stringOrDefault(String str, String def) {
        if (Util.isBlank(str)) {
            return def;
        }

        return str;
    }

    private static SecretKey getKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static String[] encrypt(String str, String password) throws Exception {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        SecretKey key = getKey(password, salt);

        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(str.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encValue);
        String encodedSalt = Base64.getEncoder().encodeToString(salt);

        return new String[]{encodedSalt, encryptedValue};
    }

    public static String decrypt(String enc, String salt, String password) throws Exception {
        SecretKey key = getKey(password, Base64.getDecoder().decode(salt));
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.getDecoder().decode(enc);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

}
