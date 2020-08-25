package com.whu.healthapp.activity.personer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.bean.user.Child;
import com.whu.healthapp.bean.user.Lady;
import com.whu.healthapp.bean.user.Normal;
import com.whu.healthapp.bean.user.Older;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.utils.Captcha;
import com.whu.healthapp.utils.UserInfo;

import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class LoginActivity extends BaseActivity {

    private TextView tvPhone, tvPassword,tvLogin;
    private Button loginButton;
    private String logCheck;
    private String phone,password,name;
    private int type;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:

                    SharedPreferences sharedPreferences=getSharedPreferences(phone, Context.MODE_PRIVATE);
                    User.getInstance(LoginActivity.this).setSel(true);
                    User.getInstance(LoginActivity.this).setPhone(phone);

                    String url = "http://47.92.55.20:80/Healthy/user.mvc?action=getuserinfo";
                    RequestParams params = new RequestParams(url);
                    params.addBodyParameter("phone", phone);
                    x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onFinished() {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                        }


                        @Override
                        public void onSuccess(String result) {
                            String jsonStr = result ;
                            JSONObject temp = JSONObject.fromObject(jsonStr);
                            type = Integer.parseInt(temp.getString("type"));
                            name = temp.getString("nickname");

                            User.getInstance(LoginActivity.this).setType(type);
                            User.getInstance(LoginActivity.this).setUserName(name);



                            Intent intent;
                            switch (type) {
                                case 0:
                                    Normal.getInstance(LoginActivity.this);
                                    User.getInstance(LoginActivity.this).setSel(true);
                                    User.getInstance(LoginActivity.this).setType(type);
                                    UserInfo.setSel(LoginActivity.this, getString(R.string.personer));
                                    intent = new Intent(LoginActivity.this, HomePagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 1:
                                    Older.getInstance(LoginActivity.this);
                                    UserInfo.setSel(LoginActivity.this, getString(R.string.older));
                                    User.getInstance(LoginActivity.this).setType(type);
                                    User.getInstance(LoginActivity.this).setSel(true);
                                    intent = new Intent(LoginActivity.this, HomePagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 3:
                                    Child.getInstance(LoginActivity.this);
                                    UserInfo.setSel(LoginActivity.this, getString(R.string.child));
                                    User.getInstance(LoginActivity.this).setType(type);
                                    User.getInstance(LoginActivity.this).setSel(true);
                                    intent = new Intent(LoginActivity.this, HomePagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 2:
                                    Lady.getInstance(LoginActivity.this);
                                    UserInfo.setSel(LoginActivity.this, getString(R.string.lady));
                                    User.getInstance(LoginActivity.this).setType(type);
                                    User.getInstance(LoginActivity.this).setSel(true);
                                    intent = new Intent(LoginActivity.this, HomePagerActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;


                            }

                            UserInfo.login(LoginActivity.this);
                            finish();



                        }
                    });


                    break;


                case 1:
                    Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TineStatusBar.setWindowStatusBarColor(this, R.color.colorPrimary);
        setContentView(R.layout.activity_login);

        //获取控件
        tvPhone = (TextView) findViewById(R.id.tv_phone);
        tvPassword = (TextView) findViewById(R.id.tv_password);
        tvLogin = (TextView)findViewById(R.id.tv_login);
        loginButton = (Button)findViewById(R.id.btn_login);


        if (UserInfo.isLogin(this)) {
            tvPhone.setVisibility(View.GONE);
            tvPassword.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            tvLogin.setVisibility(View.VISIBLE);
            tvLogin.setText("正在登录……");
            Log.e("login",String.valueOf(User.getInstance(LoginActivity.this).getType()));

            String phone = User.getInstance(LoginActivity.this).getPhone();
            String url = "http://47.92.55.20:80/Healthy/user.mvc?action=getuserinfo";
            RequestParams params = new RequestParams(url);
            params.addBodyParameter("phone", phone);
            x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                @Override
                public void onFinished() {
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }


                @Override
                public void onSuccess(String result) {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    String jsonStr = result ;
                    JSONObject temp = JSONObject.fromObject(jsonStr);
                    type = Integer.parseInt(temp.getString("type"));
                    name = temp.getString("nickname");

                    User.getInstance(LoginActivity.this).setType(type);
                    Log.e("login",String.valueOf(User.getInstance(LoginActivity.this).getType()));
                    User.getInstance(LoginActivity.this).setUserName(name);

                    Intent intent ;
                    intent = new Intent(LoginActivity.this, PersonSelect.class);
                    startActivity(intent);
                    finish();
                    Log.e("login",String.valueOf(User.getInstance(LoginActivity.this).getType()));

                }
            });


        }
    }


    /**
     * 按钮的点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_regeist:
                intent = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent, 0);

                break;
            case R.id.btn_login:
                //登录
                //验证
                phone = tvPhone.getText().toString();
                password = tvPassword.getText().toString();
                if (!Captcha.phoneValid(phone)) {
                    showTip(R.string.tip_phone_error);
                    return;
                }
                if (!Captcha.pswValid(password)) {
                    showTip(R.string.tip_password_error);
                    return;
                }

                String url = "http://47.92.55.20:80/Healthy/user.mvc?action=1";
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("phone", phone);
                params.addBodyParameter("password", password);
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }


                    @Override
                    public void onSuccess(String result) {
                       if(result.equals("Success")){
                           User.getInstance(LoginActivity.this).clear();
                           handler.sendEmptyMessage(0);
                       }else if(result.equals("fail")){
                           User.getInstance(LoginActivity.this).clear();
                           handler.sendEmptyMessage(1);
                       }else{
                           handler.sendEmptyMessage(2);
                       }
                    }
                });
                //登录
//                if (logCheck.equals("success")) {
//                    intent = new Intent(this, PersonSelect.class);
//                    startActivity(intent);
//                    UserInfo.login(this);
//                    finish();
//                } else if(logCheck.equals("fail")){
//                    Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_LONG).show();
//
//                }
                break;
            case R.id.tv_back:
                finish();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && UserInfo.isLogin(this)) {
            Intent intent = new Intent(this, PersonSelect.class);



            startActivity(intent);
            finish();
        }
    }
}


