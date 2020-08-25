package com.whu.healthapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

import java.util.List;

public class TextSpeak {
	private Context context;
	private SpeechSynthesizer mTts;
	String TAG = "Xunfei Sound";
	private static final String APPID = "appid=5391665f";

	public TextSpeak(final Context thiscontext) {
		// TODO Auto-generated constructor stub
		this.context = thiscontext;
		// 设置申请的应用的appid
		SpeechUtility.createUtility(context, APPID);  //设置讯飞应用ID
		mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
		// 设置引擎类型
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
		// 设置语速
		mTts.setParameter(SpeechConstant.SPEED, "50");
		// 设置音调
		mTts.setParameter(SpeechConstant.PITCH, "50");
	}

	public void speak(String text) {
		//tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		//开始合成
		int code = mTts.startSpeaking(text, null);
		if (code != 0) {
			Log.e(TAG,"语音合成失败");
		} else
			Log.d(TAG,"语音合成成功");
	}

	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code");

		}
	};
	/*
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
		} 
		@Override
		public void onSpeakPaused() {
		}
		@Override
		public void onSpeakResumed() {
		}
		public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
		}
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
		}

		public void onCompleted(SpeechError error) {
			if(error!=null)
			{
				Log.d("mySynthesiezer complete code:", error.getErrorCode()+"");
			}
			else
			{
				Log.d("mySynthesiezer complete code:", "0");
			}
		}
		@Override
		public IBinder asBinder() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void onBufferProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onCompleted(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onSpeakProgress(int arg0) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
 */
	public void stopSpeak() {
		mTts.stopSpeaking();
	}

	protected void onDestroy() {
		mTts.stopSpeaking();
		mTts.destroy();
	}


	/**
	 * 执行本地安装 语音+
	 *
	 * @param context
	 * @param
	 * @return
	 */
	private void processInstall(Context context) {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		AutoInstall.setUrl("file:///android_asset/xufei.apk");
		// 本地安装方式
		AutoInstall.install(context);

	}

	/**
	 * 检测科大讯飞语音+引擎是否安装
	 *
	 * @return
	 */
	public static boolean checkSpeechServiceInstall(Context context) {
		String packageName = "com.iflytek.speechcloud";
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if (packageInfo.packageName.equals(packageName)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

}
