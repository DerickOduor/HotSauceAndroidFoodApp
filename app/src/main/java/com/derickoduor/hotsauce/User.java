package com.derickoduor.hotsauce;

/**
 * Created by Derick Oduor on 3/24/2018.
 */

public class User {
    private String username,password;
    private int id,phone;

    public User(String username, String password, int id,int phone) {
        this.username = username;
        this.password = password;
        this.id = id;
        this.phone=phone;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
