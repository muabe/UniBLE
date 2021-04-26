package com.muabe.unible.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import com.muabe.unible.client.exception.BleException;
import com.muabe.unible.client.exception.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import androidx.annotation.RequiresApi;


class BleScanner{
    static final long DEFAULT_SCAN_PERIOD = 10000;
    private BluetoothLeScanner leScanner;
    private Handler handler = new Handler();
    private CallBack callBack;
    private KitkatCallback kitkatCallback;
    private BleListener listener;
    private BleState state;

    BleScanner(BluetoothAdapter adapter, BleState state) {
        this.state = state;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leScanner = adapter.getBluetoothLeScanner();
        } else {
            kitkatCallback = new KitkatCallback();
        }
    }

    @SuppressLint("MissingPermission")
    void scan(BleListener listener, long timeout) {
        this.listener = listener;
        if (!state.scanning) {
            state.scanning = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, timeout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callBack = new CallBack(listener);
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                ScanSettings scansettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(0)
                        .build();
                leScanner.startScan(filters, scansettings, callBack);
            } else {
//                adapter.startLeScan(kitkatCallback);
            }
        }
    }

    @SuppressLint("MissingPermission")
    void stop() {
        if(state.scanning) {
            handler.removeMessages(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                leScanner.stopScan(callBack);
                listener.onScanStop(callBack.getResult());
            } else {
//            adapter.stopLeScan(kitkatCallback);
            }
        }
        state.scanning = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    class CallBack extends ScanCallback {
        List<BluetoothDevice> devices;
        BleListener listener;

        CallBack(BleListener listener) {
            devices = Collections.synchronizedList(new ArrayList<BluetoothDevice>());
            this.listener = listener;
        }

        List<BluetoothDevice> getResult() {
            return devices;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(state.scanning) {
                for (BluetoothDevice device : devices) {
                    if(device.getAddress().equals(result.getDevice().getAddress())){
                        return;
                    }
                }
                devices.add(result.getDevice());
                listener.onScanResult(result.getDevice(), result, result.getRssi(), result.getScanRecord().getBytes());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            listener.onException(new BleException(Message.SCAN_FAILED,"errorCode:"+errorCode));
            Log.e("BleScanner", "onScanFailed >" + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("BleScanner", "onBatchScanResults >" + results.size());
        }
    }

    /**
     * 킷캣 이하의 경우
     */
    class KitkatCallback implements BluetoothAdapter.LeScanCallback {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        }
    }
}