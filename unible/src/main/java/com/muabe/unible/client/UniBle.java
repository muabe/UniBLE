package com.muabe.unible.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.muabe.unible.client.profile.GattService;
import com.muabe.unible.client.profile.GattServiceHub;
import com.muabe.unible.client.profile.annotation.Characteristic;
import com.muabe.unible.client.profile.annotation.Desciptor;
import com.muabe.unible.client.profile.annotation.Service;
import com.muabe.unible.client.exception.BleException;
import com.muabe.unible.client.exception.Message;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public class UniBle implements BleListener{
    public final int REQUEST_SCAN_RESULT = 1221;
    private BluetoothAdapter adapter;
    private BleBroadcastReceiver bleBroadcastReceiver;
    BleListener listener;
    private BleScanner scanner;
    long scanTimeout = 10000;
    private Context context;
    private Activity activity;
    PostJob post = PostJob.NONE;
    GattReceiver gattReceiver;
    Handler handler = new Handler();

    BleState state;

    BluetoothDevice currDevice = null;
    BluetoothGatt currGatt = null;

    public enum PostJob{
        NONE,
        SCAN_TO_ENABLE_BT,
        PAIR_TO_CONNECT
    }

    public interface OnBluetoothAutoEnableListener{
        void confirm();
        void cancel();
    }

    private BoardCastStateAdapter boardCastStateAdapter = new BoardCastStateAdapter() {
        @Override
        void bluetoothOn() {
            if (post == PostJob.SCAN_TO_ENABLE_BT) {
                startScan(scanTimeout);
            }
        }

        @Override
        void bluetoothOff() {
            disconnect();
            if(currDevice != null && currGatt != null) {
                onException(new BleException(Message.BT_STATE_OFF));
            }
        }

        @Override
        void onBonded(BluetoothDevice device) {
            state.bonding = false;
            UniBle.this.onBonded(device);
        }

        @Override
        void onBondNone(BluetoothDevice device) {
            state.bonding = false;
            listener.onException(new BleException(Message.BT_BOND_FAILED));
        }
    };


    private ConnectStateAdapter.ConnectStateListener connectStateListener = new ConnectStateAdapter.ConnectStateListener() {
        @Override
        public void connecting(BluetoothGatt gatt) {

        }

        @Override
        public void disconnected(BluetoothGatt gatt) {
            if(currDevice != null && currGatt != null) {
                onException(new BleException(Message.STATE_DISCONNECTED));
            }else{
                if(state.connecting) {
                    onException(new BleException(Message.CAN_NOT_CONNECT, "[" + gatt.getDevice().getAddress() + "]"));
                }else{
                    onDisconnected();
                }
            }
            state.connecting = false;
        }
        @Override
        public void disconnecting(BluetoothGatt gatt) {
            state.connecting = false;
        }

        @Override
        public void unknown(BluetoothGatt gatt) {
            state.connecting = false;
            onException(new BleException(Message.UNKNOWN_CONNECT_ERROR));
        }
    };

    @Override
    public void onScanResult(final BluetoothDevice device, final ScanResult result, final int rssi, final byte[] scanRecord) {
        listener.onScanResult(device, result, rssi, scanRecord);
    }

    @Override
    public void onScanStop(final List<BluetoothDevice> list) {
        listener.onScanStop(list);
    }

    @Override
    public void onBonded(final BluetoothDevice device) {
        if(post == PostJob.PAIR_TO_CONNECT) {
            post = PostJob.NONE;
            connect(device);
        }else{
            listener.onBonded(device);
        }

    }

    @Override
    public void onConnected(UniBle uniBle, final BluetoothGatt gatt) {
        setConnectionInfo(gatt.getDevice(), gatt);
        listener.onConnected(this, gatt);
    }

    @Override
    public void onDisconnected() {
        listener.onDisconnected();
    }

    @Override
    public void onException(final BleException bleException) {
        listener.onException(bleException);

    }


    public UniBle(Context context, BleListener listener) {
        this.context = context;
        this.listener = listener;
        this.state = new BleState();
        this.bleBroadcastReceiver = new BleBroadcastReceiver(this, boardCastStateAdapter);
        this.gattReceiver = new GattReceiver(this);
    }

    public void setListener(BleListener listener){
        this.listener = listener;
    }

    Context getContext(){
        return this.context;
    }

    public void regist(){
        bleBroadcastReceiver.regist();
    }

    public void unRegist(){
        disconnect();
        bleBroadcastReceiver.unRegist();
    }

    private BluetoothAdapter getAdapter(){
        if(adapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }

    public void enableAutoBluetooth(Activity activity){
        this.activity = activity;
    }

    public void disableAutoBluetooth(){
        this.activity = null;
    }

    @SuppressLint("MissingPermission")
    private int checkBluetooth(){
        BluetoothAdapter adapter = getAdapter();
        if(adapter == null){
            onException(new BleException(Message.BT_NOT_SUPPORT));
            return -1;
        }
        if(adapter.isEnabled()){
            return 0;
        }else{
            return 1;
        }
    }


    @SuppressLint("MissingPermission")
    public static boolean isEnableBluetooth(Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        return adapter != null && adapter.isEnabled();
    }

    public boolean enableBluetooth(Activity activity){
        if (isBluetoothAutoOn()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_SCAN_RESULT);
            return true;
        }
        return false;
    }

    public static void bluetoothOn(){
        BluetoothAdapter.getDefaultAdapter().enable();
    }

    public static void bluetoothOff(){
        BluetoothAdapter.getDefaultAdapter().disable();
    }

    public void enableAutoBluetoothResult(int requestCode, int resultCode, OnBluetoothAutoEnableListener listener){
        if(requestCode == REQUEST_SCAN_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                listener.confirm();
            } else {
                listener.cancel();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private boolean isBluetoothAutoOn(){
        return activity != null && getAdapter() != null && !getAdapter().isEnabled();
    }

    public void startScan(long timeout){
        int check = checkBluetooth();
        if(check == 0) {
            if (scanner == null) {
                scanner = new BleScanner(getAdapter(), state);
            }
            scanner.stop();
            this.scanTimeout = timeout;
            scanner.scan(listener, timeout);
        }else if(check == 1) {
            if(isBluetoothAutoOn()){
                post = PostJob.SCAN_TO_ENABLE_BT;
                enableBluetooth(activity);
            }else{
                listener.onException(new BleException(Message.BT_NOT_ENABLE));
            }
        }
    }

    public void stopScan(){
        if(scanner != null){
            if(post == PostJob.SCAN_TO_ENABLE_BT){
                post = PostJob.NONE;
            }
            scanner.stop();
        }
    }

    public boolean isScaning(){
        return state.scanning;
    }

    @SuppressLint("MissingPermission")
    public BluetoothDevice getPairedDevice(BluetoothAdapter btAdapter, String deviceAddress){
        if(deviceAddress == null){
            return null;
        }
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(deviceAddress.equals(device.getAddress()))
                    return device;
            }
        }
        return null;
    }

    void setConnectionInfo(BluetoothDevice device, BluetoothGatt gatt){
        this.currDevice = device;
        this.currGatt = gatt;
    }

    public BluetoothDevice getConnectedevice(){
        return currDevice;
    }

    public BluetoothGatt getConnectedGatt(){
        return currGatt;
    }

    public void connect(String address){
        if(checkBluetooth() >= 0) {
            BluetoothDevice device = getAdapter().getRemoteDevice(address);
            this.connect(device);
        }
    }

    @SuppressLint("MissingPermission")
    public void connect(BluetoothDevice device){
        int check = checkBluetooth();
        if(check == 0) {
            if (BluetoothDevice.BOND_BONDED == device.getBondState()) {
                BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                if(currGatt ==null || bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED) {
                    if(state.connecting){
                        onException(new BleException(Message.ALEADY_CONNECTING));
                    }else{
                        state.connecting = true;
                        gattReceiver.setConnectAdapter(new ConnectStateAdapter(device, connectStateListener));
                        device.connectGatt(context, false, gattReceiver);
                    }
                }else if(currGatt != null && bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED){
                    onException(new BleException(Message.ALEADY_CONNECTED));
                }else if(currGatt != null && bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTING){
                    onException(new BleException(Message.ALEADY_CONNECTING));
                }else {
                    onException(new BleException(Message.ALEADY_CONNECTING));
                }
            } else if (BluetoothDevice.BOND_BONDING == device.getBondState()) {
                onException(new BleException(Message.BT_BONDING));
            } else {
                post = PostJob.PAIR_TO_CONNECT;
                paring(device);
            }
        }else if(check == 1){
            listener.onException(new BleException(Message.BT_NOT_ENABLE, "connect"));
        }
    }

    public void connectNotPairing(String address){
        if(checkBluetooth() >= 0) {
            BluetoothDevice device = getAdapter().getRemoteDevice(address);
            this.connectNotPairing(device);
        }
    }

    @SuppressLint("MissingPermission")
    public void connectNotPairing(BluetoothDevice device){
        int check = checkBluetooth();
        if(check == 0) {
            if (BluetoothDevice.BOND_BONDED == device.getBondState()) {
                BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                if(currGatt ==null || bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_DISCONNECTED) {
                    if(state.connecting){
                        onException(new BleException(Message.ALEADY_CONNECTING));
                    }else{
                        state.connecting = true;
                        gattReceiver.setConnectAdapter(new ConnectStateAdapter(device, connectStateListener));
                        device.connectGatt(context, false, gattReceiver);
                    }
                }else if(currGatt != null && bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED){
                    onException(new BleException(Message.ALEADY_CONNECTED));
                }else if(currGatt != null && bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTING){
                    onException(new BleException(Message.ALEADY_CONNECTING));
                }else {
                    onException(new BleException(Message.ALEADY_CONNECTING));
                }
            } else if (BluetoothDevice.BOND_BONDING == device.getBondState()) {
                onException(new BleException(Message.BT_BONDING));
            } else {
                state.connecting = true;
                gattReceiver.setConnectAdapter(new ConnectStateAdapter(device, connectStateListener));
                device.connectGatt(context, false, gattReceiver);
            }
        }else if(check == 1){
            listener.onException(new BleException(Message.BT_NOT_ENABLE, "connect"));
        }
    }

    @SuppressLint("MissingPermission")
    public boolean isConnected(){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return currGatt !=null && bluetoothManager.getConnectionState(currGatt.getDevice(), BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED;
    }

    public void disconnect(){
        currDevice = null;
        if(currGatt != null) {
            currGatt.disconnect();
            currGatt.close();
        }
        state.connecting = false;
        currGatt = null;
    }

    @SuppressLint("MissingPermission")
    public void paring(BluetoothDevice device){
        int check = checkBluetooth();
        if(check == 0) {
            boardCastStateAdapter.initBond(device);
            device.createBond();
        }else if(check == 1){
            listener.onException(new BleException(Message.BT_NOT_ENABLE, "paring"));
        }
    }

    public UniBle addService(GattService service){
        Service serviceValue = service.getClass().getAnnotation(Service.class);
        if(serviceValue == null){
            throw new RuntimeException(service.getClass().getName()+"서비스 등록 오류 : @Service를 정의하지 않은 클래스("+service.getClass().getName()+")");
        }
        String name = serviceValue.name();
        if(name.isEmpty()){
            name = service.getClass().getSimpleName();
        }
        String uuid = serviceValue.value();
        GattServiceHub gattServiceHub = new GattServiceHub(name, uuid);

        try {
            for(Field field : service.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if(field.getName().equals("uniBle")){
                    field.set(service, this);
                }

                Characteristic characteristic = field.getAnnotation(Characteristic.class);
                if(characteristic != null) {
                    name = characteristic.name();
                    uuid = (String) field.get(service);
                    if (name.isEmpty()) {
                        name = uuid;
                    }
                    gattServiceHub.addCharacteristic(name, uuid);
                    continue;
                }
                Desciptor desciptor = field.getAnnotation(Desciptor.class);

                if(desciptor != null) {
                    name = desciptor.name();
                    uuid = (String) field.get(service);
                    if (name.isEmpty()) {
                        name = uuid;
                    }
                    gattServiceHub.addDesciptor(name, uuid);
                    continue;
                }
            }
            gattServiceHub.setGattService(service);
            gattReceiver.services.put(gattServiceHub.getName(), gattServiceHub);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    public GattServiceHub getHub(Class<? extends GattService> gattServiceClass){
        return gattReceiver.services.get(gattServiceClass.getSimpleName());
    }

    public <T extends GattService> T getGattService(Class<T> gattServiceClass){
        return (T)getHub(gattServiceClass).getGattGervice();
    }


}

