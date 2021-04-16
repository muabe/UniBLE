package com.muabe.unible.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class ConnectStateAdapter {

    ConnectStateListener listener;
    BluetoothDevice device;

    interface ConnectStateListener{
        void connecting(BluetoothGatt gatt);
        void disconnected(BluetoothGatt gatt);
        void disconnecting(BluetoothGatt gatt);
        void unknown(BluetoothGatt gatt);

    }

    ConnectStateAdapter(BluetoothDevice device, ConnectStateListener listener){
        this.device = device;
        this.listener = listener;
    }
}
