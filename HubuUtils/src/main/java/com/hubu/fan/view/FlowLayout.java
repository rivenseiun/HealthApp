package com.hubu.fan.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// init
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowLayout(Context context) {
		this(context, null);
	}

	/**
	 * 存储所有的View
	 */
	private List<List<View>> mAllViews = new ArrayList<List<View>>();
	/**
	 * 存放每行的高度
	 */
	private List<Integer> mLineHeight = new ArrayList<Integer>();

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mAllViews.clear();
		mLineHeight.clear();

		// 当前ViewGroup的宽度
		int width = getWidth();

		// 设置行高和行宽
		int lineWidth = 0;
		int lineHeight = 0;

		// 每一行的控件
		List<View> lineViews = new ArrayList<View>();

		// 获取子元素的个数（用于遍历）
		int cCount = getChildCount();

		// 获取所有控件和没行的高度
		for (int i = 0; i < cCount; i++) {

			// 获取子元素
			View childView = getChildAt(i);
			// 获取子元素的参数
			MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

			// 如果这个控件在此行容纳不了
			if (lineWidth + childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
				// 添加此行控件
				mAllViews.add(lineViews);
				mLineHeight.add(lineHeight);
				// 初始化每一行的值
				lineWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
				lineHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
				lineViews = new ArrayList<View>();

			} else {// 控件在一行
					// 设置行宽和行高
				lineWidth += childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
				lineHeight = Math.max(lineHeight, childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
			}
			lineViews.add(childView);
			if (i == cCount - 1) {// 添加最后一行的控件
				mAllViews.add(lineViews);
				mLineHeight.add(lineHeight);
			}
		}

		// 用于定位控件
		int left = getPaddingLeft();
		int top = getPaddingRight();

		// 获取行数
		int lines = mAllViews.size();
		for (int i = 0; i < lines; i++) {
			// 获取一行的控件
			lineViews = mAllViews.get(i);
			// 获取每一行控件的数量
			int nums = lineViews.size();

			for (int j = 0; j < nums; j++) {
				// 获取子元素
				View viewChild = lineViews.get(j);
				// 判断view的状态
				if (viewChild.getVisibility() == View.GONE) {
					continue;
				}

				// 获取子元素的参数
				MarginLayoutParams lp = (MarginLayoutParams) viewChild.getLayoutParams();

				// 对子元素布局
				int lc = left + lp.leftMargin;
				int rc = lc + viewChild.getMeasuredWidth();
				int tc = top + lp.topMargin;
				int bc = tc + viewChild.getMeasuredHeight();

				left = rc + lp.rightMargin;
				viewChild.layout(lc, tc, rc, bc);
			}

			left = getPaddingLeft();
			top += mLineHeight.get(i);

		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// 获取控件传入的测量值和测量模式
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

		// wrapcontent
		int width = 0;
		int height = 0;// 存储wrapcontent的宽度和高度

		int lineWidth = 0;
		int lineHeight = 0;// 记录每一行的高度和宽度

		int cCount = getChildCount();// 得到内部元素的个数

		for (int i = 0; i < cCount; i++) {
			// 拿到子View
			View child = getChildAt(i);

			// 测量子View的宽和高
			measureChild(child, widthMeasureSpec, heightMeasureSpec);
			// 得到子View的
			MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

			// 子View占据的宽和高
			int childWidth = child.getMeasuredWidth()+ lp.leftMargin + lp.rightMargin;
			int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

			if (lineWidth + childWidth > sizeWidth) {
				// 对比得到最大的宽度
				width = Math.max(width, lineWidth);
				// 得到新行
				lineWidth = childWidth;
				// 得到行高
				height += lineHeight;
				lineHeight = childHeight;
			} else {// 未换行的情况
					// 叠加行宽
				lineWidth += childWidth;
				// 得到当前行最大的高度
				lineHeight = Math.max(lineHeight, childHeight);
			}

			if (i == cCount - 1) {
				width = Math.max(width, lineWidth);
				height += lineHeight;
			}

		}

		setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
				modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom());

	}

	/*
	 * 
	 * 与当前group对应的layoutparams
	 */
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

}
