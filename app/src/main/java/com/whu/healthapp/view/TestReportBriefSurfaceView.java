package com.whu.healthapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.whu.healthapp.R;


/**
 * Created by fan on 2016/1/3.
 */
public class TestReportBriefSurfaceView extends SurfaceView implements Runnable, SurfaceHolder.Callback {


    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread thread;
    private boolean isRunning;
    private int mHeight;
    private int mWidth;
    private Paint circlePaint;
    private Paint linePaint;
    private Paint textPaint;//文字画笔
    private Paint clickPaint;//点击的文字画笔
    private Point p[];//六个点
    private String tags[] = new String[]{"心率", "体温", "血氧", "综合", "血压", "脉率"};
    private int judge[] = new int[]{0, 0, 0, 0, 0, 0};
    private int scaleSize;//每次缩减的大小

    private int prefectSize, goodSize, seriousSize;//缩减所对应的等级
    private int pgsSizeAll[];//上面的集合
    private int currentScaleSize[];//每个店的当前缩减值

    //等级
    public static final int PREFECT = 0;
    public static final int GOOD = 1;
    public static final int SERIOUS = 2;

    private  int flushTime = 50;


    public TestReportBriefSurfaceView(Context context) {
        this(context, null);
    }

    public TestReportBriefSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        isTheOnce = true;
        //init
        mHolder = getHolder();
        mHolder.addCallback(this);

    }


    @Override
    public void run() {

        //绘制
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < flushTime) {
                try {
                    Thread.sleep(flushTime - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void init() {
        mHeight = getHeight();
        mWidth = getWidth();
        scaleSize = getResources().getDimensionPixelOffset(R.dimen.dpunit);
        circlePaint = new Paint();
        linePaint = new Paint();
        textPaint = new Paint();
        clickPaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(getResources().getColor(R.color.perfect));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(getResources().getColor(R.color.perfect));
        linePaint.setStrokeWidth(2*scaleSize);
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setColor(0xFF000000);
        textPaint.setTextSize(getResources().getDimensionPixelOffset(R.dimen.test_reports_textsize));
        textPaint.setTextAlign(Paint.Align.CENTER);
        clickPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clickPaint.setColor(getResources().getColor(R.color.theme_color));
        clickPaint.setTextSize(getResources().getDimensionPixelOffset(R.dimen.test_reports_clicksize));
        clickPaint.setTextAlign(Paint.Align.CENTER);
        //初始化缩减值

        prefectSize = 15 * scaleSize;
        goodSize = 25 * scaleSize;
        seriousSize = 35 * scaleSize;
        pgsSizeAll = new int[3];
        pgsSizeAll[0] = prefectSize;
        pgsSizeAll[1] = goodSize;
        pgsSizeAll[2] = seriousSize;

        currentScaleSize = new int[6];
        for (int i = 0; i < 6; i++) {
            currentScaleSize[i] = 3;
        }
        p = new Point[6];
        p[0] = new Point(mWidth / 2, mHeight - scaleSize * 3);
        p[1] = new Point(mWidth - scaleSize * 3, mHeight / 2 + mHeight / 4);
        p[2] = new Point(mWidth - scaleSize * 3, mHeight / 2 - mHeight / 4);
        p[3] = new Point(mWidth / 2, scaleSize * 3);
        p[4] = new Point(scaleSize * 3, mHeight / 2 - mHeight / 4);
        p[5] = new Point(scaleSize * 3, mHeight / 2 + mHeight / 4);
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                Path pa = new Path();
                mCanvas.drawColor(0xFFFFFFFF);

                pa.moveTo(p[0].x, p[0].y);
                pa.lineTo(p[1].x, p[1].y);
                pa.lineTo(p[2].x, p[2].y);
                pa.lineTo(p[3].x, p[3].y);
                pa.lineTo(p[4].x, p[4].y);
                pa.lineTo(p[5].x, p[5].y);
                pa.close();
                mCanvas.drawPath(pa, linePaint);
                for (int i = 0; i < 6; i++) {
                    //获取当前缩减值
                    if (currentScaleSize[i] <= prefectSize + 1) {
                        circlePaint.setColor(getResources().getColor(R.color.perfect));
                    } else if (currentScaleSize[i] > prefectSize + 1 && currentScaleSize[i] <= goodSize + 1) {
                        circlePaint.setColor(getResources().getColor(R.color.good));
                    } else {
                        circlePaint.setColor(getResources().getColor(R.color.serious));
                    }
                    ;
                    mCanvas.drawCircle(p[i].x, p[i].y, 10, circlePaint);
                    mCanvas.drawText(tags[i], p[i].x, p[i].y, textPaint);
                }

                mCanvas.drawText("指导建议", mWidth / 2, mHeight / 2, clickPaint);
            }
            boolean isChange = false;//视图是否改变
            //应当缩小的值
            int shoudSize = pgsSizeAll[judge[0]];
            if (currentScaleSize[0] < shoudSize) {
                p[0].y -= scaleSize;
                currentScaleSize[0] += scaleSize;
                isChange = true;
            }
            shoudSize = pgsSizeAll[judge[1]];
            if (currentScaleSize[1] < shoudSize) {
                p[1].x -= scaleSize;
                p[1].y -= scaleSize;
                currentScaleSize[1] += scaleSize;
                isChange = true;
            }

            shoudSize = pgsSizeAll[judge[2]];
            if (currentScaleSize[2] < shoudSize) {
                p[2].x -= scaleSize;
                p[2].y += scaleSize;
                currentScaleSize[2] += scaleSize;
                isChange = true;
            }

            shoudSize = pgsSizeAll[judge[3]];
            if (currentScaleSize[3] < shoudSize) {
                p[3].y += scaleSize;
                currentScaleSize[3] += scaleSize;
                isChange = true;
            }

            shoudSize = pgsSizeAll[judge[4]];
            if (currentScaleSize[4] < shoudSize) {
                p[4].x += scaleSize;
                p[4].y += scaleSize;
                currentScaleSize[4] += scaleSize;
                isChange = true;
            }

            shoudSize = pgsSizeAll[judge[5]];
            if (currentScaleSize[5] < shoudSize) {
                p[5].x += scaleSize;
                p[5].y -= scaleSize;
                currentScaleSize[5] += scaleSize;
                isChange = true;
            }

//            if(!isChange){
//                //如果动画完毕，暂停绘制
//                isRunning = false;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
                mCanvas = null;
            }
        }
    }

    //设置评分标准
    public void setJudge(int xinlv, int tiwen, int xueyang, int zonghe, int xueya, int mailv) {
        judge = new int[]{xinlv, tiwen, xueyang, zonghe, xueya, mailv};
    }

    private boolean isTheOnce = true;//是否第一次运行


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        init();
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        thread.interrupt();
    }
}
