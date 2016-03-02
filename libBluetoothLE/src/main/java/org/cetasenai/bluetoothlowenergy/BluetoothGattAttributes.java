package org.cetasenai.bluetoothlowenergy;

import java.util.HashMap;

/**
 * Created by Maiko de Andrade on 01/03/16.
 */
public class BluetoothGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    static {
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");

        attributes.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
