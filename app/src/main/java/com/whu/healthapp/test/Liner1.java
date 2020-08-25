package com.whu.healthapp.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.apkfuns.logutils.LogUtils;

/**
 * Created by 47462 on 2016/9/21.
 */
public class Liner1 extends RelativeLayout {
    public Liner1(Context context) {
        super(context);
    }

    public Liner1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Liner1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Liner1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("onInterceptTouchEvent1 ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d("onInterceptTouchEvent1 ACTION_MOVE");
                return true;
//                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d("onInterceptTouchEvent1 ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.d("onInterceptTouchEvent1 ACTION_CANCEL");
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                LogUtils.d("onTouchEvent1 ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.d("onTouchEvent1 ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                LogUtils.d("onTouchEvent1 ACTION_UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtils.d("onTouchEvent1 ACTION_CANCEL");
                break;
        }
        return false;
    }
}
