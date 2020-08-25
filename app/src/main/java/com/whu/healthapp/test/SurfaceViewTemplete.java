package com.whu.healthapp.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by FAN on 2016/8/29.
 */
public class SurfaceViewTemplete extends SurfaceView implements  SurfaceHolder.Callback,Runnable{

    private SurfaceHolder holder ;
    private Thread mThread ;
    private boolean isRun  = false;
    private int flushTime = 50;

    private Paint paint;

    private Context context;

    public SurfaceViewTemplete(Context context) {
        this(context, null);
    }

    public SurfaceViewTemplete(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewTemplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        holder = getHolder();
        holder.addCallback(this);
        this.context = context;

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new Thread(this);
        paint = new Paint();
        LinearGradient lg=new LinearGradient(0,0,100,100,Color.RED, Color.BLUE, Shader.TileMode.MIRROR);
        paint.setShader(lg);
        isRun = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
        mThread = null;
    }

    @Override
    public void run() {
        while (isRun){
            long start = System.currentTimeMillis();
            long end = System.currentTimeMillis();
            if(end-start<flushTime){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            draw();
        }
    }

    /**
     * 绘制界面
     */
    private void draw() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                canvas.drawRect(new Rect(0,0,200,200),paint);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(canvas != null){
                holder.unlockCanvasAndPost(canvas);
            }
        }

    }
}
