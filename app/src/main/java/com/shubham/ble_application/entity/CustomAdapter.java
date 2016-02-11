package com.shubham.ble_application.entity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shubham.ble_application.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by LENOVO on 12/5/2015.
 */
public class CustomAdapter extends BaseAdapter{

    Context context;
    int ResourceLayoutId;
    ArrayList<ScannedInformation> devices = new ArrayList<>();

    public CustomAdapter(Context context , int ResourceLayoutId, ArrayList<ScannedInformation> devices)
    {
        this.context = context;
        this.ResourceLayoutId = ResourceLayoutId;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class DeviceHolder{
        public TextView DeviceName;
        public TextView Rssi;
        public TextView Mac;
        //public TextView Adv_data;
        public TextView Id;
        public TextView Network_id;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = convertView;
        DeviceHolder holder;

        rowView = inflater.inflate(ResourceLayoutId, parent, false);

        holder = new DeviceHolder();
        holder.DeviceName = (TextView)rowView.findViewById(R.id.tVSSID);
        holder.Rssi = (TextView)rowView.findViewById(R.id.tVRSSI);
        holder.Mac = (TextView)rowView.findViewById(R.id.tVMAC);
        //holder.Adv_data = (TextView)rowView.findViewById(R.id.adv_data);
        holder.Id = (TextView)rowView.findViewById(R.id.Id);
        holder.Network_id = (TextView)rowView.findViewById(R.id.Nt_Id);

        rowView.setTag(holder);

        ScannedInformation data = devices.get(position);
        holder.Rssi.setText(Integer.toString(data.getRssi()));
        holder.DeviceName.setText(data.getDevice().getName());
        holder.Mac.setText(data.getDevice().getAddress());
        //holder.Adv_data.setText(Arrays.toString(data.getRecord()));

        byte[] adv_data = data.getRecord();
        int offset_adv_data = adv_data[0] + 1;
        offset_adv_data += adv_data[offset_adv_data] + 1;
        offset_adv_data += adv_data[offset_adv_data] + 1;
        byte id = adv_data[offset_adv_data + 6];
        byte network_id = adv_data[offset_adv_data + 7];
        byte AppVersion = adv_data[offset_adv_data + 10];
//        if(AppVersion >10){
//            AppVersion /= AppVersion;
//        }
        if(id != -1) {
            holder.Id.setText("ID: "+Integer.toHexString(id & 0xFF)+" Configured, ");
        }
        else{
            holder.Id.setText("ID: N/A, ");
        }
        if(network_id != -1){
            holder.Network_id.setText("NW_ID: " + Integer.toHexString(network_id & 0xFF) + ", Ver: "+ Integer.toHexString(AppVersion & 0xFF));
        }
        else{
            holder.Network_id.setText("NT_ID: N/A" + ", Ver: "+ Integer.toHexString(AppVersion & 0xFF));
        }
        return rowView;
    }
}
