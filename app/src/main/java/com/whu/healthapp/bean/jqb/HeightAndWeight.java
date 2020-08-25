package com.whu.healthapp.bean.jqb;


public class HeightAndWeight {

    private int id;
    private String userId;
    private String time;
    private String height;
    private String weight;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    @Override
    public String toString() {
        return "HeightAndWeight{" +
                "id=" + id +
                ", userId=" + userId +
                ", time='" + time + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                '}';
    }

}
