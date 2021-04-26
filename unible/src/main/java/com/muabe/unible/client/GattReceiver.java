package com.muabe.unible.client;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.muabe.unible.client.profile.GattServiceHub;
import com.muabe.unible.client.exception.BleException;
import com.muabe.unible.client.exception.Message;

import java.util.Hashtable;
import java.util.UUID;

public class GattReceiver extends BluetoothGattCallback {
    Hashtable<String, GattServiceHub> services = new Hashtable<>();

    BleListener listener;
    ConnectStateAdapter connectAdapter;

    GattReceiver(BleListener listener){
        this.listener = listener;
    }

    public void setConnectAdapter(ConnectStateAdapter connectAdapter){
         this.connectAdapter = connectAdapter;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d("GattReceiver", "Connecting "+gatt.getDevice().getAddress()+"="+(newState == BluetoothProfile.STATE_CONNECTED));
        if(connectAdapter != null &&connectAdapter.device.getAddress().equals(gatt.getDevice().getAddress())) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                connectAdapter.listener.connecting(gatt);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectAdapter.listener.disconnected(gatt);
                connectAdapter = null;
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                connectAdapter.listener.disconnecting(gatt);
            } else {
                connectAdapter.listener.disconnected(gatt);
                connectAdapter = null;
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Log.d("GattReceiver", "Service Discovered = "+(status == BluetoothGatt.GATT_SUCCESS));

        if (status == BluetoothGatt.GATT_SUCCESS) {
            for (BluetoothGattService service : gatt.getServices()) {
                for(String key : services.keySet()){
                    GattServiceHub gattServiceHub = services.get(key);
                    gattServiceHub.initCharacteristic(service, gatt, services);
                }
            }
            listener.onConnected(null, gatt);
        }else{
            gatt.disconnect();
            gatt.close();
            listener.onException(new BleException(Message.SERVICES_DISCOVER_FAILURE));
        }
    }

    private BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String uuid){
        return service.getCharacteristic(UUID.fromString(uuid));
    }



    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        for(String key : services.keySet()){
            GattServiceHub hub = services.get(key);
            if(characteristic.getService().getUuid().equals(hub.getServiceUUID())){
                hub.getListener().onCharacteristicChanged(gatt, hub, characteristic);
                break;
            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d("GattReceiver", "Characteristic Read "+gatt.getDevice().getAddress()+"="+(status == BluetoothGatt.GATT_SUCCESS));
        if (status == BluetoothGatt.GATT_SUCCESS) {
            for(String key : services.keySet()){
                GattServiceHub hub = services.get(key);
                if(characteristic.getService().getUuid().equals(hub.getServiceUUID())){
//                    for(String name : hub.getCharacteristicList().keySet()){
//                        if(characteristic.getUuid().equals(hub.getCharacteristic(name).getUuid())){
                            hub.getListener().onCharacteristicRead(gatt, hub, characteristic);
                            break;
//                        }
//                    }
//                    break;
                }
            }
        }else{
            listener.onException(new BleException(Message.CHARACTERISTIC_READ_FAILURE));
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d("GattReceiver", "onCharacteristicWrite GATT_SUCCESS :"+status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            for (String key : services.keySet()) {
                GattServiceHub hub = services.get(key);
                if (characteristic.getService().getUuid().equals(hub.getServiceUUID())) {
                    hub.getListener().onCharacteristicWrite(gatt, hub, characteristic);
                }
            }
        }else{
            listener.onException(new BleException(Message.CHARACTERISTIC_READ_FAILURE));
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("GattReceiver", "onDescriptorRead");

        if (status == BluetoothGatt.GATT_SUCCESS) {
            for(String key : services.keySet()){
                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                GattServiceHub hub = services.get(key);
                if(characteristic.getService().getUuid().equals(hub.getServiceUUID())){
                    hub.getListener().onDescriptorRead(gatt, hub, descriptor);
                    break;
                }
            }
        }else{
            listener.onException(new BleException(Message.CHARACTERISTIC_READ_FAILURE));
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("GattReceiver", "onDescriptorWrite : "+status);
        Log.d("GattReceiver", descriptor.getCharacteristic().getUuid().toString()+" : "+descriptor.getUuid().toString());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            for (String key : services.keySet()) {
                BluetoothGattCharacteristic characteristic = descriptor.getCharacteristic();
                GattServiceHub hub = services.get(key);
                if (characteristic.getService().getUuid().equals(hub.getServiceUUID())) {
                    hub.getListener().onDescriptorWrite(gatt, hub, descriptor);
                }
            }
        }else{
            listener.onException(new BleException(Message.DESCIPTOR_WRITE_FAILURE));
        }
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        Log.e("GattReceiver", "onReliableWriteCompleted");
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        Log.e("GattReceiver", "onReadRemoteRssi");
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        Log.e("GattReceiver", "onPhyUpdate");
    }

    @Override
    public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        Log.e("GattReceiver", "onPhyRead");
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        Log.e("GattReceiver", "onMtuChanged");
    }



}