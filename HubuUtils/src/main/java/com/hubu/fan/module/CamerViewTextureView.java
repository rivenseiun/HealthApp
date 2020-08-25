package com.hubu.fan.module;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by FAN on 2016/8/30.
 */
public class CamerViewTextureView extends TextureView implements  TextureView.SurfaceTextureListener {
    private SurfaceHolder holder ;
    private Camera camera;
    Camera.Parameters parameters;

    public CamerViewTextureView(Context context) {
        this(context, null);
    }

    public CamerViewTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CamerViewTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        camera = Camera.open();
        parameters = camera.getParameters();
        try {
            camera.setPreviewTexture(surface);
            camera.setDisplayOrientation(90);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        camera.stopPreview();
        camera.release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
