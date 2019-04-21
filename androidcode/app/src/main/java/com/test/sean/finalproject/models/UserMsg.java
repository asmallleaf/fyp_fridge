package com.test.sean.finalproject.models;

import com.google.gson.annotations.SerializedName;

// this is a model class for json decoder
// it can be used to decode a special the json file sent by web server
public class UserMsg {
    // the result of this response
    private String state;
    // the serialized name is due to different name ruls between python and java
    @SerializedName(value = "userName",alternate = {"user_name"})
    private String userName;
    @SerializedName(value = "fridgeNum",alternate = {"fridge_code","fridge_num"})
    private String fridgeNum;
    private String listCode;
    private String isShow;

    // the getters and setters
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFridgeNum() {
        return fridgeNum;
    }

    public void setFridgeNum(String fridgeNum) {
        this.fridgeNum = fridgeNum;
    }

    public String getListCode() {
        return listCode;
    }

    public void setListCode(String listCode) {
        this.listCode = listCode;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }
}
