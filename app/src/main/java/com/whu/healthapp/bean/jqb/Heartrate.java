package com.whu.healthapp.bean.jqb;

/**
 * Created by Riven on 2017/3/27.
 */

public class Heartrate {
    private int id;
    private String userId;
    private String time;
    private String heartrate;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
