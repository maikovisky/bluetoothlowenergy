package org.cetasenai.bluetoothlowenergy;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

/**
 * Created by maiko on 19/02/16.
 */
public class BluetoothService extends Service {

    private static final String TAGNAME = "BluetoothService";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdpter;
    private BluetoothGatt    mBluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED  = 2;
    public static final String ACTION_GATT_CONNECT = "bluetooth.le.ACTION_GATT_CONNECT";
    public static final String ACTION_GATT_DISCONNECTED = "bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE = "bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "bluetooth.le.EXTRA_DATA";

    private String mBluetoothAddress;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECT;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                mBluetoothGatt.discoverServices();
            }
            else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);
            }

            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status == mBluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAGNAME, "onServicesDiscovered recived: " + status);
            }
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(status == mBluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE);
            }
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.d(TAGNAME, characteristic.getUuid().toString());
        final byte[] data = characteristic.getValue();
        if(data != null && data.length >0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data){
                stringBuilder.append(String.format("%02X ", byteChar));
            }
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
        }
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onUnBind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {
        Log.d(TAGNAME, "Initialize");
        if(mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager == null) {
                Log.e(TAGNAME, "Unable to initialize BluetoothManager");
                return false;
            }
        }

        mBluetoothAdpter =  mBluetoothManager.getAdapter();
        if(mBluetoothAdpter == null) {
            Log.e(TAGNAME, "Unable to initialize BluetoothAdapter");
            return false;
        }

        return true;
    }

    public boolean connect(String aAddress) {
        Log.d(TAGNAME, "Connect at address: " + aAddress);

        if(mBluetoothAdpter == null || aAddress == null) {
            Log.w(TAGNAME, "BluetoothAdpter not initialize or unspecified address.");
            return false;
        }

        if(mBluetoothAddress != null && aAddress.equals(mBluetoothAddress) && mBluetoothGatt != null) {
            Log.d(TAGNAME, "Try to use an existing mBlutoothGatt for connection.");
            if(mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdpter.getRemoteDevice(aAddress);
        if(device == null) {
            Log.w(TAGNAME, "Device not found. Unable to connect");
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAGNAME, "Trying to conncet a new connection");
        mBluetoothAddress = aAddress;
        mConnectionState = STATE_CONNECTING;

        return true;
    }

    public void disconnect() {
        if(mBluetoothAdpter == null || mBluetoothGatt == null) {
            Log.w(TAGNAME, "Bluetooth not initialized");
            mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if(mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if(mBluetoothAdpter == null || mBluetoothGatt == null) {
            Log.w(TAGNAME, "Bluetooth not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic, boolean enable) {
        if(mBluetoothAdpter == null || mBluetoothGatt == null) {
            Log.w(TAGNAME, "Bluetooth not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);


    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if(mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
}
