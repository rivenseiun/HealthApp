package com.whu.healthapp.forum.entity;

import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by wanderlust on 2017/4/22.
 */

public class ArticleEntity implements Comparable {
    private String articleId;               //文章id
    private int userId;                     //发布者用户id
    private String userName;                //发布者用户名
    private String userAvatar;              //发布者用户头像
    private String time;                    //发布日期
    private String title;                   //文章标题
    private String text;                    //文章内容
    private List<String> imgUrlList;           //图片列表
    private int favorCount;                 //点赞数
    private boolean isFavored;              //自己是否已点赞
    private int commentCount;               //评论数
    private List<CommentEntity> commentList;      //评论列表
    //    private List<String> tag;//文章标签  智能分析

    public ArticleEntity(String articleId, int userId, String userName, String userAvatar, String time,
                         String title, String text, List<String> imgUrlList, int favorCount,
                         boolean isFavored, int commentCount, List<CommentEntity> commentList) {
        this.articleId = articleId;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.time = time;
        this.title = title;
        this.text = text;
        this.imgUrlList = imgUrlList;
        this.favorCount = favorCount;
        this.isFavored = isFavored;
        this.commentCount = commentCount;
        this.commentList = commentList;
    }

    //测试用
    public ArticleEntity(){}


    //测试用
    public ArticleEntity(String articleId,String userName, String time, String text, List<String> imgUrlList, int favorCount, int commentCount, List<CommentEntity> commentList)  {
        this.articleId = articleId;
        this.userName = userName;
        this.time = time;
        this.text = text;
        this.imgUrlList = imgUrlList;
        this.favorCount = favorCount;
        this.commentCount = commentCount;
        this.commentList = commentList;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getimgUrlList() {
        return imgUrlList;
    }

    public void setimgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public int getFavorCount() {
        return favorCount;
    }

    public void setFavorCount(int favorCount) {
        this.favorCount = favorCount;
    }

    public boolean isFavored() {
        return isFavored;
    }

    public void setFavored(boolean favored) {
        isFavored = favored;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<CommentEntity> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentEntity> commentList) {
        this.commentList = commentList;
    }


    @Override
    public int compareTo(@NonNull Object another) {
        ArticleEntity anotherArticle = (ArticleEntity)another;
        return Timestamp.valueOf(this.getTime()).compareTo(Timestamp.valueOf(((ArticleEntity) another).getTime()));

    }
}
