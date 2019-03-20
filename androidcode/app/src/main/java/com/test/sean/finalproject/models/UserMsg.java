package com.test.sean.finalproject.models;

import com.google.gson.annotations.SerializedName;

public class UserMsg {
    private String state;
    @SerializedName(value = "userName",alternate = {"user_name"})
    private String userName;
    @SerializedName(value = "fridgeNum",alternate = {"fridge_code","fridge_num"})
    private String fridgeNum;
    private String listCode;
    private String isShow;

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
