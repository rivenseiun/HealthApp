package com.whu.healthapp.activity.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hubu.fan.utils.HttpUtils.ResponseListener;
import com.hubu.fan.utils.SystemBarTintManager;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.asses.AssesBodyTemperature;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.bluetooth.BluetoothCommands;
import com.whu.healthapp.bluetooth.BluetoothConnectThread;
import com.whu.healthapp.bluetooth.BluetoothDevice;
import com.whu.healthapp.bluetooth.data.DataHandler;
import com.whu.healthapp.bluetooth.data.DataPath;
import com.whu.healthapp.db.TiwenDB;
import com.whu.healthapp.utils.DateUtils;
import com.whu.healthapp.utils.TextSpeak;
import com.whu.healthapp.view.Thermometer;

import net.sf.json.JSONObject;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BodyTemperatureActivity extends BaseActivity implements ResponseListener {

    private int START_OR_PAUSE;
    private Thermometer thermometer;//温度计

    //蓝牙连接
    BluetoothSocket socket; //蓝牙socket
    BluetoothConnectThread bluetoothThread;
    boolean isBluetoothConnected = false;   //蓝牙连接成功


    private OutputStream outStr = null;//发送命令的流

    private boolean isTesting = false;//是否正在测试

    private TextView tvTiwen; //体温显示
    private TextView tvTag;//体温标志

    private ProgressDialog pdWait;//等待框
    private int dbTestId;
    private String temperature;

    private String grade;//健康指数评分
    private String comment;//健康建议

    private TextSpeak speak;

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
        if (android.os.Build.VERSION.SDK_INT > 18) {
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏
            tintManager.setTintColor(Color.parseColor("#FFFF6666"));
        }
        setContentView(R.layout.activity_test_bodytemperature);
        //保持屏幕常量
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.acquire();
        speak = new TextSpeak(this);
        thermometer = (Thermometer) findViewById(R.id.the_measure);
        tvTiwen = (TextView) findViewById(R.id.tv_tiwennum);
        tvTag = (TextView) findViewById(R.id.tv_tag);
        START_OR_PAUSE = 0;

        //开启蓝牙测试
        bluetoothThread = new BluetoothConnectThread(BluetoothDevice.getDeviceMac(this)); //创建bluetoothThrad
        if (!bluetoothThread.checkBtAdapterExists()) {
            Toast.makeText(getBaseContext(), getResources().getString(R.string.nobluetooth), Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!bluetoothThread.checkBtAdapterEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
        }
        if (pdWait == null) {
            pdWait = new ProgressDialog(this);
            pdWait.setTitle("提示");
            pdWait.setMessage("正在连接设备...");
            pdWait.setCancelable(false);
            pdWait.show();
        }
//	    	    imgSensorGuide.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = bluetoothThread.connect();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (socket != null) { //连接成功
                            if (pdWait != null) {
                                pdWait.dismiss();
                                pdWait = null;
                            }
                            isBluetoothConnected = true;
                            startMeasurement();//发送测量指令，开始测量
                            isTesting = true;
                            runnow();     //开始读数
                        } else {
                            //connection failed
                            finish();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.bluetootherror), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }).start();

        dbTestId = getIntent().getIntExtra("dbtest_id", -1);
    }

    private InputStream inStr = null;
    private File filepath;
    private int head1 = 0;
    private int head2 = 0; //有效数据包头，0x55 0xaa,然后23个字节
    int bostat, bovalue, bopulse; //血氧状态、血氧值和血氧脉率
    private int[] bowave;
    private int WAITNUM = 50; //默认10，每次刷新显示点个数,即隔几个点刷新一次，数越大中间间隔时间越长，对内存无影响
    private int WAITTIME = 20; //读数缓冲时间<20ms，过大导致内存不足
    private Handler showDataHandler = new Handler();
    float temp1, temp2;
    ; //体温数据


    //子函数-直接从串口读数并处理
    public void runnow() {
        new Thread(new Runnable() {
            int[] datas = new int[21];
            int pt = 1;
            byte[] readbytes = new byte[23]; //读入23个字节
            int[] data = new int[23];

            public void run() {
                try {
                    inStr = socket.getInputStream();
                    if (inStr != null) {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.CHINA);
                        String date = sDateFormat.format(new java.util.Date());
                        filepath = new File(DataPath.savepicUrl,
                                "EasyHealth" + date + ".sav");
                        File file = new File(DataPath.savepicUrl);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (!filepath.exists()) {
                            filepath.createNewFile();
                        }
                        FileOutputStream outfile = new FileOutputStream(filepath, true);

                        head1 = inStr.read() & 0xff;
                        while (head1 != -1 && isBluetoothConnected) {
                            if (head1 == 0x55) {  //检查数据头1，含数据头一共25个字节
                                head2 = inStr.read() & 0xff;
                                if (head2 == 0xaa) { //检查数据头2
                                    inStr.read(readbytes);
                                    outfile.write(readbytes);
                                    for (int i = 0; i < 23; i++) {
                                        data[i] = (int) readbytes[i] & 0xff;
                                    }
                                    if (DataHandler.check(data)) {
                                        datas = DataHandler.handle(data);
                                    }
                                    //如果datas不为空，处理数据
                                    if (datas != null) {
                                        switch (datas[2]) { //即DATA0内容
                                            case 13: //体温1,需要换算
                                                temp1 = (float) ((datas[5] + 200.0) / 10.0);
                                                break;
                                            case 15: //体温2，需要换算
                                                temp2 = (float) ((datas[5] + 200.0) / 10.0);
                                                break;
                                            default:
                                                break;
                                        }

//										respt[pt - 1] = datas[19]; //datas19-呼吸波形（0-63）
                                        if (pt % WAITNUM == 0) { //每WAITNUM个点后刷新一次曲线
                                            showDataHandler.post(showDataRunnable); //显示数据
                                            pt = 0;
                                        }
                                        pt++;
                                        try {
                                            Thread.sleep(WAITTIME); //采样间隔时间--影响心电图显示
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }    //end of head2
                            } //end of head1
                            head1 = inStr.read() & 0xff; //如果head1和head2不符合，读下一个字节
                        }
                        inStr.close();
                        outfile.flush();
                        outfile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private long lastSpeakTime = 0;
    //子函数- 显示体检数据
    Runnable showDataRunnable = new Runnable() {
        public void run() {
            if (isTesting) {
                if (temp1 > 45 || temp1 == 0) {
                    tvTiwen.setText("测量中");
                    thermometer.setTargetTemperature(0);
                } else {
                    tvTiwen.setText(String.valueOf(temp1));
                    thermometer.setTargetTemperature(temp1);
                }
            }


            if (temp1 < 45 && temp1 > 30) {
                if (temp1 > 37.5) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    if (time - lastSpeakTime > 4000) {
                        speak.speak("体温偏高");
                        lastSpeakTime = time;

                    }
                    tvTag.setText("体温偏高");
                } else if (temp1 < 36) {
                    tvTag.setText("体温偏低，请继续测量");

                } else {
                    tvTag.setText("体温正常");
                }
            } else {
                tvTag.setText("正在测量");
            }


        }
    };


    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                //弹出提示框
                new AlertDialog.Builder(this).setTitle("提示").setMessage("是否直接退出？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //停止退出
                        if (bluetoothThread != null) {
                            stopBluetoothThread(); //紧急终止血压测量
                        }
                        showDataHandler.removeCallbacks(showDataRunnable);
                        finish();
                    }
                }).show();
                break;
            case R.id.tv_pause:
                //停止运行
                if (isTesting) {
                    isTesting = false;
                    ((TextView) v).setText("开始");
                } else {
                    isTesting = true;
                    ((TextView) v).setText("暂停");
                }
                break;
            case R.id.tv_stop:
                //停止并生成报告
                showDataHandler.removeCallbacks(showDataRunnable);
                if (bluetoothThread != null) {
                    if (pdWait == null) {
                        pdWait = new ProgressDialog(this);
                        pdWait.setTitle("提示");
                        pdWait.setMessage("正在生成报告请等待...");
                        pdWait.setCancelable(false);
                        pdWait.show();
                    }
                    stopBluetoothThread(); //紧急终止血压测量
                    if (temp1 > 45 || temp1 == 0) {
                        finish();
                        showTip(getResources().getString(R.string.nouserfuldata));
                        return;
                    }
                    //存放数据
                    TestDataBean data = new TestDataBean();
                    data.setKind(TestDataBean.TEST_TIWEN);
                    data.setTiwen(tvTiwen.getText().toString().trim());
                    //data.setTiwen((int)temp1 + "");
                    data.setUserId(User.getInstance(BodyTemperatureActivity.this).getPhone());
                    data.setTime(DateUtils.getCurrentDate());
                    TiwenDB db = new TiwenDB(this);
                    db.addData(data);
                    temperature= temp1+"";
                    postHandler.sendEmptyMessage(0);
                    String url = "http://47.92.55.20:80/Healthy/data.mvc?action=2";
                    RequestParams params = new RequestParams(url);
                    String phone = User.getInstance(BodyTemperatureActivity.this).getPhone();
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("temperature", temperature);
                    x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onFinished() {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(BodyTemperatureActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        }


                        @Override
                        public void onSuccess(String result) {
                            if(result.equals("error")){
                                Toast.makeText(BodyTemperatureActivity.this, "错误：用户不存在", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(BodyTemperatureActivity.this, "上传成功", Toast.LENGTH_LONG).show();

                                //成功获取json类型的体重数据，对其进行解析
                                String jsonStr = result;
                                JSONObject job = JSONObject.fromObject(jsonStr);
                                grade = job.getString("grade");
                                comment = job.getString("comment");
                                Intent intent=new Intent(BodyTemperatureActivity.this, AssesBodyTemperature.class);
                                intent.putExtra("temperature",tvTiwen.getText());
                                intent.putExtra("grade",grade);
                                intent.putExtra("comment",comment);
                                startActivity(intent);
                                finish();

                                Toast.makeText(BodyTemperatureActivity.this, result, Toast.LENGTH_LONG).show();
                            }




                        }
                    });

                }


                break;
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("提示").setMessage("是否直接退出？").setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //停止退出
                if (bluetoothThread != null) {
                    stopBluetoothThread();
                }
                showDataHandler.removeCallbacks(showDataRunnable);
                finish();
            }
        }).show();
    }

    //停止蓝牙线程和数据显示
    public void stopBluetoothThread() {
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
            Thread dummy = bluetoothThread;
            bluetoothThread = null;
            dummy.interrupt();
        }
        if (showDataHandler != null) {
            showDataHandler.removeCallbacks(showDataRunnable);
        }
    }

    //向设备发送命令
    private void startMeasurement() {
        sendCommandToBluetooth(12); //滤波开关
        sendCommandToBluetooth(13); //起搏检测
        sendCommandToBluetooth(14); //窒息报警时间
        sendCommandToBluetooth(15); //血氧检测模式
        sendCommandToBluetooth(17); //工频模式
        sendCommandToBluetooth(81); //设置5导联
        sendCommandToBluetooth(992); //开启除颤输出同步
        sendCommandToBluetooth(994); //呼吸开
    }

    //发送蓝牙命令
    public void sendCommandToBluetooth(int type) {
        BluetoothCommands btcommand = new BluetoothCommands();
        byte[] comm = btcommand.getBluetoothCommand(type, getApplicationContext());
        try {
            outStr = socket.getOutputStream();
            outStr.write(comm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRightCompleteResponse(int requestCode, String result, Object tag) {

    }

    @Override
    public void onUpdateResponse(int requestCode, int progress, Object tag) {

    }

    @Override
    public void onErrorResponse(int requestCode, String result, Object tag) {

    }
}
