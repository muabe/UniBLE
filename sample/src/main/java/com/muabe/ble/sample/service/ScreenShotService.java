package com.muabe.ble.sample.service;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.util.Log;

import com.muabe.ble.sample.util.Util;
import com.muabe.unible.client.profile.GattService;
import com.muabe.unible.client.profile.GattServiceHub;
import com.muabe.unible.client.profile.annotation.Characteristic;
import com.muabe.unible.client.profile.annotation.Desciptor;
import com.muabe.unible.client.profile.annotation.Service;

import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

import androidx.annotation.RequiresApi;

@Service("caecface-e1d9-11e6-bf01-fe55135034f0")
public class ScreenShotService extends GattService {
     public interface ReadResult{
          void readImageSize(int size);
          void readImageCount(int count);
          void onProcess(int currentPosition, int totalPosition, int currentBytes, int totalBytes);
          void onCompleted(byte[] imageData);
     }

     @Characteristic
     public static final String touch = "caec2ebc-e1d9-11e6-bf01-fe55135034f1";

     @Characteristic
     public static final String imageSize = "caec2ebc-e1d9-11e6-bf01-fe55135034f2";

     @Characteristic
     public static final String imageCount = "caec2ebc-e1d9-11e6-bf01-fe55135034f3";

     @Characteristic
     public static final String downloadImage = "caec2ebc-e1d9-11e6-bf01-fe55135034f4";

     @Desciptor
     public static final String downloadImageRequest = "00002902-0000-1000-8000-00805f9b34fb";


     int totalBytes = 0;
     int currentBytes = 0;
     int packetCount = 0;
     int currentPacketCount = 0;
     byte[] imageData = null;

     BluetoothGatt gatt;
     GattServiceHub service;
     Hashtable<String, GattServiceHub> services;
     ReadResult result;


     public ScreenShotService(ReadResult result){
          this.result = result;
     }

     @Override
     public void onServicesDiscovered(BluetoothGatt gatt, GattServiceHub service, Hashtable<String, GattServiceHub> services) {
          Log.d("ScreenShotService","onServicesDiscovered");
          this.gatt = gatt;
          this.service = service;
          this.services = services;
          service.indication(downloadImage, downloadImageRequest, gatt);
     }


     public void touch(int x, int y){
          if(gatt!=null) {
               BluetoothGattCharacteristic characteristic = service.getCharacteristic(touch);
               characteristic.setValue(Util.getRawBytes(x, y));
               service.writing(touch, gatt);
          }
     }

     public void imageSize(){
          if(gatt!=null) {
               totalBytes = 0;
               packetCount = 0;
               service.reading(imageSize, gatt);
          }
     }

     void requestPacket(BluetoothGatt gatt2, BluetoothGattCharacteristic characteristic2){
          Log.d("ScreenShotService","request:"+currentPacketCount);
          BluetoothGattCharacteristic characteristic = service.getCharacteristic(downloadImage);
          byte[] data = Util.int32ToBytes(currentPacketCount);
          String log = "";
          for(byte d:data){
               log += Integer.toHexString(d)+" ";
          }
          Log.i("ScreenShotService",log);
          characteristic.setValue(data);
          service.writing(downloadImage, gatt);
     }

     @Override
     public void onCharacteristicRead(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic) {
          if(equalsCharacteristic(imageSize, characteristic)){
               Log.d("ScreenShotService", "read imageSize");
               totalBytes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
               result.readImageSize(totalBytes);
               service.reading(imageCount, gatt);
          }else if(equalsCharacteristic(imageCount, characteristic)){
               Log.d("ScreenShotService", "read imageCount");
               packetCount = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT32, 0);
               result.readImageCount(packetCount);
               currentBytes = 0;
               currentPacketCount = 0;
               imageData = new byte[totalBytes];
               requestPacket(gatt, characteristic);
          }
     }

     @RequiresApi(api = Build.VERSION_CODES.O)
     @Override
     public void onCharacteristicChanged(BluetoothGatt gatt, GattServiceHub hub, @NotNull BluetoothGattCharacteristic characteristic) {
          byte[] data = characteristic.getValue();
          currentBytes += data.length;
          Log.d("ScreenShotService",currentPacketCount+"/"+packetCount+"  "+ currentBytes +"/"+ totalBytes);
          result.onProcess(currentPacketCount, packetCount, currentBytes, totalBytes);
          if(++currentPacketCount >= packetCount){
               result.onCompleted(imageData);
          }
     }

}



