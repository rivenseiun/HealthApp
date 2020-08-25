package com.whu.healthapp.mio.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.mio.utils.Constant;
import com.whu.healthapp.mio.utils.CustomToast;
import com.whu.healthapp.mio.utils.HorizontalScrollViewAdapter;
import com.whu.healthapp.mio.utils.SleepModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyHorizontalScrollView extends HorizontalScrollView implements
		OnClickListener
{
	
	private Dialog dialog;
	private Context context;
	/**
	 * 
	 */
	public interface CurrentImageChangeListener
	{
		void onCurrentImgChanged(String startDate, String endDate);
	}

	/**
	 * 条目点击时的回调
	 * 
	 * 
	 */
	public interface OnItemClickListener
	{
		void onClick(View view, int pos);
	}

	private CurrentImageChangeListener mListener;

	private OnItemClickListener mOnClickListener;

	private static final String TAG = "MyHorizontalScrollView";

	/**
	 * HorizontalListView中的LinearLayout
	 */
	private LinearLayout mContainer = (LinearLayout)getChildAt(0);

	/**
	 * 子元素的宽度
	 */
	private int mChildWidth;
	/**
	 * 子元素的高度
	 */
	private int mChildHeight;
	/**
	 * 当前最后一张图片的index
	 */
	private int mCurrentIndex;
	/**
	 * 当前第一张图片的下标
	 */
	private int mFristIndex;
	/**
	 * 当前第一个View
	 */
	private View mFirstView;
	/**
	 * 数据适配器
	 */
	private HorizontalScrollViewAdapter mAdapter;
	/**
	 * 每屏幕最多显示的个数
	 */
	private int mCountOneScreen;
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWitdh;

	
	/**
	 * 保存View与位置的键值对
	 */
	private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

	private int time = 5;
	
	
	private int tempIndex = 0;
	private int rightTempIndex = 0;
	
	//加载数据的次数
	private int loadingCount = 0;
	
	//每次滑动加载的数据为26条
	private final int DATA_NUM = 26;
	
	private int emptyViewSum;
	
	private List<String> sleepDataList = new ArrayList<String>();
	
//	private Handler handler = new Handler() {
//		
//		public void handleMessage(android.os.Message msg) {
//			Log.d("handler", "handler execute");
//			Looper.loop();
//			if (msg.what == 0x11) {
//				sleepDataList = (ArrayList<String>)msg.obj;
//				Log.d("sleepData", sleepDataList.size()+ "  ");
//			}
//			
//			//Looper.loop();
//		};
//	};
	
	
	public MyHorizontalScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context=context;
		// 获得屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * 加载右侧数据
	 */
	protected void loadNext()
	{
		
		Log.d("currentindex", mCurrentIndex+" " + emptyViewSum + mAdapter.getCount());
		Log.d("firstindex", mFristIndex+" ");
		Log.d("rightindex", rightTempIndex+" ");
		// 数组边界值计算
		if (mCurrentIndex == mAdapter.getCount() - 2 + emptyViewSum)
		{
			return;
		}

		//移除第一View，且将水平滚动位置置0
		scrollTo(0, 0);
		mViewPos.remove(mContainer.getChildAt(0));
		mContainer.removeViewAt(0);
		tempIndex++;
		//View不够则获取空VIEW
		if (mAdapter.getCount() < mCurrentIndex) {
			//获取下一空View，并且设置onclick事件，且加入容器中
			View view = mAdapter.getEmptyView(++rightTempIndex, null, mContainer);
			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewPos.put(view, rightTempIndex);
			//当前第一张图片小标
			mFristIndex++;
			mCurrentIndex++;
		} else  {
			mCurrentIndex++;
			rightTempIndex++;
			if (mAdapter.getCount() <= mCurrentIndex) {
				//获取下一空View，并且设置onclick事件，且加入容器中
				View view = mAdapter.getEmptyView(rightTempIndex, null, mContainer);
				view.setOnClickListener(this);
				mContainer.addView(view);
				mViewPos.put(view, rightTempIndex);
				//当前第一张图片小标
				mFristIndex++;
			} else {
				//获取下一View，并且设置onclick事件，且加入容器中
				View view = mAdapter.getView(rightTempIndex+mAdapter.getCount() - 5, null, mContainer);
				view.setOnClickListener(this);
				mContainer.addView(view);
				mViewPos.put(view, rightTempIndex);
				//当前第一张图片小标
				mFristIndex++;
			}
		}
		//如果设置了滚动监听则触发


	}
	/**
	 * 加载左侧数据
	 */
	protected void loadPre()
	{
		
		//获得当前应该显示为第一张图片的下标
		int index = mCurrentIndex - mCountOneScreen;
		Log.d("firstindex", mFristIndex+" ");
		Log.d("currentindex", mCurrentIndex+" ");
		Log.d("index", index+" ");
		Log.d("righttemp", rightTempIndex+" ");
		//还没有移到最左侧时的操作
		if (index >= 0)
		{
			//移除最后一条
			int oldViewPos = mContainer.getChildCount() - 1;
			mViewPos.remove(mContainer.getChildAt(oldViewPos));
			mContainer.removeViewAt(oldViewPos);
			tempIndex--;
			//将此View放入第一个位置
			View view = mAdapter.getView(index, null, mContainer);
			mViewPos.put(view, tempIndex);
			mContainer.addView(view, 0);
			view.setOnClickListener(this);
			//水平滚动位置向左移动view的宽度个像素
			scrollTo(mChildWidth, 0);
			//当前位置--，当前第一个显示的下标--
			
			mCurrentIndex--;
			rightTempIndex--;
			mFristIndex--;

		} 
		else {
			Log.d("sleepdatalist", sleepDataList.size()+" ");
			//添加数据
			if (sleepDataList.size() != 0) {
			
				SleepModel model = new SleepModel();
				SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, 0 - time);
				time++;
				Log.d("time", time+" ");
				model.setTime(sdf2.format(cal.getTime()));
				model.setDeepSleepTime(Integer.parseInt(sleepDataList.get(time - 6).split(",")[0]));
				model.setQianSleepTime(Integer.parseInt(sleepDataList.get(time - 6).split(",")[1]));
				mAdapter.addData(model);
				
				
				//数据加载完毕  且View已经更新完毕
				if (time - sleepDataList.size() == 5) {
					sleepDataList.clear();
					count = -1;
					loadingCount++;
				}
				//移除最后一条
				int oldViewPos = mContainer.getChildCount() - 1;
				mViewPos.remove(mContainer.getChildAt(oldViewPos));
				mContainer.removeViewAt(oldViewPos);
				
				index = 0;
				
				//ViewPos的左边界
				tempIndex--;
				//将此View放入第一个位置
				View view = mAdapter.getView(index, null, mContainer);
				mViewPos.put(view, tempIndex);
				mContainer.addView(view, 0);
				view.setOnClickListener(this);
				//水平滚动位置向左移动view的宽度个像素
				scrollTo(mChildWidth, 0);
				//当前位置重置--，当前第一个显示的下标重置--
				mCurrentIndex++;
				mFristIndex = 0;
				rightTempIndex--;
				if (mCurrentIndex >= mCountOneScreen - 1) {
					mCurrentIndex--;
				}
				mFristIndex--;
			} else {
				if (count == 1 || count == -1) {
					count = 2;
					Log.d("startTda", count+" ");
					//Toast.makeText(getContext(), "加载数据中", 2000).show();
					CustomToast.showToast(getContext(), "加载数据中", 2000);
//					dialog = new MyDialog(context).loadDialog1();
//					dialog.show();
				}
				return;
			}
		}
	}

	/**
	 * 滑动时的回调
	 */
	public void notifyCurrentImgChanged(String startDate,String endDate)
	{
		
		mListener.onCurrentImgChanged(startDate,endDate);
	}

	/**
	 * 初始化数据，设置数据适配器
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter)
	{
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);
		if (this.mAdapter.getCount() == 0) {
			return;
		}
		
		// 获得适配器中第一个View
		final View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);

		// 强制计算当前View的宽和高
		if (mChildWidth == 0 && mChildHeight == 0)
		{
			int w = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			int h = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			view.measure(w, h);
			mChildHeight = view.getMeasuredHeight();
			mChildWidth = view.getMeasuredWidth();
			//Log.e(TAG, view.getMeasuredWidth() + "," + view.getMeasuredHeight());
			// 计算每次加载多少个View
			mCountOneScreen = (mScreenWitdh / mChildWidth == 0)?mScreenWitdh / mChildWidth+1:mScreenWitdh / mChildWidth+2;

			Log.e(TAG, "mCountOneScreen = " + mCountOneScreen
					+ " ,mChildWidth = " + mChildWidth);
			

		}
		//初始化第一屏幕的元素
		initFirstScreenChildren(mCountOneScreen);
	}


	/**
	 * 加载第一屏的View
	 * 
	 * @param mCountOneScreen
	 */
	public void initFirstScreenChildren(int mCountOneScreen)
	{
		
		
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewPos.clear();
		Log.d("initfirst", mContainer.getChildCount()+" ");
		//scrollBy(-mScreenWitdh / 2, 0);
		int temp = mCountOneScreen;
		if (mCountOneScreen > mAdapter.getCount()) {
			temp = mAdapter.getCount();
		
			for (int i = 0; i < temp; i++)
			{
				View view = mAdapter.getView(i, null, mContainer);
				view.setOnClickListener(this);
				mContainer.addView(view);
				mViewPos.put(view, i);
				mCurrentIndex = i;
				rightTempIndex = i;
			}
			//初始化后面的空数据
			for (int j = temp;j < mCountOneScreen;j++) {
				View emptyView = mAdapter.getEmptyView(j, null, mContainer);
				//view.setOnClickListener(this);
				mContainer.addView(emptyView);
				mViewPos.put(emptyView, j);
				mCurrentIndex = j;
				rightTempIndex = j;
				emptyViewSum++;
			}
		}

	}

	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (Constant.PHONE_NUMBER == null) {
			CustomToast.showToast(getContext(), "您还未登录，请先登录", 2000);
			
		} else {
		
		switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				//Log.e(TAG, getScrollX() + "");
	
				int scrollX = getScrollX();
				// 如果当前scrollX为view的宽度，加载下一张，移除第一张
				if (scrollX >= mChildWidth)
				{
					Log.d("tag", "right");
					loadNext();
				}
				// 如果当前scrollX = 0，  到了最左边
				if (scrollX == 0)
				{
					Log.d("tag", "left");
					
					loadPre();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal2 = Calendar.getInstance();
					cal2.add(Calendar.DATE, -5 - loadingCount * DATA_NUM);
					endDate = sdf.format(cal2.getTime());
					
					cal2.add(Calendar.DATE, -25 - loadingCount * DATA_NUM);
					startDate = sdf.format(cal2.getTime()); 
					if (mListener != null && mCurrentIndex - mCountOneScreen < 0) {
						//sleepDataList.clear();
						notifyCurrentImgChanged(startDate,endDate);
					}
				}
				break;
		}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void onClick(View v)
	{
		if (mOnClickListener != null)
		{
			//TextView deep = (TextView) v.findViewById(R.id.deep_values_btn);
			//TextView qian = (TextView) v.findViewById(R.id.qian_values_btn);
			if (mAdapter.getCount() - 5 + mViewPos.get(v) < mAdapter.getCount()) {
				TextView time = (TextView)v.findViewById(R.id.textview);
				time.setTextColor(Color.rgb(126, 166, 191));
				ImageView sanjiao = (ImageView)v.findViewById(R.id.sanjiao);
				sanjiao.setVisibility(View.VISIBLE);
				
				mOnClickListener.onClick(v, mViewPos.get(v));
			}
		}
	}

	
	public void setOnItemClickListener(OnItemClickListener mOnClickListener)
	{
		this.mOnClickListener = mOnClickListener;
	}

	public void setCurrentImageChangeListener(
			CurrentImageChangeListener mListener)
	{
		this.mListener = mListener;
	}
	
	
	
	public LinearLayout getmContainer() {
		
		return mContainer;
	}
	
	public void clearView () {
		mViewPos.clear();
	}
	
	private String startDate;
	private String endDate;
	

	public void setSleepDataList(List<String> sleepDataList) {
		
		this.sleepDataList = sleepDataList;
		if (sleepDataList.size() == 0) {
			CustomToast.showToast(getContext(), "没有更多数据了", 2000);
		}
	}
	
	public List<String> getSleepDataList() {
		return sleepDataList;
	}
	private int count = 1;
	
	public int getCount() {
		return count;
	}
	//private String startDate = 
	
	public int getLoadingCount() {
		return loadingCount;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	public void setLoadingCount(int loadingCount) {
		this.loadingCount = loadingCount;
	}
	
	public int getTime() {
		return time;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public void setTempIndex(int tempIndex) {
		this.tempIndex = tempIndex;
	}
	
	public void setEmptyViewSum(int emptyViewSum) {
		this.emptyViewSum = emptyViewSum;
	}
}
