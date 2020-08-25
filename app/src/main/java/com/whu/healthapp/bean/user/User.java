package com.whu.healthapp.bean.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.whu.healthapp.utils.ContextNullException;


public class User {

    public SharedPreferences mSpUser;
    public Context mContext;
    protected static User User;

    private int id;
    private String phone;
    private String userName;
    private String nickName;
    private String password;
    private String sex;
    private Double height;
    private Double weight;
    private int age;
    private String address;
    private int type;
    private int bust;
    private int waist;
    private int hips;
    private boolean isSel;


    public boolean isSel() {
        return mSpUser.getBoolean("isSel", false);
    }

    public void setSel(boolean sel) {
        mSpUser.edit().putBoolean("isSel", sel).commit();
    }

    protected User() {
    }

    public String getPhone() {
        return mSpUser.getString("phone",null);
    }

    public void setPhone(String phone) {
        mSpUser.edit().putString("phone",phone).commit();
    }

    ;

    public static User getInstance(Context context) {
        synchronized (User.class) {
            if (User != null) {
                return User;
            }
            if (context == null) {
                throw new ContextNullException();
            }
            if (User == null) {
                User = Normal.getInstance(context);
                User.mSpUser = context.getApplicationContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
                User.mContext = context.getApplicationContext();
            }
            return User;
        }

    }

    public void clear(){

        setPhone("");
        setUserName("");
        setSel(false);


    }


    public int getId() {
        return mSpUser.getInt("ID",3);
    }

    public void setId(int id) {
        mSpUser.edit().putInt("ID", id).commit();
    }

    public String getUserName() {
        return mSpUser.getString("USERNAME", "");
    }

    public void setUserName(String userName) {
        mSpUser.edit().putString("USERNAME", userName).commit();
    }

    public String getNickName() {
        return mSpUser.getString("NICKNAME", "");
    }

    public void setNickName(String nickName) {
        mSpUser.edit().putString("NICKNAME", nickName).commit();
    }

    public String getPassword() {
        return mSpUser.getString("PASSWORD", "");
    }

    public void setPassword(String password) {
        mSpUser.edit().putString("PASSWORD", password).commit();
    }


    public int getAge() {
        return mSpUser.getInt("AGE", -1);
    }

    public void setAge(int age) {
        mSpUser.edit().putInt("AGE", age).commit();
    }

    public String getAddress() {
        return mSpUser.getString("ADDRESS", "");
    }

    public void setAddress(String address) {
        mSpUser.edit().putString("ADDRESS", address).commit();
    }

    public Double getWeight() {
        return weight;
    }

    public int getType() {
        return mSpUser.getInt("TYPE", -1);
    }

    public void setType(int type) {
        mSpUser.edit().putInt("TYPE", type).commit();
    }

    public int getBust() {
        return mSpUser.getInt("BUST", -1);
    }

    public void setBust(int bust) {
        mSpUser.edit().putInt("BUST", bust).commit();
    }

    public int getWaist() {
        return mSpUser.getInt("WAIST", -1);
    }

    public void setWaist(int waist) {
        mSpUser.edit().putInt("WAIST", waist).commit();
    }

    public int getHips() {
        return mSpUser.getInt("HIPS", -1);
    }

    public void setHips(int hips) {
        mSpUser.edit().putInt("HIPS", hips).commit();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", age=" + age +
                ", address='" + address + '\'' +
                ", type=" + type +
                ", bust=" + bust +
                ", waist=" + waist +
                ", hips=" + hips +
                '}';
    }


    public String getSex() {
        return sex;
    }

    public Double getHeight() {
        return height;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
