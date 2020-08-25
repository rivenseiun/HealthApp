package com.whu.healthapp.bbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.whu.healthapp.R;
import com.whu.healthapp.bbs.adapter.ImageBucketAdapter;
import com.whu.healthapp.bean.forum.ImageBucket;
import com.whu.healthapp.utils.CustomConstants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageBucketChooseActivity extends AppCompatActivity {

    private ImageFetcher mHelper;
    private List<ImageBucket> mDataList = new ArrayList<ImageBucket>();
    private ListView mListView;
    private ImageBucketAdapter mAdapter;
    private int availableSize;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_bucket_choose);

        mHelper = ImageFetcher.getInstance(getApplicationContext());
        initData();
        initView();
    }

    private void initData()
    {
        mDataList = mHelper.getImagesBucketList(false);
        availableSize = getIntent().getIntExtra(
                IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                CustomConstants.MAX_IMAGE_SIZE);
    }

    private void initView()
    {
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new ImageBucketAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);
        TextView titleTv  = (TextView) findViewById(R.id.title);
        titleTv.setText("相册");
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                selectOne(position);

                Intent intent = new Intent(ImageBucketChooseActivity.this,
                        ImageChooseActivity.class);
                intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                        (Serializable) mDataList.get(position).imageList);
                intent.putExtra(IntentConstants.EXTRA_BUCKET_NAME,
                        mDataList.get(position).bucketName);
                intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                        availableSize);
                ImageBucketChooseActivity.this.finish();
                startActivity(intent);
            }
        });

        TextView cancelTv = (TextView) findViewById(R.id.action);
        cancelTv.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ImageBucketChooseActivity.this,
                        NewArticleActivity.class);
                ImageBucketChooseActivity.this.finish();
                startActivity(intent);
            }
        });
    }

    private void selectOne(int position)
    {
        int size = mDataList.size();
        for (int i = 0; i != size; i++)
        {
            if (i == position) mDataList.get(i).selected = true;
            else
            {
                mDataList.get(i).selected = false;
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
