package com.whu.healthapp.bbs.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.bean.forum.ArticleItem;
import com.whu.healthapp.bean.forum.CommentItem;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.view.CommentListView;
import com.whu.healthapp.view.GridView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class MainListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<ArticleItem> articleItems=new ArrayList<>();
    public static String COMMENT_URL = "http://47.92.55.20:80/Healthy/comment.mvc?action=0";
    public static String LIKE_URL = "http://47.92.55.20:80/Healthy/passage.mvc?action=1";



    public MainListViewAdapter(Context context, List<ArticleItem> articleItems) {
        this.context = context;
        this.articleItems = articleItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return articleItems.size();
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
        //private GridView mGridView;

        private TextView favor;
        private Button commentButton;
        private Button likeButton;
        private GridView mImgGridView;
        private CommentListView mListView;
        private TextView main_item_content;
        private TextView main_item_id;
        private TextView main_item_articleid;
        private TextView main_item_time;



    }

    @Override
    public View getView(int arg0, View convertView, ViewGroup arg2) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_main_item, null);
            viewHolder = new ViewHolder();
            viewHolder.main_item_content = (TextView) convertView.findViewById(R.id.main_item_content);
            viewHolder.main_item_id = (TextView) convertView.findViewById(R.id.main_item_id);
            viewHolder.main_item_time = (TextView) convertView.findViewById(R.id.main_item_time);
            viewHolder.main_item_articleid = (TextView) convertView.findViewById(R.id.main_item_articleid);
            //viewHolder.mGridView = (GridView) convertView.findViewById(R.id.gv_comment_head);
            viewHolder.likeButton = (Button)convertView.findViewById(R.id.like_btn);
            viewHolder.commentButton = (Button)convertView.findViewById(R.id.comment_btn);
            viewHolder.favor = (TextView)convertView.findViewById(R.id.gv_comment_head);
            viewHolder.mImgGridView = (GridView) convertView.findViewById(R.id.gv_listView_main_gridView);
            viewHolder.mListView = (CommentListView) convertView.findViewById(R.id.lv_item_listView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleItem articleItem = articleItems.get(arg0);
        final List<CommentItem> commentItems = articleItem.getCommentItems();
        List<String> res=articleItem.getRes();

        viewHolder.main_item_content.setText(articleItem.getContent());
        viewHolder.main_item_time.setText(articleItem.getTime());
        viewHolder.main_item_id.setText(articleItem.getUserName());
        viewHolder.main_item_articleid.setText(articleItem.getarticleId());
        //viewHolder.mGridView.setVisibility(View.VISIBLE);
        viewHolder.mImgGridView.setVisibility(View.VISIBLE);
        int favor=articleItem.getFavor();
        viewHolder.favor.setText(String.valueOf(favor));


        final CommentListViewAdapter commentListViewAdapter = new CommentListViewAdapter(context, commentItems);
        viewHolder.mImgGridView.setAdapter(new GridViewAdapter(context,res));
        viewHolder.mListView.setAdapter(commentListViewAdapter);




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
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        //上传成功，则显示的点赞数+1
                        int favor = Integer.parseInt(viewHolder.favor.getText().toString());
                        favor++;
                        viewHolder.favor.setText(String.valueOf(favor));
                        viewHolder.likeButton.setClickable(false);
                    }
                });






            }
        });



        //评论按钮事件
        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText comment = new EditText(context);
                //弹出评论的对话框
                new AlertDialog.Builder(context)
                        .setTitle("发表评论")
                        .setView(comment)
                        .setPositiveButton("评论", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(comment.getText().toString().equals("")){
                                    Toast.makeText(context,"评论不能为空！",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    RequestParams params = new RequestParams(COMMENT_URL);
                                    // params.setMultipart(true);




                                    //预留的部分：上传用户id和文章id
                                    params.addBodyParameter("phone", User.getInstance(context).getPhone());
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
                                            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                                        }

                                        @Override
                                        public void onSuccess(String result) {
                                            Toast.makeText(context, viewHolder.main_item_articleid.getText().toString(), Toast.LENGTH_LONG).show();
                                            Toast.makeText(context, "评论成功", Toast.LENGTH_LONG).show();

                                            //如果评论成功，则将刚刚发布的评论添加到文章的评论列表中显示
                                            commentItems.add(new CommentItem(User.getInstance(context).getUserName(),comment.getText().toString()));
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


        //viewHolder.mGridView.setAdapter(new HeadCommentGridViewAdapter(context,favor));

        return convertView;
    }
}
