package com.whu.healthapp.forum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.bbs.adapter.GridViewAdapter;
import com.whu.healthapp.bbs.adapter.MainListViewAdapter;
import com.whu.healthapp.bean.forum.ArticleItem;
import com.whu.healthapp.bean.forum.CommentItem;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.forum.entity.ArticleEntity;
import com.whu.healthapp.forum.entity.CommentEntity;
import com.whu.healthapp.view.CommentListView;
import com.whu.healthapp.view.GridView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static com.iflytek.cloud.resource.Resource.setText;

/**
 * Created by wanderlust on 2017/4/22.
 */

public class ForumAdapter extends BaseQuickAdapter<ArticleEntity, ForumAdapter.ViewHolder> {
    Typeface iconfont = HomePagerActivity.getIconfont();
    private LayoutInflater inflater;
    private NineGridImageView<String> mNglContent;
    private ListView mCommentContent;
    private Button commentButton;
    private Button likeButton;
    public static String COMMENT_URL = "http://47.92.55.20:80/Healthy/comment.mvc?action=0";
    public static String LIKE_URL = "http://47.92.55.20:80/Healthy/passage.mvc?action=1";






    public ForumAdapter(List<ArticleEntity> data) {
        super(data);
    }

    public ForumAdapter(int layoutResId, List<ArticleEntity> data) {
        super(layoutResId, data);
    }










    @Override
    protected void convert(final ViewHolder viewHolder, final ArticleEntity item) {
        
        /*
        viewHolder.setText(R.id.forum_item_username, item.getUserName())
                .setText(R.id.forum_item_time, item.getTime())
                .setText(R.id.forum_item_text, item.getText())
                .setText(R.id.forum_item_favor_count, String.valueOf(item.getFavorCount()))
                .setBackgroundRes(R.id.forum_item_comment_btn,R.drawable.bbs_comment_btn)
                .setText(R.id.forum_item_comment_count, String.valueOf(item.getCommentCount()))
                .addOnClickListener(R.id.forum_item_favor_btn)
                .addOnClickListener(R.id.forum_item_comment_btn);*/
        
        viewHolder.setBackgroundRes(R.id.forum_item_comment_btn,R.drawable.bbs_comment_btn);
        
        viewHolder.main_item_content = (TextView) viewHolder.getView(R.id.forum_item_text);
        viewHolder.main_item_id = (TextView) viewHolder.getView(R.id.forum_item_username);
        viewHolder.main_item_time = (TextView) viewHolder.getView(R.id.forum_item_time);
        viewHolder.main_item_articleid = (TextView) viewHolder.getView(R.id.main_item_articleid);
        //viewHolder.mGridView = (GridView) viewHolder.getView(R.id.gv_comment_head);
        viewHolder.likeButton = (Button)viewHolder.getView(R.id.forum_item_favor_btn);
        viewHolder.commentButton = (Button)viewHolder.getView(R.id.forum_item_comment_btn);
        viewHolder.favorCount = (TextView)viewHolder.getView(R.id.forum_item_favor_count);
        viewHolder.commentCount = (TextView)viewHolder.getView(R.id.forum_item_comment_count);
        viewHolder.mImgGridView = (NineGridImageView<String>) viewHolder.getView(R.id.forum_item_nglimages);
        viewHolder.mListView = (CommentListView) viewHolder.getView(R.id.lv_item_listView);

        
        viewHolder.main_item_id.setText(item.getUserName());
        viewHolder.main_item_time.setText(item.getTime());
        viewHolder.main_item_content.setText(item.getText());
        viewHolder.main_item_articleid.setText(item.getArticleId());
        viewHolder.favorCount.setText(String.valueOf(item.getFavorCount()));
        viewHolder.commentCount.setText(String.valueOf(item.getCommentCount()));





        Glide.with(mContext)    //用户头像
                .load(item.getUserAvatar())
                .crossFade()
                .into((ImageView) viewHolder.getView(R.id.forum_item_useravatar));

        //mNglContent = (NineGridImageView<String>) viewHolder.getView(R.id.forum_item_nglimages);
        viewHolder.mImgGridView.setImagesData(item.getimgUrlList());
        NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context mContext, ImageView imageView, String s) {
                Glide.with(mContext).load(s).placeholder(R.drawable.default_loading_image).into(imageView);
            }

            @Override
            protected ImageView generateImageView(Context mContext) {
                return super.generateImageView(mContext);
            }

            @Override
            protected void onItemImageClick(Context mContext, ImageView imageView, int index, List<String> list) {
                Toast.makeText(mContext, "image position is " + index, Toast.LENGTH_SHORT).show();
            }
        };
        viewHolder.mImgGridView.setAdapter(mAdapter);



        //图片九宫格
        if (!item.getimgUrlList().isEmpty()) {
            viewHolder.mImgGridView.setVisibility(View.VISIBLE);

        }

        //评论列表
        mCommentContent = viewHolder.getView(R.id.forum_item_lv_comments);
        final CommentListViewAdapter commentListViewAdapter = new CommentListViewAdapter(mContext, item.getCommentList());
        mCommentContent.setAdapter(commentListViewAdapter);






        //点赞按钮事件
        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestParams params = new RequestParams(LIKE_URL);
                //params.setMultipart(true);



                //预留的部分：上传点赞的文章id
                params.addBodyParameter("articleid", viewHolder.main_item_articleid.getText().toString());

                x.http().post(params, new Callback.CommonCallback<String>(){
                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        //上传成功，则显示的点赞数+1
                        //Toast.makeText(mContext, viewHolder.main_item_articleid.getText().toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(mContext, "点赞成功", Toast.LENGTH_LONG).show();
                        int favor = Integer.parseInt(viewHolder.favorCount.getText().toString());
                        favor++;
                        viewHolder.favorCount.setText(String.valueOf(favor));
                        viewHolder.setBackgroundRes(R.id.forum_item_favor_btn,R.drawable.like_pressed);
                        viewHolder.likeButton.setClickable(false);
                    }
                });






            }
        });

        
        


        //评论按钮事件
        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText comment = new EditText(mContext);
                //弹出评论的对话框
                new AlertDialog.Builder(mContext)
                        .setTitle("发表评论")
                        .setView(comment)
                        .setPositiveButton("评论", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(comment.getText().toString().equals("")){
                                    Toast.makeText(mContext,"评论不能为空！",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    RequestParams params = new RequestParams(COMMENT_URL);
                                    // params.setMultipart(true);




                                    //预留的部分：上传用户id和文章id
                                    params.addBodyParameter("phone", User.getInstance(mContext).getPhone());
                                    params.addBodyParameter("articleid",viewHolder.main_item_articleid.getText().toString());
                                    params.addBodyParameter("content",comment.getText().toString() );

                                    x.http().post(params, new Callback.CommonCallback<String>(){
                                        @Override
                                        public void onFinished() {

                                        }

                                        @Override
                                        public void onCancelled(CancelledException cex) {

                                        }

                                        @Override
                                        public void onError(Throwable ex, boolean isOnCallback) {
                                            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onSuccess(String result) {
                                            Toast.makeText(mContext, viewHolder.main_item_articleid.getText().toString(), Toast.LENGTH_LONG).show();
                                            Toast.makeText(mContext, "评论成功", Toast.LENGTH_LONG).show();
                                            int commentCount = Integer.parseInt(viewHolder.commentCount.getText().toString());
                                            commentCount++;

                                            //如果评论成功，则将刚刚发布的评论添加到文章的评论列表中显示
                                            item.getCommentList().add(new CommentEntity(User.getInstance(mContext).getUserName(),comment.getText().toString()));
                                            commentListViewAdapter.notifyDataSetChanged();
                                        }
                                    });



                                }






                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        });





















    }




    public class ViewHolder extends BaseViewHolder{
        //private GridView mGridView;

        private TextView favorCount;
        private TextView commentCount;
        private Button commentButton;
        private Button likeButton;
        private NineGridImageView<String> mImgGridView;
        private CommentListView mListView;
        private TextView main_item_content;
        private TextView main_item_id;
        private TextView main_item_articleid;
        private TextView main_item_time;


        public ViewHolder(View view){
            super(view);
        }










    }




}
