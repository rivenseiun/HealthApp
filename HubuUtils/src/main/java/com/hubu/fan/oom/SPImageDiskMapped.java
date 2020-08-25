package com.hubu.fan.oom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hubu.fan.exception.ContextIsNullException;

/**
 * Created by 47462 on 2016/10/18.
 */
public class SPImageDiskMapped {

    private static SPImageDiskMapped mSpImageDiskMapped;
    /**
     * 文件地址映射
     */
    public SharedPreferences spDisk;
    public static final String DISKCACHENAME = "IMGDISKCACHE";

    private SPImageDiskMapped(Context context){
        spDisk = context.getSharedPreferences(DISKCACHENAME, Context.MODE_PRIVATE);
    }

    /**
     * 单实例
     * @param context
     * @return
     */
    public static SPImageDiskMapped getInstance(Context context){
        if(mSpImageDiskMapped == null){
            if(context == null){
                throw new ContextIsNullException();
            }
            if(context instanceof Activity){
                mSpImageDiskMapped = new SPImageDiskMapped(context.getApplicationContext());
            }
            if(context instanceof Application){
                mSpImageDiskMapped = new SPImageDiskMapped(context);
            }
        }
        return mSpImageDiskMapped;
    }

    /**
     * 添加映射
     * @param uri
     * @param path
     */
    public void map(String uri,String path){
        spDisk.edit().putString(uri,path).commit();
    }

    /**
     * 获取映射地址
     * @param uri
     * @return
     */
    public String get(String uri){
        return spDisk.getString(uri,null);
    }

    /**
     * 清除映射表
     *
     */
    public void clearTempFile() {
        spDisk.edit().clear().commit();

    }
}
