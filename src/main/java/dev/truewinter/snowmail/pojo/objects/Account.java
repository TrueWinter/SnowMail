package dev.truewinter.snowmail.pojo.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
import com.fasterxml.jackson.annotation.JsonView;
import dev.truewinter.snowmail.pojo.Views;
import org.jetbrains.annotations.ApiStatus;

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

    @ApiStatus.Internal
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @ApiStatus.Internal
    public void setPassword(String password) {
        setPassword(password, true);
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
