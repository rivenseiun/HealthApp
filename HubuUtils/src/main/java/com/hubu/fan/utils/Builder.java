package com.hubu.fan.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 47462 on 2016/10/9.
 */
public class Builder {

    /**
     * 构建等待窗口
     * @param title
     * @param message
     */
    public static void buildProgressDialog(ProgressDialog pd,String title,String message,DialogInterface.OnKeyListener onKeyListener,boolean cancelable){
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setOnKeyListener(onKeyListener);
        pd.setCancelable(cancelable);
        pd.show();
    }

    /**
     * 构建时间任务
     * @param activity
     * @param when
     * @param runnable
     */
    public static Timer buildTimerTask(final Activity activity,long when, final Runnable runnable){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(runnable);
            }
        }, when);
        return timer;

    }

    /**
     * 构建时间任务
     * @param activity
     * @param period
     * @param runnable
     */
    public static Timer buildTimerTask(final Activity activity,int when,long period, final Runnable runnable){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(runnable);
            }
        }, when,period);
        return timer;

    }
}
