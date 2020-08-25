package com.whu.healthapp.activity.personer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hubu.fan.utils.Builder;
import com.hubu.fan.utils.CommonUtils;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.utils.Captcha;
import com.whu.healthapp.utils.UserInfo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Timer;

public class RegisterActivity extends BaseActivity {
    //控件
    private TextView tvCommand;
    private Button btnValid;
    private RadioGroup sexGroup;
    private Spinner spinner;
    private EditText etPhone, etValid, etPsw,etName,etHeight,etWeight,etAge,etAddress;
    private LinearLayout llContainerPhone, llContainerPsw;
    private ProgressDialog pdWait;//等待框

    private String sex;
    private int type;

    //当前界面状态
    private int state = 0;
    private static final int STATE_PHONE = 0;
    private static final int STATE_PSW = 1;

    //发送验证码后计时
    private int time;
    private static final int INIT_TIME = 10;//初始数据
    private Timer timer;//计时器

    private String valid;

    //是否上传成功
    private Boolean postSucceed;
    private String errorInfo;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    UserInfo.login(RegisterActivity.this);
                    finish();
                    break;
                case 1:
                    pdWait.dismiss();
                    //Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_LONG).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //获取控件
        tvCommand = (TextView) findViewById(R.id.tv_command);
        btnValid = (Button) findViewById(R.id.btn_sendValidAgain);
        etPhone = (EditText) findViewById(R.id.et_no);
        etValid = (EditText) findViewById(R.id.et_valid);
        etPsw = (EditText) findViewById(R.id.et_psw);
        etName = (EditText)findViewById(R.id.et_name);


        etHeight = (EditText)findViewById(R.id.et_height);
        etHeight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        sexGroup = (RadioGroup)findViewById(R.id.et_sex);
        etWeight = (EditText)findViewById(R.id.et_weight);
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etAge = (EditText)findViewById(R.id.et_age);
        etAge.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_NORMAL);
        etAddress = (EditText)findViewById(R.id.et_address);

        spinner = (Spinner)findViewById(R.id.et_type_spinner);

        ArrayAdapter spinnerAdapter;




        //将可选内容与ArrayAdapter连接起来
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.types, android.R.layout.simple_spinner_item);

        //设置下拉列表的风格
       spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter2 添加到spinner中
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                type = position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });










        //radio button
        sexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                                 // TODO Auto-generated method stub
                                 //获取变更后的选中项的ID
                                 int radioButtonId = arg0.getCheckedRadioButtonId();
                                 //根据ID获取RadioButton的实例
                                 RadioButton rb = (RadioButton)findViewById(radioButtonId);
                                 //更新文本内容，以符合选中项
                                 if(rb.getText().toString().equals("男")){
                                     sex = "male";
                }else sex = "female";
            }



        });


        llContainerPhone = (LinearLayout) findViewById(R.id.ll_content_phone);
        llContainerPsw = (LinearLayout) findViewById(R.id.ll_content_psw);

        //初始化
        llContainerPsw.setVisibility(View.GONE);
        state = STATE_PHONE;
        pdWait = new ProgressDialog(this);
    }


    /**
     * 发送验证码
     */
    public void sendValid() {
        //验证手机号
        final String phone = etPhone.getText().toString();
        if (!Captcha.phoneValid(phone)) {
            showTip(R.string.tip_phone_error);
            return;
        }
        Builder.buildProgressDialog(pdWait, getResources().getString(R.string.tip), getResources().getString(R.string.tip_sendValid), new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                }
                return false;
            }
        }, false);
        //模拟网络线程
        Builder.buildTimerTask(this, 2000, new Runnable() {
            @Override
            public void run() {
                //验证码发送成功
                pdWait.dismiss();
                llContainerPsw.setVisibility(View.VISIBLE);
                llContainerPhone.setVisibility(View.GONE);
                tvCommand.setText(getResources().getString(R.string.title_regeist_click));
                state = STATE_PSW;
                //计时发送
                numberTimer();

            }
        });
    }

    /**
     * 计时
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void numberTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        //初始化
        time = INIT_TIME;
        btnValid.setText(String.valueOf(time));
        btnValid.setEnabled(false);
        timer = Builder.buildTimerTask(this, 0, 1000, new Runnable() {
            @Override
            public void run() {
                time--;
                btnValid.setText(String.valueOf(time));
                if (time < 0) {
                    btnValid.setText(getResources().getString(R.string.sendValidAgain));
                    btnValid.setEnabled(true);
                    timer.cancel();
                    timer.purge();
                }

            }
        });
        try {
            valid = String.valueOf((int) (Math.random() * 10000));
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher); //设置图标
            builder.setTicker("验证码通知");
            builder.setContentTitle("通知"); //设置标题
            builder.setContentText("验证码：" + valid); //消息内容
            builder.setWhen(System.currentTimeMillis()); //发送时间
            builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
            builder.setAutoCancel(true);//打开程序后图标消失

            Notification notification = builder.build();
            nm.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 注册
     */
    private void regeist() {

        //验证
        String phone = etPhone.getText().toString().trim();
        String valid = etValid.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String password = etPsw.getText().toString().trim();



        if (!CommonUtils.isAvailableAll(valid)) {
            pdWait.dismiss();
            return;
        }
        if (!Captcha.pswValid(password)) {
            showTip(R.string.tip_password_error);
            pdWait.dismiss();
            return;
        }
        if (!valid.equals(this.valid)) {
            showTip(R.string.tip_valid_error);
            pdWait.dismiss();
            return;
        }
        if(name.equals("")){
            showTip("请输入用户昵称");
            pdWait.dismiss();
            return;
        }


        User.getInstance(RegisterActivity.this).setPassword(password);
        User.getInstance(RegisterActivity.this).setUserName(name);
        User.getInstance(RegisterActivity.this).setPhone(phone);




        User.getInstance(RegisterActivity.this).setSex(sex);
        User.getInstance(RegisterActivity.this).setType(type);

        //Log.e("error",String.valueOf(User.getInstance(RegisterActivity.this).getType()));


        //发送用户注册信息到服务器
        String url = "http://47.92.55.20/Healthy/user.mvc?action=0";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("username", name);
        params.addBodyParameter("password", password);
        params.addBodyParameter("type", String.valueOf(type));

        if(etWeight.getText().toString().trim().equals("")||etHeight.getText().toString().trim().equals("")||etAge.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "身高，体重及年龄不能为空！", Toast.LENGTH_LONG).show();
        }
        else {
            Double weight = Double.valueOf(etWeight.getText().toString().trim());
            Double height = Double.valueOf(etHeight.getText().toString().trim());
            int age = Integer.parseInt(etAge.getText().toString());


            if (height > 210 || height < 50) {
                Toast.makeText(RegisterActivity.this, "请输入正确的身高数值！", Toast.LENGTH_LONG).show();
            } else if (age >= 120) {
                Toast.makeText(RegisterActivity.this, "请输入正确的年龄数值！", Toast.LENGTH_LONG).show();
            } else {

                //等待框
                Builder.buildProgressDialog(pdWait, getResources().getString(R.string.tip), getResources().getString(R.string.tip_regeist), new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {

                        }
                        return false;
                    }
                }, false);

                User.getInstance(RegisterActivity.this).setWeight(weight);
                User.getInstance(RegisterActivity.this).setHeight(height);
                User.getInstance(RegisterActivity.this).setAge(age);


                String address = etAddress.getText().toString().trim();
                User.getInstance(RegisterActivity.this).setAddress(address);
                params.addBodyParameter("address", String.valueOf(address));
                params.addBodyParameter("type", String.valueOf(type));


                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(RegisterActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }


                    @Override
                    public void onSuccess(String result) {
                        //Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();

                        if (result.equals("success")) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                            String url = "http://47.92.55.20/Healthy/data.mvc?action=8";
                            RequestParams params = new RequestParams(url);

                            params.addBodyParameter("phone", etPhone.getText().toString().trim());
                            params.addBodyParameter("weight", User.getInstance(RegisterActivity.this).getWeight().toString());
                            params.addBodyParameter("age", String.valueOf(User.getInstance(RegisterActivity.this).getAge()));
                            if (User.getInstance(RegisterActivity.this).getSex().equals("male")) {
                                params.addBodyParameter("sex", "1");
                            } else params.addBodyParameter("sex", "0");
                            params.addBodyParameter("height", User.getInstance(RegisterActivity.this).getHeight().toString());

                            x.http().post(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onFinished() {
                                }

                                @Override
                                public void onCancelled(CancelledException cex) {
                                }

                                @Override
                                public void onError(Throwable ex, boolean isOnCallback) {
                                    Toast.makeText(RegisterActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                                    Message message = new Message();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }


                                @Override
                                public void onSuccess(String result) {
                                    Message message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                }
                            });


                        } else {
                            Toast.makeText(RegisterActivity.this, "该用户名已存在", Toast.LENGTH_LONG).show();
                            User.getInstance(RegisterActivity.this).clear();
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                        }


                    }
                });
            }
        }

        //注册，，，
        //模拟网络线程
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        UserInfo.login(RegisterActivity.this);
//                        finish();
//                    }
//                });
//
//            }
//        }, 2000);


    }


    /**
     * 按钮的点击事件
     *
     * @param view
     */
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.tv_back:
                switch (state) {
                    case STATE_PHONE:
                        //退出程序
                        finish();
                        break;
                    case STATE_PSW:
                        //返回填写手机号
                        llContainerPsw.setVisibility(View.GONE);
                        llContainerPhone.setVisibility(View.VISIBLE);
                        tvCommand.setText(getResources().getString(R.string.title_validno));
                        state = STATE_PHONE;
                        break;
                }
                break;
            //功能按键
            case R.id.tv_command:
                switch (state) {
                    case STATE_PHONE:
                        //发送验证码
                        sendValid();
                        break;
                    case STATE_PSW:
                        //注册
                        regeist();

                        break;
                }
                break;
            case R.id.btn_sendValidAgain:
                sendValid();
                break;

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
