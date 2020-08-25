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
public class BodyTDsiplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<TestDataBean> datas = new ArrayList<>();
    private AlmightyAdapter<TestDataBean> adapter ;
    private Button back;
    private  String phone;
    public static  String BODYTEMPERATURE_URL ="http://47.92.55.20:80/Healthy/person.mvc?action=13";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_tiwen);
        listview = (ListView) findViewById(R.id.listview);
        //返回按钮
        back = (Button)findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BodyTDsiplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                BodyTDsiplayActivity.this.finish();
            }
        });

        //phone = "13979886479";
         phone = User.getInstance(BodyTDsiplayActivity.this).getPhone();


        //向服务器请求JSON数据并解析，填入List<HeightAndWeight> datas
        RequestParams params = new RequestParams(BODYTEMPERATURE_URL);
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
                Toast.makeText(BodyTDsiplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的体温数据，对其进行解析
                String jsonStr = "{temperaturedata:" + result + "}";

                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("temperaturedata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());


                        TestDataBean bean = new TestDataBean();
                        if (!temp.getString("temperature").equals("null") ) {
                            bean.setTime(temp.getString("date"));
                            bean.setUserId(phone);
                            bean.setTiwen(String.valueOf(temp.getDouble("temperature")));
                            datas.add(bean);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                adapter = new AlmightyAdapter<TestDataBean>(BodyTDsiplayActivity.this, R.layout.listview_item_tiwen, datas) {
                    @Override
                    public void setView(AlmightyViewHolder viewHolder, TestDataBean content) {
                        ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                        ((TextView) viewHolder.getView(R.id.tv_tiwen)).setText("体温：" + content.getTiwen() + "℃");
                    }
                };
                listview.setAdapter(adapter);
                //TiwenDB db = new TiwenDB(this);
                //List<TestDataBean> temp = db.getDatas(User.getInstance(BodyTDsiplayActivity.this).getPhone());
                //datas.addAll(temp);
                adapter.notifyDataSetChanged();
            }
        });


        //本地数据库获取数据
        adapter = new AlmightyAdapter<TestDataBean>(BodyTDsiplayActivity.this, R.layout.listview_item_tiwen, datas) {
            @Override
            public void setView(AlmightyViewHolder viewHolder, TestDataBean content) {
                ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                ((TextView) viewHolder.getView(R.id.tv_tiwen)).setText("体温：" + content.getTiwen() + "℃");
            }
        };
        listview.setAdapter(adapter);
        //TiwenDB db = new TiwenDB(this);
        //List<TestDataBean> temp = db.getDatas(User.getInstance(BodyTDsiplayActivity.this).getPhone());
        //datas.addAll(temp);
        adapter.notifyDataSetChanged();



    }

    @Override
    public void onClick(View v) {

    }
}
