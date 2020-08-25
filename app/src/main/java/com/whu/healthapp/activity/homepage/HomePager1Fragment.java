package com.whu.healthapp.activity.homepage;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hubu.fan.utils.CommonUtils;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.test.BloodActivity;
import com.whu.healthapp.activity.test.BloodOxygenActivity;
import com.whu.healthapp.activity.test.BodyTemperatureActivity;
import com.whu.healthapp.activity.test.ChildHeightAndWeightActivity;
import com.whu.healthapp.activity.test.ElectrocarDiogramActivity;
import com.whu.healthapp.activity.test.LadyWeightActivity;
import com.whu.healthapp.activity.test.OlderXtXzActivity;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.bluetooth.BluetoothCommands;
import com.whu.healthapp.bluetooth.BluetoothConnectThread;
import com.whu.healthapp.bluetooth.BluetoothDevice;
import com.whu.healthapp.mio.HeartRateActivity;
import com.whu.healthapp.mio.SleepActivity;
import com.whu.healthapp.mio.StepActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jiang.YX on 2017/2/13.
 */

public class HomePager1Fragment extends Fragment implements View.OnClickListener {

    //蓝牙
    private String bluetoothMac = "20:14:04:17:34:91"; //1号大盒子
    private BluetoothSocket socket;//蓝牙socket
    private BluetoothConnectThread bluetoothThread;
    private BluetoothAdapter mBluetoothAdapter = null;
    String[] deviceNames;//蓝牙设备名称
    String[] deviceMac;//蓝牙设备地址

    //private Button btnXindian, btnXueyang, btnTiwen, btnXueya, btnHeight, btnWeight, btnXtxz, btnMioXinlv, btnMioSleep, btnStep;
    private ImageView[] imageViews = null;
    private ImageView imageView = null;
    private ViewPager advPager = null;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;

    private TextView tvRec1, tvXindian, tvXueyang, tvXueya, tvTiwen,
                    tvRect2, tvMioXinlv, tvMIOSport, tvMioSleep,
                    tvRect3, tvXtxz, tvWomanWeight, tvHeightWeight;

    private RelativeLayout rlXtxz, rlWomanWeight, rlHeightWeight;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage_tab1, container, false);

        Typeface iconfont = Typeface.createFromAsset(getActivity().getResources().getAssets(), "iconfont/iconfont.ttf");

        //广告栏
        advPager = (ViewPager) view.findViewById(R.id.adv_pager);

        //健亲宝
        tvRec1 = (TextView) view.findViewById(R.id.rec_1);
        tvXindian = (TextView) view.findViewById(R.id.assess_ecg);
        tvXueyang = (TextView) view.findViewById(R.id.assess_blood_oxygen);
        tvXueya = (TextView) view.findViewById(R.id.assess_blood_pressure);
        tvTiwen = (TextView) view.findViewById(R.id.assess_temprature);
        tvRec1.setTypeface(iconfont);
        tvXindian.setTypeface(iconfont);
        tvXueyang.setTypeface(iconfont);
        tvXueya.setTypeface(iconfont);
        tvTiwen.setTypeface(iconfont);

        //MIO
        tvRect2 = (TextView) view.findViewById(R.id.rec_2);
        tvMioXinlv = (TextView) view.findViewById(R.id.assess_heartrate);
        tvMIOSport = (TextView) view.findViewById(R.id.assess_sport);
        tvMioSleep = (TextView) view.findViewById(R.id.assess_sleep);
        tvRect2.setTypeface(iconfont);
        tvMioXinlv.setTypeface(iconfont);
        tvMIOSport.setTypeface(iconfont);
        tvMioSleep.setTypeface(iconfont);

        //手动输入
        tvRect3 = (TextView) view.findViewById(R.id.rec_3);
        tvXtxz = (TextView) view.findViewById(R.id.assess_blood_sugar);
        tvWomanWeight = (TextView) view.findViewById(R.id.assess_woman_weight);
        tvHeightWeight = (TextView) view.findViewById(R.id.asses_height_weight);
        tvRect3.setTypeface(iconfont);
        tvXtxz.setTypeface(iconfont);
        tvWomanWeight.setTypeface(iconfont);
        tvHeightWeight.setTypeface(iconfont);
        rlWomanWeight = (RelativeLayout) view.findViewById(R.id.rl_woman_weight);
        rlHeightWeight = (RelativeLayout) view.findViewById(R.id.rl_height_weight);
        rlXtxz = (RelativeLayout) view.findViewById(R.id.rl_blood_sugar);

        //点击事件
        tvXindian.setOnClickListener(this);
        tvXueya.setOnClickListener(this);
        tvTiwen.setOnClickListener(this);
        tvXueyang.setOnClickListener(this);
        tvHeightWeight.setOnClickListener(this);
        tvWomanWeight.setOnClickListener(this);
        tvXtxz.setOnClickListener(this);
        tvMioXinlv.setOnClickListener(this);
        tvMioSleep.setOnClickListener(this);
        tvMIOSport.setOnClickListener(this);

        rlWomanWeight.setVisibility(View.GONE);
        rlHeightWeight.setVisibility(View.GONE);


        int type = User.getInstance(getActivity()).getType();
        if (type ==3) {
            rlHeightWeight.setVisibility(View.VISIBLE);
        } else if (type==2) {
            rlWomanWeight.setVisibility(View.VISIBLE);
        } else {
            rlXtxz.setVisibility(View.VISIBLE);
        }
//        if(User.getInstance(getActivity()) instanceof Child){
//            btnHeight.setVisibility(View.VISIBLE);
//        }else if(User.getInstance(getActivity()) instanceof Lady){
//            btnWeight.setVisibility(View.VISIBLE);
//        }else {
//            btnXtxz.setVisibility(View.VISIBLE);
//        }



//        GlobalImage globalImage = GlobalImage.getInstance(getActivity().getApplicationContext());
//        btnXindian.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xindian)));
//        btnXueya.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueya)));
//        btnTiwen.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.tiwen)));
//        btnXueyang.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.xueyang)));
//        btnHeight.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.childhw)));
//        btnWeight.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.ladyw)));
//        btnXtxz.setBackground(new BitmapDrawable(getActivity().getResources(), globalImage.get(R.drawable.olderxtxz)));

        initViewPager();

        return view;
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.assess_ecg:
                if (!checkBlueTooth()) {
                    return;
                }
                intent = new Intent(getActivity(), ElectrocarDiogramActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_blood_pressure:
                if (!checkBlueTooth()) {
                    return;
                }
                intent = new Intent(getActivity(), BloodActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_temprature:
                if (!checkBlueTooth()) {
                    return;
                }
                intent = new Intent(getActivity(), BodyTemperatureActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_blood_oxygen:
                if (!checkBlueTooth()) {
                    return;
                }
                intent = new Intent(getActivity(), BloodOxygenActivity.class);
                startActivity(intent);
                break;
            case R.id.asses_height_weight:
                intent = new Intent(getActivity(), ChildHeightAndWeightActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_woman_weight:
                intent = new Intent(getActivity(), LadyWeightActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_blood_sugar:
                intent = new Intent(getActivity(), OlderXtXzActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_sleep:
                intent = new Intent(getActivity(), SleepActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_sport:
                intent = new Intent(getActivity(), StepActivity.class);
                startActivity(intent);
                break;
            case R.id.assess_heartrate:
                intent = new Intent(getActivity(), HeartRateActivity.class);
                startActivity(intent);
                break;
        }
    }


    //检测蓝牙是否可用
    private boolean checkBlueTooth() {
        //获取蓝牙信息
        String bluetoothMac = BluetoothDevice.getDeviceMac(getActivity());
        if (!CommonUtils.isAvailable(bluetoothMac)) {
            //没有蓝牙 弹出选择框选择蓝牙
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getActivity(), "本机没有连接蓝牙", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            } else {
                showBondedMac();
            }
            return false;
        }
        bluetoothThread = new BluetoothConnectThread(bluetoothMac);//创建蓝牙线程
        if (!bluetoothThread.checkBtAdapterExists()) {
            Toast.makeText(getActivity(), "本机没有连接蓝牙", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!bluetoothThread.checkBtAdapterEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            return false;
        }
        return true;
    }


    //显示已查询蓝牙
    public void showBondedMac() {
        //扫描并显示所有已配对的蓝牙设备
        Set<android.bluetooth.BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() == 0) {
            Toast.makeText(getActivity(), "没有搜索到任何蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }
        deviceNames = new String[devices.size()];
        deviceMac = new String[devices.size()];

        //获取蓝牙名称
        int i = 0;
        for (android.bluetooth.BluetoothDevice device : devices) {
            deviceNames[i] = device.getName();
            deviceMac[i] = device.getAddress();
            i++;
        }

                //弹出对话框
        new AlertDialog.Builder(getActivity()).setTitle("选择蓝牙设备")
                .setSingleChoiceItems(deviceNames, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mac = deviceMac[which];
                        BluetoothDevice.setDeviceMac(getActivity(), mac);
                        BluetoothDevice.setDeviceName(getActivity(), deviceNames[which]);
                        Toast.makeText(getActivity(), "请继续点击测量", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    //向设备发送蓝牙命令
    public void sendCommandToBluetooth(int type) {
        OutputStream outStr = null;
        BluetoothCommands btcommand = new BluetoothCommands();
        byte[] comm = btcommand.getBluetoothCommand(type, getActivity().getApplicationContext());
        try {
            outStr = socket.getOutputStream();
            outStr.write(comm);
        } catch (IOException e) {
            Log.e("JQB", e.toString());
        } finally {
            if (outStr != null) {
                try {
                    outStr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //停止蓝牙线程
    public void stopBluetoothThread() {
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
            Thread dummy = bluetoothThread;
            bluetoothThread = null;
            dummy.interrupt();
            socket = null;
        }
    }



    private void initViewPager() {
        //      这里存放的是四张广告背景
        List<View> advPics = new ArrayList<View>();
        ImageView img1 = new ImageView(getActivity());
        img1.setBackgroundResource(R.drawable.ad1);
        advPics.add(img1);

        ImageView img2 = new ImageView(getActivity());
        img2.setBackgroundResource(R.drawable.ad2);
        advPics.add(img2);

        ImageView img3 = new ImageView(getActivity());
        img3.setBackgroundResource(R.drawable.ad3);
        advPics.add(img3);

        ImageView img4 = new ImageView(getActivity());
        img4.setBackgroundResource(R.drawable.ad4);
        advPics.add(img4);

        //      对imageviews进行填充
        imageViews = new ImageView[advPics.size()];


        advPager.setAdapter(new AdvAdapter(advPics));
        advPager.setOnPageChangeListener(new GuidePageChangeListener());
        advPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }

        }).start();
    }


    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > imageViews.length - 1) {
            what.getAndAdd(-4);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            advPager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }

    };

    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);

        }

    }

    private final class AdvAdapter extends PagerAdapter {
        private List<View> views = null;

        public AdvAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

    }
}