package com.whu.healthapp.activity.homepage;

import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.personer.LoginActivity;
import com.whu.healthapp.activity.personer.PersonSelect;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.bluetooth.BluetoothCommands;
import com.whu.healthapp.bluetooth.BluetoothConnectThread;
import com.whu.healthapp.bluetooth.BluetoothDevice;
import com.whu.healthapp.utils.UserInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

/**
 * Created by Jiang.YX on 2017/2/13.
 */


public class HomePager3Fragment extends Fragment implements View.OnClickListener {

    private RelativeLayout rlPersonerSel ,rlDeviceSel ,rlLogout ;
    private TextView tvPersonel ,tvDeviceSel,displayPhone,displayName,displayType;

    //蓝牙

    private BluetoothSocket socket; //蓝牙socket
    private BluetoothConnectThread bluetoothThread;
    private BluetoothAdapter mBluetoothAdapter = null;
    public SharedPreferences mSharedPreferences;
    public Context mContext;
    String[] deviceNames;//蓝牙设备名称
    String[] deviceMac;//蓝牙设备地址


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage_tab3,container,false);
        rlPersonerSel = (RelativeLayout) view.findViewById(R.id.rl_personersel);
        rlDeviceSel = (RelativeLayout) view.findViewById(R.id.rl_devicesel);
        rlLogout = (RelativeLayout) view.findViewById(R.id.rl_logout);
        tvPersonel = (TextView) view.findViewById(R.id.tv_personersel);
        tvDeviceSel = (TextView) view.findViewById(R.id.tv_devicesel);


        displayName = (TextView)view.findViewById(R.id.display_name);
        displayPhone = (TextView)view.findViewById(R.id.display_phone);
        displayType = (TextView)view.findViewById(R.id.display_type);



        rlPersonerSel.setOnClickListener(this);
        rlDeviceSel.setOnClickListener(this);
        rlLogout.setOnClickListener(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            tvPersonel.setText(UserInfo.getSel(getActivity()));
            displayPhone.setText(User.getInstance(getActivity()).getPhone());
            displayName.setText(User.getInstance(getActivity()).getUserName());

        switch (User.getInstance(getActivity()).getType()){
            case 0:
                displayType.setText("类型：普通");
                break;
            case 1:
                displayType.setText("类型：老人");
                break;

            case 2:
                displayType.setText("类型：妇女");
                break;

            case 3:
                displayType.setText("类型：小孩");
                break;

        }




            tvDeviceSel.setText(BluetoothDevice.getDeviceName(getActivity()));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.rl_devicesel:
                showBondedMac();
                break;
            case R.id.rl_personersel:
                User.getInstance(getActivity().getApplicationContext());
                User.getInstance(getActivity().getApplicationContext()).setSel(false);
                intent = new Intent(getActivity(), PersonSelect.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.rl_logout:
                String phone=User.getInstance(getActivity()).getPhone();
                String name=User.getInstance(getActivity()).getUserName();
                int type=User.getInstance(getActivity()).getType();
                mSharedPreferences=getActivity().getApplicationContext().getSharedPreferences(phone, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=mSharedPreferences.edit();
                editor.putString("name",name);
                editor.putInt("type",type);
                editor.commit();

                intent = new Intent(getActivity(), LoginActivity.class);
                User.getInstance(getActivity()).clear();
                startActivity(intent);
                UserInfo.logout(getActivity());
                getActivity().finish();
                break;
        }
    }


    //子函数：显示已查询蓝牙
    public void showBondedMac() {
        //扫描并显示所有已配对的蓝牙设备

        Set<android.bluetooth.BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();

        if (devices.size() == 0) {
            Toast.makeText(getActivity(), "没有搜索到任何蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }
        deviceNames = new String[devices.size()];
        deviceMac = new String[devices.size()];
        //获取蓝牙名称
        int i = 0;
        for (android.bluetooth.BluetoothDevice device : devices) {
            deviceNames[i] = device.getName();
            deviceMac[i] = device.getAddress();
            i++;
        }


        //弹出对话框
        new AlertDialog.Builder(getActivity()).setTitle("选择蓝牙设备")
                .setSingleChoiceItems(deviceNames, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mac = deviceMac[which];
                        BluetoothDevice.setDeviceMac(getActivity(), mac);
                        BluetoothDevice.setDeviceName(getActivity(), deviceNames[which]);
                        tvDeviceSel.setText(BluetoothDevice.getDeviceName(getActivity()));
                        dialog.dismiss();
                    }
                }).show();

    }

    //向设备发送蓝牙命令
    public void sendCommandToBluetooth(int type) {
        OutputStream outStr = null;
        BluetoothCommands btcommand = new BluetoothCommands();
        byte[] comm = btcommand.getBluetoothCommand(type, getActivity().getApplicationContext());
        try {
            outStr = socket.getOutputStream();
            outStr.write(comm);
        } catch (IOException e) {
            Log.e("JQB", e.toString());
        } finally {
            if (outStr != null) {
                try {
                    outStr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //停止蓝牙线程
    public void stopBluetoothThread() {
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
            Thread dummy = bluetoothThread;
            bluetoothThread = null;
            dummy.interrupt();
            socket = null;
        }

    }
}