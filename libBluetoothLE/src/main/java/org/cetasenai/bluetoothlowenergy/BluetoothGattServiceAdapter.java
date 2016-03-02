package org.cetasenai.bluetoothlowenergy;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by maiko on 01/03/16.
 */
public class BluetoothGattServiceAdapter extends BaseAdapter {

    private final static String TAGNAME = "BLEGattServiceAdapter";
    private final LayoutInflater inflater;
    private final ArrayList<BluetoothGattService> bluetoothGattServiceArrayList = new ArrayList<>();
    private int resource;
    private int resourceUuid = 0;
    private int resourceName = 0;
    private int resourceData = 0;

    /**
     *
     * @param context
     * @param resource Resource da list_view
     */
    public BluetoothGattServiceAdapter(Context context, int resource) {
        inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    public void add(BluetoothGattService gattService) {
        Log.d(TAGNAME, "[add] Gatt Service uuid: " + gattService.getUuid().toString());
        bluetoothGattServiceArrayList.add(gattService);
    }

    @Override
    public int getCount() {
        return bluetoothGattServiceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bluetoothGattServiceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String uuid;
        if(convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder(convertView, resourceUuid, resourceName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothGattService gattService = bluetoothGattServiceArrayList.get(position);
        uuid = gattService.getUuid().toString();
        viewHolder.setUuid(uuid);
        viewHolder.setName(BluetoothGattAttributes.lookup(uuid, "Unknown"));

        return convertView;
    }

    public void setResourceUuid(int resourceUuid) {
        this.resourceUuid = resourceUuid;
    }
    public void setResourceName(int resourceName) {
        this.resourceName = resourceName;
    }
    public void setResourceData(int resourceData) {
        this.resourceData = resourceData;
    }

    static final class ViewHolder {
        TextView uuid;
        TextView name;

        private ViewHolder(View view, int resourceUuid, int resourceName) {
            if(resourceUuid != 0)
                uuid = (TextView) view.findViewById(resourceUuid);

            if(resourceName != 0)
                uuid = (TextView) view.findViewById(resourceName);
        }


        void setUuid(String uuid) {
            if(this.uuid != null)
                this.uuid.setText(uuid);
        }

        void setName(String name) {
            if(this.name != null)
                this.name.setText(name);
        }
    }
}
