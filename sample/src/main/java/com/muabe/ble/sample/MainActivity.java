package com.muabe.ble.sample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.muabe.unible.client.BleListener;
import com.muabe.unible.client.UniBle;
import com.muabe.unible.client.exception.BleException;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements BleListener{
    UniBle ble;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("permission", "permission");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Android M Permission check
            if(this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }
        }
        Log.d("permission", "시작");
        ble = new UniBle(this, this);
        ble.enableAutoBluetooth(this);
        ble.regist();
        ble.startScan(10*1000);
        Log.d("permission", "끝");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ble.unRegist();
    }

    @Override
    public void onRequestPermissionsResult(int reqeustCode, String permission[], int[] grantResults){
        switch (reqeustCode){
            case 1000 :{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("permission", "coarse location permission granted");
                }
            }
        }
    }

    @Override
    public void onScanResult(BluetoothDevice device, ScanResult result, int rssi, byte[] scanRecord) {
        Log.e("dd",device.getName()+":"+device.getAddress());
    }

    @Override
    public void onScanStop(List<BluetoothDevice> list) {
        Log.e("dd","stop");
    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onConnected(UniBle uniBle, BluetoothGatt gatt) {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onException(BleException bleException) {

    }
}