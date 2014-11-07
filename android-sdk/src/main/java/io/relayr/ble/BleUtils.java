package io.relayr.ble;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.nio.ByteBuffer;
import java.util.UUID;

import javax.inject.Inject;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleUtils {

    protected static final int STATUS_BLE_ENABLED = 0;
    private static final int STATUS_BLUETOOTH_NOT_AVAILABLE = 1;
    private static final int STATUS_BLE_NOT_AVAILABLE = 2;
    private static final int STATUS_BLUETOOTH_DISABLED = 3;

    private final static int REQUEST_ENABLE_BLUETOOTH = 1;

    private final BluetoothAdapter mBluetoothAdapter;
    private final PackageManager mPackageManager;

    @Inject public BleUtils(BluetoothAdapter bluetoothAdapter, PackageManager packageManager) {
        mBluetoothAdapter = bluetoothAdapter;
        mPackageManager = packageManager;
    }

    public boolean isBleAvailable() {
        return getBleStatus() == STATUS_BLE_ENABLED;
    }

    protected boolean isSdk18() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public boolean isBleSupported() {
        return isSdk18() && isBleSupportedIndeed();
    }

    protected boolean isBleSupportedIndeed() {
        return mPackageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public void promptUserToActivateBluetooth(Activity activity) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        }
    }

    protected int getBleStatus() {
        if (!isBleSupported()) return STATUS_BLE_NOT_AVAILABLE;

        return mBluetoothAdapter == null ? STATUS_BLUETOOTH_NOT_AVAILABLE:
                mBluetoothAdapter.isEnabled() ? STATUS_BLE_ENABLED:
                        STATUS_BLUETOOTH_DISABLED;
    }

    public static UUID fromBytes(byte[] value) {
        ByteBuffer bb = ByteBuffer.wrap(value);
        long firstLong = bb.getLong();
        long secondLong = bb.getLong();
        return new UUID(firstLong, secondLong);
    }

    public static String getShortUUID(UUID uuid) {
        return getShortUUID(uuid.toString());
    }
    public static String getShortUUID(String longUUID) {
        return longUUID.substring(4, 8);
    }
}
