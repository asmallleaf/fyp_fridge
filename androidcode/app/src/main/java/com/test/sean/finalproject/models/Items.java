package com.test.sean.finalproject.models;

import com.google.gson.annotations.SerializedName;
import com.test.sean.finalproject.SupportTool;
import com.test.sean.finalproject.item.Item;
import com.test.sean.finalproject.toolbox.DbTool;

import org.litepal.LitePalApplication;
import org.litepal.crud.LitePalSupport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Items extends LitePalSupport {
    private int id;
    @SerializedName(value = "itemListNum",alternate ={"item_list_code"})
    private String itemListNum;
    @SerializedName(value = "itemName",alternate = {"item_name"})
    private String itemName;
    @SerializedName(value = "itemNum",alternate = {"item_num"})
    private int itemNum;
    @SerializedName(value = "itemTime",alternate = {"storage_time"})
    private String itemTime;
    private String tab;

    public void setId(int mid){
        this.id=mid;
    }
    public int getId(){
        return this.id;
    }

    public String getItemListNum() {
        return itemListNum;
    }

    public void setItemListNum(String itemListNum) {
        this.itemListNum = itemListNum;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemNum() {
        return itemNum;
    }

    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    public Date getItemTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date mitemTime = new Date();
        try {
            mitemTime = dateFormat.parse(this.itemTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return mitemTime;
    }

    /*public void setItemTime(Date itemTime) {
        this.itemTime = itemTime;
    }*/
    public void setItemTime(String itemTime){
        this.itemTime = itemTime;
    }

    public void setTab(String tab){
        this.tab = tab;
    }
    public String getTab(){
        return this.tab;
    }

    public Item toItem(){
        Double differ = DbTool.differTimeInSecond(new Date(),this.getItemTime()).doubleValue();
        Item item = new Item(this.itemName,this.itemNum,differ,this.tab);
        return item;
    }
}
