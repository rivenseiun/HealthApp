package com.whu.healthapp.bbs.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.whu.healthapp.R;
import com.whu.healthapp.bbs.ImageDisplayer;
import com.whu.healthapp.bean.forum.ImageItem;
import com.whu.healthapp.utils.CustomConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jiang.YX on 2016/10/29.
 */
public class ImagePublishAdapter extends BaseAdapter {
    private List<ImageItem> mImageList = new ArrayList<ImageItem>();
    private Context mContext;

    public ImagePublishAdapter(Context context, List<ImageItem> imageList) {
        this.mContext = context;
        this.mImageList = imageList;
    }

    public int getCount() {
        // 多返回一个用于展示添加图标
        if (mImageList == null) {
            return 1;
        } else if (mImageList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return CustomConstants.MAX_IMAGE_SIZE;
        } else {
            return mImageList.size() + 1;
        }
    }

    public Object getItem(int position) {
        if (mImageList != null
                &&mImageList.size() == CustomConstants.MAX_IMAGE_SIZE) {
            return mImageList.get(position);
        }

        else if (mImageList == null || position - 1 < 0
                || position > mImageList.size()) {
            return null;
        } else {
            return mImageList.get(position - 1);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        // 所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
        convertView = View.inflate(mContext, R.layout.item_publish, null);
        ImageView imageIv = (ImageView) convertView.findViewById(R.id.item_grid_image);

        if (isShowAddItem(position)) {
            imageIv.setImageResource(R.drawable.btn_add_pic);
            imageIv.setBackgroundResource(R.color.bg_gray);
        } else {
            final ImageItem item = mImageList.get(position);
            ImageDisplayer.getInstance(mContext).displayBmp(imageIv,
                    item.thumbnailPath, item.sourcePath);
        }

        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = mImageList == null ? 0 : mImageList.size();
        return position == size;
    }

}
