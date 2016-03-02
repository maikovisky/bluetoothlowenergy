/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Maiko de Andrade <maikovisky@gmail.com>
 */

package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Maiko de Andrade on 16/02/16.
 */
public interface BluetoothEvent {

    void onEventBluetooth(BluetoothDevice bluetoothDevice, int rssi);
}
