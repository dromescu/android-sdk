package io.relayr.ble.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import io.relayr.ble.BleDevice;
import rx.Observable;
import rx.functions.Func1;

/**
 * A class representing the service associated with the CONNECTED_TO_MASTER_MODULE mode
 * @See {@link io.relayr.ble.BleDeviceMode}
 */
public class MasterModuleService extends BaseService {
    protected MasterModuleService(BleDevice device, BluetoothGatt gatt,
                                  BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<MasterModuleService> connect(final BleDevice bleDevice,
                                                          final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver)
                .map(new Func1<BluetoothGatt, MasterModuleService>() {
                    @Override
                    public MasterModuleService call(BluetoothGatt gatt) {
                        return new MasterModuleService(bleDevice, gatt, receiver);
                    }
                });
    }

}
