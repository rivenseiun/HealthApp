package com.whu.healthapp.bbs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.bean.forum.CommentItem;

import java.util.List;


public class CommentListViewAdapter extends BaseAdapter {

	private static final String TAG = "CommentListViewAdapter";
	private LayoutInflater inflater;
	private Context context;
	private int number;
	private List<CommentItem> commentItems;
	private String text;
	private String str;

	public CommentListViewAdapter(Context context,List<CommentItem> commentItems)  {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.commentItems=commentItems;
	}

	@Override
	public int getCount() {

		return commentItems.size();
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

		private TextView mTextView;

	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listview_comment_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_comment_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		CommentItem commentItem=commentItems.get(arg0);
		String id=commentItem.getID();
		String comment=commentItem.getComment();

		text = id+":"+comment;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			str = String.valueOf(ch);
			if (str.equals(":")) {
				number = i;
				break;
			}
		}

		SpannableStringBuilder builder = new SpannableStringBuilder(text);

		ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.parseColor("#336699"));
		builder.setSpan(redSpan, 0, number, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		viewHolder.mTextView.setText(builder);
		return convertView;
	}
}
