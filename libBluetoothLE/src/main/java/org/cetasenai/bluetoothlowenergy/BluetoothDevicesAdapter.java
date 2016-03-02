package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maiko de Andrade on 18/02/16.
 *
 * Classe para visualizaçao das informaçoes dos devices Bluetooth
 */
public class BluetoothDevicesAdapter extends BaseAdapter {

    private final String TAGNAME = "BluetoothDevicesAdapter";
    private final ArrayList<BluetoothDevice> bluetoothDevicesArrayList = new ArrayList<>();
    private final LayoutInflater inflater;
    private int resource;
    private int resourceName = 0;
    private int resourceAddress = 0;
    private int resourceSignal = 0;

    /**
     *
     * @param context
     * @param resource Resource da list_view
     */
    public BluetoothDevicesAdapter(Context context, int resource) {
        inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    /**
     * Define os resources dos campos que serão preenchidos no listview.
     * @param resourceName
     * @param resourceAddress
     * @param resourceSignal
     */
    public void setResources(int resourceName, int resourceAddress, int resourceSignal) {
        setResourceName(resourceName);
        setResourceAddress(resourceAddress);
        setResourceSignal(resourceSignal);
    }

    public void setResourceName(int resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceAddress(int resourceAddress) {
        this.resourceAddress = resourceAddress;
    }

    public void setResourceSignal(int resourceSignal) {
        this.resourceSignal = resourceSignal;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }


    public void add(BluetoothDevice device, int rssi) {
        Log.i(TAGNAME, "Add: " + device.getAddress());
        final List<BluetoothDevice> devices = new ArrayList<>(getDevices());
        for (BluetoothDevice d: devices) {
            if(device.getAddress().equals(d.getAddress())) {
                Log.i(TAGNAME, "Found: " + d.getAddress());
                return;
            }
        }

        bluetoothDevicesArrayList.add(device);

    }

    public List<BluetoothDevice> getDevices() {
        return bluetoothDevicesArrayList;
    }

    @Override
    public int getCount() {
        return bluetoothDevicesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothDevicesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder(convertView, resourceName, resourceAddress, resourceSignal);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = bluetoothDevicesArrayList.get(position);
        viewHolder.setValues(device.getName(), device.getAddress(), "");


        return convertView;
    }

    static final class ViewHolder {
        TextView name;
        TextView address;
        TextView signal;

        void setValues(String name, String address, String singal) {
            setName(name);
            setAddress(address);
            setSignal(singal);
        }

        void setName(String name) {
            if(this.name != null)
                this.name.setText(name);
        }

        void setAddress(String address) {
            if(this.address != null)
                this.address.setText(address);
        }

        void setSignal(String signal) {
            if(this.signal != null)
                this.signal.setText(signal);
        }


        private ViewHolder(View view, int resourceName, int resourceAddress, int resourceSignal) {
            if(resourceName != 0)
                name = (TextView) view.findViewById(resourceName);

            if(resourceAddress != 0)
                address = (TextView) view.findViewById(resourceAddress);

            if(resourceSignal != 0)
                signal = (TextView) view.findViewById(resourceSignal);
        }
    }
}
