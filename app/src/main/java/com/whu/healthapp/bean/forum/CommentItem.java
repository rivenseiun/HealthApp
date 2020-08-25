package com.whu.healthapp.bean.forum;

/**
 * Created by Jiang.YX on 2016/11/18.
 */

public class CommentItem {
    private String ID;
    private String comment;

    public CommentItem(String ID,String comment){
        this.comment=comment;
        this.ID=ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
