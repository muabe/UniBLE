package com.muabe.ble.sample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;

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
        ble = new UniBle(this, this);
        ble.connect("");

    }

    @Override
    public void onScanResult(BluetoothDevice device, ScanResult result, int rssi, byte[] scanRecord) {

    }

    @Override
    public void onScanStop(List<BluetoothDevice> list) {

    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onConnected(UniBle uniBle, BluetoothGatt gatt) {
        uniBle.getGattService()
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onException(BleException bleException) {

    }
}