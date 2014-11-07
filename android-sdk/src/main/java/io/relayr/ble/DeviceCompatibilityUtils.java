package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

public class DeviceCompatibilityUtils {

    private static final String TAG = "DeviceCompatibilityUtils";

    private static boolean isSdk19() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean createBond(BluetoothDevice device) {
        if (isSdk19()) doCreateBond(device);
        return callMethod(device, "createBond");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean doCreateBond(BluetoothDevice device) {
        return device.createBond();
    }

    public static boolean removeBond(BluetoothDevice device) {
        return callMethod(device, "removeBond");
    }

    public static boolean refresh(BluetoothGatt gatt) {
        try {
            Method localMethod = gatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(gatt);
            }
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurred while performing: refresh", localException.getCause());
        }
        return false;
    }

    private static boolean callMethod(BluetoothDevice device, String methodName) {
        try {
            Method localMethod = device.getClass().getMethod(methodName, (Class[]) null);
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(device);
            }
        } catch (Exception localException) {
            Log.e(TAG, "An exception occurred while performing: " + methodName, localException.getCause());
        }
        return false;
    }



}
