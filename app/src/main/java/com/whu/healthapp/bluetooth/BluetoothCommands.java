package com.whu.healthapp.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jiang.YX on 2017/2/13.
 */


public class BluetoothCommands {
    SharedPreferences userInfo;
    private int bpp1lowpos,bpp1highpos,bpp2pos;
    private byte bpp1low,bpp1high,bpp1,bpp2;
    private byte[] comm = new byte[6];   //{ 0x55, (byte) 0xAA, cmd, p1,p2,sum };// 测量命令
    //发送血压命令
    public byte[] getBluetoothCommand(int type,Context context) {
        comm[0] = 0x55;
        comm[1] = (byte) 0xAA;

        userInfo = context.getSharedPreferences("easy_health_user_info", 0);

        switch (type) {
            case 1: //开始血压测量
                bpp1lowpos = 0;
                bpp1highpos = 0;
                bpp2pos = 0;
                if (!userInfo.getString("bpp1lowpos", "").equals("")){
                    bpp1lowpos = Integer.valueOf(userInfo.getString("bpp1lowpos", ""));
                }
                if (!userInfo.getString("bpp1highpos", "").equals("")){
                    bpp1highpos = Integer.valueOf(userInfo.getString("bpp1highpos", ""));
                }
                if (!userInfo.getString("bpp2pos", "").equals("")){
                    bpp2pos = Integer.valueOf(userInfo.getString("bpp2pos", ""));
                }
                switch (bpp1lowpos) { //决定血压测定低4位
                    case 0: //成人180
                        bpp1low = 0x00;
                        break;
                    case 1: //成人160
                        bpp1low = 0x01;
                        break;
                    case 2: //成人150
                        bpp1low = 0x02;
                        break;
                    case 3: //成人140
                        bpp1low = 0x03;
                        break;
                    case 4: //儿童120
                        bpp1low = 0x04;
                        break;
                    case 5: //儿童100
                        bpp1low = 0x05;
                        break;
                    case 6: //儿童70
                        bpp1low = 0x08;
                        break;
                    default:
                        bpp1low = 0x00;
                        break;
                }
                switch (bpp1highpos) { //决定血压测定低4位
                    case 0: //正向
                        bpp1high = 0x00;
                        break;
                    case 1: //逆向
                        bpp1high = 0x01;
                        break;
                    case 2: //快速
                        bpp1high = 0x02;
                        break;
                    case 3: //取消快速
                        bpp1high = 0x03;
                        break;
                    case 4: //开始自动测量
                        bpp1high = 0x04;
                        break;
                    case 5: //取消快速
                        bpp1high = 0x05;
                        break;
                    default:
                        bpp1high = 0x00;
                        break;
                }
                bpp1 = (byte) (bpp1low + bpp1high << 4); //合成p1命令  高4位字节用10进制是x16
                switch (bpp2pos) { //p2命令
                    case 0: //1分钟
                        bpp2 = 0x01;
                        break;
                    case 1: //2分钟
                        bpp2 = 0x02;
                        break;
                    case 2: //3分钟
                        bpp2 = 0x03;
                        break;
                    case 3: //4分钟
                        bpp2 = 0x04;
                        break;
                    case 4: //5分钟
                        bpp2 = 0x05;
                        break;
                    case 5: //10分钟
                        bpp2 = 0x10;
                        break;
                    case 6: //30分钟
                        bpp2 = 0x30;
                        break;
                    case 7: //60分钟
                        bpp2 = 0x60;
                        break;
                    default:
                        bpp2 = 0x01;
                        break;
                }
                comm[2] = 0x01;//cmd
                comm[3] = bpp1; //p1
                comm[4] = bpp2; //p2
                break;
            case 2: //终止血压测量
                comm[2] = 0x02;
                comm[3] = 0x00;
                comm[4] = 0x00;
                break;
            case 3: //血压校准
                comm[2] = 0x03;
                comm[3] = 0x00;
                comm[4] = 0x00;
                break;
            case 4: //读模块存储器，每8个字节一组，p1=0为第0组，p1=1为第一组...
                comm[2] = 0x04;
                comm[3] = 0x01;
                comm[4] = 0x00;
                break;
            case 5: //写模块存储器,p1为地址，p2为写入值
                comm[2] = 0x05;
                comm[3] = 0x00;
                comm[4] = 0x00;
                break;
            case 6: //报警音量音调，p1 =0-7音调,p2为音调
                comm[2] = 0x06;
                comm[3] = 0x02; //0-7
                comm[4] = 0x01; //0-2音调
                break;
            case 7: //报警方式
                comm[2] = 0x07;
                comm[3] = 0x00; //p1=0关闭，1-连续报警,2-间断报警
                comm[4] = 0x00;
                break;
            case 8: //心电控制
                comm[2] = 0x08; //cmd
                //bit0-2 主导联选择，000I，001II，010III，011CAL，bit4-3增益选择（00:x0.5,01:x1,02:x2）
                //bit6-5 方式选择（00-诊断，01-监护，02-手术，03-软件滤波关闭），bit7=0强滤波，bit7=1弱滤波
                int ecglead=0,ecggain=0,ecgmode=0,ecgfilter=0;
                if (!userInfo.getString("ecglead", "").equals("")){
                    ecglead = Integer.valueOf(userInfo.getString("ecglead", ""));
                }
                if (!userInfo.getString("ecggain", "").equals("")){
                    ecggain = Integer.valueOf(userInfo.getString("ecggain", ""));
                }
                if (!userInfo.getString("ecgmode", "").equals("")){
                    ecgmode = Integer.valueOf(userInfo.getString("ecgmode", ""));
                }
                if (!userInfo.getString("ecgfilter", "").equals("")){
                    ecgfilter = Integer.valueOf(userInfo.getString("ecgfilter", ""));
                }
                int cmdbit1=0,cmdbit2=0,cmdbit3=0,cmdbit4=0;
                switch (ecglead) { //主导联选择
                    case 0:
                        cmdbit1 = 0x00; //I
                        break;
                    case 1:
                        cmdbit1 = 0x01; //II
                        break;
                    case 2:
                        cmdbit1 = 0x10; //III
                        break;
                    case 3:
                        cmdbit1 = 0x11; //CAL
                        break;
                    default:
                        cmdbit1 = 0x00;
                        break;
                }
                switch (ecggain) {
                    case 0:
                        cmdbit2 = 0x00; //增益x0.5
                        break;
                    case 1:
                        cmdbit2 = 0x01; //增益x1
                        break;
                    case 2:
                        cmdbit2 = 0x02; //增益x2
                        break;
                    default:
                        cmdbit2 = 0x00;
                        break;
                }
                switch (ecgmode) {
                    case 0:
                        cmdbit3 = 0x00; //诊断
                        break;
                    case 1:
                        cmdbit3 = 0x01; //监护
                        break;
                    case 2:
                        cmdbit3 = 0x02; //手术
                        break;
                    case 3:
                        cmdbit3 = 0x03; //软件滤波关闭
                        break;
                    default:
                        cmdbit3 = 0x00;
                        break;
                }
                switch (ecgfilter) {
                    case 0:
                        cmdbit4 = 0x00; //强滤波
                        break;
                    case 1:
                        cmdbit4 = 0x01; //弱滤波
                        break;
                    default:
                        cmdbit4 = 0x00;
                        break;
                }
                //
                comm[3] = (byte) (cmdbit1 + cmdbit2 << 3 + cmdbit3 << 5 + cmdbit4 << 7); //p2-心电命令字节
                comm[4] = 0; //p2-0
                break;
            case 9: //呼吸控制
                comm[2] = 0x09; //
                //p1:bit1-0,增益,bi2：导联，1:LL,0:LA ,p2 =0
                //p1=0ffh时为呼吸开头命令，p2=0关，p2=1开
                int respgain=0,resplead=0;
                if (!userInfo.getString("respgain", "").equals("")){
                    respgain = Integer.valueOf(userInfo.getString("respgain", ""));
                }
                if (!userInfo.getString("resplead", "").equals("")){
                    resplead = Integer.valueOf(userInfo.getString("resplead", ""));
                }
                switch (respgain) {
                    case 0:
                        cmdbit1 = 0x00;
                        break;
                    case 1:
                        cmdbit1 = 0x01;
                        break;
                    case 2:
                        cmdbit1 = 0x10;
                        break;
                    case 3:
                        cmdbit1 = 0x11;
                        break;
                    default:
                        cmdbit1 = 0x00;
                        break;
                }
                switch (resplead) {
                    case 0:
                        cmdbit2 = 0x00; //增益x0.5
                        break;
                    case 1:
                        cmdbit2 = 0x01; //增益x1
                        break;
                    case 2:
                        cmdbit2 = 0x02; //增益x2
                        break;
                    default:
                        cmdbit2 = 0x00;
                        break;
                }
                comm[3] = (byte)(cmdbit1 + cmdbit2 << 2); //p1-
                comm[4] = 0; //p2-0
                break;
            case 10: //血压清零
                comm[2] = 0x0a;
                comm[3] = 0x00;
                comm[4] = 0x00;
                break;
            case 11: //心率平均个数
                comm[2] = 0x0b;
                comm[3] = 0x10; //p1-心率平均个数
                comm[4] = 0x00;
                break;
            case 12: //滤波开关
                comm[2] = 0x0c;
                comm[3] = 0x01; //p1,p2=0-关掉滤波，p1>0或p2>0 -打开滤波
                comm[4] = 0x00;
                break;
            case 13: //起搏检测开关
                comm[2] = 0x0d;
                comm[3] = 0x01; //p1,p2=0-关掉起搏检测，p1>0p2>0 -打开起搏检测
                comm[4] = 0x00;
            case 14: //窒息报警时间
                comm[2] = 0x0e; //
                comm[3] = 0x10; //p1-窒息检测时间
                comm[4] = 0x50; //
            case 15: //血氧模式
                comm[2] = 0x0f; //
                comm[3] = 0x02; //p1: 02-06各种模式
                comm[4] = 0x00; //
            case 17: //工频模式
                comm[2] = 0x11;
                comm[3] = 0; //
                comm[4] = 0x50; //p2：0x50:50Hz,0x60:60Hz
            case 80: //心率通道选择
                comm[2] = (byte)0x80; //cmd - 心率通道
                comm[3] = 0; //p1
                comm[4] = 0; //p2：0-通道1，1-通道2，2-通道3
                break;
            case 81: //心电导联切换，设置5导联
                comm[2] = (byte)0x81; //cmd - 心率通道
                comm[3] = 0; //p1
                comm[4] = 1; //p2：0-三导联，1-五导联
                break;
            case 82: //心电导联设置，只有3导联有效
                comm[2] = (byte)0x82; //cmd - 心率通道
                comm[3] = 0; //p1
                comm[4] = 1; //p2：0-2，I-III导
                break;
            case 83: //病人信息命令
                comm[2] = (byte)0x83; //cmd - 心率通道
                comm[3] = 0; //p1
                comm[4] = 1; //p2：000B成人、001B小儿、010B新生儿,bit3 病人是否佩戴起搏器,0B-是，1B-否
                break;
            case 84: //读出模块id
                comm[2] = (byte)0x84; //
                comm[3] = 0; //p1
                comm[4] = 1; //p2
                break;
            case 991: //主程序支持
                comm[2] = (byte)0x99; //
                comm[3] = (byte)0x88; //p1
                comm[4] = (byte)0x77; //p2
            case 992: //开启除颤输出同步
                comm[2] = (byte)0x99; //
                comm[3] = (byte)0x99; //p1
                comm[4] = (byte)0x99; //p2
            case 993: //关闭除颤输出同步
                comm[2] = (byte)0x99; //
                comm[3] = (byte)0xaa; //p1
                comm[4] = (byte)0xaa; //p2
            case 994: //呼吸开
                comm[2] = (byte)0x99; //
                comm[3] = (byte)0xbb; //p1
                comm[4] = (byte)0xbb; //p2
            case 995: //呼吸关
                comm[2] = (byte)0x99; //
                comm[3] = (byte)0xcc; //p1
                comm[4] = (byte)0xcc; //p2
            case 996: //滤波模式50hz
                comm[2] = (byte)0x11; //
                comm[3] = (byte)0x00; //p1
                comm[4] = (byte)0x50; //p2
            case 997: //滤波模式60hz
                comm[2] = (byte)0x11; //
                comm[3] = (byte)0x00; //p1
                comm[4] = (byte)0x60; //p2
            default:
                break;
        }
        comm[5] = (byte) ((comm[2] + comm[3] + comm[4]) % 256);
        return comm;
    }
}