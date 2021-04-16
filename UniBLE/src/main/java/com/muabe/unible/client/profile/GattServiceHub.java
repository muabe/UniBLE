package com.muabe.unible.client.profile;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.muabe.unible.client.CharacterDto;

import java.util.Hashtable;
import java.util.UUID;

public class GattServiceHub {
    private Hashtable<String, GattServiceHub> services;

    public interface ServicesDiscoveredListener {
        void onServicesDiscovered(BluetoothGatt gatt, GattServiceHub hub, Hashtable<String, GattServiceHub> services);
        void onCharacteristicRead(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic);
        void onDescriptorWrite(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattDescriptor descriptor);
        void onCharacteristicChanged(BluetoothGatt gatt, GattServiceHub hub, BluetoothGattCharacteristic characteristic);
    }

    private GattService gattGervice;
    private String name;
    private UUID uuid;
    Hashtable<String, CharacterDto> characters = new Hashtable<>();
    Hashtable<String, String> descriptors = new Hashtable<>();


    public GattServiceHub(String name, String uuid) {
        this.name = name;
        this.uuid = UUID.fromString(uuid);
    }

    public GattServiceHub(String uuid) {
        this(uuid, uuid);
    }

    public UUID getServiceUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public GattServiceHub getHub(Class<? extends GattService> clz){
        return services.get(clz.getSimpleName());
    }

    public GattService getGattGervice(){
        return gattGervice;
    }

    public Hashtable<String, CharacterDto> getCharacteristicList(){
        return characters;
    }

    public InputCharacteristic addCharacteristic(String name, String uuid) {
        InputCharacteristic builder = new InputCharacteristic(this);
        builder.addCharacteristic(name, uuid);
        return builder;
    }

    public void addDesciptor(String name, String uuid) {
        descriptors.put(name, uuid);
    }

    public InputCharacteristic getInputCharacteristic(String name){
        return new InputCharacteristic(name, this);
    }

    public GattServiceHub setGattService(GattService gattService) {
        this.gattGervice = gattService;
        return this;
    }

    public ServicesDiscoveredListener getListener(){
        return gattGervice;
    }

    public GattServiceHub setUuid(String name, BluetoothGattService service) {
        CharacterDto info = characters.get(name);
        info.setCharacteristic(service);
        return this;
    }

    public BluetoothGattCharacteristic getCharacteristic(String name) {
        CharacterDto info = characters.get(name);
        if (info == null) {
            return null;
        }
        return info.getCharacteristic();
    }

    public void initCharacteristic(BluetoothGattService service, BluetoothGatt gatt, Hashtable<String, GattServiceHub> services) {
        if (hasService(service)) {
            Log.i("GattServiceHub", "----------서비스 찾음-----------");
            this.services = services;
            for (String key : characters.keySet()) {
                Log.i("GattServiceHub", key + " 서비스등록:" + characters.get(key).getCharacteristicUUID());
                characters.get(key).setCharacteristic(service);
            }
            if (gattGervice != null) {
                gattGervice.onServicesDiscovered(gatt, this, services);
            }
        }
    }

    public void reading(String name, BluetoothGatt gatt) {
        Log.i("GattServiceHub", "reading : " + name);
        BluetoothGattCharacteristic characteristic = getCharacteristic(name);
        if(characteristic == null){
            throw new RuntimeException("등록하지 않은 Characteristic 이름 입니다.[" + name + "]");
        }
        gatt.readCharacteristic(getCharacteristic(name));
    }


    public void listening(String charName, String descName, BluetoothGatt gatt) {
        CharacterDto info = characters.get(charName);
        if(info.getCharacteristic() == null){
            throw new RuntimeException("등록하지 않은 Characteristic 이름 입니다.[" + name + "]");
        }
        gatt.setCharacteristicNotification(info.getCharacteristic(), true);
        BluetoothGattDescriptor descriptor = info.getCharacteristic().getDescriptor(UUID.fromString(descriptors.get(descName)));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    public void writing(String name, BluetoothGatt gatt){
        gatt.writeCharacteristic(getCharacteristic(name));
    }

    public void chear() {
        for (String key : characters.keySet()) {
            characters.get(key).setCharacteristic(null);
        }
    }

    private boolean hasService(BluetoothGattService service) {
        return this.uuid.equals(service.getUuid());
    }

    @Override
    public String toString() {
        return "Service Name=" + name + ", UUID=" + uuid.toString();
    }



}
