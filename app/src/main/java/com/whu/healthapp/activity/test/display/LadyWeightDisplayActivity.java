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
import com.whu.healthapp.bean.jqb.HeightAndWeight;
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
public class LadyWeightDisplayActivity extends BaseActivity implements View.OnClickListener {

    private ListView listview;
    private List<HeightAndWeight> datas = new ArrayList<>();
    private AlmightyAdapter<HeightAndWeight> adapter;
    private TextView tvTime, tvHeight, tvWeight;
    private Button back;
    public static String LADYWEIGHT_URL = "http://47.92.55.20:80/Healthy/person.mvc?action=3";
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_ladyweight);
        listview = (ListView) findViewById(R.id.listview);
        //返回按钮
        back = (Button) findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LadyWeightDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                LadyWeightDisplayActivity.this.finish();
            }
        });

        phone = User.getInstance(LadyWeightDisplayActivity.this).getPhone();
        //向服务器请求JSON数据并解析，填入List<HeightAndWeight> datas
        RequestParams params = new RequestParams(LADYWEIGHT_URL);
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
                Toast.makeText(LadyWeightDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的体重数据，对其进行解析
                String jsonStr = "{ladyweightdata:" + result + "}";

                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("ladyweightdata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());


                        HeightAndWeight bean = new HeightAndWeight();
                        if(!temp.getString("date").equals("null")&&!temp.getString("weight").equals("null")){
                            bean.setTime(temp.getString("date"));
                            bean.setUserId(phone);
                            bean.setWeight(String.valueOf(temp.getString("weight")));
                            datas.add(bean);
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

        adapter = new AlmightyAdapter<HeightAndWeight>(LadyWeightDisplayActivity.this, R.layout.listview_item_lady_weight, datas) {
                @Override
            public void setView(AlmightyViewHolder viewHolder, HeightAndWeight content) {
                    if(!content.getWeight().equals("null")){
                        ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                        ((TextView) viewHolder.getView(R.id.tv_weight)).setText("体重：" + content.getWeight() + "KG");
                    }

            }
        };
        listview.setAdapter(adapter);

        /*
        LadyWeightDB db = new LadyWeightDB(this);
        List<HeightAndWeight> temp = db.getDatas(User.getInstance(LadyWeightDisplayActivity.this).getPhone());
        datas.addAll(temp);*/
        adapter.notifyDataSetChanged();
            }


    });
    }








    /*
    //向服务器请求JSON数据并解析，获取该用户当前所有孕妇体重测量记录
    private void getData() {
        RequestParams params = new RequestParams(LADYWEIGHT_URL);
        params.setMultipart(true);
        //请求参数为当前的用户手机号码
        params.addBodyParameter("phone", phone);

        x.http().post(params, new Callback.CommonCallback<String>(){
            @Override
            public void onFinished() {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(LadyWeightDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的文章数据，对其进行解析
                String jsonStr =  "{ladyweightdata:"+result+"}";
                parseJSONWithJSONObject(jsonStr);
            }
        });
    }

    private void parseJSONWithJSONObject(String jsonStr){
        try {
            JSONObject job = JSONObject.fromObject(jsonStr);
            JSONArray jsonArray = job.getJSONArray("ladyweightdata");
            Iterator<JSONArray> itr = jsonArray.iterator();
            while(itr.hasNext()){
                JSONObject temp = JSONObject.fromObject(itr.next());

                Log.d("WeightDisplayActivity","date: " + temp.getString("date"));
                Log.d("WeightDisplayActivity","weight: " + temp.getInt("weight"));

                HeightAndWeight bean = new HeightAndWeight();
                bean.setTime(temp.getString("date"));
                bean.setUserId(phone);
                bean.setWeight(String.valueOf(temp.getInt("weight"));
                datas.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onClick(View v) {

    }
}
