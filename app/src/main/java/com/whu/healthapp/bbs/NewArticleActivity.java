package com.whu.healthapp.bbs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.whu.healthapp.R;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.bbs.adapter.ImagePublishAdapter;
import com.whu.healthapp.bean.forum.ImageItem;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.utils.CustomConstants;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NewArticleActivity extends AppCompatActivity {

    private GridView newArticleGridView;
    private TextView newArticleSendTV;
    private EditText newArticleContent;
    private ImagePublishAdapter mAdapter;
    public static List<ImageItem> mImageList = new ArrayList<ImageItem>();
    public static String url = "http://47.92.55.20:80/Healthy/passage.mvc?action=0";



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_article);
       verifyStoragePermissions(this);
        newArticleContent = (EditText) findViewById(R.id.newArticleContent);
        initData();

        //清空图片列表：即如果是通过MainActivity进入该活动（新建了文章），则将 图片列表进行清空，而如果是选择完图片后返回该活动，则不清空图片列表。
        Intent intent = getIntent();
        if(intent.getIntExtra("clear",0)==1){
            mImageList.clear();
        }

        initView();
    }

    protected void onPause() {
        super.onPause();
        saveTempToPref();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveTempToPref();
    }

    private void saveTempToPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = JSON.toJSONString(mImageList);
        sp.edit().putString(CustomConstants.PREF_TEMP_IMAGES, prefStr).commit();
    }

    private void getTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        String prefStr = sp.getString(CustomConstants.PREF_TEMP_IMAGES, null);
        if (!TextUtils.isEmpty(prefStr)) {
            List<ImageItem> tempImages = JSON.parseArray(prefStr,
                    ImageItem.class);
            mImageList = tempImages;
        }
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                CustomConstants.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(CustomConstants.PREF_TEMP_IMAGES).commit();
    }

    @SuppressWarnings("unchecked")
    private void initData() {
        getTempFromPref();
        List<ImageItem> incomingDataList = (List<ImageItem>) getIntent()
                .getSerializableExtra(IntentConstants.EXTRA_IMAGE_LIST);
        if (incomingDataList != null) {
            //由于数据库限制只能上传4张图片
                for(int i = 0;i<incomingDataList.size();i++){
                    mImageList.add(incomingDataList.get(i));


            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        notifyDataChanged(); // 当在ImageZoomActivity中删除图片时，返回这里需要刷新
    }

    public void initView() {
        newArticleGridView= (GridView) findViewById(R.id.newArticleGridview);
        newArticleGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ImagePublishAdapter(this, mImageList);
        newArticleGridView.setAdapter(mAdapter);
        newArticleGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == getDataSize()) {
                    new PopupWindows(NewArticleActivity.this, newArticleGridView);
                } else {
                    Intent intent = new Intent(NewArticleActivity.this,
                            ImageZoomActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_IMAGE_LIST,
                            (Serializable) mImageList);
                    intent.putExtra(IntentConstants.EXTRA_CURRENT_IMG_POSITION,
                            position);
                    startActivity(intent);
                }
            }
        });



        newArticleSendTV = (TextView) findViewById(R.id.newArticleSendTV);
        newArticleSendTV.setText("发送");



        //单击发送按钮，将要发布的内容进行上传
        newArticleSendTV.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //Toast.makeText(NewArticleActivity.this, User.getInstance(NewArticleActivity.this).getPhone(), Toast.LENGTH_LONG).show();

                if(newArticleContent.getText().toString().equals("")){
                    Toast.makeText(NewArticleActivity.this, "请输入文字内容", Toast.LENGTH_LONG).show();

                }
                else{
                    //如果有图片，就将图片上传

                    RequestParams params = new RequestParams(url);
                    params.setMultipart(true);

                    if(mImageList.size() != 0){
                        for(int i = 0; i<mImageList.size();i++){
                            int j = i+1;
                            //params.addBodyParameter("file"+j, new File(mImageList.get(i).sourcePath));
                            File temp = scal(Uri.parse(mImageList.get(i).sourcePath));
                            params.addBodyParameter("file"+j,temp );
                            //Toast.makeText(NewArticleActivity.this, String.valueOf(temp.length()),Toast.LENGTH_LONG);

                        }
                    }


                    params.addBodyParameter("tag","true");

                    params.addBodyParameter("phone", User.getInstance(NewArticleActivity.this).getPhone());//上传用户手机号码
                    try {
                        String content = new String(newArticleContent.getText().toString().getBytes(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    params.addBodyParameter("content", newArticleContent.getText().toString());//上传文章内容

                    if(newArticleContent.getText().toString().length()>=20){
                        params.addBodyParameter("title", newArticleContent.getText().toString().substring(0,20));//截取文章内容的前20个字符作为标题
                    }
                    else params.addBodyParameter("title", newArticleContent.getText().toString());


                    x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<Integer>() {
                        @Override
                        public void onSuccess(Integer result) {

                            Toast.makeText(NewArticleActivity.this, "发送成功", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(NewArticleActivity.this, HomePagerActivity.class);
                            startActivity(intent);
                            NewArticleActivity.this.finish();
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            Toast.makeText(NewArticleActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });


                    mImageList.clear();




                }

            }
        });
    }

    private int getDataSize() {
        return mImageList == null ? 0 : mImageList.size();
    }

    private int getAvailableSize() {
        int availSize = CustomConstants.MAX_IMAGE_SIZE - mImageList.size();
        if (availSize >= 0) {
            return availSize;
        }
        return 0;
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.item_popupwindow, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    takePhoto();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(NewArticleActivity.this,
                            ImageBucketChooseActivity.class);
                    intent.putExtra(IntentConstants.EXTRA_CAN_ADD_IMAGE_SIZE,
                            getAvailableSize());
                    NewArticleActivity.this.finish();
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void takePhoto() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File vFile = new File(Environment.getExternalStorageDirectory()
                + "/myimage/", String.valueOf(System.currentTimeMillis())
                + ".jpg");
        if (!vFile.exists()) {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();
        } else {
            if (vFile.exists()) {
                vFile.delete();
            }
        }
        path = vFile.getPath();
        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                if (mImageList.size() < CustomConstants.MAX_IMAGE_SIZE
                        && resultCode == -1 && !TextUtils.isEmpty(path)) {
                    ImageItem item = new ImageItem();
                    item.sourcePath = path;
                    mImageList.add(item);
                }
                break;
        }
    }

    private void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );

        }
    }


    //图片上传压缩算法
    public static boolean getCacheImage(String filePath,String cachePath){
        OutputStream out = null;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true; //设置为true，只读尺寸信息，不加载像素信息到内存
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, option); //此时bitmap为空
        option.inJustDecodeBounds = false;
        int bWidth = option.outWidth;
        int bHeight= option.outHeight;
        int toWidth = 400;
        int toHeight = 800;
        int be = 1; //be = 1代表不缩放
        if(bWidth/toWidth>bHeight/toHeight&&bWidth>toWidth){
            be = (int)bWidth/toWidth;
        }else if(bHeight>toHeight){
            be = (int)bHeight/toHeight;
        }
        option.inSampleSize = be; //设置缩放比例
        bitmap = BitmapFactory.decodeFile(filePath, option);
        try {
            out = new FileOutputStream(new File(cachePath));
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
    }


    public static File scal(Uri fileUri){
        String path = fileUri.getPath();
        File outputFile = new File(path);
        long fileSize = outputFile.length();
        final long fileMaxSize = 200 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                fos.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }else{
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }


    public static Uri createImageFile(){
// Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+ timeStamp +"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,".jpg", storageDir);
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

// Save a file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }
    public static void copyFileUsingFileChannels(File source, File dest){
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }






}
