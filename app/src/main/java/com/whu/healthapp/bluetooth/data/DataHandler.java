package com.whu.healthapp.bluetooth.data;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataHandler {
	//清除整数组
	public static int[] clearData(int[] data) {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		return data;
	}
	//清楚实数组
	public static float[] clearData(float[] data) {
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		}
		return data;
	}

	//校验取出的数据包
	public static boolean check(int[] data) {
		int sum0 = 0;
		int sum1 = 0;
		for (int i = 0; i <= 16; i++) {
			sum0 = (sum0 + data[i]);
		}
		for (int i = 17; i <= 21; i++) {
			sum1 = (sum1 + data[i]);
		}
		return (sum0 % 256 == data[17]) && (sum1 % 256 == (data[22]));
	}
	//处理读入的25个字节
	public static int[] handle(int[] data) { //data是不含数据头的23个字节
		//data: (0x55,0xaa）
		//status0,status1,data0,ecgwi14,ecgwi3,ecgwi2,ecgwi1
		//ecgwii4,ecgwii3,ecgwii2,ecgwii1,ecgwv4,ecgwv3,ecgwv2,ecgwv1,satw
		//respw,sum0,data1,data2,data3,data4,sum1
		int[] handleData = new int[21];
		int i = 0;
		// 处理STATUS0，其bit5~bit0
		handleData[i] = data[0] & 0x3f; //datas0-status0的低6位
		i++;
		// STATUS0,bit7 有无起搏器
		handleData[i] = data[0] >> 7 & 0x01;//datas1-status0的第7位
		i++;
		// 处理STATUS1 ,bit4~bit0作为索引, status, handleData[2]=0->ECG, =3->ic_tongji_mailv
		handleData[i] = data[1] & 0x1f; //datas2-status1的低5位，脉搏声及data0数据内容指示
		i++;
		// STATUS1,bit7 有无脉搏声
		handleData[i] = data[1] >> 7 & 0x01; //datas3-status1的第7位,=1时ECG有脉搏声
		i++;
		// STATUS1,bit6 脉搏氧有脉搏声
		handleData[i] = data[1] >> 6 & 0x01;//datas4-status1的第6位，=1时血氧有脉搏声
		i++;
		// 处理DATA0
		// STATUS1的BIT5为DATA0的BIT8
		handleData[i] = data[2] + (data[1] >> 5 & 0x01) * 256; //datas5-data0和status1组合
		i++;
		// 处理心电图数据

		for (int j = 0; j < 4; j++) { //datas6,10,14-心电I/II/V数据
			if (j == 0) {
				handleData[i + j] = data[3] + ((data[19] >> 6) & 0x03) * 256; //I
				handleData[i + j + 4] = data[7] + ((data[20] >> 6) & 0x03) * 256; //II
				handleData[i + j + 8] = data[11] + ((data[21] >> 6) & 0x03) * 256; //V
			} else if (j == 1) {
				handleData[i + j] = data[4] + ((data[19] >> 4) & 0x03) * 256; //I
				handleData[i + j + 4] = data[8] + ((data[20] >> 4) & 0x03) * 256; //II
				handleData[i + j + 8] = data[12] + ((data[21] >> 4) & 0x03) * 256; //V
			} else if (j == 2) {
				handleData[i + j] = data[5] + ((data[19] >> 2) & 0x03) * 256; //I
				handleData[i + j + 4] = data[9] + ((data[20] >> 2) & 0x03) * 256; //II
				handleData[i + j + 8] = data[13] + ((data[21] >> 2) & 0x03) * 256; //V
			} else if (j == 3) {
				handleData[i + j] = data[6] + (data[19] & 0x03) * 256; //I
				handleData[i + j + 4] = data[10] + (data[20] & 0x03) * 256; //II
				handleData[i + j + 8] = data[14] + (data[21] & 0x03) * 256; //V
			}
		}
		i = 18;
		// SATW
		handleData[i] = data[15];//datas18,SATW
		i++;
		// RESPW
		handleData[i] = data[16];//datas19,RESPW
		i++;
		// DATA1
		handleData[i] = data[18];//datas20,DATA1
		return handleData;
	}


	//子函数-健康数据存入数据库
	//子函数-数据存入数据库
	public static void saveHealthDataToDatabase(Context context, HealthEnty healthdata){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		String date = sdf.format(new Date());
		ContentValues values = new ContentValues();
		values.put("name", healthdata.getUserName());
		values.put("date", date);
		values.put("tiwen", healthdata.getTiwen());
		values.put("xueyang", healthdata.getXueyang());
		values.put("mailv", healthdata.getMailv());
		values.put("shuzhangya", healthdata.getShuzhangya());
		values.put("shousuoya", healthdata.getShousuoya());
		values.put("dongmaya", healthdata.getDongmaya());
		values.put("resprate", healthdata.getResprate());
		values.put("xinlv", healthdata.getXinlv());
		values.put("ecgcad", healthdata.getEcgcad());
		values.put("jieguo", healthdata.getJieguo());
		values.put("xindian", healthdata.getXindian());
		values.put("savname", healthdata.getSavname());
		HealthDBhelper healthHelper = new HealthDBhelper(context);
		healthHelper.insert(values,0);   //0代表数据信息表
		healthHelper.close();
	}

}
