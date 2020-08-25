package com.whu.healthapp.bbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.bbs.adapter.MainListViewAdapter;
import com.whu.healthapp.bean.forum.ArticleItem;
import com.whu.healthapp.bean.forum.CommentItem;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.view.bbsMainListView;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/*
public class MainActivity extends Activity {
    private ImageButton editNewArticleButton;
    private bbsMainListView mListView;
    private Handler mHandler;
    List<ArticleItem> articleItems=new ArrayList<>();
    List<CommentItem> commentItems=new ArrayList<>();
    List<String> res=new ArrayList<>();
    int  favor;
    String phone;
    LayoutInflater inflater;
    public static String ARTICLE_URL = "http://139.196.25.137:80/Healthy/passage.mvc?action=2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //向服务器请求数据，获取当前该用户的所有文章
        RequestParams params = new RequestParams(ARTICLE_URL);
        params.setMultipart(true);



        //请求参数为当前的用户id
        ///params.addBodyParameter("phone","13979886479");//测试时使用的数据
        //正式使用时，使用下面的方法
        params.addBodyParameter("phone", User.getInstance(MainActivity.this).getPhone());


        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //Toast.makeText(MainActivity.this(), result, Toast.LENGTH_LONG).show();


                //成功获取json类型的文章数据，对其进行解析
                //List<ArticleEntity> list=new ArrayList<ArticleEntity>(JSONArray.parseArray(result ,ArticleEntity.class));

                String picUrl = "http://139.196.25.137:80/Healthy/upload/";
                String result1 = "{articles:"+result+"}";

                net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(result1);
                JSONArray jsonArray = job.getJSONArray("articles");
                Iterator<JSONArray> itr = jsonArray.iterator();
                while(itr.hasNext()){

                    //建立数组存放文章的评论列表
                    List<CommentItem> commentItems=new ArrayList<>();
                    //建立数组存放文章的图片
                    List<String> res=new ArrayList<>();

                    ArticleItem articleitem;

                    JSONObject temp = JSONObject.fromObject(itr.next());
                    int likeCount = temp.getInt("like");//获得解析出的赞数。后面类似
                    String articleId = temp.getString("articleid");

                    if(!temp.getString("pircture_1").equals("")){
                        String pic1 = picUrl+temp.getString("pircture_1");//图片1的网址

                        res.add(pic1);

                    }

                    if(!temp.getString("pircture_2").equals("")){
                        String pic2 = picUrl+temp.getString("pircture_2");
                        res.add(pic2);
                    }
                    if(!temp.getString("pircture_3").equals("")){
                        String pic3 = picUrl+temp.getString("pircture_3");
                        res.add(pic3);
                    }
                    if(!temp.getString("pircture_4").equals("")){
                        String pic4 = picUrl+temp.getString("pircture_4");
                        res.add(pic4);
                    }



                    //获取文章标题和内容，由于可能出现中文，要进行转码
                    try {
                        String title = new String(temp.getString("title").getBytes(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String Content = null;
                    try {
                        Content = new String(temp.getString("content").getBytes(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    //解析评论列表
                    JSONArray jComments = temp.getJSONArray("comments");
                    Iterator<JSONArray> iComments = jComments.iterator();
                    while(iComments.hasNext()){
                        JSONObject tComments = JSONObject.fromObject(iComments.next());

                        //评论内容和用户名有可能出现中文，进行转码
                        String commentUserName = null;//获得解析出的评论用户id
                        try {
                            commentUserName = new String (tComments.getString("commentusername").getBytes(),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String comment = null;//获得解析出的评论内容
                        try {
                            comment = new String (tComments.getString("comment").getBytes(),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String commentTime = tComments.getString("commenttime");
                        CommentItem commentItem=new CommentItem(commentUserName,comment);
                        commentItems.add(commentItem);
                    }
                    //由于某些原因，目前不能上传时间，但保留这个构造方法。
                    articleitem=new ArticleItem(commentItems,Content,articleId,"", User.getInstance(MainActivity.this).getUserName(),res,likeCount);
                    articleItems.add(articleitem);

                }


            }
        });







        CommentItem commentItem=new CommentItem("A","This is A comment");
        commentItems.add(commentItem);
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        favor=3;
        ArticleItem articleItem1=new ArticleItem(commentItems,"今天天气真差","武汉","11月18日","James",res,favor);


        CommentItem commentItem6=new CommentItem("F","This is F comment");
        CommentItem commentItem7=new CommentItem("G","This is G commment");
        CommentItem commentItem8=new CommentItem("H","This is H commment");
        CommentItem commentItem9=new CommentItem("I","This is I commment");
        CommentItem commentItem10=new CommentItem("J","This is J commment");
        commentItems.clear();
        commentItems.add(commentItem6);
        commentItems.add(commentItem7);
        commentItems.add(commentItem8);
        commentItems.add(commentItem9);
        commentItems.add(commentItem10);
        res.clear();
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        res.add("http://139.196.25.137:80/Healthy/upload/44444");
        favor = 5;
        ArticleItem articleItem2=new ArticleItem(commentItems,"这是一段示例文字","上海","11月17日","James",res,favor);

        articleItems.add(articleItem1);
        articleItems.add(articleItem2);







        editNewArticleButton = (ImageButton)findViewById(R.id.editNewArticleBtn);
        editNewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(MainActivity.this(),phone,Toast.LENGTH_LONG);
                Intent intent = new Intent(MainActivity.this, NewArticleActivity.class);
                intent.putExtra("clear",1); //清空图片列表的值
                startActivity(intent);
            }
        });

        //论坛界面加载
        mHandler = new Handler();
        mListView = (bbsMainListView)findViewById(R.id.bbs_main_listView);
        LayoutInflater inflater2=(LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addHeaderView = inflater2.inflate(R.layout.bbs_main_head,null);
        ImageView mReplaceBackground = (ImageView) addHeaderView.findViewById(R.id.rl_click_replace_background);
        mListView.addHeaderView(addHeaderView);
        MainListViewAdapter adapter =new MainListViewAdapter(MainActivity.this,articleItems);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }
}
*/