package com.whu.healthapp.jpush;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.hubu.fan.exception.ContextIsNullException;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushSets {

    private Context mContext ;
    private static JPushSets jPushSets;

    /**
     * 单例模式
     * @param context
     */
    private JPushSets(Context context){
        if(context == null){
            throw new ContextIsNullException();
        }
        mContext = context;
    }

    /**
     * 单例模式 获取实例
     * @param context
     * @return
     */
    public static JPushSets getInstance(Context context){
        if(jPushSets == null){
            jPushSets = new JPushSets(context);
        }
        return jPushSets;
    }

    /**
     * 设置标签
     * @param tagSet
     */
    public void setTag(Set<String> tagSet){
        //筛选有效的标签
        tagSet = JPushInterface.filterValidTags(tagSet);
        //调用JPush API设置Alias
        JPushInterface.setAliasAndTags(mContext, null, tagSet, mTagsCallback);
    }

    /**
     * 设置别名
     * @param alias
     */
    public void setAlias(String alias){
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
        JPushInterface.setAliasAndTags(mContext, alias, null, mAliasCallback);
    }

    /**
     * 回调接口
     */
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set alias success";
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }
            LogUtils.i(logs);

        }

    };


    /**
     * 回调接口
     */
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag  success";
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;

            }
            LogUtils.e(logs);
        }

    };



    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(mContext, (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(mContext, null, (Set<String>) msg.obj, mTagsCallback);
                    break;
            }
        }
    };




    /**
     *设置通知提示方式 - 基础属性
     */
    private void setStyleBasic(int drawable){
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(mContext);
        builder.statusBarDrawable = drawable;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
        Toast.makeText(mContext, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }


    /**
     *设置通知栏样式 - 定义通知栏Layout
     */
    private void setStyleCustom(int layout,int icon,int title,int text,int drawable){
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(mContext,layout,icon, title, text);
        builder.layoutIconDrawable = drawable;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setPushNotificationBuilder(2, builder);
        Toast.makeText(mContext,"Custom Builder - 2", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置推送的时间
     * @param days 0表示Sunday  6表示Saturday
     * @param startTime
     * @param endTime
     */
    private String setPushTime(Set<Integer> days, TimePicker startTime, TimePicker endTime){

        int startime = startTime.getCurrentHour();
        int endtime = endTime.getCurrentHour();
        if (startime > endtime) {
            return "开始时间不能大于结束时间";
        }
        //调用JPush api设置Push时间
        JPushInterface.setPushTime(mContext, days, startime, endtime);
        return "设置成功" ;

    }


}
