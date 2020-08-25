package com.whu.healthapp.set;

import android.content.Context;
import android.content.SharedPreferences;

import com.whu.healthapp.R;


/**
 * Created by 12242 on 2016/1/14.
 */
public class BootSetting {

    private static final String SEETING_FILE_NAME = "SEETING_FILE_NAME";

    public static void setBpObject(Context context,int bpObject){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("BPOBJECT", bpObject).commit();
    }
    public static int getBpObject(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("BPOBJECT", 0);
    }

    public static void setBpPattern(Context context, int bpPattern){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("BPPATTERN", bpPattern).commit();
    }
    public static int getBpPattern(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("BPPATTERN", 0);
    }

    public static void setBapTime(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("BAPTIME", bapTime).commit();
    }
    public static int getBapTime(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("BAPTIME", 0);
    }

    public static void setElSelect(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("ELSELECT", bapTime).commit();
    }
    public static int getElSelect(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("ELSELECT", 0);
    }

    public static void setWfGain(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("WFGAIN", bapTime).commit();
    }
    public static int getWfGain(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("WFGAIN", 0);
    }

    public static void setEpPattern(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("EPPATTERN", bapTime).commit();
    }
    public static int getEpPattern(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("EPPATTERN", 0);
    }

    public static void setEcgFiltering(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("ECGFILTERING", bapTime).commit();
    }
    public static int getEcgFiltering(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("ECGFILTERING", 0);
    }

    public static void setAhRate(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("AHRATE", bapTime).commit();
    }
    public static int getAhRate(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("AHRATE", 0);
    }

    public static void setEcgChannel(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("ECGCHANNEL", bapTime).commit();
    }
    public static int getEcgChannel(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("ECGCHANNEL", 0);
    }

    public static void setPaDetection(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("PADETECTION", bapTime).commit();
    }
    public static int getPaDetection(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("PADETECTION", 0);
    }

    public static void setIatbSynchronization(Context context,int bapTime){
        SharedPreferences.Editor et =context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).edit();
        et.putInt("IATBSYNCHRONIZATION", bapTime).commit();
    }
    public static int getIatbSynchronization(Context context){
        return context.getSharedPreferences(SEETING_FILE_NAME,Context.MODE_PRIVATE).getInt("IATBSYNCHRONIZATION", 0);
    }

    //min
    public static int getAutoApTime(Context context){
        String[] array = context.getResources().getStringArray(R.array.autopress);
       return  Integer.parseInt(array[getBapTime(context)])*60*1000;
    }
}
