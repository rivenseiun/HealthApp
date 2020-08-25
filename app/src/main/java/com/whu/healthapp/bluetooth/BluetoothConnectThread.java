package com.whu.healthapp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by Jiang.YX on 2017/2/13.
 */

public class BluetoothConnectThread extends Thread{
    String macAddress = "";
    boolean connecting;
    boolean connected;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    public BluetoothSocket socket;
    String bluetoothSerialPort = "00001101-0000-1000-8000-00805F9B34FB";
    int connectTime = 0;
    String TAG = "Bluetooth";

    public BluetoothConnectThread(String mac) {
        macAddress = mac;
    }

    public BluetoothSocket connect() {
        connecting = true;
        connected = false;
        if(mBluetoothAdapter == null){
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
        mBluetoothAdapter.cancelDiscovery();
        try {
            socket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(bluetoothSerialPort));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        //adapter.cancelDiscovery();
        //while (!connected && connectTime <= 10) {
        while (!connected && connectTime <= 10 && connecting) {
            connectDevice();
        }
        return socket;
    }

    public void cancel() {
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        } finally {
            connecting = false;
        }
    }

    protected void connectDevice() {
        try {

            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class.getMethod("createBond");
                creMethod.invoke(mBluetoothDevice);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        mBluetoothAdapter.cancelDiscovery();
        try {
            socket.connect();
            connected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            connectTime++;
            connected = false;
            try {
                socket.close();
                socket = null;
            } catch (IOException e2) {
                e2.printStackTrace();
                Log.e(TAG, e2.toString());
            }
        }
        finally {
            connecting = false;
        }
    }


    public boolean checkBtAdapterExists() {
        boolean result = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            result = true;
        }
        return result;
    }


    public boolean checkBtAdapterEnabled() {
        boolean result = false;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            result = true;
        }
        return result;
    }
}
