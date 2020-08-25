package com.whu.healthapp.bean.jqb;


public class XtXz {

    private int id;
    private String userId;
    private String time;
    private String xt;
    private String xz;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getXt() {
        return xt;
    }

    public void setXt(String xt) {
        this.xt = xt;
    }

    public String getXz() {
        return xz;
    }

    public void setXz(String xz) {
        this.xz = xz;
    }

    @Override
    public String toString() {
        return "XtXz{" +
                "id=" + id +
                ", userId=" + userId +
                ", time='" + time + '\'' +
                ", xt='" + xt + '\'' +
                ", xz='" + xz + '\'' +
                '}';
    }
}
