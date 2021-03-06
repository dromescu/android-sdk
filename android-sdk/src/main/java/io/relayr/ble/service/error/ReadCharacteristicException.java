package io.relayr.ble.service.error;

import android.bluetooth.BluetoothGattCharacteristic;

import io.relayr.ble.BluetoothGattStatus;

public class ReadCharacteristicException extends Exception {

    public final BluetoothGattCharacteristic characteristic;
    public final int status;

    public ReadCharacteristicException(BluetoothGattCharacteristic characteristic, int status) {
        this.characteristic = characteristic;
        this.status = status;
    }

    @Override
    public String toString() {
        return BluetoothGattStatus.toString(status);
    }
}
