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
public class ChildHeightAndWeightDisplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<HeightAndWeight> datas = new ArrayList<>();
    private AlmightyAdapter<HeightAndWeight> adapter ;
    private TextView tvTime,tvHeight,tvWeight ;
    private Button back;
    private String phone;
    public static String CHILDHEIGHT_URL="http://47.92.55.20:80/Healthy/person.mvc?action=5";
    public static String CHILDWEIGHT_URL = "http://47.92.55.20:80/Healthy/person.mvc?action=4";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_child_heightandweight);
        listview = (ListView) findViewById(R.id.listview);

        //返回按钮
        back = (Button)findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChildHeightAndWeightDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                ChildHeightAndWeightDisplayActivity.this.finish();
            }
        });



        phone = User.getInstance(ChildHeightAndWeightDisplayActivity.this).getPhone();
        //由于在服务器端，身高和体重数据是分开的，所以需要分开解析再合并
        //身高
        //向服务器请求JSON数据并解析，填入List<HeightAndWeight> datas
        RequestParams params = new RequestParams(CHILDHEIGHT_URL);
        params.setMultipart(true);
        //请求参数为当前的用户手机号码
        params.addBodyParameter("phone", phone);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onFinished() {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(ChildHeightAndWeightDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的血糖数据，对其进行解析
                String jsonStr = "{childheightdata:" + result + "}";


                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("childheightdata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());


                        HeightAndWeight bean = new HeightAndWeight();
                        bean.setTime(temp.getString("date"));
                        bean.setUserId(phone);
                        bean.setHeight(String.valueOf(temp.getString("height")));
                        datas.add(bean);




                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }



                //体重
                RequestParams params = new RequestParams(CHILDWEIGHT_URL);
                params.setMultipart(true);
                //请求参数为当前的用户手机号码
                params.addBodyParameter("phone", phone);
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onFinished() {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(ChildHeightAndWeightDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        //成功获取json类型的血脂数据，对其进行解析
                        String jsonStr = "{childweight:" + result + "}";
                        //Log.e("BLOODFAT","YES");

                        try {
                            JSONObject job = JSONObject.fromObject(jsonStr);
                            JSONArray jsonArray = job.getJSONArray("childweight");

                            //Iterator<JSONArray> itr = jsonArray.iterator();
                            for(int i = 0;i<jsonArray.size();i++){
                                JSONObject temp = JSONObject.fromObject(jsonArray.get(i));
                                datas.get(i).setWeight(String.valueOf(temp.getString("weight")));


                            }
                            List<HeightAndWeight> tempData = new ArrayList<>();

                            for(int i = 0;i<datas.size();i++){
                                if(!datas.get(i).getHeight().equals("null")&&!datas.get(i).getWeight().equals("null")){
                                    tempData.add(datas.get(i));
                                }

                            }




                            adapter = new AlmightyAdapter<HeightAndWeight>(ChildHeightAndWeightDisplayActivity.this,R.layout.listview_item_child_hw,tempData) {
                                @Override
                                public void setView(AlmightyViewHolder viewHolder, HeightAndWeight content) {
                                    ((TextView)viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                                    ((TextView)viewHolder.getView(R.id.tv_height)).setText("身高："+content.getHeight()+"CM");
                                    ((TextView)viewHolder.getView(R.id.tv_weight)).setText("体重："+content.getWeight()+"KG");
                                }
                            };
                            listview.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });






            }
        });













        adapter = new AlmightyAdapter<HeightAndWeight>(this,R.layout.listview_item_child_hw,datas) {
            @Override
            public void setView(AlmightyViewHolder viewHolder, HeightAndWeight content) {
                ((TextView)viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                ((TextView)viewHolder.getView(R.id.tv_height)).setText("身高："+content.getHeight()+"CM");
                ((TextView)viewHolder.getView(R.id.tv_weight)).setText("体重："+content.getWeight()+"KG");
            }
        };
        listview.setAdapter(adapter);
        //HeightWeightDB db = new HeightWeightDB(this);
        //List<HeightAndWeight> temp = db.getDatas(User.getInstance(ChildHeightAndWeightDisplayActivity.this).getPhone());
        //datas.addAll(temp);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View v) {

    }
}
