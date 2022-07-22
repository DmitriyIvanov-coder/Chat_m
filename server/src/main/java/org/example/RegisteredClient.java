package org.example;

public class RegisteredClient {
    private String login;
    private String password;
    private String nickname;

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public RegisteredClient(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }
}
