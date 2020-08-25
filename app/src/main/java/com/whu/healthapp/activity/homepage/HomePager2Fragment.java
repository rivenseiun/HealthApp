package com.whu.healthapp.activity.homepage;

//import android.annotation.TargetApi;
//import android.app.Fragment;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.whu.healthapp.R;
//import com.whu.healthapp.activity.test.display.BloodDisplayActivity;
//import com.whu.healthapp.activity.test.display.BloodOxygenDisplayActivity;
//import com.whu.healthapp.activity.test.display.BodyTDsiplayActivity;
//import com.whu.healthapp.activity.test.display.ChildHeightAndWeightDisplayActivity;
//import com.whu.healthapp.activity.test.display.EcgDisplayActivity;
//import com.whu.healthapp.activity.test.display.LadyWeightDisplayActivity;
//import com.whu.healthapp.activity.test.display.OlderXtxzDisplayActivity;
//
//
//public class HomePager2Fragment extends Fragment implements View.OnClickListener {
//
//    private Button btnHW ;
//    private Button btnXindian,btnXueyang,btnTiwen,btnXueya,btnWeight ,btnXtxz;
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_homepage_tab2,container,false);
//        btnHW = (Button) view.findViewById(R.id.btn_wh);
//        btnXueya = (Button) view.findViewById(R.id.btn_xueya);
//        btnXindian = (Button) view.findViewById(R.id.btn_xindian);
//        btnTiwen = (Button) view.findViewById(R.id.btn_tiwen);
//        btnXueyang = (Button) view.findViewById(R.id.btn_xueyang);
//        btnWeight = (Button) view.findViewById(R.id.btn_weight);
//        btnXtxz = (Button) view.findViewById(R.id.btn_xtxz);
//        btnXueya.setOnClickListener(this);
//        btnXindian.setOnClickListener(this);
//        btnTiwen.setOnClickListener(this);
//        btnXueyang.setOnClickListener(this);
//        btnHW.setOnClickListener(this);
//        btnWeight.setOnClickListener(this);
//        btnXtxz.setOnClickListener(this);
//
////        GlobalImage globalImage = GlobalImage.getInstance(getActivity().getApplicationContext());
////        btnXindian.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xindian)));
////        btnXueya.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueya)));
////        btnTiwen.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.tiwen)));
////        btnXueyang.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueyang)));
////        btnHW.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.childhw)));
////        btnWeight.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.ladyw)));
////        btnXtxz.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.olderxtxz)));
//        return view;
//    }
//
//    @Override
//    public void onClick(View v) {
//        Intent intent = null;
//        switch (v.getId()){
//            case R.id.btn_wh:
//                intent = new Intent(getActivity(), ChildHeightAndWeightDisplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_xueya:
//                intent = new Intent(getActivity(), BloodDisplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_xindian:
//                intent = new Intent(getActivity(), EcgDisplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_xueyang:
//                intent = new Intent(getActivity(), BloodOxygenDisplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_tiwen:
//                intent = new Intent(getActivity(), BodyTDsiplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_weight:
//                intent = new Intent(getActivity(), LadyWeightDisplayActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.btn_xtxz:
//                intent = new Intent(getActivity(), OlderXtxzDisplayActivity.class);
//                startActivity(intent);
//                break;
//
//        }
//    }
//}

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.test.display.BloodDisplayActivity;
import com.whu.healthapp.activity.test.display.BloodOxygenDisplayActivity;
import com.whu.healthapp.activity.test.display.BodyTDsiplayActivity;
import com.whu.healthapp.activity.test.display.ChildHeightAndWeightDisplayActivity;
import com.whu.healthapp.activity.test.display.EcgDisplayActivity;
import com.whu.healthapp.activity.test.display.LadyWeightDisplayActivity;
import com.whu.healthapp.activity.test.display.OlderXtxzDisplayActivity;
import com.whu.healthapp.bean.jqb.HeightAndWeight;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.jqb.XtXz;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.db.HeightWeightDB;
import com.whu.healthapp.db.TiwenDB;
import com.whu.healthapp.db.XindianDB;
import com.whu.healthapp.db.XtXzDB;
import com.whu.healthapp.db.XueyaDB;
import com.whu.healthapp.db.XueyangDB;
import com.whu.healthapp.record.CenterViewPager;
import com.whu.healthapp.record.CenterViewPagerAdapter;
import com.whu.healthapp.record.ZoomOutPageTransformer;
import com.whu.healthapp.utils.UserInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.whu.healthapp.activity.test.display.LadyWeightDisplayActivity.LADYWEIGHT_URL;


/**
 * Created by 47462 on 2016/10/9.
 */
public class HomePager2Fragment extends Fragment implements View.OnClickListener {

    private Button btnHW;
    private Button btnXindian, btnXueyang, btnTiwen, btnXueya, btnWeight, btnXtxz;
    protected CenterViewPager viewPager;
    private String phone = User.getInstance(getActivity()).getPhone();

    //网络获取
    private List<HeightAndWeight> ladyWeightDatas = new ArrayList<>();
    private List<HeightAndWeight> childHeightDatas = new ArrayList<>();


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_fu_pager, container, false);
        viewPager = (CenterViewPager) view.findViewById(R.id.centerViewPager);
        initPager();

        /*
        btnHW = (Button) view.findViewById(R.id.btn_wh);
        btnXueya = (Button) view.findViewById(R.id.btn_xueya);
        btnXindian = (Button) view.findViewById(R.id.btn_xindian);
        btnTiwen = (Button) view.findViewById(R.id.btn_tiwen);
        btnXueyang = (Button) view.findViewById(R.id.btn_xueyang);
        btnWeight = (Button) view.findViewById(R.id.btn_weight);
        btnXtxz = (Button) view.findViewById(R.id.btn_xtxz);




        btnXueya.setOnClickListener(this);
        btnXindian.setOnClickListener(this);
        btnTiwen.setOnClickListener(this);
        btnXueyang.setOnClickListener(this);
        btnHW.setOnClickListener(this);
        btnWeight.setOnClickListener(this);
        btnXtxz.setOnClickListener(this);*/

//        GlobalImage globalImage = GlobalImage.getInstance(getActivity().getApplicationContext());
//        btnXindian.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xindian)));
//        btnXueya.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueya)));
//        btnTiwen.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.tiwen)));
//        btnXueyang.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueyang)));
//        btnHW.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.childhw)));
//        btnWeight.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.ladyw)));
//        btnXtxz.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.olderxtxz)));
        return view;
    }


    private void initPager() {

        //心率测量数据页面
        final List<View> views = new ArrayList<>();
        final LayoutInflater mLi = LayoutInflater.from(getActivity());
        View heartrate = mLi.inflate(R.layout.pager_heartrate, null);
        Button viewHeartrate = (Button) heartrate.findViewById(R.id.btn_view_heartrate);
        TextView recentHeartrate = (TextView) heartrate.findViewById(R.id.heartrate);
        TextView heartrateTime = (TextView) heartrate.findViewById(R.id.action_heartrate_activetime);

        //页面显示最新添加的数据和添加时间
        XindianDB db6 = new XindianDB(getActivity());
        List<TestDataBean> temp6 = db6.getDatas(User.getInstance(getActivity()).getPhone());

        if (!temp6.isEmpty()) {
            recentHeartrate.setText(temp6.get(temp6.size() - 1).getXinlv());
            heartrateTime.setText(temp6.get(temp6.size() - 1).getTime());

        } else {

            recentHeartrate.setTextSize(30);
            recentHeartrate.setText("暂无数据");
            heartrateTime.setText("暂无数据");
        }


        viewHeartrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EcgDisplayActivity.class);
                startActivity(intent);
            }
        });
        heartrate.setTag(0);
        views.add(heartrate);


        //血氧测量数据页面
        View bloodOxygen = mLi.inflate(R.layout.pager_bloodoxygen, null);
        Button viewBloodOxygen = (Button) bloodOxygen.findViewById(R.id.btn_view_bloodoxygen);
        TextView recentBaohedu = (TextView) bloodOxygen.findViewById(R.id.baohedu);
        //TextView recentMailv = (TextView) bloodOxygen.findViewById(R.id.mailv);
        TextView bloodOxygenTime = (TextView) bloodOxygen.findViewById(R.id.action_bloodoxygen_activetime);

        //页面显示最新添加的数据和添加时间
        XueyangDB db5 = new XueyangDB(getActivity());
        List<TestDataBean> temp5 = db5.getDatas(User.getInstance(getActivity()).getPhone());

        if (!temp5.isEmpty()) {
            recentBaohedu.setText(temp5.get(temp5.size() - 1).getXueyangbhd());
            //recentMailv.setText(temp5.get(temp5.size() - 1).getMailv());
            bloodOxygenTime.setText(temp5.get(temp5.size() - 1).getTime());


        } else {

            recentBaohedu.setTextSize(30);
            //recentMailv.setTextSize(30);
            recentBaohedu.setText("暂无数据");
            //recentMailv.setText("暂无数据");
            bloodOxygenTime.setText("暂无数据");


        }


        viewBloodOxygen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BloodOxygenDisplayActivity.class);
                startActivity(intent);
            }
        });
        bloodOxygen.setTag(1);
        views.add(bloodOxygen);


        //血压测量数据页面

        View bloodPressure = mLi.inflate(R.layout.pager_bloodpressure, null);
        Button viewBloodPressure = (Button) bloodPressure.findViewById(R.id.btn_view_bloodpressure);
        TextView recentShuzhangya = (TextView) bloodPressure.findViewById(R.id.shuzhangya);
        TextView recentShousuoya = (TextView) bloodPressure.findViewById(R.id.shousuoya);
        TextView recentAvg = (TextView) bloodPressure.findViewById(R.id.avg);
        TextView BloodPressureTime = (TextView) bloodPressure.findViewById(R.id.action_bloodpressure_activetime);

        //页面显示最新添加的数据和添加时间
        XueyaDB db4 = new XueyaDB(getActivity());
        List<TestDataBean> temp4 = db4.getDatas(User.getInstance(getActivity()).getPhone());

        if (!temp4.isEmpty()) {
            recentShousuoya.setText(temp4.get(temp4.size() - 1).getShousuoya());
            recentShuzhangya.setText(temp4.get(temp4.size() - 1).getShuzhangya());
            recentAvg.setText(temp4.get(temp4.size() - 1).getAveya());
            BloodPressureTime.setText(temp4.get(temp4.size() - 1).getTime());

        } else {
            recentShousuoya.setTextSize(20);
            recentAvg.setTextSize(20);
            recentShuzhangya.setTextSize(20);
            recentShousuoya.setText("暂无数据");
            recentShuzhangya.setText("暂无数据");
            recentAvg.setText("暂无数据");
            BloodPressureTime.setText("暂无数据");

        }


        viewBloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BloodDisplayActivity.class);
                startActivity(intent);
            }
        });
        bloodPressure.setTag(2);
        views.add(bloodPressure);


        //体温测量数据页面
        View temperature = mLi.inflate(R.layout.pager_temperature, null);
        Button viewTempareture = (Button) temperature.findViewById(R.id.btn_view_temperature);
        TextView recentTemperature = (TextView) temperature.findViewById(R.id.temperature);
        TextView TemperatureTime = (TextView) temperature.findViewById(R.id.action_temperature_activetime);

        //页面显示最新添加的数据和添加时间
        TiwenDB db3 = new TiwenDB(getActivity());
        List<TestDataBean> temp3 = db3.getDatas(User.getInstance(getActivity()).getPhone());
        if (!temp3.isEmpty()) {
            recentTemperature.setText(temp3.get(temp3.size() - 1).getTiwen());
            TemperatureTime.setText(temp3.get(temp3.size() - 1).getTime());

        } else {
            recentTemperature.setTextSize(30);
            recentTemperature.setText("暂无数据");
            TemperatureTime.setText("暂无数据");

        }

        viewTempareture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BodyTDsiplayActivity.class);
                startActivity(intent);
            }
        });
        temperature.setTag(3);
        views.add(temperature);


        //运动测量数据页面
        View step = mLi.inflate(R.layout.pager_step, null);
        Button viewStep = (Button) step.findViewById(R.id.btn_view_step);
        TextView recentStep = (TextView) step.findViewById(R.id.step);



        /*
        viewStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BodyTDsiplayActivity.class);
                startActivity(intent);
            }
        });*/
        step.setTag(4);
        views.add(step);


        //血糖血脂测量数据页面
        View bloodSugar = mLi.inflate(R.layout.pager_bloodsugar, null);
        Button viewBloodSugar = (Button) bloodSugar.findViewById(R.id.btn_view_bloodsugar);

        TextView recentBloodSugar = (TextView) bloodSugar.findViewById(R.id.blood_sugar);
        TextView recentBloodFat = (TextView) bloodSugar.findViewById(R.id.blood_fat);
        TextView BloodSugarTime = (TextView) bloodSugar.findViewById(R.id.action_bloodsugar_activetime);

        //页面显示最新添加的数据和添加时间
        XtXzDB db = new XtXzDB(getActivity());
        List<XtXz> temp = db.getDatas(User.getInstance(getActivity()).getPhone());
        if (!temp.isEmpty()) {
            recentBloodSugar.setText(temp.get(temp.size() - 1).getXt());
            recentBloodFat.setText(temp.get(temp.size() - 1).getXz());
            BloodSugarTime.setText(temp.get(temp.size() - 1).getTime());

        } else {
            recentBloodSugar.setTextSize(30);
            recentBloodFat.setTextSize(30);
            recentBloodSugar.setText("暂无数据");
            recentBloodFat.setText("暂无数据");
            BloodSugarTime.setText("暂无数据");

        }

        viewBloodSugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OlderXtxzDisplayActivity.class);
                startActivity(intent);
            }
        });
        bloodSugar.setTag(5);
        views.add(bloodSugar);


        //儿童身高测量数据页面
        final View childhaw = mLi.inflate(R.layout.pager_childrenhaw, null);
        final Button viewhaw = (Button) childhaw.findViewById(R.id.btn_view_haw);
        final TextView recentWeight = (TextView) childhaw.findViewById(R.id.child_weight);
        final TextView recentHeight = (TextView) childhaw.findViewById(R.id.child_height);
        final TextView ChildrenhawTime = (TextView) childhaw.findViewById(R.id.action_childrenhaw_activetime);

        HeightWeightDB db1 = new HeightWeightDB(getActivity());
        List<HeightAndWeight> temp1 = db1.getDatas(User.getInstance(getActivity()).getPhone());

        if (!temp1.isEmpty()) {
            recentWeight.setText(temp1.get(temp1.size() - 1).getWeight());
            recentHeight.setText(temp1.get(temp1.size() - 1).getHeight());
            ChildrenhawTime.setText(temp1.get(temp1.size() - 1).getTime());
        } else {
            recentWeight.setTextSize(30);
            recentHeight.setTextSize(30);
            recentWeight.setText("暂无数据");
            recentHeight.setText("暂无数据");
            ChildrenhawTime.setText("暂无数据");
        }
        viewhaw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChildHeightAndWeightDisplayActivity.class);
                startActivity(intent);
            }
        });


        final String sel = UserInfo.getSel(getActivity());
        if (sel.equals("小孩")) {
            childhaw.setTag(6);
            views.add(childhaw);
        }








        //孕妇体重测量数据页面
        final View ladyWeight = mLi.inflate(R.layout.pager_ladyweight, null);
        final Button viewLadyWeight = (Button) ladyWeight.findViewById(R.id.btn_view_ladyweight);
        final TextView recentLadyWeight = (TextView) ladyWeight.findViewById(R.id.lady_weight);
        final TextView recentLadywTime = (TextView) ladyWeight.findViewById(R.id.action_ladyweight_activetime);


        //页面显示最新添加的数据和添加时间
        RequestParams params = new RequestParams(LADYWEIGHT_URL);
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
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }






            @Override
            public void onSuccess(String result) {

                //成功获取json类型的数据，对其进行解析
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
                        ladyWeightDatas.add(bean);
                        }

                        if (!ladyWeightDatas.isEmpty()) {
                            recentLadyWeight.setText(ladyWeightDatas.get(ladyWeightDatas.size() - 1).getWeight());
                            recentLadywTime.setText(ladyWeightDatas.get(ladyWeightDatas.size() - 1).getTime());


                        } else {
                            recentLadyWeight.setTextSize(30);
                            recentLadyWeight.setText("暂无数据");
                            recentLadywTime.setText("暂无数据");

                        }



                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


                viewLadyWeight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LadyWeightDisplayActivity.class);
                        startActivity(intent);
                    }
                });

                if (sel.equals("妇女")) {
                    childhaw.setTag(6);
                    views.add(ladyWeight);
                }













                CenterViewPagerAdapter adapter = new CenterViewPagerAdapter(getActivity().getApplicationContext(), views);
                viewPager.setAdapter(adapter);
                viewPager.enableCenterLockOfChilds();
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            }
        });
    }


    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_wh:
                intent = new Intent(getActivity(), ChildHeightAndWeightDisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_xueya:
                intent = new Intent(getActivity(), BloodDisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_xindian:
                intent = new Intent(getActivity(), EcgDisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_xueyang:
                intent = new Intent(getActivity(), BloodOxygenDisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_tiwen:
                intent = new Intent(getActivity(), BodyTDsiplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_weight:
                intent = new Intent(getActivity(), LadyWeightDisplayActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_xtxz:
                intent = new Intent(getActivity(), OlderXtxzDisplayActivity.class);
                startActivity(intent);
                break;

        }
    }
}
