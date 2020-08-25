package com.whu.healthapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.whu.healthapp.R;


public class TestAllSurfaceView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private Thread thread;
	private boolean isRunning = true;
	
	
	private int mWidth ;//宽度
	private int mHeight ;//高度
	private int initCircleSize ;//初始化圆的大小
	private int currentCircleSize ;//当前圆的大小
	private int maxCircleSize ;//最大圆的大小
	private int currentAlpah ;//当前透明度
	private int color ;//圆的颜色
	private Paint paint ;//画笔u
	
	public TestAllSurfaceView(Context context) {
		this(context, null);
	}

	public TestAllSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mHolder = getHolder();
		mHolder.addCallback(this);
		setKeepScreenOn(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mWidth = getWidth();
		mHeight = getHeight();
		initCircleSize = getResources().getDimensionPixelOffset(R.dimen.test_init_circle_size);
		currentCircleSize = initCircleSize;
		maxCircleSize =  getResources().getDimensionPixelOffset(R.dimen.test_max_circle_size);
		currentAlpah = 255;
		color = getResources().getColor(R.color.theme_color);
		paint = new Paint();
		paint.setAlpha(currentAlpah);
		paint.setStyle(Style.FILL);
		paint.setColor(color);
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
		thread=null;
	}

	@Override
	public void run() {
		while (isRunning) {
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			if(end - start < 50){
				try {
					Thread.sleep(50-(end-start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void draw() {
		try {
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null) {
				//绘制
				mCanvas.drawColor(0xFFFFFFFF);
				mCanvas.drawCircle(mWidth/2, mHeight/2, currentCircleSize, paint);
				currentCircleSize+=10;
				currentAlpah-=10;
				if(currentAlpah<0){
					currentAlpah = 255;
					currentCircleSize = initCircleSize;
				}
				paint.setAlpha(currentAlpah);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		this.setAlpha(0);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		super.dispatchDraw(canvas);

	}
}
