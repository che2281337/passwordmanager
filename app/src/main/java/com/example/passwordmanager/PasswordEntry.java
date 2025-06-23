package com.example.passwordmanager;

public class PasswordEntry {
    private int id;
    private String serviceName;
    private String login;
    private String password;
    private String notes;

    public PasswordEntry(int id, String serviceName, String login, String password, String notes) {
        this.id = id;
        this.serviceName = serviceName;
        this.login = login;
        this.password = password;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNotes() {
        return notes;
    }
}