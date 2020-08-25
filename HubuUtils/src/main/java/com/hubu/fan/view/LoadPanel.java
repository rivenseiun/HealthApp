package com.hubu.fan.view;

import com.hubu.fan.app.R;
import com.hubu.fan.view.gifview.GifView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author fan 加载面板，快速加载等待面板
 */
public class LoadPanel extends RelativeLayout {

	private View loadView = null;

	private boolean isLoad = false;// 加载视图是否被加载
	
	private boolean isHide = false;
	
	private GifView loadGif ;
	
	private TextView loadText;

	// 构造函数
	public LoadPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// init
		// 加载背景图片
		loadView = LayoutInflater.from(context).inflate(
				R.layout.hubu_loadpanel, this, false);
		loadGif = (GifView) loadView.findViewById(R.id.hubu_gv_loadimg);
		loadText = (TextView) loadView.findViewById(R.id.hubu_tv_loadtxt);
	}

	public LoadPanel(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadPanel(Context context) {
		this(context, null);
	}

	// 判断其子控件有且仅有只有一个子控件
	@Override
	public void addView(View child) {
		if (getChildCount() > 0 && !isLoad) {
			throw new IllegalStateException(
					"LoadPanel can host only one direct child");
		}
		super.addView(child);
	}

	@Override
	public void addView(View child, int index) {
		if (getChildCount() > 0 && !isLoad) {
			throw new IllegalStateException(
					"LoadPanel can host only one direct child");
		}
		super.addView(child, index);
	}

	@Override
	public void addView(View child, ViewGroup.LayoutParams params) {
		if (getChildCount() > 0 && !isLoad) {
			throw new IllegalStateException(
					"LoadPanel can host only one direct child");
		}
		super.addView(child, params);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (getChildCount() > 0 && !isLoad) {
			throw new IllegalStateException(
					"LoadPanel can host only one direct child");
		}
		super.addView(child, index, params);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// 加载面板，覆盖底层面板
		if (!isLoad) {
			isLoad = true;
			super.addView(loadView);
		}
	}

	/**
	 * 设置面板背景颜色
	 */
	public void setLoadPanelBackgroundColor(int resid) {
		LinearLayout background = (LinearLayout) loadView.findViewById(R.id.hubu_ll_background);
		background.setBackgroundResource(resid);
	}

	/**
	 * 显示面板
	 */
	public void show() {
		loadView.setVisibility(View.VISIBLE);
		isHide = false;
	}

	/**
	 * 隐藏面板
	 */
	public void hide() {
		loadView.setVisibility(View.GONE);
		isHide = true;
	}
	
	/**
	 * 隐藏图像面板
	 */
	public void hideLoadView(){
		loadGif.setVisibility(View.GONE);
	}
	
	/**
	 * 显示图像面板
	 */
	public void showLoadView(){
		loadGif.setVisibility(View.VISIBLE);
	}

	public void setLoadGif(int gifId) {
		loadGif.setMovieResource(gifId);
	}
	
	public void setLoadText(String text){
		loadText.setHint(text);
	}

	/**
	 * 设置动画
	 * 
	 * @param gifId
	 * @param whith
	 *            单位dp
	 * @param height
	 */
	public void setLoadGif(int gifId, int whith, int height) {
		loadGif.setMovieResource(gifId);
		int whith_ = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, whith, getContext().getResources()
						.getDisplayMetrics());
		int height_ = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, height, getContext().getResources()
				.getDisplayMetrics());
		loadGif.getLayoutParams().width = whith_;
		loadGif.getLayoutParams().height = height_;
	}
	
	/**
	 * 设置静态图片
	 * @param bitmap
	 * @param width
	 * @param height
	 */
	public void setLoadImageBitmap(Bitmap bitmap,int width,int height){
		loadGif.setImageBitmap(bitmap);
	}
	
	
	/**
	 * 设置面板的背景透明度
	 * @param alpha
	 */
	public void setLoadPanelAlpha(float alpha){
		LinearLayout background = (LinearLayout) loadView.findViewById(R.id.hubu_ll_background);
		background.setAlpha(alpha);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(isHide){
			return super.dispatchTouchEvent(ev);
		}
		return false;
	}
	
	
}
