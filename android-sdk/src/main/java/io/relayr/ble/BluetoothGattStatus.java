package io.relayr.ble;

import android.bluetooth.BluetoothGatt;

public abstract class BluetoothGattStatus {

    public static final int GATT_FAILURE_BINDING_REQUIRED = 133;

    public static String toString(int status) {
        switch (status) {
            case BluetoothGatt.GATT_FAILURE: {
                return "Failure";
            }
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION: {
                return "Insufficient authentication for a given operation";
            }
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION: {
                return "Insufficient encryption for a given operation";
            }
            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH: {
                return "A write operation exceeds the maximum length of the attribute";
            }
            case BluetoothGatt.GATT_INVALID_OFFSET: {
                return "A read or write operation was requested with an invalid offset";
            }
            case BluetoothGatt.GATT_READ_NOT_PERMITTED: {
                return "GATT read operation is not permitted";
            }
            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED: {
                return "The given request is not supported";
            }
            case BluetoothGatt.GATT_SUCCESS: {
                return "A GATT operation completed successfully";
            }
            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED: {
                return "GATT write operation is not permitted";
            }
            case GATT_FAILURE_BINDING_REQUIRED: {
                return "GATT failure: pairing required";
            }
            default: return "Not identified error";
        }
    }

    public static boolean isFailureStatus(int status) {
        switch (status) {
            case BluetoothGatt.GATT_FAILURE:
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
            case BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH:
            case BluetoothGatt.GATT_INVALID_OFFSET:
            case BluetoothGatt.GATT_READ_NOT_PERMITTED:
            case BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED:
            case BluetoothGatt.GATT_WRITE_NOT_PERMITTED:
            case GATT_FAILURE_BINDING_REQUIRED:
                return true;
            case BluetoothGatt.GATT_SUCCESS:
            default: return false;
        }
    }

}
