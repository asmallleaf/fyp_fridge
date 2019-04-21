package com.test.sean.finalproject.models;

import com.google.gson.annotations.SerializedName;

// this is a model class for json decoder
// it can be used to decode most of the json file sent by web server
public class SEMsg {
    // the result of this response
    private String state;
    // the possible second value
    @SerializedName(value = "tab",alternate = {"success","error","nameLength","nameBlank",
            "passwdBlank","passwd2Blank","differentPasswd","fridgeBlank","codeLength","SignatureExpired",
             "BadSignature","LoginFailed","tokenUnmatched","Unknown problem"})
    private String tab;
    // the possible third value
    @SerializedName(value="listCode",alternate = {"token","temperature"})
    private String listCode;

    public final int SUCCESS = 200;
    public final int ERROR = 400;

    // the getters and setters
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
