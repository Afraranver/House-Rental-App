package com.example.rentalhouse.Model;

public class RegisterModel {

    public String username;
    public String email;
    public String password;
    public Integer type;

    public RegisterModel(){

    }

    public RegisterModel(String username, String email, String password, Integer type ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
