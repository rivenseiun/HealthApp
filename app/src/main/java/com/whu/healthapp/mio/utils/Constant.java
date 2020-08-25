package com.whu.healthapp.mio.utils;

/**
 * Created by xf on 2016/1/30.
 */
public class Constant {
	public static final int MSG_FROM_CLIENT = 0;
	public static final int MSG_FROM_SERVER = 1;
	public static final int REQUEST_SERVER = 2;
	public static final int HEARTATE_CONNECT_SUCCESS = 300;
	public static final int HEARTATE_DB_SAVE = 311;
	public static final int STEP_UPDATE = 400;
	public static final int ACT_HIS_1 = 431;
	public static final int ACT_HIS_2 = 432;
	public static final int ACT_HIS_3 = 433;
	public static final int ACT_HIS_4 = 434;
	public static final int ACT_HIS_5 = 435;
	public static final int ACT_HIS_6 = 436;
	public static final int ACT_HIS_7 = 437;
	public static final int PROCESSVIEW_MSG = 500;
	public static final int FOOT_MSG = 600;
	public static final int SLEEPDATA_DB_SAVE = 0x11;
	
	
	public static final String SP_GOAL_STEPS = "goal_steps";
	public static final String SP_SETTING = "setting";
	public static final String SP_USER = "mobile_user";
	
	
	
	public static String SERVER_URL = "120.25.251.229/mobile/port/";
	public static String PHONE_NUMBER;
	public static String SIGN_IN_BROADCAST = "com.bbcc.mobilehealth.SIGN_IN_BROADCAST";
	public static String SIGN_OUT_BROADCAST = "com.bbcc.mobilehealth.SIGN_OUT_BROADCAST";
	public static String GET_HEALTH = "http://120.25.251.229/mobile/port/get/getHealth.php";
}
