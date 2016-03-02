package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Maiko de Andrade on 29/01/16.
 */
public class BluetoothScan  {

    private final static String TAGNAME = "BluetoothScan";
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int SCAN_PERIOD = 10000;

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> listDevices = new ArrayList<>();
    private ArrayList<String> listNameDevices = new ArrayList<String>();
    private Map<BluetoothDevice, Integer> mapDevices = new HashMap<>();
    protected boolean mScanning = false;
    protected Handler mHandler;
    protected BluetoothLeScanner mLEScanner;
    protected ScanSettings settings;
    protected List<ScanFilter> filters;

    BluetoothEvent bluetoothEvent;

    public void setHandlerListner(BluetoothEvent handlerListner) {
        bluetoothEvent = handlerListner;
    }

    public BluetoothScan(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isSupported() {
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e(TAGNAME, "Bluetooth not support");
            return false;
        }
        return true;
    }

    /**
     * Verify if Bluetooth Low Energy is support on device.
     * @return True if bluetooth LE is support
     */
    public boolean isSupportedLE() {
        if(!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e(TAGNAME, "Bluetooth LE is not support");
            return false;
        }
        return true;
    }

    public boolean isEnabled() {
        if(mBluetoothAdapter == null) return false;

        return mBluetoothAdapter.isEnabled();

    }

    public void Discovery() {
        Log.d(TAGNAME, "Discovery");
        if(mBluetoothAdapter.isDiscovering()) {
            Log.d(TAGNAME,"isDiscovering");
            mBluetoothAdapter.cancelDiscovery();
        }
        else {
            Log.d(TAGNAME,"not isDiscovering");
            listDevices.clear();
            listNameDevices.clear();
            mBluetoothAdapter.startDiscovery();
            mContext.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    /**
     *
     */
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAGNAME, "Bluetooth receive.");
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.w(TAGNAME, device.getName() + "\n" + device.getAddress());
                listDevices.add(device);
                listNameDevices.add(device.getName());
                bluetoothEvent.onEventBluetooth(device, 0);
            }
        }
    };

    public void unregisterReceiver() {
        mContext.unregisterReceiver(bReceiver);
    }

    public void DiscoveryLE() {
        scanLeDevice(true);
    }

    public void scanLeDevice(final boolean enable){
        if(enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = true;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    protected BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord){
            Log.d(TAGNAME, "LeScanCallback");
            bluetoothEvent.onEventBluetooth(device, rssi);
        }
    };

    public Map<BluetoothDevice, Integer>getMapDevices() { return mapDevices; }

    public ArrayList<String> getListNameDevices() {
        return listNameDevices;
    }

    public ArrayList<BluetoothDevice> getListDevices() {
        return listDevices;
    }
}
