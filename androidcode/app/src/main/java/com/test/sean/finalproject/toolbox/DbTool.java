package com.test.sean.finalproject.toolbox;


import java.util.Date;

public class DbTool {
    public static final String KEY_SHA = "SHA";

    public DbTool(){}
    public static String getApi(String host,String api){
        return host+api;
    }

    public static Long differTimeInSecond(Date now, Date past){
        Long timeNow = now.getTime()/1000;
        Long timePast = past.getTime()/1000;
        return timeNow-timePast;
    }

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

    public static Long differTimeInMile(Date now,Date past){
        Long timeNow = now.getTime();
        Long timePast = past.getTime();
        return timeNow-timePast;
    }

    public static Boolean str2bool(String str){
        if(str.equals("true") || str.equals("True") || str.equals("TRUE"))
            return true;
        else
            return false;
    }

    public static String bool2str(Boolean bool){
        if(bool){
            return "True";
        }
        else
            return "False";
    }
}
