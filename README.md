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

## Create 2 list view like this:

### ListView for show Bluetooth Devices
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:orientation="vertical">

	<TextView
		android:id="@+id/txBleDeviceName"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:textSize="16dp" />
	<TextView
		android:id="@+id/txBleDeviceAddress"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:textSize="12dp" />
</LinearLayout>
```

### ListView for show Bluetooth Device UUID
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="UUID: "
        android:textSize="16dp"
        android:id="@+id/textView2"
        android:textStyle="bold" />

    <TextView
        android:id="@+id/txGattServiceUUID"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="14dp"
        android:layout_alignParentTop="true"
        android:layout_toEndOf="@+id/textView2"
        android:paddingLeft="3dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name: "
        android:textSize="16dp"
        android:id="@+id/textView3"
        android:layout_below="@+id/txGattServiceUUID"
        android:layout_alignParentStart="true"
        android:textStyle="bold" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12dp"
        android:id="@+id/txGattServiceName"
        android:layout_alignTop="@+id/textView3"
        android:layout_alignEnd="@+id/txGattServiceUUID"
        android:layout_toEndOf="@+id/textView3"
        android:paddingLeft="3dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Data"
        android:id="@+id/textView4"
        android:layout_below="@+id/textView3"
        android:layout_alignParentStart="true"
        android:layout_alignEnd="@+id/txGattServiceName"
        android:layout_centerInParent="true"
        android:textSize="16dp"
        android:textStyle="bold"
        android:textAlignment="center" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:text=""
        android:id="@+id/txGattServiceData"
        android:layout_below="@+id/textView4"
        android:layout_alignParentStart="true"
        android:layout_alignEnd="@+id/txGattServiceName" />
</RelativeLayout>

```

## Create a Activity for listing Bluetooth device
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
        Log.d("BLE", "[onEventBluetooth]");
        Log.d("BLE", bluetoothDevice.getBluetoothClass().toString());

		// Add BluetoothDevice in adapter
        devicesAdapter.add(bluetoothDevice, rssi);
        devicesAdapter.notifyDataSetChanged();
    }

}
```

## Create activity for list UUID os bluetooth device

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
        Log.d("BLE", "[displayData] Action: " + action);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices){
        Log.d("BLE", "[displayGattServices]");
        String uuid = null;

        if(gattServices == null) {
            Log.w("BLE", "[displayGattServices] No Bluetooth Gatt Service list.");
            return;
        }

        for(BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            Log.d("BLE", "[displayGattServices] UUID: " + uuid);
            gattServiceAdapter.add(gattService);
        }
        gattServiceAdapter.notifyDataSetChanged();
    }
}
```
## Getting Started


# Contributors

# License
