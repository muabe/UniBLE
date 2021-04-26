package com.muabe.unible.client;

import android.bluetooth.BluetoothDevice;

public abstract class BoardCastStateAdapter {
    BluetoothDevice device;

    public void initBond(BluetoothDevice device){
        this.device = device;
    }

    abstract void bluetoothOn();

    abstract void bluetoothOff();

    abstract void onBonded(BluetoothDevice device);

    abstract void onBondNone(BluetoothDevice device);
}
