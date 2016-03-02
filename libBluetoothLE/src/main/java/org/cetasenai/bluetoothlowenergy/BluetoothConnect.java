/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Maiko de Andrade <maikovisky@gmail.com>
 */
package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by maiko on 19/02/16.
 */
public class BluetoothConnect {

    private static final String TAGNAME = "BluetoothConnect";
    private Context context;
    private BluetoothDevice device;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothService mBluetoothService;
    private BluetoothGattCallback mBluetoothGattCallback;
    private String mDeviceAddress;


    public BluetoothConnect(Context context, BluetoothDevice device) {
        Log.d(TAGNAME, "BluetoothConnect");
        this.context = context;
        this.device = device;
       // mBluetoothGatt = this.device.connectGatt(context, false, mBluetoothGattCallback);
        Intent gattServiceIntent = new Intent(this.context, BluetoothService.class);
        this.context.bindService(gattServiceIntent, mServiceConnection, this.context.BIND_AUTO_CREATE);

    }

    public void disconnect() {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAGNAME, "onServiceConnected: ");
            mBluetoothService = ((BluetoothService.LocalBinder) service).getService();
            if(!mBluetoothService.initialize()) {
                Log.e(TAGNAME, "Unable initialize Bluetooth");
                return;
            }
            mBluetoothService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAGNAME, "onServiceDisconnected");
            mBluetoothService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAGNAME, "Action: " + action);
        }
    };
}
