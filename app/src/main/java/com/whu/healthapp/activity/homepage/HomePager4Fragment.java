package com.whu.healthapp.activity.homepage;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.whu.healthapp.R;
import com.whu.healthapp.bbs.NewArticleActivity;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.forum.ForumAdapter;
import com.whu.healthapp.forum.entity.ArticleEntity;
import com.whu.healthapp.forum.entity.CommentEntity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class HomePager4Fragment extends Fragment implements BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private ForumAdapter forumAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

//    private static final int TOTAL_COUNTER = 18;

    private static final int PAGE_SIZE = 10;

    private int delayMillis = 1000;

    private int mCurrentCounter = 0;

    //private List<ArticleEntity> articleList = new ArrayList<>();
    private boolean isSuccess = true;
    private boolean mLoadMoreEndGone = false;
    //private ImageButton editNewArticleButton;
    private TextView tvNewArticle;
    List<ArticleEntity> articleItems=new ArrayList<>();
    private Handler mHandler;
    int favor;
    String phone;


    //public static String ARTICLE_URL = "http://47.92.55.20:80/Healthy/passage.mvc?action=2";//
    public static String LOAD_MORE_URL = "http://47.92.55.20/Healthy/passage.mvc?action=3";
    public static String REFRESH_URL   = "http://47.92.55.20/Healthy/passage.mvc?action=4";



    private List<String> imgUrlList = new ArrayList<>();
    private List<CommentEntity> commentList = new ArrayList<>();



    //private List<ArticleEntity> loadList = new ArrayList<ArticleEntity>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_main,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.forum_rv_article_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Typeface iconfont = Typeface.createFromAsset(getActivity().getResources().getAssets(), "iconfont/iconfont.ttf");
        //添加新文章的按钮事件
        tvNewArticle = (TextView) view.findViewById(R.id.tv_new_article);
        tvNewArticle.setTypeface(iconfont);
        tvNewArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(),phone,Toast.LENGTH_LONG);
                Intent intent = new Intent(getActivity(), NewArticleActivity.class);
                intent.putExtra("clear",1); //清空图片列表的值
                startActivity(intent);
                //getActivity().finish();
            }
        });


        //setTitle("Pull TO Refresh Use");
        //setBackBtn();
        initAdapter();




        return view;
    }


    public List<ArticleEntity> getSampleData1(int lenth) {
        List<ArticleEntity> articleList = new ArrayList<>();
        List<String> imgList = new ArrayList<>();
        List<CommentEntity> cmtList = new ArrayList<>();

        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");

        cmtList.add(new CommentEntity("Tom", "下拉刷新"));
        cmtList.add(new CommentEntity("Jerry", "I'm a mouse."));

        for (int i = 0; i < lenth; i++) {
            ArticleEntity articleEntity = new ArticleEntity();
            articleEntity.setUserName("Chad" + i);
            articleEntity.setTime("04/05/" + i);
            articleEntity.setUserAvatar("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
            articleEntity.setText("BaseRecyclerViewAdpaterHelper https://www.recyclerview.org");
            articleEntity.setFavorCount(i);
            articleEntity.setCommentCount(i+1);
            articleEntity.setimgUrlList(imgList);
            articleEntity.setCommentList(cmtList);
            articleList.add(articleEntity);
        }
        return articleList;
    }

    public List<ArticleEntity> getSampleData2(int lenth) {
        List<ArticleEntity> articleList = new ArrayList<>();
        List<String> imgList = new ArrayList<>();
        List<CommentEntity> cmtList = new ArrayList<>();

        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
        imgList.add("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");

        cmtList.add(new CommentEntity("Tom", "上拉加载"));
        cmtList.add(new CommentEntity("Jerry", "I'm a mouse."));

        for (int i = 0; i < lenth; i++) {
            ArticleEntity articleEntity = new ArticleEntity();
            articleEntity.setUserName("Chad" + i);
            articleEntity.setTime("04/05/" + i);
            articleEntity.setUserAvatar("https://avatars1.githubusercontent.com/u/7698209?v=3&s=460");
            articleEntity.setText("BaseRecyclerViewAdpaterHelper https://www.recyclerview.org");
            articleEntity.setFavorCount(i);
            articleEntity.setCommentCount(i+1);
            articleEntity.setimgUrlList(imgList);
            articleEntity.setCommentList(cmtList);
            articleList.add(articleEntity);
        }
        return articleList;
    }

//上拉刷新
    @Override
    public void onLoadMoreRequested() {
        mSwipeRefreshLayout.setEnabled(false);


        //网络请求：从服务器获取并解析文章数据
        RequestParams params = new RequestParams(LOAD_MORE_URL);
        params.setMultipart(true);
        //请求参数为当前的用户id
        ///params.addBodyParameter("phone","13979886479");//测试时使用的数据
        //正式使用时，使用下面的方法
        params.addBodyParameter("phone", User.getInstance(getActivity()).getPhone());
        params.addBodyParameter("amount","10");
        params.addBodyParameter("amount2","5");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                forumAdapter.loadMoreFail();
                mSwipeRefreshLayout.setEnabled(true);

                isSuccess = false;
            }

            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                List<ArticleEntity> articleList = new ArrayList<>();
                isSuccess = true;
                Log.e("getdata", "network success");
                //成功获取json类型的文章数据，对其进行解析
                String picUrl = "http://47.92.55.20:80/Healthy/upload/";
                String result1 = "{articles:" + result + "}";
                net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(result1);
                JSONArray jsonArray = job.getJSONArray("articles");
                Iterator<JSONArray> itr = jsonArray.iterator();
                while (itr.hasNext()) {

                    //建立数组存放文章的评论列表
                    List<CommentEntity> commentItems = new ArrayList<>();
                    //建立数组存放文章的图片
                    List<String> res = new ArrayList<>();

                    ArticleEntity articleitem;

                    JSONObject temp = JSONObject.fromObject(itr.next());
                    int likeCount = temp.getInt("like");//获得解析出的赞数。后面类似
                    String articleId = temp.getString("articleid");
                    String time = temp.getString("time");
                    String name = temp.getString("nickname");

                    if (!temp.getString("pircture_1").equals("")) {
                        String pic1 = picUrl + temp.getString("pircture_1");//图片1的网址
                        res.add(pic1);
                    }

                    if (!temp.getString("pircture_2").equals("")) {
                        String pic2 = picUrl + temp.getString("pircture_2");
                        res.add(pic2);
                    }
                    if (!temp.getString("pircture_3").equals("")) {
                        String pic3 = picUrl + temp.getString("pircture_3");
                        res.add(pic3);
                    }
                    if (!temp.getString("pircture_4").equals("")) {
                        String pic4 = picUrl + temp.getString("pircture_4");
                        res.add(pic4);
                    }


                    //获取文章标题和内容，由于可能出现中文，要进行转码
                    try {
                        String title = new String(temp.getString("title").getBytes(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String Content = null;
                    try {
                        Content = new String(temp.getString("content").getBytes(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //String time = temp.getString("articleid")


                    //解析评论列表
                    JSONArray jComments = temp.getJSONArray("comments");
                    Iterator<JSONArray> iComments = jComments.iterator();
                    int commentCount = 0;
                    while (iComments.hasNext()) {
                        commentCount++;
                        JSONObject tComments = JSONObject.fromObject(iComments.next());

                        //评论内容和用户名有可能出现中文，进行转码
                        String commentUserName = null;//获得解析出的评论用户id
                        try {
                            commentUserName = new String(tComments.getString("commentusername").getBytes(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String comment = null;//获得解析出的评论内容
                        try {
                            comment = new String(tComments.getString("comment").getBytes(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String commentTime = tComments.getString("commenttime");
                        CommentEntity commentItem = new CommentEntity(commentUserName, comment);
                        commentItems.add(commentItem);
                    }
                    //由于某些原因，目前不能上传时间，但保留这个构造方法。
                    //Toast.makeText(getActivity(), res.get(0), Toast.LENGTH_LONG).show();
                    articleitem = new ArticleEntity(articleId, name, time, Content, res, likeCount, commentCount, commentItems);
                    articleList.add(0, articleitem);

                }
                Collections.sort(articleList);
                Collections.reverse(articleList);

                if (articleList.isEmpty()) {
                    forumAdapter.loadMoreEnd(true);
                    forumAdapter.loadMoreEnd(mLoadMoreEndGone);
                    Toast.makeText(getActivity(), "加载完毕，无更多数据", Toast.LENGTH_LONG).show();
                } else {
                    forumAdapter.addData(articleList);
                    articleItems.clear();
                    forumAdapter.loadMoreComplete();
                }
                mSwipeRefreshLayout.setEnabled(true);


            }
        });
    }




    //下拉刷新
    @Override

    public void onRefresh() {


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                forumAdapter.setEnableLoadMore(false);
                //获取下拉的数据

                //网络请求：从服务器获取并解析文章数据
                RequestParams params = new RequestParams(REFRESH_URL);
                params.setMultipart(true);
                //请求参数为当前的用户id
                ///params.addBodyParameter("phone","13979886479");//测试时使用的数据
                //正式使用时，使用下面的方法
                params.addBodyParameter("phone", User.getInstance(getActivity()).getPhone());
                params.addBodyParameter("amount","10");
                params.addBodyParameter("amount2","5");
                x.http().post(params, new Callback.CommonCallback<String>(){
                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();

                        isSuccess = false;
                    }

                    @Override
                    public void onSuccess(String result) {
                        //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
                        List<ArticleEntity> articleList = new ArrayList<>();
                        isSuccess = true;
                        Log.e("getdata","network success");
                        //成功获取json类型的文章数据，对其进行解析
                        //List<ArticleEntity> list=new ArrayList<ArticleEntity>(JSONArray.parseArray(result ,ArticleEntity.class));

                        String picUrl = "http://47.92.55.20:80/Healthy/upload/";
                        String result1 = "{articles:"+result+"}";

                        net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(result1);
                        JSONArray jsonArray = job.getJSONArray("articles");
                        Iterator<JSONArray> itr = jsonArray.iterator();
                        while(itr.hasNext()) {

                            //建立数组存放文章的评论列表
                            List<CommentEntity> commentItems = new ArrayList<>();
                            //建立数组存放文章的图片
                            List<String> res = new ArrayList<>();

                            ArticleEntity articleitem;

                            JSONObject temp = JSONObject.fromObject(itr.next());
                            int likeCount = temp.getInt("like");//获得解析出的赞数。后面类似
                            String articleId = temp.getString("articleid");
                            String time = temp.getString("time");


                            if (!temp.getString("pircture_1").equals("")) {
                                String pic1 = picUrl + temp.getString("pircture_1");//图片1的网址
                                res.add(pic1);
                            }

                            if (!temp.getString("pircture_2").equals("")) {
                                String pic2 = picUrl + temp.getString("pircture_2");
                                res.add(pic2);
                            }
                            if (!temp.getString("pircture_3").equals("")) {
                                String pic3 = picUrl + temp.getString("pircture_3");
                                res.add(pic3);
                            }
                            if (!temp.getString("pircture_4").equals("")) {
                                String pic4 = picUrl + temp.getString("pircture_4");
                                res.add(pic4);
                            }


                            //获取文章标题和内容，由于可能出现中文，要进行转码
                            try {
                                String title = new String(temp.getString("title").getBytes(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String Content = null;
                            try {
                                Content = new String(temp.getString("content").getBytes(), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            //String time = temp.getString("articleid")
                            String name= new String(temp.getString("nickname"));


                            //解析评论列表
                            JSONArray jComments = temp.getJSONArray("comments");
                            Iterator<JSONArray> iComments = jComments.iterator();
                            int commentCount = 0;
                            while (iComments.hasNext()) {
                                commentCount++;
                                JSONObject tComments = JSONObject.fromObject(iComments.next());

                                //评论内容和用户名有可能出现中文，进行转码
                                String commentUserName = null;//获得解析出的评论用户id
                                try {
                                    commentUserName = new String(tComments.getString("commentusername").getBytes(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String comment = null;//获得解析出的评论内容
                                try {
                                    comment = new String(tComments.getString("comment").getBytes(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                String commentTime = tComments.getString("commenttime");
                                CommentEntity commentItem = new CommentEntity(commentUserName, comment);
                                commentItems.add(commentItem);
                            }
                            //由于某些原因，目前不能上传时间，但保留这个构造方法。
                            //Toast.makeText(getActivity(), res.get(0), Toast.LENGTH_LONG).show();
                            articleitem = new ArticleEntity(articleId, name, time, Content, res, likeCount, commentCount, commentItems);
                            articleList.add(0, articleitem);

                        }

                        Collections.sort(articleList);
                        Collections.reverse(articleList);
                            if (articleList.isEmpty()){
                                Toast.makeText(getActivity(), "暂无最新数据", Toast.LENGTH_LONG).show();
                                //Toast.makeText(getActivity(), getData(REFRESH_URL).get(0).getText(), Toast.LENGTH_LONG).show();
                                mSwipeRefreshLayout.setRefreshing(false);
                                forumAdapter.setEnableLoadMore(true);
                            }
                            else {
                                //Toast.makeText(getActivity(), "更新了"+articleList.size()+"条数据", Toast.LENGTH_LONG).show();
                                forumAdapter.setNewData(articleList);
                                mRecyclerView.scrollToPosition(0);
                                mSwipeRefreshLayout.setRefreshing(false);
                                forumAdapter.setEnableLoadMore(true);

                            }

                    }

                });



            }
        }, delayMillis);
    }


    //从网络端获取不同到数据并解析（不同的操作对应不同的url），返回成功解析的数组；
    private List<ArticleEntity> getData(String url){
        final List<ArticleEntity> articleList = new ArrayList<>();
        //网络请求：从服务器获取并解析文章数据
        RequestParams params = new RequestParams(url);
        params.setMultipart(true);

        //请求参数为当前的用户id
        ///params.addBodyParameter("phone","13979886479");//测试时使用的数据
        //正式使用时，使用下面的方法
        params.addBodyParameter("phone", User.getInstance(getActivity()).getPhone());
        params.addBodyParameter("amount","10");
        params.addBodyParameter("amount2","5");

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();

                isSuccess = false;
            }

            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();

                isSuccess = true;
                Log.e("getdata","network success");
                //成功获取json类型的文章数据，对其进行解析
                //List<ArticleEntity> list=new ArrayList<ArticleEntity>(JSONArray.parseArray(result ,ArticleEntity.class));

                String picUrl = "http://47.92.55.20:80/Healthy/upload/";
                String result1 = "{articles:"+result+"}";

                net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(result1);
                JSONArray jsonArray = job.getJSONArray("articles");
                Iterator<JSONArray> itr = jsonArray.iterator();
                while(itr.hasNext()){

                    //建立数组存放文章的评论列表
                    List<CommentEntity> commentItems=new ArrayList<>();
                    //建立数组存放文章的图片
                    List<String> res=new ArrayList<>();

                    ArticleEntity articleitem;

                    JSONObject temp = JSONObject.fromObject(itr.next());
                    int likeCount = temp.getInt("like");//获得解析出的赞数。后面类似
                    String articleId = temp.getString("articleid");
                    String time = temp.getString("time");
                    String name= new String(temp.getString("nickname"));



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
                    //String time = temp.getString("articleid")


                    //解析评论列表
                    JSONArray jComments = temp.getJSONArray("comments");
                    Iterator<JSONArray> iComments = jComments.iterator();
                    int commentCount = 0;
                    while(iComments.hasNext()){
                        commentCount++;
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
                        CommentEntity commentItem=new CommentEntity(commentUserName,comment);
                        commentItems.add(commentItem);
                    }
                    //由于某些原因，目前不能上传时间，但保留这个构造方法。
                    //Toast.makeText(getActivity(), res.get(0), Toast.LENGTH_LONG).show();
                    articleitem = new ArticleEntity(articleId,name,time,Content,res,likeCount,commentCount,commentItems);
                    articleList.add(0,articleitem);


                }
                Collections.sort(articleList);
                Collections.reverse(articleList);


            }

        });
        return articleList;








    }


    private void initAdapter() {
        forumAdapter = new ForumAdapter(R.layout.forum_item_article,  getData(REFRESH_URL));
        forumAdapter.openLoadAnimation();
        forumAdapter.setOnLoadMoreListener(this, mRecyclerView);
        forumAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        ForumAdapter.setAutoLoadMoreSize(3);
        mRecyclerView.setAdapter(forumAdapter);
        mCurrentCounter = forumAdapter.getData().size();

    }
}



/*
public class HomePager4Fragment extends Fragment implements View.OnClickListener {

    private ImageButton editNewArticleButton;
    private bbsMainListView mListView;
    private Handler mHandler;
    List<ArticleItem> articleItems=new ArrayList<>();
    List<CommentItem> commentItems=new ArrayList<>();
    List<String> res=new ArrayList<>();
    int favor;
    String phone;
    LayoutInflater inflater;
    public static String ARTICLE_URL = "http://47.92.55.20:80/Healthy/passage.mvc?action=2";




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main,container,false);
        //向服务器请求数据，获取当前该用户的所有文章
        RequestParams params = new RequestParams(ARTICLE_URL);
        params.setMultipart(true);



        //请求参数为当前的用户id
        ///params.addBodyParameter("phone","13979886479");//测试时使用的数据
        //正式使用时，使用下面的方法
        params.addBodyParameter("phone", User.getInstance(getActivity()).getPhone());


        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onFinished() {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();


                //成功获取json类型的文章数据，对其进行解析
                //List<ArticleEntity> list=new ArrayList<ArticleEntity>(JSONArray.parseArray(result ,ArticleEntity.class));

                String picUrl = "http://47.92.55.20:80/Healthy/upload/";
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
                    //Toast.makeText(getActivity(), res.get(0), Toast.LENGTH_LONG).show();
                    articleitem=new ArticleItem(commentItems,Content,articleId,"", User.getInstance(getActivity()).getUserName(),res,likeCount);
                    articleItems.add(articleitem);

                }


        }
        });













        editNewArticleButton = (ImageButton)view.findViewById(R.id.editNewArticleBtn);
        editNewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(),phone,Toast.LENGTH_LONG);
                Intent intent = new Intent(getActivity(), NewArticleActivity.class);
                intent.putExtra("clear",1); //清空图片列表的值
                startActivity(intent);
                getActivity().finish();
            }
        });

        //论坛界面加载
        mHandler = new Handler();
        mListView = (bbsMainListView)view.findViewById(R.id.bbs_main_listView);
        LayoutInflater inflater2=(LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addHeaderView = inflater2.inflate(R.layout.bbs_main_head,null);
        ImageView mReplaceBackground = (ImageView) addHeaderView.findViewById(R.id.rl_click_replace_background);
        mListView.addHeaderView(addHeaderView);
        MainListViewAdapter adapter =new MainListViewAdapter(getActivity(),articleItems);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();




        //Toast.makeText(getActivity(), articleItems.get(0).getRes().get(0),Toast.LENGTH_LONG).show();

        return view;
    }



    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){

        }
    }



}
*/
