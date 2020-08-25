package com.whu.healthapp.activity;


import android.app.Activity;
import android.widget.Toast;


public class BaseActivity extends Activity {

    public void showTip(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    public void showTip(int message){
        Toast.makeText(this,getResources().getString(message),Toast.LENGTH_LONG).show();
    }
}
