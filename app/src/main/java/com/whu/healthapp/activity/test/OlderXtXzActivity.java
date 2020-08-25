package com.whu.healthapp.activity.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.hubu.fan.utils.CommonUtils;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.asses.AssesXtXz;
import com.whu.healthapp.bean.jqb.XtXz;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.db.XtXzDB;
import com.whu.healthapp.utils.DateUtils;

import net.sf.json.JSONObject;

import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by 47462 on 2016/11/15.
 */
public class OlderXtXzActivity extends BaseActivity implements View.OnClickListener{

    private EditText etXt,etXz ;
    private DatePicker dp ;
    private String postXt,postXz;

    private String grade;
    private String comment;



    private Handler postHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

            }
            super.handleMessage(msg);
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_older_xtxz);
        etXt = (EditText) findViewById(R.id.et_xt);
        etXt.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etXz = (EditText) findViewById(R.id.et_xz);
        etXz.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dp = (DatePicker) findViewById(R.id.dp_date);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String xt = etXt.getText().toString().trim();
                String xz = etXz.getText().toString().trim();
                String time = dp.getYear()+"-"+ (dp.getMonth()+1) +"-"+dp.getDayOfMonth();
                if(CommonUtils.isAvailableAll(xt,xz)){
                    XtXz xtxz = new XtXz();
                    xtxz.setTime(time);
                    xtxz.setUserId(User.getInstance(OlderXtXzActivity.this).getPhone());
                    xtxz.setXt(xt);
                    xtxz.setXz(xz);
                    XtXzDB db = new XtXzDB(this);
                    db.addData(xtxz);
                    postXt=xt;
                   postXz=xz;
                    postHandler.sendEmptyMessage(0);

                    String url = "http://47.92.55.20:80/Healthy/data.mvc?action=4";
                    RequestParams params = new RequestParams(url);
                    params.setMultipart(true);
                    String phone = User.getInstance(OlderXtXzActivity.this).getPhone();
                    time= DateUtils.getCurrentDate();
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("bsuger",postXt);
                    params.addBodyParameter("bfat", postXz);

                    x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onFinished() {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(OlderXtXzActivity.this, "上传失败，error:"+ex.getMessage(), Toast.LENGTH_LONG).show();

                        }


                        @Override
                        public void onSuccess(String result) {
                            if(result.equals("error")){
                                Toast.makeText(OlderXtXzActivity.this, "错误：用户不存在", Toast.LENGTH_LONG).show();
                            }
                            //上传成功：返回模型分析结果
                            else {
                                Toast.makeText(OlderXtXzActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                //Toast.makeText(OlderXtXzActivity.this, result, Toast.LENGTH_LONG).show();

                                //成功获取json类型的体重数据，对其进行解析
                                String jsonStr = result;
                                JSONObject job = JSONObject.fromObject(jsonStr);
                                grade = job.getString("grade");
                                comment = job.getString("comment");

                                Intent intent=new Intent(OlderXtXzActivity.this, AssesXtXz.class);
                                intent.putExtra("xt",postXt);
                                intent.putExtra("xz",postXz);
                                intent.putExtra("grade",grade);
                                intent.putExtra("comment",comment);
                                startActivity(intent);
                                finish();

                                //Toast.makeText(OlderXtXzActivity.this, result, Toast.LENGTH_LONG).show();
                            }


                        }

                    });
                    LogUtils.d(db.getDatas(User.getInstance(OlderXtXzActivity.this).getPhone()).toString());


                }else{
                    showTip("请输入正确的血糖和血脂值");
                }
                break;
        }
    }
}
