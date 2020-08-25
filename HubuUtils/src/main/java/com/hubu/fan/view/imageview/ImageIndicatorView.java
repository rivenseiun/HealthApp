package com.hubu.fan.view.imageview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hubu.fan.app.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/**
 * 用户指引,宣传画控件(类似于Gallery效果)
 * 
 * @author fan
 * 
 */
public class ImageIndicatorView extends RelativeLayout {
	/**
	 * ViewPager控件 
	 */
	private ViewPager viewPager;
	/**
	 * 指示器容器
	 */
	private LinearLayout indicateLayout;


	

	/**
	 * 页面列表
	 */
	private List<View> viewList = new ArrayList<View>();

	private Handler refreshHandler;

	/**
	 * 滑动位置通知回调监听对象
	 */
	private OnItemChangeListener onItemChangeListener;

	/**
	 * 单个界面点击回调监听对象
	 */
	private OnItemClickListener onItemClickListener;
	/**
	 * 总页面条数
	 */
	private int totelCount = 0;
	/**
	 * 当前页索引
	 */
	private int currentIndex = 0;
	

	/**
	 * 圆形列表+箭头提示器
	 */
	public static final int INDICATE_ARROW_ROUND_STYLE = 0;

	/**
	 * 操作导引提示器
	 */
	public static final int INDICATE_USERGUIDE_STYLE = 1;

	/**
	 * INDICATOR样式
	 */
	private int indicatorStyle = INDICATE_ARROW_ROUND_STYLE;

	/**
	 * 最近一次划动时间
	 */
	private long refreshTime = 0l;

	/**
	 * 广告位置监听接口
	 */
	public interface OnItemChangeListener {
		void onPosition(int position, int totalCount);
	}

	/**
	 * 条目点击事件监听接口
	 */
	public interface OnItemClickListener {
		void OnItemClick(View view, int position);
	}

	public ImageIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init(context);
	}

	public ImageIndicatorView(Context context) {
		super(context);
		this.init(context);
	}
	
	/**
	 * @param context
	 */
	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.hubu_image_indicator_layout, this);
		this.viewPager = (ViewPager) findViewById(R.id.hubu_view_pager);
		this.indicateLayout = (LinearLayout) findViewById(R.id.hubu_indicater_layout);


		this.viewPager.setOnPageChangeListener(new PageChangeListener());

		this.refreshHandler = new ScrollIndicateHandler(ImageIndicatorView.this);
		adHandler =new AdsHandler(this);
	}

	/**
	 * 取ViewPager实例
	 * 
	 * @return
	 */
	public ViewPager getViewPager() {
		return viewPager;
	}

	/**
	 * 取当前位置Index值
	 */
	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * 取总VIEW数目
	 */
	public int getTotalCount() {
		return this.totelCount;
	}

	/**
	 * 取最近一次刷新时间
	 */
	public long getRefreshTime() {
		return this.refreshTime;
	}

	/**
	 * 添加单个View
	 * 
	 * @param view
	 */
	public void addViewItem(View view) {
		final int position = viewList.size();
		view.setOnClickListener(new ItemClickListener(position));
		this.viewList.add(view);
	}

	/**
	 * 条目点击事件监听类
	 */
	private class ItemClickListener implements OnClickListener {
		private int position = 0;

		public ItemClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			if (onItemClickListener != null) {
				onItemClickListener.OnItemClick(view, position);
			}
		}
	}

	/**
	 * 设置显示图片Drawable数组
	 * 
	 * @param resArray
	 *            Drawable数组
	 */
	public void setupLayoutByDrawable(final Integer resArray[]) {
		if (resArray == null)
			throw new NullPointerException();

		this.setupLayoutByDrawable(Arrays.asList(resArray));
	}

	/**
	 * 设置显示图片Drawable列表
	 * 
	 * @param resList
	 *            Drawable列表
	 */
	public void setupLayoutByDrawable(final List<Integer> resList) {
		if (resList == null)
			throw new NullPointerException();

		final int len = resList.size();
		if (len > 0) {
			for (int index = 0; index < len; index++) {
				final View pageItem = new ImageView(getContext());
				pageItem.setBackgroundResource(resList.get(index));
				addViewItem(pageItem);
			}
		}
	}

	/**
	 * 设置当前显示项
	 * 
	 * @param index
	 *            postion
	 */
	public void setCurrentItem(int index) {
		this.currentIndex = index;
	}

	

	/**
	 * 添加位置监听回调
	 * 
	 * @param onGuideListener
	 */
	public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
		if (onItemChangeListener == null) {
			throw new NullPointerException();
		}
		this.onItemChangeListener = onItemChangeListener;
	}

	/**
	 * 设置条目点击监听对象
	 * 
	 * @param onItemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * 显示
	 */
	public void show(int time) {
		this.adTime = time;
		this.totelCount = viewList.size();
		final LayoutParams params = (LayoutParams) indicateLayout.getLayoutParams();
		if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle) {// 操作指引
			params.bottomMargin = 45;
		}
		this.indicateLayout.setLayoutParams(params);
		// 初始化指示器
		for (int index = 0; index < this.totelCount; index++) {
			final View indicater = new ImageView(getContext());
			this.indicateLayout.addView(indicater, index);
		}
		this.refreshHandler.sendEmptyMessage(currentIndex);
		// 为ViewPager配置数据
		this.viewPager.setAdapter(new MyPagerAdapter(this.viewList));
		this.viewPager.setCurrentItem(Integer.MAX_VALUE/2 - Integer.MAX_VALUE/2%viewList.size(), false);
		adHandler.sendEmptyMessageDelayed(time, time);
		
	}
	
	private int adTime = 0 ; 
	
	/**
	 * 轮播的handler
	 */
	private  AdsHandler adHandler ;
	
	private static class AdsHandler extends Handler{
		private WeakReference<ImageIndicatorView> mView ;
		public AdsHandler(ImageIndicatorView view) {
			mView = new WeakReference<ImageIndicatorView>(view);
		}
		@Override
		public void handleMessage(Message msg) {
			//用弱连接会有空指针的危险
			if(mView.get() == null || mView.get().viewPager == null ){
				return ;
			}
			mView.get().viewPager.setCurrentItem(mView.get().getCurrentIndex()+1);
			this.sendEmptyMessageDelayed(msg.what, msg.what);
		}
	}
	
	/* 
		释放连接，防止内存泄漏
	 */
	protected void onDetachedFromWindow() {
		adHandler.removeMessages(adTime);
	};


	/**
	 * 页面变更监听
	 */
	private class PageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int index) {
			currentIndex = index;
			refreshHandler.sendEmptyMessage(currentIndex);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	}

	/**
	 * 刷新提示器
	 */
	protected void refreshIndicateView() {
		this.refreshTime = System.currentTimeMillis();
		//设置指示图标
		for (int index = 0; index < totelCount; index++) {
			final ImageView imageView = (ImageView) this.indicateLayout.getChildAt(index);
			if (this.currentIndex%viewList.size() == index) {
				imageView.setBackgroundResource(R.drawable.hubu_image_indicator_focus);
			} else {
				imageView.setBackgroundResource(R.drawable.hubu_image_indicator);
			}
		}
		//
		if (this.onItemChangeListener != null) {// 页面改更了
			try {
				this.onItemChangeListener.onPosition(this.currentIndex, this.totelCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 滚动刷新指示器的handler
	 * ScrollIndicateHandler
	 */
	private static class ScrollIndicateHandler extends Handler {
		private final WeakReference<ImageIndicatorView> scrollIndicateViewRef;

		public ScrollIndicateHandler(ImageIndicatorView scrollIndicateView) {
			this.scrollIndicateViewRef = new WeakReference<ImageIndicatorView>(
					scrollIndicateView);

		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ImageIndicatorView scrollIndicateView = scrollIndicateViewRef.get();
			if (scrollIndicateView != null) {
				scrollIndicateView.refreshIndicateView();
			}
		}
	}

	private class MyPagerAdapter extends PagerAdapter {
		private List<View> pageViews = new ArrayList<View>();

		public MyPagerAdapter(List<View> pageViews) {
			this.pageViews = pageViews;
		}

		@Override
		public int getCount() {
			//获得无线右滑效果
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1 % pageViews.size()));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			View view = pageViews.get(arg1%pageViews.size());
			((ViewPager) arg0).addView(view);
			return view;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	
	

}
