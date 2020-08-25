package com.hubu.fan.oom;

import android.graphics.Bitmap;

/**
 * Created by 47462 on 2016/10/18.
 */
public interface ImageCache {

    /**
     * 放入图片缓存区域
     * @param uri
     * @param bitmap
     */
    public void put(String uri,Bitmap bitmap);

    /**
     * 创建图片
     * @param uri
     * @return
     */
    public Bitmap create(String uri);

    /**
     * 根据大小压缩并创建图片
     * @param uri
     * @return
     */
    public Bitmap create(String uri,Integer width,Integer height);

    /**
     * 创建图片
     * @param id  drawable的ID
     * @return
     */
    public Bitmap create(Integer id);

    /**
     * 根据大小压缩并创建图片
     * @param id
     * @return
     */
    public Bitmap create(Integer id,Integer width,Integer height);

    /**
     * 获取图片 如果没有回执行创建
     * @param uri
     * @return
     */
    public Bitmap get(String uri);

    /**
     * 获取图片 如果没有回执行创建
     * @param id
     * @return
     */
    public Bitmap get(Integer id);

    /**
     * 获取图片 如果没有回执行创建
     * @param uri
     * @return
     */
    public Bitmap get(String uri,Integer width,Integer height);

    /**
     * 获取图片 如果没有回执行创建
     * @param id
     * @return
     */
    public Bitmap get(Integer id,Integer width,Integer height);

    /**
     * 回收图片
     * @param uri
     * @return
     */
    public void recycle(String uri);

    public void clear();



}
