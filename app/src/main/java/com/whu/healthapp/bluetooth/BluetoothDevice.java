package com.whu.healthapp.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

import com.whu.healthapp.R;

/**
 * Created by Jiang.YX on 2017/2/13.
 */

public class BluetoothDevice {

    public static String getDeviceMac(Context context){
        SharedPreferences sp = context.getSharedPreferences("MAC",Context.MODE_PRIVATE);
        return sp.getString("MAC","");
    }

    public static void setDeviceMac(Context context,String mac){
        SharedPreferences sp = context.getSharedPreferences("MAC",Context.MODE_PRIVATE);
        sp.edit().putString("MAC",mac).commit();
    }

    public static void setDeviceName(Context context,String name) {
        SharedPreferences sp = context.getSharedPreferences("MAC",Context.MODE_PRIVATE);
        sp.edit().putString("MACNAME",name).commit();
    }

    public static String getDeviceName(Context context){
        SharedPreferences sp = context.getSharedPreferences("MAC",Context.MODE_PRIVATE);
        return sp.getString("MACNAME",context.getString(R.string.tip_nodevice));
    }
}