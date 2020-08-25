package com.whu.healthapp.activity.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hubu.fan.utils.HttpUtils.ResponseListener;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.asses.AssesBloodOxygen;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.bluetooth.BluetoothCommands;
import com.whu.healthapp.bluetooth.BluetoothConnectThread;
import com.whu.healthapp.bluetooth.BluetoothDevice;
import com.whu.healthapp.bluetooth.data.DataHandler;
import com.whu.healthapp.bluetooth.data.DataPath;
import com.whu.healthapp.db.XueyangDB;
import com.whu.healthapp.utils.DateUtils;
import com.whu.healthapp.utils.LoaclPath;
import com.whu.healthapp.utils.TextSpeak;

import net.sf.json.JSONObject;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
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

public class BloodOxygenActivity extends BaseActivity implements ResponseListener {

    //蓝牙连接
    BluetoothSocket socket; //蓝牙socket
    BluetoothConnectThread bluetoothThread;
    boolean isBluetoothConnected = false;   //蓝牙连接成功

    private LinearLayout bodrawinglayout;//绘制父控件
    private XYSeries boseries; //存储血氧值
    private XYMultipleSeriesDataset boDataset;
    private XYMultipleSeriesRenderer borenderer;
    private GraphicalView bochart;
    private OutputStream outStr = null;//发送命令的流

    private int XBONUM;//血氧曲线和心电曲线每屏显示个数
    private double BOWIDNUM = 10;

    private TextView tvBo, tvBp, tvBoSta;
    private TextView tvStop, tvPause;

    private boolean isTesting = false;//是否正在测试

    private ProgressDialog pdWait;//等待框
    private TextView tvTestTag;//正在测量，请稍后

    private String xueyangAddress = "";
    private int dbTestId;
    private String postXueYang;
    private String postMaiLv;

    private TextSpeak speak;

    private String grade;//健康指数评分
    private String comment;//健康建议
    private Drawable dStart, dPause;//定义开始和暂停的图像


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
        setContentView(R.layout.activity_test_bloodoxygen);
        //保持屏幕常量
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.acquire();
        tvTestTag = (TextView) findViewById(R.id.tv_testtag);
        speak = new TextSpeak(this);
        tvBo = (TextView) findViewById(R.id.tv_bonum);
        tvBp = (TextView) findViewById(R.id.tv_bpnum);
        tvStop = (TextView) findViewById(R.id.tv_stop);
        tvPause = (TextView) findViewById(R.id.tv_pause);
        tvBoSta = (TextView) findViewById(R.id.tv_bostatues);
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
        //init初始化
        //根据屏幕大小确定显示点数
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenwidth = dm.widthPixels;
        bowave = new int[WAITNUM];
        XBONUM = (int) ((float) screenwidth / BOWIDNUM);

        //蓝牙连接成功
        //绘制血氧图
        bodrawinglayout = (LinearLayout) findViewById(R.id.linearLayoutBODrawing);
        boseries = new XYSeries(getResources().getString(R.string.oxygen));
        boDataset = new XYMultipleSeriesDataset();             //创建一个数据集的实例，这个数据集将被用来创建图表
        boDataset.addSeries(boseries);             //将点集添加到这个数据集中
        borenderer = buildRenderer(Color.BLUE, PointStyle.POINT, true);
        borenderer.setChartTitle("血氧饱和度曲线");//设置为X轴的标题
        setChartSettings(borenderer, "X", "Y", 0, XBONUM, 0, 100, Color.WHITE, Color.WHITE);
        bochart = ChartFactory.getLineChartView(getApplicationContext(), boDataset, borenderer);
        bodrawinglayout.addView(bochart, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

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
                                            case 0: //心电状态ECGS,与心电控制命令相同
                                                break;
                                            case 1: //脉搏氧状态SATS
                                                bostat = datas[5];
                                                break;
                                            case 4: //脉搏氧脉率
                                                bopulse = datas[5];
                                                break;
                                            case 7: //脉搏氧饱和度
                                                bovalue = datas[5];
                                                break;
                                            default:
                                                break;
                                        }

                                        if (datas[18] != 1) { //datas18-血氧波形（0-99）
                                            bowave[pt - 1] = datas[18];
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

    //判断是否刷新图像
    private long firstDisplay = 0;
    private boolean isDisplay = false;
    //子函数- 显示体检数据
    Runnable showDataRunnable = new Runnable() {
        public void run() {
            if (isTesting) {
                if (firstDisplay == 0) {
                    firstDisplay = Calendar.getInstance().getTimeInMillis();
                }
                if (Calendar.getInstance().getTimeInMillis() - firstDisplay > 3000) {
                    isDisplay = true;
                }
                if (isDisplay) {
                    updateBoChart();
                }
                if (bovalue == 0 || bopulse == 0) {
                    tvTestTag.setVisibility(View.VISIBLE);
                } else {
                    tvTestTag.setVisibility(View.GONE);
                }
//                if (bovalue == 0)
//                    tvBo.setText("测量中");
//                else
                tvBo.setText(bovalue + "");
//                if (bopulse == 0)
//                    tvBp.setText("测量中");
//                else
                tvBp.setText(bopulse + "");
                if (bostat == 128) {
                    tvBoSta.setText("手指连接状态：" + "未连接");
                } else {
                    tvBoSta.setText("手指连接状态：" + "已连接");
                }

                if (bovalue < 95 && bovalue != 0) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    if (time - lastSpeakTime > 4000) {
                        speak.speak("血氧饱和度低于正常值95");
                        lastSpeakTime = time;
                    }
                }

                if (!(bopulse <= 100 && bopulse >= 60) && bopulse != 0) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    if (time - lastSpeakTime > 4000) {
                        speak.speak("脉率不在正常值范围内");
                        lastSpeakTime = time;
                    }
                }

            }
//			txtOxygen.setText(String.valueOf(bovalue)); //display blood ic_tongji_oxygen value

            //System.gc();//系统内存垃圾回收

        }
    };

    //子函数-刷新血氧图
    private void updateBoChart() {
        //设置好下一个需要增加的节点
        int LINETOP = 100; //坐标区间：0-100
        int[] boy = new int[WAITNUM];
        for (int i = 0; i < WAITNUM; i++) {
            boy[i] = (int) ((float) bowave[i] / 3) + LINETOP / 2;
        }
        //boDataset.removeSeries(boseries);
        boDataset.clear();//移除所有series
        boDataset.addSeries(getSeries(WAITNUM, XBONUM, boseries, boy, 20));     //内存泄露2
        //bochart.invalidate();
        bochart.postInvalidate();  //在非UI主线程中调用postInvalidate()，具体参考api
    }

    //子函数-获取XYSeries
    private XYSeries getSeries(int bufnum, int xnum, XYSeries series, int[] yvalue, int mnum) {
        int[] xv = new int[xnum];
        int[] yv = new int[xnum];

        int length = series.getItemCount();    //当点数超过XNUM时，长度永远是XNUM

        if (length > xnum) {
            //将旧的点集中x和y的数值取出来放入backup中，并且将x的值加bufnum，造成曲线向右平移的效果
            for (int i = 0; i < xnum; i++) {
                xv[xnum - i - 1] = (int) series.getX(length - i - 1) - bufnum; //x值向右移bufnum个数
                yv[xnum - i - 1] = (int) series.getY(length - i - 1);
            }
            length = xnum;
        } else {
            for (int i = 0; i < length; i++) {
                xv[length - i - 1] = (int) series.getX(length - i - 1) - bufnum; //x值向右移bufnum个数
                yv[length - i - 1] = (int) series.getY(length - i - 1);
            }
        }
        series.clear();            //点集先清空，为了做成新的点集而准备
        for (int k = 0; k < length; k++) {
            series.add(xv[k], yv[k]);
        }
        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
        for (int i = 0; i < bufnum; i++) {
            series.add(length - bufnum + i, yvalue[i]);
        }
        //

        return series;
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

    private SharedPreferences userInfo;//配置信息

    //向设备发送命令
    private void startMeasurement() {
        userInfo = getSharedPreferences("easy_health_user_info", 0);
        sendCommandToBluetooth(9); //发送呼吸控制命令
        sendCommandToBluetooth(12); //滤波开关
        sendCommandToBluetooth(15); //血氧检测模式
        sendCommandToBluetooth(17); //工频模式

        if (Integer.parseInt(userInfo.getString("ecgjump", "0")) == 0) {
            sendCommandToBluetooth(992);//除颤同步开
        } else {
            sendCommandToBluetooth(993);//除颤同步关
        }
        sendCommandToBluetooth(994); //呼吸开
    }

    //子函数-图表设置
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setBackgroundColor(Color.BLACK);
//		renderer.setYLabels(10);
//		renderer.setXLabels(40);
        renderer.setMarginsColor(getResources().getColor(R.color.maincolor));
        renderer.setMargins(new int[]{0, 0, 0, 0});
        renderer.setShowLabels(false);
        renderer.setShowGridX(false);// 是否显示网格
//		renderer.setAntialiasing(true);
//		renderer.setGridColor(getResources().getColor(R.color.white));// 设置网格颜色
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPointSize((float) 2);
        renderer.setPanEnabled(false, false);//设置x,y坐标轴不会因用户划动屏幕而移动(false false不移动,true false移动)
        renderer.setClickEnabled(true); //可以点击true否则FALSE
        renderer.setShowLegend(false);
    }

    //子函数-曲线相关
    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(2);
        renderer.addSeriesRenderer(r);
        return renderer;
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

    public void viewClick(View view) {
        switch (view.getId()) {
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
                    if (bovalue == 0 && bopulse == 0) {
                        finish();
                        showTip(getResources().getString(R.string.nouserfuldata));
                        return;
                    }
                    //存放数据
                    TestDataBean data = new TestDataBean();
                    data.setKind(TestDataBean.TEST_XUEYANG);
                    data.setXueyangbhd(tvBo.getText().toString().trim());
                    data.setMailv(tvBp.getText().toString().trim());
                    //保存血氧图
                    Bitmap bitmap = null;
                    bitmap = convertViewToBitmap(bodrawinglayout);
                    writeSDcard(bitmap);
                    data.setXueyangAddress(xueyangAddress);
                    data.setUserId(User.getInstance(BloodOxygenActivity.this).getPhone());
                    data.setTime(DateUtils.getCurrentDate());
                    XueyangDB db = new XueyangDB(this);
                    db.addData(data);

                    postXueYang=tvBo.getText().toString().trim();
                    postMaiLv=tvBp.getText().toString().trim();
                    postHandler.sendEmptyMessage(0);

                    String url = "http://47.92.55.20:80/Healthy/data.mvc?action=0";
                    RequestParams params = new RequestParams(url);
                    String phone = User.getInstance(BloodOxygenActivity.this).getPhone();
                    params.addBodyParameter("phone", phone);
                    params.addBodyParameter("oxygen", postXueYang);
                    x.http().post(params, new org.xutils.common.Callback.CommonCallback<String>() {
                        @Override
                        public void onFinished() {
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(BloodOxygenActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();

                        }


                        @Override
                        public void onSuccess(String result) {
                            if(result.equals("error")){
                                Toast.makeText(BloodOxygenActivity.this, "错误：用户不存在", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(BloodOxygenActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                //Toast.makeText(LadyWeightActivity.this, result, Toast.LENGTH_LONG).show();

                                //成功获取json类型的体重数据，对其进行解析
                                String jsonStr =result;
                                JSONObject job = JSONObject.fromObject(jsonStr);
                                grade = job.getString("grade");
                                comment = job.getString("comment");
                                Intent intent=new Intent(BloodOxygenActivity.this, AssesBloodOxygen.class);
                                intent.putExtra("xueyang",postXueYang);
                                intent.putExtra("mailv",postMaiLv);
                                intent.putExtra("grade",grade);
                                intent.putExtra("comment",comment);
                                startActivity(intent);
                                finish();

                                //Toast.makeText(LadyWeightActivity.this, result, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

                break;
            case R.id.tv_pause:
                //停止运行
                if (isTesting) {
                    isTesting = false;
                    tvPause.setText("开始");
                    if (dStart == null) {
                        dStart = getResources().getDrawable(R.drawable.ic_test_start);
                    }
                    tvPause.setCompoundDrawablesWithIntrinsicBounds(dStart, null, null, null);
                } else {
                    isTesting = true;
                    tvPause.setText("暂停");
                    if (dPause == null) {
                        dPause = getResources().getDrawable(R.drawable.ic_test_pause);
                    }
                    tvPause.setCompoundDrawablesWithIntrinsicBounds(dPause, null, null, null);
                }
                break;
        }

    }

    public Bitmap convertViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
//	    	view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//	        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//	        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    private void writeSDcard(Bitmap bitmap) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.CHINA);
        String date = sDateFormat.format(new java.util.Date());
        String iamgeName = "";

        iamgeName = "xueyang" + date + ".png";
        xueyangAddress = LoaclPath.getUserFolder(this) + "/" + iamgeName;
        File bitmapFile = new File(xueyangAddress);
        FileOutputStream bitmapWtriter = null;
        try {
            bitmapWtriter = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, bitmapWtriter);
            bitmapWtriter.close();
            bitmap.recycle();
            bitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
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
}
