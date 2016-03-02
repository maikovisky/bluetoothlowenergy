package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Maiko de Andrade on 16/02/16.
 */
public interface BluetoothEvent {

    void onEventBluetooth(BluetoothDevice bluetoothDevice, int rssi);
}
