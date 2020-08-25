package com.hubu.fan.oom;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ListView;

import com.hubu.fan.exception.ContextIsNullException;
import com.hubu.fan.utils.PictureUtil;

/**
 * Created by 47462 on 2016/10/19.
 */
public class ListviewCache extends LruCache<String,Bitmap> {

    int mWidth  = 0 ;
    int mHeight = 0;
    Context mContext ;

    public ListviewCache(Context context,int maxSize,int width,int height)  {
        super(maxSize);
        if(context == null){
            throw new ContextIsNullException();
        }
        mContext = context;
        mWidth = width ;
        mHeight = height;
    }
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    private  ListviewCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected Bitmap create(String key) {
        Bitmap bitmap = null;
        // 内存中没有
        // 判断是uri或者是本地路径
        int index = key.indexOf(":");
        if (index == -1) {
            // 本地路径
            if(mWidth <=0 || mHeight <=0){
                bitmap = PictureUtil.getCustomerBitmapByFilePath(key);
            }else {
                bitmap = PictureUtil.getCustomerBitmapByFilePath(key, mWidth, mHeight);
            }
        } else {
            // 网络地址，从映射表中找寻本地地址
            String result = SPImageDiskMapped.getInstance(mContext).get(key);
            if (!result.equals("failed")) {
                // 本地路径读取
                if(mWidth <=0 || mHeight <= 0){
                    bitmap = PictureUtil.getCustomerBitmapByFilePath(result);
                }else {
                    bitmap = PictureUtil.getCustomerBitmapByFilePath(result, mWidth, mHeight);
                }
            }
        }
        return bitmap;

    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {

        if(oldValue != null && !oldValue.isRecycled()){
            oldValue.recycle();
        }
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
