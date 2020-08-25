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
import com.whu.healthapp.activity.asses.AssesLadyWeight;
import com.whu.healthapp.bean.jqb.HeightAndWeight;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.db.LadyWeightDB;
import com.whu.healthapp.utils.DateUtils;

import net.sf.json.JSONObject;

import org.xutils.http.RequestParams;
import org.xutils.x;


/**
 * Created by 47462 on 2016/11/15.
 */
public class LadyWeightActivity extends BaseActivity implements View.OnClickListener{

    private EditText etWeight ;
    private DatePicker dp ;
    private String ladyWeight;

    private String grade;//健康指数评分
    private String comment;//健康建议

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
        setContentView(R.layout.activity_test_lady_weight);
        etWeight = (EditText) findViewById(R.id.et_weight);
        etWeight.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        dp = (DatePicker) findViewById(R.id.dp_date);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String weight = etWeight.getText().toString().trim();
                String time = dp.getYear()+"-"+ (dp.getMonth()+1) +"-"+dp.getDayOfMonth();
                if(CommonUtils.isAvailableAll(weight)){
                    HeightAndWeight hw = new HeightAndWeight();
                    hw.setTime(time);
                    hw.setUserId(User.getInstance(LadyWeightActivity.this).getPhone());
                    hw.setWeight(weight);
                    LadyWeightDB db = new LadyWeightDB(this);
                    db.addData(hw);
                    ladyWeight=weight;
                    postHandler.sendEmptyMessage(0);
                    LogUtils.d(db.getDatas(User.getInstance(LadyWeightActivity.this).getPhone()).toString());

                    String url = "http://47.92.55.20:80/Healthy/data.mvc?action=6";
                    RequestParams params = new RequestParams(url);
                    String phone = User.getInstance(LadyWeightActivity.this).getPhone();
                    time = DateUtils.getCurrentDate();
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("time",time);
                    params.addBodyParameter("pweight", ladyWeight);
                    x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onFinished() {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(LadyWeightActivity.this,  "上传失败，error:"+ex.getMessage(), Toast.LENGTH_LONG).show();

                        }


                        @Override
                        public void onSuccess(String result) {
                            if(result.equals("error")){
                                Toast.makeText(LadyWeightActivity.this, "错误：用户不存在", Toast.LENGTH_LONG).show();
                            }
                            //上传成功：返回模型分析结果
                            else {
                                Toast.makeText(LadyWeightActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                //Toast.makeText(LadyWeightActivity.this, result, Toast.LENGTH_LONG).show();

                                //成功获取json类型的体重数据，对其进行解析
                                String jsonStr = result;
                                JSONObject job = JSONObject.fromObject(jsonStr);
                                grade = job.getString("grade");
                                comment = job.getString("comment");

                                Intent intent=new Intent(LadyWeightActivity.this, AssesLadyWeight.class);
                                intent.putExtra("ladyWeight",ladyWeight);
                                intent.putExtra("grade",grade);
                                intent.putExtra("comment",comment);
                                startActivity(intent);
                                finish();


                                //Toast.makeText(LadyWeightActivity.this, result, Toast.LENGTH_LONG).show();
                            }


                        }
                    });



                }else{
                    showTip(R.string.tip_lady_error);
                }
                break;
        }
    }
}
