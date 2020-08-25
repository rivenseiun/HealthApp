package com.hubu.fan.module;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.hardware.Camera;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCaptureSession;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraDevice;
//import android.hardware.camera2.CameraManager;
//import android.support.v4.app.ActivityCompat;
//import android.util.AttributeSet;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import java.util.Arrays;
//
///**
// * Created by FAN on 2016/8/30.
// * Camera2写的  但是没写成功，不必在意这些细节，留着以后再写
// */
//public class Camer2View extends SurfaceView implements SurfaceHolder.Callback {
//    private SurfaceHolder holder;
//    private Camera camera;
//    Camera.Parameters parameters;
//    private Context context;
//
//    private CameraManager manager;
//    private CameraCharacteristics cc;
//
//    public Camer2View(Context context) {
//        this(context, null);
//    }
//
//    public Camer2View(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public Camer2View(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        holder = getHolder();
//        holder.addCallback(this);
//        this.context = context;
//    }
//
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
////        try {
//////            camera = Camera.open();
//////            camera.setPreviewDisplay(holder);
//////
//////            parameters = camera.getParameters();
//////            //自动对焦
//////            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//////            camera.setParameters(parameters);
//////            camera.startPreview();
//////            camera.setDisplayOrientation(90);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            cc = manager.getCameraCharacteristics("0");
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//
//                return;
//            }
//            manager.openCamera("0", new CameraDevice.StateCallback() {
//                @Override
//                public void onOpened(CameraDevice camera) {
//                    try {
//                        camera.createCaptureSession(Arrays.asList(Camer2View.this.getHolder().getSurface()), new CameraCaptureSession.StateCallback() {
//                            @Override
//                            public void onConfigured(CameraCaptureSession session) {
//
//                            }
//
//                            @Override
//                            public void onConfigureFailed(CameraCaptureSession session) {
//
//                            }
//                        }, null);
//
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onDisconnected(CameraDevice camera) {
//                        camera.close();
//
//                }
//
//                @Override
//                public void onError(CameraDevice camera, int error) {
//
//                }
//            }, null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
//}

class Camera2View {

}
