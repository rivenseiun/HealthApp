package com.hubu.fan.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.hubu.fan.utils.CommonUtils;

/**
 * @author fan
 */
public class HttpUtilsConn {

    private Context mContext; // 当前上下文

//    private static String session = null; // 联网session请求
    private static final String SESSION = "SESSION";
    private SharedPreferences sp ;

    ResponseListener mResponseListener; // 请求响应监听器

    // 请求成功与否的请求码
    private static final int HTTP_SUCCESS = 1;
    private static final int HTTP_FAILED = -1;

    // msg消息传递所需要的参数，为内置参数，可以不予理会
    private static final String REQUESTCODETAG = "REQUESTCODETAG";
    private static final String RESULTTAG = "RESULTTAG";

    private int connectTime = 20;
    private int socketTime = 20;

    public HttpUtilsConn(Context context, ResponseListener listener) {
        mContext = context;
        mResponseListener = listener;
        httpHandler = new HttpHandler(mResponseListener,context);
        sp = context.getSharedPreferences(SESSION,Context.MODE_PRIVATE);
    }

    public HttpUtilsConn(Context context, ResponseListener listener, int socketTime, int connectTime) {
        mContext = context;
        mResponseListener = listener;
        httpHandler = new HttpHandler(mResponseListener, context);
        this.socketTime = socketTime;
        this.connectTime = connectTime;
        sp = context.getSharedPreferences(SESSION,Context.MODE_PRIVATE);
    }

    public final static void clearSession(Context context){
        context.getSharedPreferences(SESSION,Context.MODE_PRIVATE).edit().clear().commit();

    }

    /**
     * 发送数据请求响应的接口
     *
     * @author fan
     */
    public interface ResponseListener {
        /**
         * 请求正确响应的方法
         *
         * @param requestCode
         * @param result      下载文件时返回的数据是文件的路径
         */
        public void onRightCompleteResponse(int requestCode, String result, Object tag);

        /**
         * 下载文件进度更新的方法
         *
         * @param requestCode
         * @param progress
         */
        public void onUpdateResponse(int requestCode, int progress, Object tag);

        /**
         * 请求错误的响应的方法
         *
         * @param requestCode
         * @param result
         */
        public void onErrorResponse(int requestCode, String result, Object tag);
    }

    /**
     * http请求后执行的主线程操作
     */
    private HttpHandler httpHandler;

    private static class HttpHandler extends Handler {
        private final WeakReference<ResponseListener> mResponseListener;
        private final WeakReference<Context> mContext;

        public HttpHandler(ResponseListener responseListener, Context context) {
            mResponseListener = new WeakReference<ResponseListener>(responseListener);
            mContext = new WeakReference<Context>(context);
        }

        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HTTP_SUCCESS:
                        if (mResponseListener.get() != null && mContext.get() !=null) {
                            mResponseListener.get().onRightCompleteResponse(msg.getData().getInt(REQUESTCODETAG),
                                    msg.getData().getString(RESULTTAG), msg.obj);
                        }
                        break;
                    case HTTP_FAILED:
                        if (mResponseListener.get() != null && mContext.get() != null) {
                            mResponseListener.get().onErrorResponse(msg.getData().getInt(REQUESTCODETAG),
                                    msg.getData().getString(RESULTTAG), msg.obj);
                        }
                        break;
                    default:
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        ;
    }

    /**
     * 获取httpclient实例
     *
     * @return
     */
    public HttpClient getHttpClient() {
        HttpClient client;
        // 设置参数
        HttpParams params = new BasicHttpParams();
        // 设置传递字符集
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(params, connectTime * 1000);
        // 响应超时
        HttpConnectionParams.setSoTimeout(params, socketTime * 1000);
        // 设置协议
        SchemeRegistry schreg = new SchemeRegistry();
        schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager conman = new ThreadSafeClientConnManager(params, schreg);
        client = new DefaultHttpClient(conman, params);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        return client;
    }

    /**
     * 执行post操作
     *
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public Thread doPost(final String uri, final Map<String, String> params, final int requestCode, final Object tag) {
        // 开启线程，请求网络数据
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(REQUESTCODETAG, requestCode);
                msg.setData(data);
                OutputStream os = null;
                InputStream is = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setReadTimeout(socketTime);
                    conn.setConnectTimeout(connectTime);
                    //设置session
                    String session = sp.getString(SESSION,"");
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("cookie",session);
                    }
                    conn.setRequestProperty("Content-Type","text/html");
                    //设置数据
                    String uploadData = getData(params);
                    conn.setRequestProperty("Content-Length", ""+uploadData.getBytes().length);
                    conn.setDoOutput(true); // 发送POST请求必须设置允许输出
                    conn.setDoInput(true); // 发送POST请求必须设置允许输入
                    //获取输出流
                    os = conn.getOutputStream();
                    os.write(uploadData.getBytes());
                    os.flush();
                    int statuCode = conn.getResponseCode();
                    if(statuCode != 200){
                        msg.what = HTTP_FAILED;
                        msg.obj = tag;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    //設置session
                    setSession(conn);
                    is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    // 定义读取的长度
                    int len = 0;
                    // 定义缓冲区
                    byte buffer[] = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        bos.write(buffer, 0, len);
                    }
                    final String result = new String(bos.toByteArray());
                    bos.close();
                    msg.what = HTTP_SUCCESS;
                    msg.obj = tag;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = tag;
                    data.putString(RESULTTAG, "网络请求本地错误，请查看相关日志信息");
                    httpHandler.sendMessage(msg);
                }finally {
                    if(is!=null){
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(os!=null){
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * 执行get操作
     *
     * @param uri
     * @param
     * @return
     * @throws Exception
     */
    public Thread doGet(final String uri, final int requestCode, final Object tag) {
        // 开启线程，请求网络数据
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(REQUESTCODETAG, requestCode);
                msg.setData(data);
                OutputStream os = null;
                InputStream is = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(socketTime);
                    conn.setConnectTimeout(connectTime);
                    //设置session
                    String session = sp.getString(SESSION,"");
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("cookie",session);
                    }
                    conn.setRequestProperty("Content-Type","text/html");
                    conn.setDoOutput(true); // 发送POST请求必须设置允许输出
                    conn.setDoInput(true); // 发送POST请求必须设置允许输入
                    int statuCode = conn.getResponseCode();
                    if(statuCode != 200){
                        msg.what = HTTP_FAILED;
                        msg.obj = tag;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    //設置session
                    setSession(conn);
                    is = conn.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    // 定义读取的长度
                    int len = 0;
                    // 定义缓冲区
                    byte buffer[] = new byte[1024];
                    // 按照缓冲区的大小，循环读取
                    while ((len = is.read(buffer)) != -1) {
                        // 根据读取的长度写入到os对象中
                        bos.write(buffer, 0, len);
                    }
                    final String result = new String(bos.toByteArray());
                    bos.close();
                    msg.what = HTTP_SUCCESS;
                    msg.obj = tag;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = tag;
                    data.putString(RESULTTAG, "网络请求本地错误，请查看相关日志信息");
                    httpHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
        return thread;
    }

    /**
     * 下载文件功能模块
     *
     * @param uri
     * @param requestCode
     */
    public void doLoadFile(final String uri, final int requestCode, final Object tag) {

        // 对进度的线程控制创建
        AsyncTask<Void, Integer, String> asyncTask = new AsyncTask<Void, Integer, String>() {
            boolean isError = false;// 判断执行是否出错

            @Override
            protected String doInBackground(Void... params) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(REQUESTCODETAG, requestCode);
                msg.setData(data);
                InputStream is = null;
                OutputStream os = null;
                HttpURLConnection conn = null;
                // 缓冲文件路径
                String dirStr = Environment.getExternalStorageDirectory() + "/temp/";
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String path = dirStr + Calendar.getInstance().getTimeInMillis();
                try {
                    // 获取结果并判断//获取的结果为数据流
                    conn = (HttpURLConnection) new URL(uri).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    String session = sp.getString(SESSION,"");
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("cookie",session);
                    }
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    int statuCode = conn.getResponseCode();
                    if (statuCode != 200) {
                        msg.what = HTTP_FAILED;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        isError = true;
                        return null;
                    }
                    long currentSize = 0;
                    is = conn.getInputStream();
                    long fileSize = conn.getContentLength();
                    // 创建文件
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    os = new FileOutputStream(file);
                    // 写入文件
                    byte[] b = new byte[500];
                    int len = -1;
                    while ((len = is.read(b)) != -1) {
                        os.write(b, 0, len);
                        currentSize += len;
                        if (fileSize != -1) {
                            publishProgress((int) (currentSize * 100 / fileSize));
                        }
                    }
                    if (fileSize == -1) {
                        publishProgress(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    data.putString(RESULTTAG, "网络请求本地错误，请查看相关日志信息");
                    httpHandler.sendMessage(msg);
                    msg.obj = tag;
                    isError = true;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return path;
            }

            /**
             * 进度更新
             */
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mResponseListener.onUpdateResponse(requestCode, values[0], tag);
            }

            /**
             * 任务完成
             */
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                /**
                 * 网络本地文件地址映射
                 */
//                if (mContext instanceof HubuActivity) {
//                    ((HubuActivity) mContext).putTempFile(uri, result);
//                }
//                if (!isError) {
//                    mResponseListener.onRightCompleteResponse(requestCode, result, tag);
//                }
            }
        };
        asyncTask.execute();
    }

    /**
     * 下载文件功能模块
     *
     * @param uri
     * @param requestCode
     */
    public void doLoadFileAsync(final String uri, final int requestCode, final Object tag) {

        // 对进度的线程控制创建
        AsyncTask<Void, Integer, String> asyncTask = new AsyncTask<Void, Integer, String>() {
            boolean isError = false;// 判断执行是否出错

            @Override
            protected String doInBackground(Void... params) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(REQUESTCODETAG, requestCode);
                msg.setData(data);
                InputStream is = null;
                OutputStream os = null;
                HttpURLConnection conn = null;
                // 缓冲文件路径
                String dirStr = Environment.getExternalStorageDirectory() + "/temp/";
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String path = dirStr + Calendar.getInstance().getTimeInMillis();
                try {
                    // 获取结果并判断//获取的结果为数据流
                    conn = (HttpURLConnection) new URL(uri).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    // 封装session
                    String session = sp.getString(SESSION,"");
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("cookie",session);
                    }
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    int statuCode = conn.getResponseCode();
                    if (statuCode != 200) {
                        msg.what = HTTP_FAILED;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        isError = true;
                        return null;
                    }
                    long currentSize = 0;
                    is = conn.getInputStream();
                    long fileSize = conn.getContentLength();
                    // 创建文件
                    File file = new File(path);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    os = new FileOutputStream(file);
                    // 写入文件
                    byte[] b = new byte[500];
                    int len = -1;
                    while ((len = is.read(b)) != -1) {
                        os.write(b, 0, len);
                        currentSize += len;
                        if (fileSize != -1) {
                            publishProgress((int) (currentSize * 100 / fileSize));
                        }
                    }
                    if (fileSize == -1) {
                        publishProgress(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    data.putString(RESULTTAG, "网络请求本地错误，请查看相关日志信息");
                    httpHandler.sendMessage(msg);
                    msg.obj = tag;
                    isError = true;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return path;
            }

            /**
             * 进度更新
             */
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                mResponseListener.onUpdateResponse(requestCode, values[0], tag);
            }

            /**
             * 任务完成
             */
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                /**
                 * 网络本地文件地址映射
                 */
//                if (mContext instanceof HubuActivity) {
//                    ((HubuActivity) mContext).putTempFile(uri, result);
//                }
//                if (!isError) {
//                    mResponseListener.onRightCompleteResponse(requestCode, result, tag);
//                }
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 上传复杂的文件信息
     *
     * @param uri
     * @param params
     * @return
     * @throws Exception
     */
    public void doMultiesPost(final String uri, final Map<String, String> params, final Map<String, String> files,
                              final int requestCode, final Object tag) {
        // 开启线程，请求网络数据
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putInt(REQUESTCODETAG, requestCode);
                msg.setData(data);
                try {
                    HttpClient client;
                    client = getHttpClient();
                   // PostMethod post = new PostMethod(uri);
                    HttpPost post = new HttpPost(uri);
                    String session = sp.getString(SESSION,"");
                    if (CommonUtils.isAvailable(session)) {
                        post.setHeader("Cookie", "JSESSIONID=" + session);
                    }
                    MultipartEntity multipartEntity = new MultipartEntity();
                    if (params != null) {
                        for (Entry<String, String> entry : params.entrySet()) {
                            if (CommonUtils.isAvailable(entry.getValue())) {
                                if (CommonUtils.isAvailable(entry.getValue())) {
                                    multipartEntity.addPart(entry.getKey(),
                                            new StringBody(entry.getValue(), Charset.forName("utf-8")));
                                }
                            }
                        }
                    }
                    if (files != null) {
                        for (Entry<String, String> entry : files.entrySet()) {
                            if (CommonUtils.isAvailable(entry.getValue())) {
                                File file = new File(entry.getValue());
                                if (file.exists()) {
                                    multipartEntity.addPart(entry.getKey(), new FileBody(new File(entry.getValue())));
                                }
                            }
                        }
                    }


                    HttpResponse response = client.execute(post);
                    int statuCode = response.getStatusLine().getStatusCode();
                    if (statuCode != HttpStatus.SC_OK) {
                        msg.what = HTTP_FAILED;
                        msg.obj = tag;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    // 获取cookie并设置，保持持续连接
                    List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if (cookie.getName().equals("JSESSIONID")) {
                                sp.edit().putString(SESSION,cookie.getValue()).commit();
                            }
                        }
                    }
                    // 获取结果并判断//获取的结果为JSon信息
                    String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    msg.what = HTTP_SUCCESS;
                    msg.obj = tag;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = tag;
                    data.putString(RESULTTAG, "网络请求本地错误，请查看相关日志信息");
                    httpHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }

    /**
     * 查看网络是否连接
     *
     * @param activity
     * @return
     */
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setSession(HttpURLConnection conn){

        String cookieval = conn.getHeaderField("set-cookie");
        String sessionid;
        if(cookieval != null) {
            sessionid = cookieval.substring(0, cookieval.indexOf(";"));
            if(CommonUtils.isAvailable(sessionid)){
                sp.edit().putString(SESSION,sessionid).commit();
            }
        }

    }

    private String getData(Map<String,String> datas){
        String uploadData = "";
        try {
            //判断是否为第一个参数
            boolean isFirst = true;
            for (Entry<String,String> data : datas.entrySet()){
                if (isFirst) {
                    uploadData =URLEncoder.encode(data.getKey(), "UTF-8")+"="+ URLEncoder.encode(data.getValue(), "UTF-8");
                }else{
                        uploadData =uploadData+"&"+ URLEncoder.encode(data.getKey(),"UTF-8")+"="+ URLEncoder.encode(data.getValue(),"UTF-8");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return uploadData;
    }

}
