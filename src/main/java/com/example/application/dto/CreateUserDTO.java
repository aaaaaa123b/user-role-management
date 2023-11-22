package com.example.application.dto;

public class CreateUserDTO {

    private String login;
    private String password;

    public CreateUserDTO() {
    }

    public CreateUserDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password +
                '}';
    }
}
