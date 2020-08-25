package com.hubu.fan.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hubu.fan.app.R;
import com.hubu.fan.oom.SPImageDiskMapped;
import com.hubu.fan.utils.CommonUtils;

/**
 * @author fan
 */
public class HttpUtils {

    private Context mContext; // 当前上下文

    //Session信息
    private static  String session = "";

    ResponseListener mResponseListener; // 请求响应监听器

    // 请求成功与否的请求码
    private static final int HTTP_SUCCESS = 1;
    private static final int HTTP_FAILED = -1;

    // msg消息传递所需要的参数，为内置参数，可以不予理会
    private static final String REQUESTCODETAG = "REQUESTCODETAG";
    private static final String RESULTTAG = "RESULTTAG";

    private int connectTime = 20;
    private int socketTime = 20;

    public HttpUtils(Context context, ResponseListener listener) {
        this(context,listener,20,20);
    }

    public HttpUtils(Context context, ResponseListener listener, int socketTime, int connectTime) {
        mContext = context;
        mResponseListener = listener;
        httpHandler = new HttpHandler(mResponseListener, context);
        this.socketTime = socketTime;
        this.connectTime = connectTime;

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
        public void onRightCompleteResponse(int requestCode, String result, Object obj);

        /**
         * 下载文件进度更新的方法
         *
         * @param requestCode
         * @param progress
         */
        public void onUpdateResponse(int requestCode, int progress, Object obj);

        /**
         * 请求错误的响应的方法
         *
         * @param requestCode
         * @param result
         */
        public void onErrorResponse(int requestCode, String result, Object obj);
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
    public Thread doPost(final String uri, final Map<String, String> params, final int requestCode, final Object obj) {
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
                    HttpPost post = new HttpPost(uri);
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                    //设置session
                    if (CommonUtils.isAvailable(session)) {
                        post.setHeader("Cookie", "JSESSIONID=" + session);
                    }
                    //设置参数
                    if (params != null) {
                        for (Entry<String, String> entry : params.entrySet()) {
                            NameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue());
                            parameters.add(param);
                        }
                    }
//                    //添加设备信息
//                    TelephonyManager TelephonyMgr = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
//                    String aid = TelephonyMgr.getDeviceId();
//                    NameValuePair AID = new BasicNameValuePair("AID",aid);
//                    parameters.add(AID);

                    post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
                    HttpResponse response = client.execute(post);
                    int statuCode = response.getStatusLine().getStatusCode();
                    if (statuCode != HttpStatus.SC_OK) {
                        msg.what = HTTP_FAILED;
                        msg.obj = obj;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    // 获取cookie并设置，保持持续连接
                    setSession(client);

                    // 获取结果并判断//获取的结果为JSon信息
                    String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    msg.what = HTTP_SUCCESS;
                    msg.obj = obj;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = obj;
                    data.putString(RESULTTAG, mContext.getResources().getString(R.string.hubu_httputils_timeout));
                    httpHandler.sendMessage(msg);
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
    public Thread doGet(final String uri, final int requestCode, final Object obj) {
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
                    HttpGet get = new HttpGet(new String(uri.getBytes(), "UTF-8"));
                    //设置session
                    if (CommonUtils.isAvailable(session)) {
                        get.setHeader("Cookie", "JSESSIONID=" + session);
                    }
                    HttpResponse response = client.execute(get);
                    int statuCode = response.getStatusLine().getStatusCode();
                    if (statuCode != HttpStatus.SC_OK) {
                        msg.what = HTTP_FAILED;
                        msg.obj = obj;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    // 获取cookie并设置，保持持续连接
                    setSession(client);
                    // 获取结果并判断//获取的结果为JSon信息
                    String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    msg.what = HTTP_SUCCESS;
                    msg.obj = obj;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = obj;
                    data.putString(RESULTTAG, mContext.getResources().getString(R.string.hubu_httputils_timeout));
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
    public AsyncTask doLoadFile(final String uri, final int requestCode, final Object obj) {

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
                mContext.getCacheDir();
                File dir = mContext.getCacheDir();
                String path = dir.getAbsolutePath()+"/" + Calendar.getInstance().getTimeInMillis();
                try {
                    // 获取结果并判断//获取的结果为数据流
                    conn = (HttpURLConnection) new URL(uri).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    // 封装session
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("Cookie", "JSESSIONID=" + session);
                    }
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    int statuCode = conn.getResponseCode();
                    if (statuCode != HttpStatus.SC_OK) {
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
                    data.putString(RESULTTAG, mContext.getResources().getString(R.string.hubu_httputils_timeout));
                    httpHandler.sendMessage(msg);
                    msg.obj = obj;
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
                mResponseListener.onUpdateResponse(requestCode, values[0], obj);
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
                if (!isError) {
//                    //地址映射
                    SPImageDiskMapped.getInstance(mContext).map(uri,result);
                    mResponseListener.onRightCompleteResponse(requestCode, result, obj);
                    return ;
                }

            }
        };
        asyncTask.execute();
        return asyncTask;
    }

    /**
     * 下载文件功能模块
     *
     * @param uri
     * @param requestCode
     */
    public AsyncTask doLoadFileAsync(final String uri, final int requestCode, final Object obj) {

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
                mContext.getCacheDir();
                File dir = mContext.getCacheDir();
                String path = dir.getAbsolutePath()+"/" + Calendar.getInstance().getTimeInMillis();
                try {
                    // 获取结果并判断//获取的结果为数据流
                    conn = (HttpURLConnection) new URL(uri).openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    // 封装session
                    if (CommonUtils.isAvailable(session)) {
                        conn.setRequestProperty("Cookie", "JSESSIONID=" + session);
                    }
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    int statuCode = conn.getResponseCode();
                    if (statuCode != HttpStatus.SC_OK) {
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
                    data.putString(RESULTTAG ,mContext.getResources().getString(R.string.hubu_httputils_timeout));
                    httpHandler.sendMessage(msg);
                    msg.obj = obj;
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
                mResponseListener.onUpdateResponse(requestCode, values[0], obj);
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
                if (!isError) {
                    //地址映射
                    SPImageDiskMapped.getInstance(mContext).map(uri,result);
                    mResponseListener.onRightCompleteResponse(requestCode, result, obj);
                    return ;
                }


            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return asyncTask;
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
                              final int requestCode, final Object obj) {
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
                    HttpPost post = new HttpPost(uri);
                    if (CommonUtils.isAvailable(session)) {
                        post.setHeader("Cookie", "JSESSIONID=" + session);
                    }
                    MultipartEntity multipartEntity = new MultipartEntity();
                    //设置参数
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
                    //设置文件
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
                    post.setEntity(multipartEntity);
                    HttpResponse response = client.execute(post);
                    int statuCode = response.getStatusLine().getStatusCode();
                    if (statuCode != HttpStatus.SC_OK) {
                        msg.what = HTTP_FAILED;
                        msg.obj = obj;
                        data.putString(RESULTTAG, "请求页面出错，状态码：" + statuCode);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    // 获取cookie并设置，保持持续连接
                    setSession(client);
                    // 获取结果并判断//获取的结果为JSon信息
                    String result = EntityUtils.toString(response.getEntity(), "UTF-8");
                    msg.what = HTTP_SUCCESS;
                    msg.obj = obj;
                    data.putString(RESULTTAG, result);
                    httpHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = HTTP_FAILED;
                    msg.obj = obj;
                    data.putString(RESULTTAG, mContext.getResources().getString(R.string.hubu_httputils_timeout));
                    httpHandler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }


    /**
     * 获取网络session并设置session
     * @param client
     */
    public void setSession(HttpClient client){
        // 获取cookie并设置，保持持续连接
        List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    if(!CommonUtils.isAvailable(session)) {
                        session = cookie.getValue();
                    }
                }
            }
        }
    }






}
