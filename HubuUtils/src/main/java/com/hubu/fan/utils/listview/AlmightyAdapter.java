package com.hubu.fan.utils.listview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public abstract class AlmightyAdapter<T> extends ArrayAdapter<T>{
	
	private Context mContext;
	private int mResource;

	/**
	 * 存放图像缓冲池
	 */

	public AlmightyAdapter(Context context, int resource, List<T> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		// 获取应用程序最大可用内存  
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;  
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlmightyViewHolder viewHolder = AlmightyViewHolder.getViewHolder(mContext, mResource, parent, convertView,position);
		setView(viewHolder,getItem(position));
		
		return viewHolder.getConvertView();
	}
	
	
	/**
	 * 暴露的方法，类似getview
	 * @param viewHolder
	 * @param content
	 */
	public abstract void setView(AlmightyViewHolder viewHolder,T content);

	
	

}
