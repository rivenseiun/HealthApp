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
import com.whu.healthapp.bean.jqb.XtXz;
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
public class OlderXtxzDisplayActivity extends BaseActivity implements View.OnClickListener{

    private ListView listview ;
    private List<XtXz> datas = new ArrayList<>();
    private AlmightyAdapter<XtXz> adapter ;
    private Button back;
    private  String phone;
    public static String BLOODSUGAR_URL = "http://47.92.55.20:80/Healthy/person.mvc?action=7";
    public static String BLOODFAT_URL ="http://47.92.55.20:80/Healthy/person.mvc?action=9";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_older_xtxz);
        listview = (ListView) findViewById(R.id.listview);
        //返回按钮
        back = (Button)findViewById(R.id.blood_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OlderXtxzDisplayActivity.this, HomePagerActivity.class);
                startActivity(intent);
                OlderXtxzDisplayActivity.this.finish();
            }
        });

        phone = User.getInstance(OlderXtxzDisplayActivity.this).getPhone();


        //由于在服务器端，血糖和血脂数据是分开的，所以需要分开解析再合并
        //血糖
        //向服务器请求JSON数据并解析，填入List<HeightAndWeight> datas
        RequestParams params = new RequestParams(BLOODSUGAR_URL);
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
                Toast.makeText(OlderXtxzDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String result) {
                //成功获取json类型的血糖数据，对其进行解析
                String jsonStr = "{bloodsugardata:" + result + "}";


                try {
                    JSONObject job = JSONObject.fromObject(jsonStr);
                    JSONArray jsonArray = job.getJSONArray("bloodsugardata");
                    Iterator<JSONArray> itr = jsonArray.iterator();
                    while (itr.hasNext()) {
                        JSONObject temp = JSONObject.fromObject(itr.next());


                        XtXz bean = new XtXz();

                            bean.setTime(temp.getString("date"));
                            bean.setUserId(phone);
                            bean.setXt(String.valueOf(temp.getString("blood_suglar")));//接口的单词拼写错误
                            datas.add(bean);




                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }



                //血脂
                RequestParams params = new RequestParams(BLOODFAT_URL);
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
                        Toast.makeText(OlderXtxzDisplayActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        //成功获取json类型的血脂数据，对其进行解析
                        String jsonStr = "{bloodfatdata:" + result + "}";
                        //Log.e("BLOODFAT","YES");

                        try {
                            JSONObject job = JSONObject.fromObject(jsonStr);
                            JSONArray jsonArray = job.getJSONArray("bloodfatdata");

                            //Iterator<JSONArray> itr = jsonArray.iterator();
                            for(int i = 0;i<jsonArray.size();i++){
                                JSONObject temp = JSONObject.fromObject(jsonArray.get(i));

                                    datas.get(i).setXz(String.valueOf(temp.getString("blood_fat")));


                            }


                            List<XtXz> tempData = new ArrayList<>();

                            for(int i = 0;i<datas.size();i++){
                                if(!datas.get(i).getXt().equals("null")&&!datas.get(i).getXz().equals("null")){
                                    tempData.add(datas.get(i));
                                }

                            }



                            adapter = new AlmightyAdapter<XtXz>(OlderXtxzDisplayActivity.this, R.layout.listview_item_older_xtxz, tempData) {
                                @Override
                                public void setView(AlmightyViewHolder viewHolder, XtXz content) {
                                    ((TextView) viewHolder.getView(R.id.tv_time)).setText(content.getTime());
                                    ((TextView) viewHolder.getView(R.id.tv_xt)).setText("血糖：" + content.getXt());
                                    ((TextView) viewHolder.getView(R.id.tv_xz)).setText("血脂：" + content.getXz());
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



    }

    @Override
    public void onClick(View v) {

    }
}
