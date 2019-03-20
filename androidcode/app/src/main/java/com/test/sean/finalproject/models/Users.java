package com.test.sean.finalproject.models;

import org.litepal.crud.LitePalSupport;

public class Users extends LitePalSupport {
    private int id;
    private String name;
    private String password;
    private String fridge_num;
    private String item_list_num;
    private String token;
    private Boolean is_show_inf;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFridge_num() {
        return fridge_num;
    }

    public void setFridge_num(String fridge_num) {
        this.fridge_num = fridge_num;
    }

    public String getItem_list_num() {
        return item_list_num;
    }

    public void setItem_list_num(String item_list_num) {
        this.item_list_num = item_list_num;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getIs_show_inf() {
        return is_show_inf;
    }

    public void setIs_show_inf(Boolean is_show_inf) {
        this.is_show_inf = is_show_inf;
    }
}
