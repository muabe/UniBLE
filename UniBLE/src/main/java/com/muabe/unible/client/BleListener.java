package com.muabe.unible.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;

import com.muabe.unible.client.exception.BleException;

import java.util.List;

public interface BleListener {
    void onScanResult(BluetoothDevice device, ScanResult result, int rssi, byte[] scanRecord);
    void onScanStop(List<BluetoothDevice> list);
    void onBonded(BluetoothDevice device);
    void onConnected(UniBle uniBle, BluetoothGatt gatt);
    void onDisconnected();
    void onException(BleException bleException);
}
