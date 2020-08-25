package com.hubu.fan.utils;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * 对文件的一些操作
 * 
 * @author fan
 * 
 */
public class FileUtils {

	/**
	 * 将uri转换为真实文件路径
	 * 
	 * @param context
	 *            当前上下文
	 * @param uri
	 *            需要转换的uri
	 * @return
	 */
	public static String getPathFromUri(Context context, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA};

		Cursor actualimagecursor = ((Activity) context).managedQuery(uri, proj, null, null, null);

		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		return img_path;
	}



	/**
	 * 获取是否能够向存储卡写入此数据量
	 * @param size
	 * @return
	 */
	public static boolean getSaveSDCard(int size){
		if(!isSDCardAvailable()){
			return false;
		}else if(getLeftVolumeInSDCard() < size){
			return false;
		}else{
			return true;
		}
	}


	/**
	 *	判断内存卡是否挂载
	 * @return
	 */
	public  static boolean isSDCardAvailable() {
		boolean sdcardAvailable = false;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			sdcardAvailable = true;
		}
		return  sdcardAvailable;
	}

	/**
	 * 获取内存卡容量
	 * @return
	 */
	public static int getLeftVolumeInSDCard(){
		int VolumeMB = 0;
		if(isSDCardAvailable()){
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			File file = new File(sdcard);
			StatFs statFs = new StatFs(file.getPath());
			long availableSpare =  (statFs.getBlockSize()*((long)statFs.getAvailableBlocks()-4));
			VolumeMB = (int) (availableSpare/1024/1024);
		}
		return VolumeMB;
	}
	

}
