package com.test.sean.finalproject.toolbox;

import java.util.Date;

// this is a toolbox for database
public class DbTool {
    public static final String KEY_SHA = "SHA";

    // define the default constructor
    public DbTool(){}
    // it is a method to combine the host address and api address
    public static String getApi(String host,String api){
        return host+api;
    }

    // this is used to calculate the difference value between two times
    public static Long differTimeInSecond(Date now, Date past){
        Long timeNow = now.getTime()/1000;
        Long timePast = past.getTime()/1000;
        return (timeNow-timePast-28800);
    }

    // this is used to convert difference of time into a unit of day
    // it will convert into string, such as 'less than 1 minute', 'x minutes'
    public static String time2Day(Long differtime){
        Double minute = Math.ceil(differtime/60.0);
        if(minute<1.0){
            return "less than 1 minute.";
        }
        else if(minute<60.0){
            int iminute = minute.intValue();
            return String.valueOf(iminute)+" minutes";
        }
        Double hour = minute/60;
        if(hour<24.0){
            int ihour = hour.intValue();
            return String.valueOf(ihour)+" hours";
        }
        Double day = hour/24;
        int iday = day.intValue();
        return String.valueOf(iday)+" days";
    }

    // it will calculate the difference value of two time in the unit of ms
    public static Long differTimeInMile(Date now,Date past){
        Long timeNow = now.getTime();
        Long timePast = past.getTime();
        return timeNow-timePast;
    }

    // it will convert the string type boolean value into boolean type value
    public static Boolean str2bool(String str){
        if(str.equals("true") || str.equals("True") || str.equals("TRUE"))
            return true;
        else
            return false;
    }

    // it will convert boolean value into string type boolean value
    public static String bool2str(Boolean bool){
        if(bool){
            return "True";
        }
        else
            return "False";
    }
}
