package com.muabe.ble.sample.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.muabe.unible.client.profile.GattService;
import com.muabe.unible.client.profile.GattServiceHub;
import com.muabe.unible.client.profile.annotation.Characteristic;
import com.muabe.unible.client.profile.annotation.Desciptor;
import com.muabe.unible.client.profile.annotation.Service;

@Service("caecface-e1d9-11e6-bf01-fe55135034f0")
public class ScreenShotService extends GattService {
//    UserDescription "X Position Characteristic"
//    XPosition UUID "caec2ebc-e1d9-11e6-bf01-fe55135034f1"
//    Int32 4바이트 전송
//
//    UserDescription "Y Position Characteristic"
//    YPosition UUID "caec2ebc-e1d9-11e6-bf01-fe55135034f2"
//    Int32 4바이트 전송
//
//    UserDescription “Totoal Packet Count Characteristic"
//    TotalN UUID "caec2ebc-e1d9-11e6-bf01-fe55135034f2"
//    Int32 4바이트 전송
//
//    UserDescription "Image Data Characteristic"
//    ImageData UUID "caec2ebc-e1d9-11e6-bf01-fe55135034f4"

     @Characteristic
     public final String screenShot = "caec2ebc-e1d9-11e6-bf01-fe55135034f4";

     @Desciptor
     public final String xPoint = "caec2ebc-e1d9-11e6-bf01-fe55135034f1";

     @Desciptor
     public final String yPoint = "caec2ebc-e1d9-11e6-bf01-fe55135034f2";

     @Override
     public void onCharacteristicRead(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {

     }

     @Override
     public void onCharacteristicChanged(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {

     }

     @Override
     public void onDescriptorWrite(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattDescriptor descriptor) {
          super.onDescriptorWrite(gatt, hub, descriptor);
     }
}



