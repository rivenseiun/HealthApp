package com.whu.healthapp.mio.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.mio.utils.HorizontalScrollViewAdapter;
import com.whu.healthapp.mio.utils.SleepModel;
import com.whu.healthapp.mio.view.MyHorizontalScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class PositionFragment extends Fragment implements OnClickListener  {

	private TextView sleepTime,deepsleep,qiansleep,title;
	private TextView rushuiTime,xinglaiTime,qingxingTime;
	private ImageView sanjiao;


	private MyHorizontalScrollView mHorizontalScrollView;
	private HorizontalScrollViewAdapter myAdapter;

	private List<SleepModel> data = new ArrayList<SleepModel>();
	private ArrayList<String> time = new ArrayList<String>();
	private int pencent[] = { 50, 56, 78, 35, 57, 78, 45, 26, 44, 60, 56, 78,
			35, 57, 78, 45, 26, 44, 60, 56, 78, 35, 57, 78 };
	private ImageView choose_date;

	private LinearLayout content;


	private String selectedDate;

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.shuimian, container,false);
		initView(rootView);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		Log.e("tag", "position:onAttach");
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("tag", "position:onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("tag", "position:onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.e("tag", "position:onPause");
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.e("tag", "position:onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.e("tag", "position:onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onClick(View view) {
	}
	private void initView(View rootView) {
		title = (TextView)rootView.findViewById(R.id.title);
		mHorizontalScrollView  = (MyHorizontalScrollView)rootView.findViewById(R.id.listview);
		deepsleep = (TextView)rootView.findViewById(R.id.deepsleep);
		qiansleep = (TextView)rootView.findViewById(R.id.qiansleep);
		sleepTime = (TextView)rootView.findViewById(R.id.sleepTime);
		qingxingTime = (TextView)rootView.findViewById(R.id.qingxingTime);
		rushuiTime = (TextView)rootView.findViewById(R.id.rushuiTime);
		xinglaiTime = (TextView)rootView.findViewById(R.id.xinglaiTime);
		title = (TextView)rootView.findViewById(R.id.title);
		choose_date = (ImageView)rootView.findViewById(R.id.choose_date);
		choose_date.setVisibility(View.GONE);
		choose_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				View v = getActivity().getLayoutInflater().inflate(R.layout.calendar_dialog, null);

				final AlertDialog dlg = new AlertDialog.Builder(getActivity()).setTitle("查找的时间").setView(v)
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
							}
						}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
							}
						}).create();

				CalendarView staCalendar = (CalendarView)v.findViewById(R.id.calendar_view);
				Calendar month = Calendar.getInstance();
				month.add(Calendar.MONTH, -1);

				Calendar thisMonth = Calendar.getInstance();
				thisMonth.add(Calendar.MONTH, 1);

				staCalendar.setMinDate(month.getTimeInMillis());
				staCalendar.setDate(new Date().getTime());
				staCalendar.setMaxDate(new Date().getTime());


				staCalendar.setOnDateChangeListener(new OnDateChangeListener() {

					@Override
					public void onSelectedDayChange(CalendarView view, int year, int month,
													int day) {
						int realMonth=month+1;
						String selectedDateStr=year+"-"+realMonth+"-"+day;

						selectedDate = realMonth + "月" + day +"日";
						title.setText(selectedDate +"睡眠情况");
						selectedDate = realMonth + "/"+day;
						myAdapter.setSelectedDate(selectedDate);
						//mHorizontalScrollView.invalidate();
					}
				});

				dlg.show();
			}
		});
		sanjiao = (ImageView)rootView.findViewById(R.id.sanjiao);


		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");

		myAdapter = new HorizontalScrollViewAdapter(getActivity());

		myAdapter.setSelectedDate(sdf2.format(new Date()));
		if (data.size() == 0) {
			initData();
		}

		deepsleep.setText(data.get(time.size() - 1).getDeepSleepTime() / 20+"小时");
		qiansleep.setText(data.get(time.size() - 1).getQianSleepTime() / 20 +"小时");
		sleepTime.setText(data.get(time.size() - 1).getDeepSleepTime() / 20 + data.get(time.size() - 1).getQianSleepTime() / 20 +"小时");
		qingxingTime.setText(24 - (data.get(time.size() - 1).getDeepSleepTime() / 20 + data.get(time.size() - 1).getQianSleepTime() / 20 )+"小时");
		rushuiTime.setText("20:00");
		int x = 20 + data.get(time.size() - 1).getDeepSleepTime() / 20 + data.get(time.size() - 1).getQianSleepTime() / 20 - 24;
		xinglaiTime.setText(String.valueOf(x)+"点");

		myAdapter.setData(data);

		mHorizontalScrollView.setOnItemClickListener(new MyHorizontalScrollView.OnItemClickListener() {
			@Override
			public void onClick(View view, int pos) {
				Log.d("size", data.size()+" ");
				Log.d("pos", pos+" ");
				Log.d("tagggg", data.get(data.size() - 5 + pos).getTime()+" ");
				if (initialView.equals(view)) {

				} else {

					TextView t = (TextView) initialView.findViewById(R.id.textview);
					ImageView triangle = (ImageView) initialView.findViewById(R.id.sanjiao);
					triangle.setVisibility(View.GONE);
					Log.d("adsasd", t.getText()+" ");
					t.setTextColor(Color.rgb(187, 187, 187));

					selectedDate = data.get(data.size() - 5 + pos).getTime();
					title.setText(selectedDate +"睡眠情况");

					initialView = view;
				}
			}
		});
		mHorizontalScrollView.initDatas(myAdapter);
		initialView = myAdapter.getView(myAdapter.getCount() - 1, null, mHorizontalScrollView.getmContainer());

	}
	View initialView;

	private void initData() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		String today = sdf.format(new Date());
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd");
		for (int i =0;i < 5 ;i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, i-4);

			time.add(sdf2.format(cal.getTime()));
		}
		Log.d("sd", title.getText()+" ");
		title.setText(today+"睡眠情况");
		for (int i = 0;i < time.size();i++) {
			SleepModel model = new SleepModel();
			model.setTime(time.get(i));
			int random1 = (int)(Math.random() * 200) + 50;
			int random2 = (int)(Math.random() * 200) + 50;
			Log.d("tag", random1 + "   "+random2);
			model.setDeepSleepTime(random1);
			model.setQianSleepTime(random2);
			data.add(model);
		}
	}
}
