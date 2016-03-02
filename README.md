# Bluetooth Low Energy Library for Android


# Installation 

In settings.gadle

```
include ':app'
include ':libBluetoothLE'
project(':libBluetoothLE').projectDir = new File('../BluetoothLowEnergy/libBluetoothLE')
```

Add BluetoothService in AndroidManifest.xml
```xml
<manifest>
   ...
   <application>
	   ...
       <service android:name="org.cetasenai.bluetoothlowenergy.BluetoothService" android:enabled="true"/>
       ...
   </application>
   ...
</manifest>
```

# Code Example

```java
public class MainActivity extends Activity implements BluetoothEvent {

	private BluetoothDevicesAdapter devicesAdapter;
    private BluetoothScan bs;
	private ListView listView;

	protected void onCreate(Bundle savedInstanceState) {
		...

		devicesAdapter = new BluetoothDevicesAdapter(this, R.layout.bluetooth_item);
        devicesAdapter.setResources(R.id.tx_bluetooth_item_name, R.id.tx_bluetootj_item_address, 0);
		listView.setAdapter(devicesAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), DeviceActivity.class);
                BluetoothDevice bDevice = (BluetoothDevice) parent.getItemAtPosition(position);
                intent.putExtra("deviceAddress", bDevice.getAddress());
                startActivity(intent);
            }
        });		


		bs.DiscoveryLE();
	}


	@Override
    protected void onDestroy() {
        super.onDestroy();
        bs.unregisterReceiver();
    }

	public void onEventBluetooth(BluetoothDevice bluetoothDevice, int rssi) {
        Log.d(TAGNAME, "[onEventBluetooth]");
        Log.d(TAGNAME, bluetoothDevice.getBluetoothClass().toString());

		// Add BluetoothDevice in adapter
        devicesAdapter.add(bluetoothDevice, rssi);
        devicesAdapter.notifyDataSetChanged();
    }

}
```


```java
public class DeviceActivity extends Activity {

	private BluetoothService mBluetoothService;
	protected ListView listViewGattServices;
	private BluetoothGattServiceAdapter gattServiceAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		...

		 // Config Adapter  
        gattServiceAdapter = new BluetoothGattServiceAdapter(this, R.layout.bluetooth_gatt_service_item);
        gattServiceAdapter.setResourceUuid(R.id.txGattServiceUUID);
        gattServiceAdapter.setResourceName(R.id.txGattServiceName);
        gattServiceAdapter.setResourceData(R.id.txGattServiceData);
        listViewGattServices.setAdapter(gattServiceAdapter);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("deviceAddress");
        txAddress.setText(mDeviceAddress);

        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothService = null;
    }

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("BLE", "[mGattUpdateReceiver] Action: " + action);

            if(BluetoothService.ACTION_GATT_CONNECT.equals(action)) {
				...
            }
            else if(BluetoothService.ACTION_GATT_DISCONNECTED.equals(action)){
				...
            }
            else if(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothService.getSupportedGattServices());
            }
            else if (BluetoothService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothService.EXTRA_DATA));
            }
            else {
				...
            }

        }
    };

	private void displayData(String action) {
        Log.d(TAGNAME, "[displayData] Action: " + action);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices){
        Log.d(TAGNAME, "[displayGattServices]");
        String uuid = null;

        if(gattServices == null) {
            Log.w(TAGNAME, "[displayGattServices] No Bluetooth Gatt Service list.");
            return;
        }

        for(BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d(TAGNAME, "[displayGattServices] UUID: " + uuid);
            gattServiceAdapter.add(gattService);
        }
        gattServiceAdapter.notifyDataSetChanged();
    }
}
```
## Getting Started


# Contributors

# License
