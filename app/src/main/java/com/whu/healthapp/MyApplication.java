package com.whu.healthapp;

import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.xutils.x;

import java.io.File;

import cn.jpush.android.api.JPushInterface;



public class MyApplication extends MultiDexApplication {

    private WindowManager wm;//手机的基本信息
    private static int windowWidth;//窗体宽度
    private static int windowHeight;//窗体高度
    private static float density;//频幕密度
    private static int dpi;//dpi


    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        //获取设备消息
        wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
        windowWidth = metric.widthPixels;     // 屏幕宽度（像素）
        windowHeight = metric.heightPixels;   // 屏幕高度（像素）
        density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        dpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        //初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                //配置打印信息
                LogUtils.getLogConfig().configAllowLog(true)//是否允许打印
                        .configTagPrefix("LogUtils")//日志log的前缀
                        .configFormatTag("%c{-5}")//tag名称
                        .configShowBorders(true);//是否显示边界

                //配置 ImageLoader
                File cacheDir = StorageUtils.getCacheDirectory(MyApplication.this);  //缓存文件夹路径
                ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                        .Builder(MyApplication.this)
//                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                        .threadPoolSize(10)//线程池内加载的数量
                        .threadPriority(Thread.NORM_PRIORITY - 2)//default 设置当前线程的优先级
                        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCacheSize(10 * 1024 * 1024)// 内存缓存的最大值
                        .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                        .diskCacheSize(50 * 1024 * 1024)
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                        .diskCacheFileCount(100) //缓存的文件数量
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                        .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        .imageDownloader(new BaseImageDownloader(MyApplication.this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                        .writeDebugLogs() // Remove for release app
                        .build();//开始构建
                ImageLoader.getInstance().init(configuration);//全局初始化此配置

                JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
                JPushInterface.init(MyApplication.this);            // 初始化 JPush
//                JPushInterface.stopPush(getApplicationContext());  //暂停通知
//                JPushInterface.resumePush(getApplicationContext()); //恢复通知
//                String rid = JPushInterface.getRegistrationID(getApplicationContext()); //获取用户唯一标识 JPUSH

            }
        }).start();


    }

    /**
     * 提示消息
     *
     * @param message
     */
    protected void showTip(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /**
     * 获取窗口高度
     */
    public static int getWindowsHeight() {
        return windowHeight;
    }


    /**
     * 获取窗口宽度
     */
    public static int getWindowsWidth() {
        return windowWidth;
    }


    /**
     * 获取屏幕密度
     */
    public static int getDpi() {
        return dpi;
    }


    /**
     * 获取屏幕密度
     */
    public static float getDensity() {
        return density;
    }

}
