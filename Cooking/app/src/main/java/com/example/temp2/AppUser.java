package com.example.temp2;

public class AppUser {
    String name;
    String username;
    String password;
    String profile;

    public AppUser() {
    }

    public AppUser(String name, String username, String password, String profile) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
