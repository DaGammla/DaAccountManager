package da.gammla;

import java.io.Serializable;

public class Account implements Serializable {

    public String platform;
    public String email;
    public String username;
    public String password;

    public Account(String platform, String email, String username, String password){
        this.platform = platform;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Account acc){
        return platform.equals(acc.platform) && email.equals(acc.email) && username.equals(acc.username) && password.equals(acc.password);
    }

}
