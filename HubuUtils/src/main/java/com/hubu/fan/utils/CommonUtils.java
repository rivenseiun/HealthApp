package com.hubu.fan.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.net.URLEncoder;

public class CommonUtils {

	/**
	 * 判断字符串是为null或者“”
	 * 
	 * @param str
	 */
	public static boolean isAvailable(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		return true;
	}

	public static boolean isAvailableAll(String... str) {
		for (String str_ : str) {
			if (!isAvailable(str_)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (null == s)
			return true;
		if (s.length() == 0)
			return true;
		if (s.trim().length() == 0)
			return true;
		return false;
	}


	/**
	 * 安装APK程序代码
	 * */
	public static void installFile(Context context, String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}


	/**
	 * 判断当前网络类型
	 * 0无网络
	 * 1是wifi
	 * 2是移动网络
	 */
	public static int getNettype(Context context){
		try {
			ConnectivityManager connectMgr = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectMgr == null){
				return 0 ;
			}
			NetworkInfo info = connectMgr.getActiveNetworkInfo();
			if (info == null || !info.isConnected()) {
				return 0;
			}

				if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					return 2;
				} else if (info.getType() == ConnectivityManager.TYPE_WIFI)
					return 1;

		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}


	/**
	 *  字符串编码（“UTF-8”）
	 * @param str 字符串
	 * @return
	 * @throws Exception
	 */
	private static String encode(String str) throws Exception{
		return URLEncoder.encode(str, "utf-8");
	}


	// 取得版本号
	public static String GetVersionName(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return "Unknown";
		}
	}


	// 取得版本ID
	public static int GetVersionId(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return 0;
		}
	}

	/**
	 * 判断是否联网
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}


	/**
	 * 获取imei
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		String imei = "" ;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imei;
	}


	/**
	 * 是否更新
	 * @param context
	 * @return
	 */
	public static boolean isGengXi(Context context,int appVirsionCodeLatest){
		PackageManager manager = context.getPackageManager();
		int	appVersionCode = 0;
		PackageInfo packageInfo = null;
		try {
			packageInfo	= manager.getPackageInfo(context.getPackageName(), 0);
			appVersionCode = packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			//Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		if(appVersionCode >= appVirsionCodeLatest){
			return false;
		}
		return  true;

	}





}
