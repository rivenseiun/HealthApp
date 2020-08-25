package com.whu.healthapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hubu.fan.utils.CommonUtils;

/**
 * Created by 47462 on 2016/9/23.
 */
public class UserInfo {




    public static int getId() {
        return 1;
    }

    public static boolean isLogin(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        return sp.getBoolean("LOGIN",false);
    }

    public static void logout(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        sp.edit().putBoolean("LOGIN",false).commit();
    }

    public static void login(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        sp.edit().putBoolean("LOGIN",true).commit();
    }

    public static boolean isSel(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        if(CommonUtils.isAvailable(sp.getString("PERSONER",""))){
            return true;
        }
        return false;
    }

    public static String getSel(Context context){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        return sp.getString("PERSONER","");

    }

    public static void setSel(Context context,String kind){
        SharedPreferences sp = context.getSharedPreferences("USER",Context.MODE_PRIVATE);
        sp.edit().putString("PERSONER", kind).commit();

    }
}
