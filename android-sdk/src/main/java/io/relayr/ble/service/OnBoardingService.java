package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import java.util.UUID;

import io.relayr.ble.BleDevice;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_ON_BOARDING_FLAG;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_PASS_KEY;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_ID;
import static io.relayr.ble.service.ShortUUID.SERVICE_ON_BOARDING;

/**
 * A class representing the On Boarding Connection BLE Service.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OnBoardingService extends BaseService {

    protected OnBoardingService(BleDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<OnBoardingService> connect(final BleDevice bleDevice,
                                                        final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver)
                .map(new Func1<BluetoothGatt, OnBoardingService>() {
                    @Override
                    public OnBoardingService call(BluetoothGatt gatt) {
                        return new OnBoardingService(bleDevice, gatt, receiver);
                    }
                });
    }

    /**
     * Writes the sensorId characteristic to the associated remote device.
     *
     * @param sensorId A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what is
     * to be written to the device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorId(byte[] sensorId) {
        return write(sensorId, SERVICE_ON_BOARDING, CHARACTERISTIC_SENSOR_ID);
    }

    /**
     * Writes the sensorPassKey characteristic to the associated remote device.
     *
     * @param passKey A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what
     * is to be written to the device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorPassKey(byte[] passKey) {
        return write(passKey, SERVICE_ON_BOARDING, CHARACTERISTIC_PASS_KEY);
    }

    /**
     * Writes the sensorOnBoardingFlag characteristic to the associated remote device.
     *
     * @return Observable<BluetoothGattCharacteristic>, an observable of what is to
     * be written to the device
     */
    public Observable<BluetoothGattCharacteristic> writeOnBoardingFlagToConnectToMasterModule() {
        return write(new byte[] {1}, SERVICE_ON_BOARDING, CHARACTERISTIC_ON_BOARDING_FLAG);
    }

    /**
     * Writes the sensorOnBoardingFlag characteristic to the associated remote device.
     *
     * @return Observable<BluetoothGattCharacteristic>, an observable of
     * what is to be written to the device
     */
    public Observable<BluetoothGattCharacteristic> writeOnBoardingFlagForDirectConnection() {
        return write(new byte[]{0}, SERVICE_ON_BOARDING, CHARACTERISTIC_ON_BOARDING_FLAG);
    }

    /**
     * Returns an observable of the Sensor Id characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details about the actions performed in
     * the background
     * @return an observable of the Sensor Id characteristic
     */
    public Observable<UUID> getSensorId() {
        return readUuidCharacteristic(SERVICE_ON_BOARDING, CHARACTERISTIC_SENSOR_ID, "Sensor Id");
    }

    /**
     * Returns an observable of the Sensor Pass Key characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details about the actions
     * performed in the background.
     * @return an observable of the Sensor Pass Key characteristic
     */
    public Observable<String> getSensorPassKey() {
        return readStringCharacteristic(SERVICE_ON_BOARDING, CHARACTERISTIC_PASS_KEY, "Pass Key");
    }

    /**
     * Returns an observable of the OnBoarding Flag characteristic. 1 indicates that
     * the device is connected to the Master Module
     * 0 indicates direct connection.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details about the actions performed
     * in the background.
     * @return an observable of the OnBoarding Flag characteristic
     */
    public Observable<Integer> getOnBoardingFlag() {
        return readByteAsAnIntegerCharacteristic(SERVICE_ON_BOARDING,
                CHARACTERISTIC_ON_BOARDING_FLAG, "OnBoarding Flag");
    }

}
