package com.hubu.fan.oom;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.hubu.fan.exception.ContextIsNullException;
import com.hubu.fan.utils.PictureUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 47462 on 2016/10/18.
 */
public class LocalImage implements ImageCache{

    public static final Map<Context,LocalImage> localImages = new HashMap<Context, LocalImage>();

    public Context context;

    // 内存控制
    protected Map<String, Bitmap> mBitmaps = new HashMap<String, Bitmap>();


    public LocalImage(Context context) {
        this.context = context;
        if(context == null){
            throw new ContextIsNullException();
        }
        localImages.put(context,this);
    }

    /**
     * 通过Context获取LocalImage的实例
     */
    public static LocalImage getLocalImage(Context context){
        if(context !=null) {
            return localImages.get(context);
        }
        return null;
    }

    /**
     * 移除LocalImage和Context的对应关系
     * @return
     */
    public static void removeLocalImage(Context context){
        if(context != null) {
            localImages.remove(context);
        }
    }




    @Override
    public void put(String uri, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        Bitmap b = mBitmaps.get(uri);
        if(b != null && !b.isRecycled()){
            return ;
        }
        // 放入
        mBitmaps.put(uri, bitmap);
    }

    @Override
    public Bitmap create(String uri) {
        return create(uri,0,0);
    }

    @Override
    public Bitmap create(String uri, Integer width, Integer height) {
        Bitmap bitmap = null;
        // 内存中没有
        // 判断是uri或者是本地路径
        int index = uri.indexOf(":");
        if (index == -1) {
            // 本地路径
            if(width <=0 || height <=0){
                bitmap = PictureUtil.getCustomerBitmapByFilePath(uri);
            }else {
                bitmap = PictureUtil.getCustomerBitmapByFilePath(uri, width, height);
            }
        } else {
            // 网络地址，从映射表中找寻本地地址
            String result = SPImageDiskMapped.getInstance(context).get(uri);
            if (!result.equals("failed")) {
                // 本地路径读取
                if(width <=0 || height <= 0){
                    bitmap = PictureUtil.getCustomerBitmapByFilePath(result);
                }else {
                    bitmap = PictureUtil.getCustomerBitmapByFilePath(result, width, height);
                }
            }
        }
        return bitmap;
    }

    @Override
    public Bitmap create(Integer id) {
        return create(id,0,0);
    }

    @Override
    public Bitmap create(Integer id, Integer width, Integer height) {
        Bitmap bitmap = null;
        if(width <=0 || height <= 0) {
            bitmap = PictureUtil.getCustomerBitmapFromResource(context.getResources(),id);
        }else {
            bitmap = PictureUtil.getCustomerBitmapFromResource(context.getResources(), id, width, height);
        }
        return bitmap;
    }

    @Override
    public Bitmap get(String uri) {
        return get(uri,0,0);
    }

    @Override
    public Bitmap get(Integer id) {
        return get(id,0,0);
    }

    @Override
    public Bitmap get(String uri, Integer width, Integer height) {
        Bitmap bitmap = null;
        // 从内存中获取
        bitmap = mBitmaps.get(uri);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
        //判断全局是否存在
        if(GlobalImage.getInstance(context).contains(uri)){
            bitmap = GlobalImage.getInstance(context).get(uri);
        }
        if(bitmap ==null || bitmap.isRecycled()) {
            bitmap = create(uri, width, height);
        }
        if(bitmap !=null && !bitmap.isRecycled()){
            put(uri,bitmap);
        }
        return bitmap;

    }

    @Override
    public Bitmap get(Integer id, Integer width, Integer height) {
        Bitmap bitmap = null;
        bitmap = mBitmaps.get(id+"");
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
        //判断全局是否存在
        if(GlobalImage.getInstance(context).contains(id+"")){
            bitmap = GlobalImage.getInstance(context).get(id+"");
        }
        if(bitmap ==null || bitmap.isRecycled()) {
            bitmap = create(id, width, height);
        }
        if(bitmap !=null && !bitmap.isRecycled()){
            put(id + "", bitmap);
        }
        return bitmap;

    }

    @Override
    public void recycle(String uri) {
        if (mBitmaps.containsKey(uri)) {
            Bitmap bitmap = mBitmaps.get(uri);
            mBitmaps.remove(uri);
            if (bitmap != null && !bitmap.isRecycled()) {
                //判断是否为全局图片
                if(!GlobalImage.getInstance(context).contains(uri)){
                    bitmap.recycle();
                }
            }
        }
    }

    @Override
    public void clear() {
        try {
            for (Map.Entry<String, Bitmap> entry : mBitmaps.entrySet()) {
                Bitmap bitmap = entry.getValue();
                if (bitmap == null) {
                    continue;
                }
                if (!bitmap.isRecycled()) {
                    //判断是否为全局图片
                    if(!GlobalImage.getInstance(context).contains(entry.getKey())){
                        bitmap.recycle();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmaps.clear();
    }

    /**
     * 图片是否存在
     * @param uri
     */
    public boolean contains(String uri){
        Bitmap bitmap = mBitmaps.get(uri);
        if(bitmap == null || bitmap.isRecycled()){
            return false;
        }
        return true;
    }
}
