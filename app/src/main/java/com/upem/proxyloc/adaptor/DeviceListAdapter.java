package com.upem.proxyloc.adaptor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import android.view.View;
import android.view.ViewGroup;


import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upem.proxyloc.R;
import com.upem.proxyloc.models.DeviceItem;

import java.util.Vector;


public class DeviceListAdapter extends BaseAdapter {


    private Context mContext;
    private Vector<DeviceItem> Vec ;

    public DeviceListAdapter(Vector<DeviceItem> vec ,Context mContext) {
        this.mContext =  mContext;
        this.Vec = vec;
    }

    @Override
    public int getCount() {
        return Vec.size();
    }

    @Override
    public Object getItem(int position) {
        return Vec.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v =  View.inflate(mContext, R.layout.device_item, null);
        TextView name = v.findViewById(R.id.device_name);
        TextView mac = v.findViewById(R.id.mac);
        name.setText(Vec.get(position).getDeviceName());
        mac.setText(Vec.get(position).getAddress());
        return v;
    }

    public void update(Vector<DeviceItem>vecup){
        Vec = vecup;
        notifyDataSetChanged();
    }
}
