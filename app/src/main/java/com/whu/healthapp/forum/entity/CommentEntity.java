package com.whu.healthapp.forum.entity;

/**
 * Created by wanderlust on 2017/4/22.
 */

public class CommentEntity {
    private String userName;  //评论用户名
    private String text;    //评论内容

    public CommentEntity(String userName, String text) {
        this.userName = userName;
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(String userId) {
        this.userName = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
