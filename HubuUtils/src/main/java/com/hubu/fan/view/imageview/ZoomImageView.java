package com.hubu.fan.view.imageview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener, OnScaleGestureListener, OnTouchListener,OnGestureListener {

	private boolean mOnce = true;//表示图片的初始化只操作一次

	private float mInitScale = 1.0f;//缩放的初始值
	private float mMaxScale = 1.0f;//缩放的最大值
//	private float mMidScale = 1.0f;//缩放的中位值

	private Matrix mMatrix;//对图片操作的举证
	private ScaleGestureDetector sgd;//对图片的缩放手势
	private GestureDetector gd ;

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// init
		mMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);
		sgd = new ScaleGestureDetector(context, this);
		gd = new GestureDetector(context,  this);
		setOnTouchListener(this);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onAttachedToWindow() {
		getViewTreeObserver().addOnGlobalLayoutListener(this);
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
		super.onDetachedFromWindow();
	}

	/*
	 * (non-Javadoc) 获取ImageView加载完成的图片
	 * 
	 * @see
	 * android.view.ViewTreeObserver.OnGlobalLayoutListener#onGlobalLayout()
	 */
	@Override
	public void onGlobalLayout() {
		if (mOnce) {

			// 获取控件的宽和高
			int width = getWidth();
			int height = getHeight();
			// 获取图片的宽和搞
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();

			/*
			 * 比较宽和高 合理的缩放图片
			 */
			float scale = 1.0f;
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			if (dw < width && dh > height) {
				scale = height * 1.0f / dh;
			}
			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}

			Log.d("mykey", String.valueOf(scale));

			/*
			 * 得到图片缩放的初始化值
			 */
			mInitScale = scale;
			mMaxScale = scale * 4;
//			mMidScale = scale * 2;

			// 设置图片居中显示
			int x = (width - dw) / 2;
			int y = (height - dh) / 2;

			mMatrix.postTranslate(x, y);
			mMatrix.postScale(mInitScale, mInitScale, width / 2, height / 2);
			setImageMatrix(mMatrix);

			mOnce = false;
		}

	}

	/**
	 * 获取当前图片的缩放比例
	 * @return
	 */
	private float getScale() {
		float f[] = new float[9];
		mMatrix.getValues(f);
		return f[Matrix.MSCALE_X];
	}

	/* (non-Javadoc)
	 * 手势的缩放时间
	 * @see android.view.ScaleGestureDetector.OnScaleGestureListener#onScale(android.view.ScaleGestureDetector)
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		// 获取当前图片的缩放比例
		float scale = getScale();
		// 获取图片的相对缩放比例
		float scaleFactor = detector.getScaleFactor();
		// 控制图片的缩放
		if ((scaleFactor > 1.0f && scale < mMaxScale) || (scaleFactor < 1.0f && scale > mInitScale)) {
			if (scaleFactor * scale < mInitScale) {
				scaleFactor = mInitScale / scale;
			}
			if (scaleFactor * scale > mMaxScale) {
				scaleFactor = mMaxScale / scale;
			}
			mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
			checkoutRightPositionWithScale();
			float f[] = new float[9];
			mMatrix.getValues(f);
			
			setImageMatrix(mMatrix);
		}
		return true;
	}
	
	
	

	/**
	 * 更正图片的位置，防止出现白边和图片不居中
	 */
	private void checkoutRightPositionWithScale() {
		
		//获取图片相对位置
		RectF rect = getRectF();
		
		//偏移量
		float deltaX = 0;
		float deltaY = 0;
		
		int width = getWidth();
		int height = getHeight();
		
		//去除白边
		if(rect.width()>=width){
			if(rect.left>=0){
				deltaX = -rect.left;
			}
			if(rect.right<=width){
				deltaX = width - rect.right;
			}
		}
		if(rect.height()>height){
			if(rect.top>=0){
				deltaY = -rect.top;
			}
			if(rect.bottom<=height){
				deltaY = height - rect.bottom;
			}
		}
		
		//讲图片居中
		if(rect.width()<width){
			deltaX = width/2 - (rect.right+rect.left)/2;
		}
		if(rect.height()<height){
			deltaY = height/2 - (rect.bottom+rect.top)/2;
		}
		mMatrix.postTranslate(deltaX, deltaY);
		
		
	}
	
	/**
	 * 获取图片当前位置
	 * @return
	 */
	private RectF getRectF(){
		Drawable d = getDrawable();
		if (d == null) {
			return null;
		}
		int dw = d.getIntrinsicWidth();
		int dh = d.getIntrinsicHeight();
		RectF rect = new RectF();
		rect.set(0, 0, dw, dh);
		mMatrix.mapRect(rect);
		return rect;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		Drawable d = getDrawable();
		if (d == null) {
			return false;
		}
		
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	
	

	/* (non-Javadoc)
	 * 对控件的触摸事件
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//放大缩小的手势操作
		sgd.onTouchEvent(event);
		gd.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		//获取图片的位置
		RectF rect = getRectF();
		int width = getWidth();
		int height = getHeight();
		
		//滑动的距离
		float dx = -distanceX;
		float dy = -distanceY;
		
		//越界判断
		if(rect.width()<=width){
			dx = 0;
		}
		if(rect.height()<=height){
			dy = 0;
		}

		if(rect.height()>=height){
			if(rect.top>=0&&dy>0){
				dy = 0;
			}
			if(rect.bottom<=height&&dy<0){
				dy = 0;
			}
		}
		
		if(rect.width()>=width){
			if(rect.left>=0&&dx>0){
				dx = 0;
			}
			if(rect.right<=width&&dx<0){
				dx = 0;
			}
		}
		
		mMatrix.postTranslate(dx, dy);
		
		setImageMatrix(mMatrix);
		
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}



}
