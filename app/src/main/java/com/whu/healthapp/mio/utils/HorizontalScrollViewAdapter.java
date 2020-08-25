package com.whu.healthapp.mio.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whu.healthapp.R;

import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollViewAdapter
{

	private Context mContext;
	private LayoutInflater mInflater;
	private List<SleepModel> mDatas;

	private String selectedDate;
	
	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
		
	}
	
	public String getSelectedDate() {
		return selectedDate;
	}
	public HorizontalScrollViewAdapter(Context context)
	{
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = new ArrayList<SleepModel>();

	}
	public void setData(List<SleepModel> data) {
		this.mDatas = data;
		//this.notifyDataSetChanged();
	}
	
	public void addData(SleepModel data) {
		this.mDatas.add(0, data);
	}
	
	public void clearData() {
		mDatas.clear();
	}
	
	public int getCount()
	{
		return mDatas.size();
	}

	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		SleepModel item = mDatas.get(position);
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.sleep_listviewitem, parent, false);
			viewHolder.deep = (TextView) convertView
					.findViewById(R.id.deep_values_btn);
			viewHolder.qian = (TextView) convertView
					.findViewById(R.id.qian_values_btn);
			viewHolder.time = (TextView)convertView.findViewById(R.id.textview);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (item.getTime().equals(selectedDate)) {
			viewHolder.time.setText(item.getTime());
			viewHolder.time.setTextColor(Color.rgb(126, 166, 191));
		} else {
		
			viewHolder.time.setText(item.getTime());
		}if (item.getDeepSleepTime() == 0) {
			setHeight(viewHolder.deep, 0);
		}
		if (item.getQianSleepTime() == 0) {
			setHeight(viewHolder.qian, 0);
		}
		
		setHeight(viewHolder.deep, item.getDeepSleepTime());
		setHeight(viewHolder.qian, item.getQianSleepTime());
		
		
		return convertView;
	}

	public View getEmptyView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.sleep_listviewitem, parent, false);
			viewHolder.deep = (TextView) convertView
					.findViewById(R.id.deep_values_btn);
			viewHolder.qian = (TextView) convertView
					.findViewById(R.id.qian_values_btn);
			viewHolder.time = (TextView)convertView.findViewById(R.id.textview);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.time.setText("");
		setHeight(viewHolder.deep, 0);
		setHeight(viewHolder.qian, 0);
		
		return convertView;
	}
	
	private class ViewHolder
	{
		TextView deep;
		TextView qian;
		TextView time;
	}
	public void setHeight(TextView btn,int heightvalues) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btn.getLayoutParams();
		params.height = heightvalues;
		btn.setLayoutParams(params);
	}
}
