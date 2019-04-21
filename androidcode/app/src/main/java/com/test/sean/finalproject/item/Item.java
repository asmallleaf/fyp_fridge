package com.test.sean.finalproject.item;
import com.test.sean.finalproject.toolbox.DbTool;
import java.util.Date;

// this is a class for item in recycle view
// a item adapter has been defined in ItemAdapter class
public class Item {
    private String item_name;
    private int item_num;
    private Double item_time;
    private String item_tab;
    // the enum is built for switching the content shown on screen
    // In default, it is set to show number of the item
    public enum flag{
        num,tab,time
    }
    private flag inf_flag;

    // the constructor of class Item
    public Item(String item_name,int item_num,Double save_time,String item_tab)
    {
        this.item_name = item_name;
        this.item_num = item_num;
        this.item_tab = item_tab;
        this.inf_flag = flag.num;
        this.item_time = save_time;
    }

    // the following functions is getter and setter of the memebers
    public String getItem_name(){
        return this.item_name;
    }

    public String getItem_num(){
        return String.valueOf(this.item_num);
    }
    public String getItem_tab(){
        return this.item_tab;
    }
    public String getItem_time(){
        return DbTool.time2Day(this.item_time.longValue());
    }
    public flag getItem_flag(){return this.inf_flag;}
    public flag setItem_flag(flag val){
        this.inf_flag = val;
        return this.inf_flag;
    }
}
