package com.whu.healthapp.activity.test.display;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hubu.fan.utils.listview.AlmightyAdapter;
import com.hubu.fan.utils.listview.AlmightyViewHolder;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.user.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 47462 on 2016/11/15.
 */
public class BloodOxygenDisplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<TestDataBean> datas = new ArrayList<>();
    private AlmightyAdapter<TestDataBean> adapter ;
    private TextView tvTime,tvHeight,tvWeight ;
    private Button back;
    public static String BLOODOXYGEN_URL ="http://47.92.55.20:/Healthy/person.mvc?action=8";
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bloodoxygen);
        listview = (ListView) findViewById(R.id.listview);
        //返回按钮
        back = (Button)findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BloodOxygenDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                BloodOxygenDisplayActivity.this.finish();
            }
        });

        adapter = new AlmightyAdapter<TestDataBean>(this,R.layout.listview_item_bloodoxygen,datas) {
            @Override
            public void setView(AlmightyViewHolder viewHolder, TestDataBean content) {

                    ((TextView)viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                    ((TextView)viewHolder.getView(R.id.tv_bobhd)).setText("血氧饱和度："+content.getXueyangbhd());


                //((TextView)viewHolder.getView(R.id.tv_mailv)).setText("脉率："+content.getMailv());
            }
        };

        phone = User.getInstance(BloodOxygenDisplayActivity.this).getPhone();
        //向服务器请求JSON数据并解析
        RequestParams params = new RequestParams(BLOODOXYGEN_URL);
        params.setMultipart(true);
        //请求参数为当前的用户手机号码
        params.addBodyParameter("phone", phone);
        // List<HeightAndWeight> data1  =this.getIntent().getExtras().getParcelableArrayList("ladyWeightDatas");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onFinished() {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(BloodOxygenDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的血氧数据，对其进行解析
                String jsonStr = "{bloodoxygendata:" + result + "}";

                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("bloodoxygendata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());
                        if(!temp.getString("blood_oxygen").equals("null")){
                            TestDataBean bean = new TestDataBean();
                            bean.setTime(temp.getString("date"));
                            bean.setUserId(phone);
                            bean.setXueyangbhd(String.valueOf(temp.getString("blood_oxygen")));
                            datas.add(bean);
                        }



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                listview.setAdapter(adapter);
                //XueyangDB db = new XueyangDB(this);
                //List<TestDataBean> temp = db.getDatas(User.getInstance(BloodOxygenDisplayActivity.this).getPhone());
                //datas.addAll(temp);
                adapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onClick(View v) {

    }
}
