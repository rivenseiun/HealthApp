package com.hubu.fan.view.imageview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class AutoAdapterHeightImageView extends ImageView implements OnGlobalLayoutListener {

	private boolean mOnce = true;// 表示图片的初始化只操作一次
	private float mInitScale = 1.0f;// 缩放的初始值
	private float mMaxScale = 1.0f;// 缩放的最大值

	private Matrix mMatrix;// 对图片操作的举证

	public AutoAdapterHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		// init
		mMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);
	}

	public AutoAdapterHeightImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AutoAdapterHeightImageView(Context context) {
		this(context, null);
	}

	@Override
	public void onGlobalLayout() {

		if (mOnce) {
			
			setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			// 获取控件的宽和高
			int width = getWidth();
			
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
			if (dw > width ) {
				scale = width * 1.0f / dw;
			}

			/*
			 * 得到图片缩放的初始化值
			 */
			mInitScale = scale;
			mMaxScale = scale * 4;
			// mMidScale = scale * 2;

			getLayoutParams().height = (int) (dh*scale);
			// 设置图片居中显示
			int x = (width - dw) / 2;
			mMatrix.postTranslate(x, (getLayoutParams().height - dh)/2);
			mMatrix.postScale(mInitScale, mInitScale, width / 2, getLayoutParams().height / 2);
			setImageMatrix(mMatrix);
			mOnce = false;
			
		}

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

}
