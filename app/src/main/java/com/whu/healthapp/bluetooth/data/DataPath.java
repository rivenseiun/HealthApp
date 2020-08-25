package com.whu.healthapp.bluetooth.data;

import java.io.File;


public class DataPath {


	public static String savepicUrl ="/sdcard/project/pic/";//默认的图片存储地址
	public static void clearCache(){
		File file = new File(savepicUrl);
		file.deleteOnExit();
	}

}
