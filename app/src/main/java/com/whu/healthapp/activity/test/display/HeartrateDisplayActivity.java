package com.whu.healthapp.activity.test.display;

/**
 * Created by Riven on 2017/3/27.
 */

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
import com.whu.healthapp.bean.jqb.Heartrate;
import com.whu.healthapp.bean.user.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class HeartrateDisplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<Heartrate> datas = new ArrayList<>();
    private AlmightyAdapter<Heartrate> adapter ;
    private TextView tvTime,tvHeight,tvWeight ;
    private Button back;
    private String phone;
    public static String HEARTRATE_URL = "http://47.92.55.20:80/Healthy/person.mvc?action=10";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_heartrate);
        listview = (ListView) findViewById(R.id.listview);

        //返回按钮
        back = (Button) findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HeartrateDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                HeartrateDisplayActivity.this.finish();
            }
        });

        //phone = "13979886479";
        phone = User.getInstance(HeartrateDisplayActivity.this).getPhone();


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
                Toast.makeText(HeartrateDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
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
                        Heartrate bean = new Heartrate();
                        if (!temp.getString("rate").equals("null")) {
                            bean.setTime(temp.getString("date"));
                            bean.setHeartrate(String.valueOf(temp.getInt("rate")));
                            datas.add(bean);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                adapter = new AlmightyAdapter<Heartrate>(HeartrateDisplayActivity.this, R.layout.listview_item_lady_weight, datas) {
                    @Override
                    public void setView(AlmightyViewHolder viewHolder, Heartrate content) {

                        ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                        ((TextView) viewHolder.getView(R.id.tv_weight)).setText("心率" + content.getHeartrate() + "BPM");
                    }
                };
                listview.setAdapter(adapter);

        /*
        List<Heartrate> temp = new ArrayList<>();

        //从本地数据库获取心率数据
        MyDBHelp myDBHelp = null;
        String insertString = "SELECT time,rate FROM heart_rate WHERE phoneNumber='"+ User.getInstance(HeartrateDisplayActivity.this).getPhone()+"'";

        SQLiteDatabase db = myDBHelp.getWritableDatabase();
        db.execSQL(insertString);
        Log.v("sql", insertString);

        //遍历查询结果集，填入数组中
        Cursor c=db.rawQuery(insertString, null);
        while(c.moveToNext()){

            String time  = c.getString(c.getColumnIndex("time"));
            String rate = c.getString(c.getColumnIndex("rate"));
            Heartrate heartrate = new Heartrate();
            heartrate.setHeartrate(rate);
            heartrate.setTime(time);
            temp.add(heartrate);


        }



        datas.addAll(temp);*/
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
