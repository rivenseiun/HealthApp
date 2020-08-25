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

import static com.whu.healthapp.activity.test.display.HeartrateDisplayActivity.HEARTRATE_URL;

/**
 * Created by 47462 on 2016/11/15.
 */
public class EcgDisplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<TestDataBean> datas = new ArrayList<>();
    private AlmightyAdapter<TestDataBean> adapter ;
    private TextView tvTime,tvHeight,tvWeight ;
    private Button back;
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ecg);
        listview = (ListView) findViewById(R.id.listview);
        //返回按钮
        back = (Button) findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EcgDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                EcgDisplayActivity.this.finish();
            }
        });


        //phone = "13979886479";


        phone = User.getInstance(EcgDisplayActivity.this).getPhone();


        //向服务器请求JSON数据并解析，填入List<HeightAndWeight> datas
        RequestParams params = new RequestParams(HEARTRATE_URL);
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
                Toast.makeText(EcgDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的心率数据，对其进行解析
                String jsonStr = "{heartratedata:" + result + "}";

                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("heartratedata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());
                        TestDataBean bean = new TestDataBean();
                        if (!temp.getString("rate").equals("null")) {
                            bean.setTime(temp.getString("date"));
                            bean.setXinlv(String.valueOf(temp.getInt("rate")));
                            datas.add(bean);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter = new AlmightyAdapter<TestDataBean>(EcgDisplayActivity.this, R.layout.listview_item_ecg, datas) {
                    @Override
                    public void setView(AlmightyViewHolder viewHolder, TestDataBean content) {
                        ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                        ((TextView) viewHolder.getView(R.id.tv_ecg)).setText("心率：" + content.getXinlv() + "BPM");
                    }
                };
                listview.setAdapter(adapter);
                //XindianDB db = new XindianDB(this);
                //List<TestDataBean> temp = db.getDatas(User.getInstance(EcgDisplayActivity.this).getPhone());
                //datas.addAll(temp);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
