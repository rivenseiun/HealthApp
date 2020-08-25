package com.hubu.fan.utils.listview;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author fan 万能ViewHolder
 */
public class AlmightyViewHolder {

	private SparseArray<View> mViews;
	private View mConvertView;
	private Context mContext;
	private int mPosition;


	public AlmightyViewHolder(Context context, int layoutId, ViewGroup parent,
			int position) {
		mViews = new SparseArray<View>();
		mContext = context;
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		mConvertView.setTag(this);
		mPosition = position;
	}

	public View getView(int id) {
		View view = mViews.get(id);
		if (view == null) {
			view = addView(id);
			return view;
		}
		return view;
	}

	/**
	 * 向viewHolder添加控件
	 * 
	 * @param viewId
	 * @return
	 */
	public View addView(int viewId) {
		View view = mConvertView.findViewById(viewId);
		if (view != null) {
			mViews.put(viewId, view);
		}
		return view;
	}

	public static AlmightyViewHolder getViewHolder(Context context,
			int layoutId, ViewGroup parent, View convertView, int position) {
		if (convertView == null) {
			return new AlmightyViewHolder(context, layoutId, parent, position);
		} else {
			AlmightyViewHolder viewHolder = (AlmightyViewHolder) convertView
					.getTag();
			viewHolder.setPosition(position);
			return viewHolder;
		}
	}

	public View getConvertView() {
		return mConvertView;
	}

	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}

	public int getPosition() {
		return mPosition;
	}

	/**
	 * 为TextView设置文本类容
	 * 
	 * @param text
	 * @param id
	 */
	public void setTextViewText(String text, int id) {
		try {
			TextView tv = (TextView) getView(id);
			tv.setText(text);
		} catch (Exception e) {
			System.out.println("不能将类型转换为TextView");
			e.printStackTrace();
		}

	}

	/**
	 * 为图片设置图像，没有放入缓冲区，需手动放入缓冲区
	 * 
	 * @param bitmap
	 * @param id
	 */
	public void setImageViewBitmap(Bitmap bitmap, int id) {
		try {
			ImageView tv = (ImageView) getView(id);
			tv.setImageBitmap(bitmap);
		} catch (Exception e) {
			System.out.println("不能将类型转换为ImageView");
			e.printStackTrace();
		}
	}

	

}
