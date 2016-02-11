package com.shubham.ble_application.entity;

import android.bluetooth.BluetoothDevice;

/**
 * Created by LENOVO on 12/4/2015.
 */
public class ScannedInformation {

    BluetoothDevice device;
    int rssi;
    byte[] record;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getRssi() { return rssi; }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getRecord() {
        return record;
    }

    public void setRecord(byte[] record) {
        this.record = record;
    }
}
