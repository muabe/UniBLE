package com.muabe.ble.sample.util;

import android.bluetooth.BluetoothDevice;

import com.muabe.ble.sample.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ScanDto extends BaseObservable {
    @Bindable
    private String name;
    @Bindable
    private String address;

    public ScanDto(){
        name = "";
        address = "";
    }

    public ScanDto(BluetoothDevice device){
        name = device.getName();
        address = device.getAddress();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.notifyPropertyChanged(BR.name);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.notifyPropertyChanged(BR.address);
    }
}
