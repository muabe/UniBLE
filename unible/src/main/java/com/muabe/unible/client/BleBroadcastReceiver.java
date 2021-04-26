package com.muabe.unible.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BleBroadcastReceiver extends BroadcastReceiver {
    UniBle uniBle;
    Context context;
    BleListener listener;

    BoardCastStateAdapter boardCastStateAdapter;

    public BleBroadcastReceiver(UniBle uniBle, BoardCastStateAdapter boardCastStateAdapter){
        this.uniBle = uniBle;
        this.context = uniBle.getContext();
        this.listener = uniBle;
        this.boardCastStateAdapter = boardCastStateAdapter;
    }

    void regist(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        filter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        context.registerReceiver(this, filter);
    }

    void unRegist(){
        context.unregisterReceiver(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        switch (action) {
            //블루투스 활성화
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                //블루투스 활성화
                if (state == BluetoothAdapter.STATE_ON) {
                    boardCastStateAdapter.bluetoothOn();
                }
                //블루투스 활성화 중
                else if (state == BluetoothAdapter.STATE_TURNING_ON) {

                }
                //블루투스 비활성화
                else if (state == BluetoothAdapter.STATE_OFF) {
                    boardCastStateAdapter.bluetoothOff();
                }
                //블루투스 비활성화 중
                else if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                }
                break;

            //블루투스 디바이스 찾음
            case BluetoothDevice.ACTION_FOUND:
                break;

            //블루투스 디바이스 검색 종료
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                break;

            //블루투스 디바이스 페어링 상태 변화
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                if(boardCastStateAdapter.device != null && boardCastStateAdapter.device.getAddress().equals(device.getAddress())) {
                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        boardCastStateAdapter.onBonded(device);
                        boardCastStateAdapter.device = null;
                    } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        boardCastStateAdapter.onBondNone(device);
                        boardCastStateAdapter.device = null;
                    }
                }
                break;
        }

    }
}
