package dev.truewinter.snowmail.api.pojo.objects;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.LongPasswordStrategies;
import com.fasterxml.jackson.annotation.JsonView;
import dev.truewinter.snowmail.api.pojo.Views;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

@ApiStatus.Internal
public class Account {
    private String username;
    @JsonView(Views.Hidden.class)
    private String password;
    private AccountRole role = AccountRole.ADMIN;
    private ArrayList<String> forms = new ArrayList<>();

    public Account() {}
    public Account(String username, String password, boolean hashed, AccountRole role, ArrayList<String> forms) {
        this.username = username;
        this.password = hashed ? password : createHash(password);
        this.role = role;
        this.forms = forms;
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

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public ArrayList<String> getForms() {
        return forms;
    }

    private String createHash(String password) {
        return BCrypt.with(BCrypt.Version.VERSION_2A, LongPasswordStrategies.none())
                .hashToString(10, password.toCharArray());
    }

    public static boolean isCorrectPassword(String password, Account account) {
        return BCrypt.verifyer(BCrypt.Version.VERSION_2A, LongPasswordStrategies.none())
                .verify(password.toCharArray(), account.getPassword().toCharArray()).verified;
    }

    public enum AccountRole {
        ADMIN,
        USER;
    }
}
