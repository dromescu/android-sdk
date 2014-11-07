package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.RelayrSdk;

import static android.content.Context.BLUETOOTH_SERVICE;

@Module(
        complete = false,
        library = true
)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleModule {

    private final Context app;

    public BleModule(Context context) {
        app = context;
    }

    private BluetoothAdapter getBluetoothAdapter() {
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager manager = (BluetoothManager) app.getSystemService(BLUETOOTH_SERVICE);
        return manager == null ? null: manager.getAdapter();
    }

    @Provides @Singleton PackageManager providePackageManager() {
        return app.getPackageManager();
    }

    @Provides @Singleton BleUtils provideBleUtils(PackageManager manager) {
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ?
                new BleUtils(null, manager): new BleUtils(getBluetoothAdapter(), manager);
    }

    @Provides @Singleton RelayrBleSdk provideRelayrBleSdk() {
        return RelayrSdk.isBleSupported() ?
                new RelayrBleSdkImpl(getBluetoothAdapter(), new BleDeviceManager()) :
                new NullableRelayrBleSdk();
    }

}
