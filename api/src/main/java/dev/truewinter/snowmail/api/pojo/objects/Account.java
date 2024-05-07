package dev.truewinter.snowmail.api.pojo.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
import com.fasterxml.jackson.annotation.JsonView;
import dev.truewinter.snowmail.api.pojo.Views;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Account {
    private String username;
    @JsonView(Views.Hidden.class)
    private String password;

    public Account() {}
    public Account(String username, String password, boolean hashed) {
        this.username = username;
        this.password = hashed ? password : createHash(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password, boolean hashed) {
        this.password = hashed ? password : createHash(password);;
    }

    private String createHash(String password) {
        return BCrypt.with(BCrypt.Version.VERSION_2A, LongPasswordStrategies.none())
                .hashToString(10, password.toCharArray());
    }

    public static boolean isCorrectPassword(String password, Account account) {
        return BCrypt.verifyer(BCrypt.Version.VERSION_2A, LongPasswordStrategies.none())
                .verify(password.toCharArray(), account.getPassword().toCharArray()).verified;
    }
}
