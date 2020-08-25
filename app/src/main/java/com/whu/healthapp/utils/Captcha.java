package com.whu.healthapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Captcha {
    public static final String RE_PHONE = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}";
    public static final String RE_PASSWORD = "^[a-zA-Z]\\w{5,17}";

    /**
     * 验证手机号
     *
     * @param phone
     * @return
     */
    public static boolean phoneValid(String phone) {
        Pattern p = Pattern.compile(RE_PHONE);
        Matcher matcher = p.matcher(phone);
        return matcher.matches();
    }

    /**
     * 验证密码
     *
     * @param psv
     * @return
     */
    public static boolean pswValid(String psv) {
        Pattern p = Pattern.compile(RE_PASSWORD);
        Matcher matcher = p.matcher(psv);
        return matcher.matches();
    }
}

