package com.whu.healthapp.bbs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.bbs.adapter.ImageGridAdapter;
import com.whu.healthapp.bean.forum.ImageItem;
import com.whu.healthapp.utils.CustomConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageChooseActivity extends AppCompatActivity {

    private List<ImageItem> mDataList = new ArrayList<ImageItem>();
    private String mBucketName;
    private int availableSize;
    private GridView mGridView;
    private TextView mBucketNameTv;
    private TextView cancelTv;
    private ImageGridAdapter mAdapter;
    private Button mFinishBtn;
    private HashMap<String, ImageItem> selectedImgs = new HashMap<String, ImageItem>();

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_choose);

        mDataList = (List<ImageItem>) getIntent().getSerializableExtra(
                IntentConstants.EXTRA_IMAGE_LIST);
        if (mDataList == null) mDataList = new ArrayList<ImageItem>();
        mBucketName = getIntent().getStringExtra(
                IntentConstants.EXTRA_BUCKET_NAME);

        if (TextUtils.isEmpty(mBucketName))
        {
            mBucketName = "请选择";
        }
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);

        initView();
        initListener();

    }

    private void initView()
    {
        mBucketNameTv = (TextView) findViewById(R.id.title);
        mBucketNameTv.setText(mBucketName);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImageGridAdapter(ImageChooseActivity.this, mDataList);
        mGridView.setAdapter(mAdapter);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        cancelTv = (TextView) findViewById(R.id.action);


        mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                + availableSize + ")");
        mAdapter.notifyDataSetChanged();
    }

    private void initListener()
    {
        mFinishBtn.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                Intent intent = new Intent(ImageChooseActivity.this,
                        NewArticleActivity.class);
                intent.putExtra(
                        IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) new ArrayList<ImageItem>(selectedImgs
                                .values()));
                startActivity(intent);
                ImageChooseActivity.this.finish();
                finish();
            }

        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                ImageItem item = mDataList.get(position);
                if (item.isSelected)
                {
                    item.isSelected = false;
                    selectedImgs.remove(item.imageId);
                }
                else
                {
                    if (selectedImgs.size() >= availableSize)
                    {
                        Toast.makeText(ImageChooseActivity.this,
                                "最多选择" + availableSize + "张图片",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    item.isSelected = true;
                    selectedImgs.put(item.imageId, item);
                }

                mFinishBtn.setText("完成" + "(" + selectedImgs.size() + "/"
                        + availableSize + ")");
                mAdapter.notifyDataSetChanged();
            }

        });

        cancelTv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ImageChooseActivity.this,
                        NewArticleActivity.class);
                intent.putExtra("clear",0);
                ImageChooseActivity.this.finish();
                startActivity(intent);
            }
        });

    }
}
