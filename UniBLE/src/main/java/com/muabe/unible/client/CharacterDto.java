package com.muabe.unible.client;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.Hashtable;
import java.util.UUID;

public class CharacterDto {
    private UUID uuid;
    private BluetoothGattCharacteristic gattCharacteristic;
    private Hashtable<String, UUID> descriptor = new Hashtable<>();

    public CharacterDto(String uuid){
        this.uuid = UUID.fromString(uuid);
    }

    public void setCharacteristic(BluetoothGattService service){
        gattCharacteristic =  service.getCharacteristic(uuid);
    }

    public UUID getCharacteristicUUID(){
        return uuid;
    }

    public BluetoothGattCharacteristic getCharacteristic(){
        return gattCharacteristic;
    }


    public void addDesciptor(String name, String uuid){
        descriptor.put(name, UUID.fromString(uuid));
    }

    public UUID getDesciptorUUID(String name){
        return descriptor.get(name);
    }



}
