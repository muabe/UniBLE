package com.muabe.unible.client.profile;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import java.util.Hashtable;

public class GattService implements GattServiceHub.ServicesDiscoveredListener {

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, GattServiceHub service, Hashtable<String, GattServiceHub> services) {

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattDescriptor descriptor) {

    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattDescriptor descriptor) {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {

    }

    protected boolean equalsCharacteristic(String uuid, BluetoothGattCharacteristic characteristic){
        return uuidEquals(uuid, characteristic.getUuid().toString());
    }

    protected boolean equalsDescriptor(String charUuid, String desciptorUuid, BluetoothGattDescriptor descriptor){
        BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
        return equalsCharacteristic(charUuid, characteristic) && uuidEquals(desciptorUuid, descriptor.getUuid().toString());
    }

    private boolean uuidEquals(String u1, String u2){
        return u1.toLowerCase().equals(u2.toLowerCase());

    }
}
