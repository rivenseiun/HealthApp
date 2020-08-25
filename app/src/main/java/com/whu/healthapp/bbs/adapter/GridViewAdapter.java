package com.whu.healthapp.bbs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.whu.healthapp.R;

import java.util.List;


public class GridViewAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<String> res;

	public GridViewAdapter(Context context, List<String> res) {
		this.context = context;
		this.res=res;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		if (res.size() > 9) {
			return 9;
		} else {
			return res.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public class ViewHolder {

		private ImageView mImageView;

	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {

		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.bbs_gridview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_gridview_img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		final String imgRes=res.get(arg0);
		//viewHolder.mImageView.setImageResource(R.drawable.image_default);//测试使用默认图片


		//正式使用时，使用以下方法获取图片，路径为从服务器解析获得的图片网址
		Picasso.with(context).load(imgRes).resize(200,200)
				.centerCrop().into(viewHolder.mImageView);



		viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

			return convertView;
	}



}
