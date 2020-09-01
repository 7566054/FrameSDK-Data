package com.supoin.framesdk.bean;

/**
 * Created by zwei on 2018/6/9.
 *
 */

public class T_Bluetooth {
    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public void setBluetoothAddress(String bluetoothAddress) {
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getPairedStatus() {
        return pairedStatus;
    }

    public void setPairedStatus(String pairedStatus) {
        this.pairedStatus = pairedStatus;
    }

    private String bluetoothName;
    private String bluetoothAddress;
    private String pairedStatus;
}
