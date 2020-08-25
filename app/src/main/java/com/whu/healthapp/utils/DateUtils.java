package com.whu.healthapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 47462 on 2016/11/15.
 */
public class DateUtils {

    public static String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }
}
