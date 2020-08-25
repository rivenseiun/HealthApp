package com.whu.healthapp.test;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * Created by 47462 on 2016/9/20.
 */
public class MyScrollView extends ScrollView {

    public ListView listView ;
    public LinearLayout linearLayout;
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (listView!=null && linearLayout !=null){
            int height = linearLayout.getHeight();
            if(listView.getFirstVisiblePosition() == 0 && ev.getAction() == MotionEvent.ACTION_MOVE){
                return super.onInterceptTouchEvent(ev);
            }
        }
        return false;
    }

}
