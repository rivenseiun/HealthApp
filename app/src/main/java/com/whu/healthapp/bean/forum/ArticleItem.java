package com.whu.healthapp.bean.forum;

import java.util.List;

/**
 * Created by Jiang.YX on 2016/11/18.
 */

public class ArticleItem {
    private int id;//文章id
    private int userId;//用户id 颁布者
    private String userName;
    private String title;
    private String content;
    //    private List<String> tag;//文章标签  智能分析
    private List<CommentItem> commentItems;
    private String time;
    private int like;
    private int num;
    private int isLike;//自己是否关注
    private String articleId;
    private List<String> res;
    private int favor;//点赞数


    public ArticleItem(List<CommentItem> commentItems, String content, String articleId, String time, String userName, List<String> res, int favor) {
        this.commentItems = commentItems;
        this.content = content;
        this.articleId = articleId;
        this.time = time;
        this.userName = userName;
        this.res = res;
        this.favor = favor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getarticleId() {
        return articleId;
    }

    public void setarticleId(String articleId) {
        this.articleId = articleId;
    }

    public List<CommentItem> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(List<CommentItem> commentItems) {
        this.commentItems = commentItems;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFavor() {
        return favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }

    public List<String> getRes() {
        return res;
    }

    public void setRes(List<String> res) {
        this.res = res;
    }
}
