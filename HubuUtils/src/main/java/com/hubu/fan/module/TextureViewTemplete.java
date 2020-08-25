package com.hubu.fan.module;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by FAN on 2016/8/29.
 */
public class TextureViewTemplete extends TextureView implements TextureView.SurfaceTextureListener {


    public TextureViewTemplete(Context context) {
        this(context, null);
    }

    public TextureViewTemplete(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureViewTemplete(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //设置监听
        setSurfaceTextureListener(this);
        Camera camera = Camera.open();
        camera.stopPreview();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
