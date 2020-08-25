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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.hubu.fan.utils.HttpUtils.ResponseListener;
import com.hubu.fan.utils.SystemBarTintManager;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.bluetooth.BluetoothCommands;
import com.whu.healthapp.bluetooth.BluetoothConnectThread;
import com.whu.healthapp.bluetooth.BluetoothDevice;
import com.whu.healthapp.bluetooth.data.DataHandler;
import com.whu.healthapp.bluetooth.data.DataPath;
import com.whu.healthapp.db.XindianDB;
import com.whu.healthapp.utils.DateUtils;
import com.whu.healthapp.utils.LoaclPath;
import com.whu.healthapp.utils.MyValueFormatter;
import com.whu.healthapp.utils.TextSpeak;
import com.whu.healthapp.view.MyMarkerView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ElectrocarDiogramActivity extends BaseActivity implements ResponseListener {

    private TextView tvECGNum, tvECGCad;

    //蓝牙连接
    BluetoothSocket socket; //蓝牙socket
    BluetoothConnectThread bluetoothThread;
    boolean isBluetoothConnected = false;   //蓝牙连接成功

    private OutputStream outStr = null;//发送命令的流

    //心电
    private int XECGNUM;//血氧曲线和心电曲线每屏显示个数
    private int[] ecgi, ecgii, ecgiii, ecgavr, ecgavl, ecgavf, ecgv; //储存待显示值
    private int WAITNUM = 50; //默认10，每次刷新显示点个数,即隔几个点刷新一次，数越大中间间隔时间越长，对内存无影响
    private double ECGWIDNUM = 3;  //默认3，值越小波形越好（屏显点越多）(width/WIDNUM)，但点数过多易导致内存不足
    private int ECGYNUM = 7; //I,II,III,aVR,aVL,aVF,V
    private XYSeries[] ecgseries; //7条心电曲线，存储心电曲线的值
    private XYMultipleSeriesDataset[] ecgDatasets;
    private GraphicalView[] ecgcharts;
    private XYMultipleSeriesRenderer[] ecgrenderers;
    private LinearLayout[] ecgdrawinglayouts;
    private LinearLayout ecgdrawinglayoutMian;
    private boolean isEcgdrawinglayouts;
    private LineChart mChart;
    MyMarkerView mv;
    int hrdata = 0; // 心率数据
    String ecgcad; //心电变异结果描述
    private TextView tvtitle;
    private TextView tvZen;
    private TextView tvHz;
    private int xhz = 0;
    private String[] xhzstr = {"50Hz", "60Hz"};

    private float zengyilv = 1;
    private Typeface mTf;
    private int num;
    private float[] Zen = {0.5f, 1f, 2f};
    private int XZen = 2;

    private ProgressDialog pdWait;//等待框
    private boolean isTesting = false;//是否正在测试

    private Button btnPause;

    private Bitmap bitmap;//心电图存放图像
    private String iamgeName;//心电图名称
    private String ecgimgPath;//心电图存放路径

    private int resprate;//呼吸
    private TextView tvHuxilv;

    private SharedPreferences userInfo ;//配置信息
    private int dbTestId;

    private String dataAddress="";//数据地址

    private TextSpeak speak;
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
        setContentView(R.layout.activity_test_electrocardiogram);
        //保持屏幕常量
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//MyTag可以随便写,可以写应用名称等
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyTag");
//在释放之前，屏幕一直亮着（有可能会变暗,但是还可以看到屏幕内容,换成PowerManager.SCREEN_BRIGHT_WAKE_LOCK不会变暗）
        wl.acquire();
        speak = new TextSpeak(this);
        userInfo = getSharedPreferences("easy_health_user_info", 0);
        tvHuxilv = (TextView) findViewById(R.id.tv_huxilv);
        btnPause = (Button) findViewById(R.id.btn_pause);
        tvECGNum = (TextView) findViewById(R.id.tv_ecgnum);
        tvECGCad = (TextView) findViewById(R.id.tv_ecgcad);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenwidth = dm.widthPixels;
        XECGNUM = (int) ((float) screenwidth / ECGWIDNUM); //honor1/6: 18-2.5, honor X1:19-3
        //初始化一次显示的点数
        ecgi = new int[WAITNUM];
        ecgii = new int[WAITNUM];
        ecgiii = new int[WAITNUM];
        ecgavr = new int[WAITNUM];
        ecgavl = new int[WAITNUM];
        ecgavf = new int[WAITNUM];
        ecgv = new int[WAITNUM];
        tvtitle = (TextView) findViewById(R.id.textviewECGDrawingdan);
        tvZen = (TextView) findViewById(R.id.textviewZen);
        XZen = Integer.parseInt(userInfo.getString("ecggain", "0"));
        tvZen.setText("增益：" + Zen[XZen % Zen.length] + "X");
        tvZen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XZen++;
                tvZen.setText("增益：" + Zen[XZen % Zen.length] + "X");
            }
        });
        tvHz = (TextView) findViewById(R.id.textviewlvbo);
        tvHz.setText("滤波模式：" + xhzstr[xhz % xhzstr.length]);
        tvHz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                xhz++;
                if (xhz % 2 == 0) {
                    sendCommandToBluetooth(996);
                } else {
                    sendCommandToBluetooth(997);
                }
                tvHz.setText("滤波模式：" + xhzstr[xhz % xhzstr.length]);
            }
        });

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
                            runnow();     //开始读数
                            isTesting = true;
                        } else {
                            //connection failed
                            finish();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.bluetootherror), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }).start();

        int[] linearlayoutEcgDrawing = new int[]{R.id.linearLayoutECGDrawing, R.id.linearLayoutECGDrawing1, R.id.linearLayoutECGDrawing2,
                R.id.linearLayoutECGDrawing3, R.id.linearLayoutECGDrawing4, R.id.linearLayoutECGDrawing5, R.id.linearLayoutECGDrawing6};
        //画心电图
        ecgdrawinglayouts = new LinearLayout[linearlayoutEcgDrawing.length];
        ecgdrawinglayoutMian = (LinearLayout) findViewById(R.id.linearLayoutECGDrawingMian);
        for (int i = 0; i < linearlayoutEcgDrawing.length; i++) {
            ecgdrawinglayouts[i] = (LinearLayout) findViewById(linearlayoutEcgDrawing[i]);
        }
        isEcgdrawinglayouts = false;
        mChart = (LineChart) findViewById(R.id.chart1);

        setupChart(mChart, Color.WHITE); //
        ecgseries = new XYSeries[ECGYNUM]; //7条心电图形
        ecgcharts = new GraphicalView[ECGYNUM];
        ecgDatasets = new XYMultipleSeriesDataset[ECGYNUM];
        ecgrenderers = new XYMultipleSeriesRenderer[ECGYNUM];
        XYSeriesRenderer ecgrender = new XYSeriesRenderer();
        ecgrender.setColor(Color.BLACK);//设置颜色
        ecgrender.setPointStyle(PointStyle.POINT);//设置点的样式
        ecgrender.setFillPoints(true);//填充点（显示的点是空心还是实心）
        ecgrender.setLineWidth(1);//设置线宽
        for (int i = 0; i < ecgseries.length; i++) {
            ecgseries[i] = new XYSeries(getResources().getString(R.string.ecg));
            ecgDatasets[i] = new XYMultipleSeriesDataset();
            ecgrenderers[i] = new XYMultipleSeriesRenderer();
            ecgDatasets[i].addSeries(ecgseries[i]);
            ecgrenderers[i].addSeriesRenderer(ecgrender);
            ecgrenderers[i].setChartTitle("I");
            ecgrenderers[i].setChartTitleTextSize(20);
            setChartSettings(ecgrenderers[i], "X", "Y", 0, XECGNUM, -60, 60, Color.WHITE, Color.WHITE);//
            ecgcharts[i] = ChartFactory.getLineChartView(getApplicationContext(), ecgDatasets[i], ecgrenderers[i]);
            ecgdrawinglayouts[i].addView(ecgcharts[i], new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }
        addOnclickListen();//设置心电图点击放大
        dbTestId = getIntent().getIntExtra("dbtest_id",-1);
    }


    private void addOnclickListen() {
        // TODO Auto-generated method stub
        final String[] name = {"Ⅰ", "Ⅱ", "Ⅲ", "AVR", "AVL", "AVF", "V"};
        for (int i = 0; i < ecgcharts.length; i++) {
            final int mynum = i;
            ecgcharts[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (isEcgdrawinglayouts) {
//						for (int i = 0; i < ecgdrawinglayouts.length; i++) {
//							ecgdrawinglayouts[i].setVisibility(View.VISIBLE);
                        ecgdrawinglayoutMian.setVisibility(View.VISIBLE);
                        mChart.setVisibility(View.GONE);
                        tvtitle.setVisibility(View.GONE);
                        tvZen.setClickable(false);
                        tvZen.setText("增益：" + Zen[XZen % Zen.length] + "X");
                        isEcgdrawinglayouts = false;
//						}
                    } else {//显示单张心电图
                        num = mynum;
                        tvZen.setClickable(true);
                        tvZen.setText("增益：" + Zen[XZen % Zen.length] + "X");
                        tvtitle.setVisibility(View.VISIBLE);
                        tvtitle.setText(name[mynum]);
                        mChart.setVisibility(View.VISIBLE);
                        ecgdrawinglayoutMian.setVisibility(View.GONE);
                        isEcgdrawinglayouts = true;
                    }
                }
            });
        }
    }

    //初始化心电图
    private void setupChart(LineChart chart, int color) {

        // no description text
        chart.setDescription("");
        chart.setNoDataText("");
        chart.setNoDataTextDescription("");

        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
//	        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);
        // enable touch gestures
        chart.setTouchEnabled(true);
//	        chart.setDrawYValues(true);
//	        chart.setValuePaintColor(Color.WHITE) ;
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);
        mChart.setDrawMarkerViews(false);
        mChart.setHighlightEnabled(false);
//        mChart.setBorderColor(Color.BLACK);
        chart.setBackgroundColor(color);
        // set custom chart offsets (automatic offset calculation is hereby disabled)
//	        chart.setViewPortOffsets(10, 0, 10, 0);
        // add data
//	        chart.setData(data);
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();
        l.setEnabled(false);
        ValueFormatter custom = new MyValueFormatter();
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(2.0f);
        leftAxis.setAxisMinValue(-2.0f);
        leftAxis.setDrawLabels(true);
        leftAxis.setStartAtZero(false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setTextColor(Color.BLACK);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(false);
        mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setAxisMaxValue(100);
        rightAxis.setStartAtZero(false);
        rightAxis.setAxisMinValue(-100);
        rightAxis.setDrawGridLines(false);

        chart.getAxisLeft().setEnabled(true);
        chart.getAxisRight().setEnabled(false);

        chart.getXAxis().setEnabled(false);

        // animate calls invalidate()...
//	        chart.animateX(2500);
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
		renderer.setGridColor(getResources().getColor(R.color.black));// 设置网格颜色
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPointSize((float) 2);
        renderer.setPanEnabled(false, false);//设置x,y坐标轴不会因用户划动屏幕而移动(false false不移动,true false移动)
        renderer.setClickEnabled(true); //可以点击true否则FALSE
        renderer.setShowLegend(false);
    }


    private InputStream inStr = null;
    private File filepath;
    private int head1 = 0;
    private int head2 = 0; //有效数据包头，0x55 0xaa,然后23个字节
    int ECGSTATUS = 255; //心电变异分析
    private Handler showDataHandler = new Handler();
    private int WAITTIME = 20; //读数缓冲时间<20ms，过大导致内存不足

    private void runnow() {
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
                        dataAddress = filepath.getAbsolutePath();
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
                                    outfile.flush();
                                    for (int i = 0; i < 23; i++) {
                                        data[i] = (int) readbytes[i] & 0xff;
                                    }
                                    if (DataHandler.check(data)) {
                                        datas = DataHandler.handle(data);
                                    }
                                    //如果datas不为空，处理数据
                                    if (datas != null) {
                                        switch (datas[2]) { //即DATA0内容

                                            case 3: //心率
                                                hrdata = datas[5];
                                                break;
                                            case 12: //呼吸率
                                                resprate = datas[5];
                                                break;
                                            default:
                                                break;
                                        }

                                        //心电变异数据datas[0]-STATUS0, datas[20]-DATA1
                                        if (datas[0] == 0) {
                                            ECGSTATUS = datas[20] >> 4 & 0x0f; //DATA1，取高4位
                                        }
                                        //心电图数据,datas[6]-I，datas[10]-II,datas[14]-V，III，aVR,aVL,aVF
                                        //只取ECGWI1/ECGWII1//ECGWV1 ,512 是中心线
                                        if (datas[6] != 0 && datas[10] != 0 && datas[14] != 0) {
                                            ecgi[pt - 1] = datas[6];
                                            ecgii[pt - 1] = datas[10]; //II
                                            ecgiii[pt - 1] = datas[10] - datas[6] + 512; //III
                                            ecgavr[pt - 1] = 512 * 2 - datas[6] / 2 - datas[10] / 2; //AVR
                                            ecgavl[pt - 1] = 512 / 2 + datas[6] - datas[10] / 2; //AVL
                                            ecgavf[pt - 1] = 512 / 2 + datas[10] - datas[6] / 2; //AVF
                                            ecgv[pt - 1] = datas[14]; //V
                                        }

                                        if (pt % WAITNUM == 0) { //每WAITNUM个点后刷新一次曲线
                                            showDataHandler.post(showDataRunnable); //显示数据
                                            pt = 0;
                                        }
                                        pt++;
                                        try {
                                            Thread.sleep(WAITTIME);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }    //end of head2
                            } //end of head1
                            head1 = inStr.read() & 0xff; //如果head1和head2不符合，读下一个字节
                        }
                        inStr.close();

                        outfile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();

    }

    private long lastSpeakTime = 0;

    private long firstDisplay = 0;
    private boolean isDisplay = false;
    //子函数- 显示体检数据
    Runnable showDataRunnable = new Runnable() {
        public void run() {
            if(firstDisplay == 0){
                firstDisplay = Calendar.getInstance().getTimeInMillis();
            }
            if(Calendar.getInstance().getTimeInMillis() - firstDisplay>3000) {
                isDisplay = true;
            }
            if(isDisplay) {
                updateEcgChart();
            }

            tvECGNum.setText(hrdata + "");
            ecgcad = getResources().getString(R.string.ecgok);
            if(hrdata != 0 ) {
                tvHuxilv.setText(resprate + "");
//			//显示心电变异分析
                switch (ECGSTATUS) {
                    case 2:
                        ecgcad = getResources().getString(R.string.ecgafib);
                        break;
                    case 3:
                        ecgcad = getResources().getString(R.string.ecgasystole);
                        break;
                    case 4:
                        ecgcad = getResources().getString(R.string.ecgtachy);
                        break;
                    case 5:
                        ecgcad = getResources().getString(R.string.ecgvtachy);
                        break;
                    case 6:
                        ecgcad = getResources().getString(R.string.ecgbrady);
                        break;
                    case 7:
                        ecgcad = getResources().getString(R.string.ecgmissbeat);
                        break;
                    case 8:
                        ecgcad = getResources().getString(R.string.ecgfreqpvc);
                        break;
                    case 9:
                        ecgcad = getResources().getString(R.string.ecgrunpvc);
                        break;
                    case 10:
                        ecgcad = getResources().getString(R.string.ecgtrigemin);
                        break;
                    case 11:
                        ecgcad = getResources().getString(R.string.ecgbigeminy);
                        break;
                    case 12:
                        ecgcad = getResources().getString(R.string.ecgront);
                        break;
                    case 13:
                        ecgcad = getResources().getString(R.string.ecginterpvc);
                        break;
                    case 14:
                        ecgcad = getResources().getString(R.string.ecgcouplet);
                        break;
                    case 15:
                        ecgcad = getResources().getString(R.string.ecgpvc);
                        break;
                    default:
                        break;
                }
                tvECGCad.setText(ecgcad);
            }else{

                tvHuxilv.setText("测量中");
                tvECGCad.setText("测量中");
            }
            if(resprate > 60){
                tvHuxilv.setText("测量中");
            }

            if(!ecgcad.equals(getResources().getString(R.string.ecgok))){
                long time = Calendar.getInstance().getTimeInMillis();
                if(time-lastSpeakTime>4000){
                    speak.speak("心电异常");
                    lastSpeakTime = time;
                }
            }
        }
    };


    //子函数-刷新心电图
    private void updateEcgChart() {
        if (isTesting) {
            int[] ecgyi = new int[WAITNUM];
            int[] ecgyii = new int[WAITNUM];
            int[] ecgyiii = new int[WAITNUM];
            int[] ecgyavr = new int[WAITNUM];
            int[] ecgyavl = new int[WAITNUM];
            int[] ecgyavf = new int[WAITNUM];
            int[] ecgyv = new int[WAITNUM];

            StringBuilder sb = new StringBuilder();
            sb = new StringBuilder();
            int zengyilvmax = 1;
            int zengyilvmin = 1;
            for (int i = 0; i < WAITNUM; i++) {
                if ((ecgii[i] - 512) > zengyilvmax) {
                    zengyilvmax = ecgii[i] - 512;
                }
                if ((ecgii[i] - 512) < zengyilvmin) {
                    zengyilvmin = ecgii[i] - 512;
                }

                sb.append(ecgii[i] - 512 + ",");
            }
            zengyilv = 100f / (float) (Math.abs(zengyilvmax) + Math.abs(zengyilvmin));
            sb.append("zengyilv=" + zengyilv + ",,,");
            sb.append("zengyilvmax=" + zengyilvmax + ",,,");
            sb.append("zengyilvmin=" + zengyilvmin + ",,,");
            sb.append("zengyilvmin222=" + (float) (Math.abs(zengyilvmax) + Math.abs(zengyilvmin)) + ",,,");
            sb = new StringBuilder();
            for (int i = 0; i < WAITNUM; i++) {
                ecgyi[i] = xjuchi((int) (((float) ecgi[i] - 512)));
                ecgyii[i] = xjuchi((int) (((float) ecgii[i] - 512)));
                ecgyiii[i] = xjuchi((int) (((float) ecgiii[i] - 512)));
                ecgyavr[i] = xjuchi((int) (((float) ecgavr[i] - 512)));
                ecgyavl[i] = xjuchi((int) (((float) ecgavl[i] - 512)));
                ecgyavf[i] = xjuchi((int) (((float) ecgavf[i] - 512)));
                ecgyv[i] = xjuchi((int) (((float) ecgv[i] - 512)));
//  			 ecgyavf[i] = (int)(((float)ecgavf[i] -512)) + ybase[5];
//  			 ecgyv[i] = (int)(((float)ecgv[i] -512)) + ybase[6];
                sb.append(ecgyii[i] + ",");
            }
            sb = new StringBuilder();

            int[][] ecgyss = new int[][]{ecgyi, ecgyii, ecgyiii, ecgyavr, ecgyavl, ecgyavf, ecgyv};
            for (int i = 0; i < ecgDatasets.length; i++) {

                ecgDatasets[i].clear(); //移除所有series

                ecgDatasets[i].addSeries(getSeries(WAITNUM, XECGNUM, ecgseries[i], ecgyss[i], i));    //内存泄露3
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[1],ecgyii));
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[2],ecgyiii));
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[3],ecgyavr));
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[4],ecgyavl));
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[5],ecgyavf));
//  		 ecgDataset.addSeries(getSeries(WAITNUM,XECGNUM,ecgseries[6],ecgyv));
                //ecgchart.invalidate(); //在非UI主线程中调用postInvalidate()，具体参考api
                ecgcharts[i].postInvalidate();
            }
            if (isEcgdrawinglayouts) {
                mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
                int length = ecgseries[num].getItemCount();    //当点数超过XNUM时，长度永远是XNUM
                int[] yv = new int[length];
                //将旧的点集中x和y的数值取出来放入backup中，并且将x的值加bufnum，造成曲线向右平移的效果
                for (int i = 0; i < length; i++) {
                    yv[i] = (int) ecgseries[num].getY(i);
                }
                LineData data = getData(yv);
                ;
                data.setValueTypeface(mTf);
                mChart.clear();
                mChart.setData(data);
                mChart.postInvalidate();
            }
        }
    }

    private LineData getData(int[] range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < range.length; i++) {
            xVals.add(i + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < range.length; i++) {
            float val = (float) range[i];
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setFillColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setDrawCircles(false);
        set1.setColor(Color.BLACK);
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.3f);
        set1.setHighLightColor(Color.BLACK);
        set1.setDrawValues(false);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        return data;
    }

    //消除晓得锯齿
    public int xjuchi(int x) {
        if (x < 3 && x > -3) {
            x = 0;
        }
        return (int) (x * Zen[XZen % Zen.length] * zengyilv);
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


    //向设备发送命令
    private void startMeasurement() {
        sendCommandToBluetooth(6); //报警音量音调
        sendCommandToBluetooth(7); //报警方式
        sendCommandToBluetooth(8); //发送心电控制命令
        sendCommandToBluetooth(11); //心率平均个数
        sendCommandToBluetooth(12); //滤波开关
        sendCommandToBluetooth(13); //起搏检测
        sendCommandToBluetooth(14); //窒息报警时间
        sendCommandToBluetooth(17); //工频模式
        //sendCommandToBluetooth(80); //心率通道计算选择命令
        sendCommandToBluetooth(81); //设置5导联

        sendCommandToBluetooth(993); //开启除颤输出同步
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

    //点击事件
    public void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_stop:
                showDataHandler.removeCallbacks(showDataRunnable);
                //停止并生成报告
                if (bluetoothThread != null) {
                    if (pdWait == null) {
                        pdWait = new ProgressDialog(this);
                        pdWait.setTitle("提示");
                        pdWait.setMessage("正在生成报告请等待...");
                        pdWait.setCancelable(false);
                        pdWait.show();
                    }
                    stopBluetoothThread();
                    if(hrdata ==0){
                        finish();
                        showTip(getResources().getString(R.string.nouserfuldata));
                        return;
                    }
                    //存放数据
                    TestDataBean data = new TestDataBean();
                    data.setKind(TestDataBean.TEST_XINLV);

                    data.setXinlv(tvECGNum.getText().toString().trim());
                    //获取心电图像数据
                    bitmap = convertViewToBitmap(ecgdrawinglayoutMian);
                    writeSDcard(bitmap);
                    data.setEcgImgPath(ecgimgPath);
                    data.setHuxi(resprate + "");
                    data.setDataAddress(dataAddress);
                    data.setUserId(User.getInstance(ElectrocarDiogramActivity.this).getPhone());
                    data.setTime(DateUtils.getCurrentDate());
                    XindianDB db = new XindianDB(this);
                    db.addData(data);
                }
                finish();
                break;
            case R.id.btn_pause:
                //停止运行
                if (isTesting) {
                    isTesting = false;
                    btnPause.setText("开始");
                } else {
                    isTesting = true;
                    btnPause.setText("暂停");
                }
                break;
            case R.id.tv_back:
                if (isEcgdrawinglayouts) {
                    ecgdrawinglayoutMian.setVisibility(View.VISIBLE);
                    mChart.setVisibility(View.GONE);
                    mChart.setDrawMarkerViews(false);
                    mChart.setHighlightEnabled(false);
                    mChart.invalidate();
                    isEcgdrawinglayouts = false;
                    tvtitle.setVisibility(View.GONE);
                } else {
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
                }
                break;
            case R.id.tv_details:
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
        iamgeName = "xindian" + date + ".png";
        ecgimgPath = LoaclPath.getUserFolder(this) + "/" + iamgeName;
        File bitmapFile = new File(ecgimgPath);
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
                    stopBluetoothThread(); //紧急终止血压测量
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
