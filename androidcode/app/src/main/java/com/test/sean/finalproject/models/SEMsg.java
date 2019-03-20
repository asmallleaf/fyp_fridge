package com.test.sean.finalproject.models;

import com.google.gson.annotations.SerializedName;

public class SEMsg {
    private String state;
    @SerializedName(value = "tab",alternate = {"success","error","nameLength","nameBlank",
            "passwdBlank","passwd2Blank","differentPasswd","fridgeBlank","codeLength","SignatureExpired",
             "BadSignature","LoginFailed","tokenUnmatched","Unknown problem"})
    private String tab;
    @SerializedName(value="listCode",alternate = {"token","temperature"})
    private String listCode;

    public final int SUCCESS = 200;
    public final int ERROR = 400;

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getListCode() {
        return listCode;
    }

    public void setListCode(String listCode) {
        this.listCode = listCode;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
