package com.whu.healthapp.bean.jqb;

import java.io.Serializable;


public class TestDataBean implements Serializable {

    public static final int TEST_ALL = 0;
    public static final int TEST_XINLV = 1;
    public static final int TEST_XUEYA = 2;
    public static final int TEST_XUEYANG = 3;
    public static final int TEST_TIWEN = 4;

    public static final int TYPE_IMG_XUEYANG = 1;
    public static final int TYPE_IMG_ECG = 2;
    public static final int TYPE_IMG_HUXI = 3;
    public static final int TYPE_IMG_DATA = 4;

    private int localId;//本地保存的数据id
    private int serverId = -1;//云端保存的数据id
    private String userId;//用户id
    private int kind = 0;//种类，0表示全部测试，1表示心率，2表示血压，3表示血氧，4表示体温
    private String time = "0";
    private String ecgImgPath = "";//心电图路径
    private String huxiAddress = "";//呼吸波路劲
    private String xueyangAddress = "";//血氧图存放地址
    private String mailv = "0";
    private String xinlv = "0";
    private String huxi = "0";
    private String tiwen = "0";
    private String xueyangbhd = "0";
    private String shousuoya = "0";
    private String shuzhangya = "0";
    private String aveya = "0";
    private String xiudaiya = "0";

    private String suggestion = "无建议";

    private String dataAddress = "";
    private String docterFeedback = "";

    public String getDocFeedback() {
        return docterFeedback;
    }

    public void setDocFeedback(String docFeedback) {
        this.docterFeedback = docFeedback;
    }

    public String getDataAddress() {
        return dataAddress;
    }

    public void setDataAddress(String dataAddress) {
        this.dataAddress = dataAddress;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getEcgImgPath() {
        return ecgImgPath;
    }

    public void setEcgImgPath(String ecgImgPath) {
        this.ecgImgPath = ecgImgPath;
    }

    public int getKind() {
        return kind;
    }

    public int getServerId() {
        return serverId;
    }

    public String getMailv() {
        return mailv;
    }

    public String getXinlv() {
        return xinlv;
    }

    public String getHuxi() {
        return huxi;
    }

    public String getTiwen() {
        return tiwen;
    }

    public String getXueyangbhd() {
        return xueyangbhd;
    }

    public String getShousuoya() {
        return shousuoya;
    }

    public String getShuzhangya() {
        return shuzhangya;
    }

    public String getAveya() {
        return aveya;
    }

    public String getXiudaiya() {
        return xiudaiya;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setServerId(int id) {
        this.serverId = id;
    }

    public void setMailv(String mailv) {
        this.mailv = mailv;
    }

    public void setXinlv(String xinlv) {
        this.xinlv = xinlv;
    }

    public void setHuxi(String huxi) {
        this.huxi = huxi;
    }

    public void setTiwen(String tiwen) {
        this.tiwen = tiwen;
    }

    public void setXueyangbhd(String xueyangbhd) {
        this.xueyangbhd = xueyangbhd;
    }

    public void setShousuoya(String shousuoya) {
        this.shousuoya = shousuoya;
    }

    public void setShuzhangya(String shuzhangya) {
        this.shuzhangya = shuzhangya;
    }

    public void setAveya(String aveya) {
        this.aveya = aveya;
    }

    public void setXiudaiya(String xiudaiya) {
        this.xiudaiya = xiudaiya;
    }

    public String getHuxiAddress() {
        return huxiAddress;
    }

    public void setHuxiAddress(String huxiAddress) {
        this.huxiAddress = huxiAddress;
    }

    public String getXueyangAddress() {
        return xueyangAddress;
    }

    public void setXueyangAddress(String xueyangAddress) {
        this.xueyangAddress = xueyangAddress;
    }


    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "TestDataBean{" +
                "time='" + time + '\'' +
                ", localId=" + localId +
                ", userId=" + userId +
                ", xinlv='" + xinlv + '\'' +
                ", ecgImgPath='" + ecgImgPath + '\'' +
                '}';
    }

    public static String getTestKindName(int kind) {
        switch (kind) {
            case 0:
                return "全部测量";
            case 1:
                return "心电测量";
            case 2:
                return "血压测量";
            case 3:
                return "血氧测量";
            case 4:
                return "体温测量";
            default:
                return "";
        }
    }
}
