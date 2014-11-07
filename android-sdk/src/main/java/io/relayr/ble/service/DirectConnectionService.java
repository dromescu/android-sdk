package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Build;

import java.util.UUID;

import io.relayr.ble.BleDevice;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.functions.Func1;

import static io.relayr.ble.parser.BleDataParser.getFormattedValue;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_CONFIGURATION;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_DATA;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_FREQUENCY;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_ID;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_LED_STATE;
import static io.relayr.ble.service.ShortUUID.CHARACTERISTIC_SENSOR_THRESHOLD;
import static io.relayr.ble.service.ShortUUID.DESCRIPTOR_DATA_NOTIFICATIONS;
import static io.relayr.ble.service.ShortUUID.SERVICE_DIRECT_CONNECTION;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static io.relayr.ble.service.Utils.getDescriptorInCharacteristic;
import static rx.Observable.error;

/**
 * A class representing the Direct Connection BLE Service.
 * The functionality and characteristics available when a device is in DIRECT_CONNECTION mode.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DirectConnectionService extends BaseService {

    DirectConnectionService(BleDevice device, BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        super(device, gatt, receiver);
    }

    public static Observable<DirectConnectionService> connect(final BleDevice bleDevice,
                                                              final BluetoothDevice device) {
        final BluetoothGattReceiver receiver = new BluetoothGattReceiver();
        return doConnect(device, receiver)
                .flatMap(new BondingReceiver.BondingFunc1())
                .map(new Func1<BluetoothGatt, DirectConnectionService>() {
                    @Override
                    public DirectConnectionService call(BluetoothGatt gatt) {
                        return new DirectConnectionService(bleDevice, gatt, receiver);
                    }
                });
    }

    public Observable<String> getReadings() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_DATA);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(CHARACTERISTIC_SENSOR_DATA));
        }
        BluetoothGattDescriptor descriptor = getDescriptorInCharacteristic(
                characteristic, DESCRIPTOR_DATA_NOTIFICATIONS);
        return mBluetoothGattReceiver
                .subscribeToCharacteristicChanges(mBluetoothGatt, characteristic, descriptor)
                .map(new Func1<BluetoothGattCharacteristic, String>() {
                    @Override
                    public String call(BluetoothGattCharacteristic characteristic) {
                        return getFormattedValue(mBleDevice.getType(), characteristic.getValue());
                    }
                });
    }

    public Observable<BluetoothGattCharacteristic> stopGettingReadings() {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_DATA);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(CHARACTERISTIC_SENSOR_DATA));
        }
        BluetoothGattDescriptor descriptor = getDescriptorInCharacteristic(
                characteristic, DESCRIPTOR_DATA_NOTIFICATIONS);
        return mBluetoothGattReceiver
                .unsubscribeToCharacteristicChanges(mBluetoothGatt, characteristic, descriptor);
    }

    /**
     * Returns an observable of the Sensor Id characteristic.
     * <p>See {@link BluetoothGatt#readCharacteristic} for details as to the actions performed in
     * the background.
     * @return an observable of the Sensor Id characteristic
     */
    public Observable<UUID> getSensorId() {
        final String text = "Sensor Id";
        return readUuidCharacteristic(SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_ID, text);
    }

    //public void readBeaconFrequency() {} // 2011

    /**
     * Indicates the sensorFrequency characteristic to the associated remote device. This is the time
     * elapsing between sending one BLE event and the next.
     *
     * <p>See {@link BluetoothGatt#readCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @return Observable<Integer>, an observable of the sensor frequency value
     */
    public Observable<Integer> getSensorFrequency() {
        final String text = "Sensor Frequency";
        return readIntegerCharacteristic(
                SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_FREQUENCY, text);
    }

    /**
     * Indicates the SensorThreshold characteristic to the associated remote device. This is the
     * value that must be exceeded for a sensor to register a change.
     *
     * <p>See {@link BluetoothGatt#readCharacteristic} for details as to the actions performed in
     * the background
     *
     * @return Observable<BluetoothGattCharacteristic>, an observable of the sensor characteristic.
     * In order to get the value from the characteristic call
     * {@link android.bluetooth.BluetoothGattCharacteristic#getValue()}
     */
    public Observable<BluetoothGattCharacteristic> getSensorThreshold() {
        final String text = "Sensor Threshold";
        return readCharacteristic(SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_THRESHOLD, text);
    }

    /**
     * Writes the sensorFrequency characteristic to the associated remote device. This is the time
     * elapsing between sending one BLE event and the next.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param sensorFrequency A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * remote device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorFrequency(byte[] sensorFrequency) {
        return write(sensorFrequency, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_FREQUENCY);
    }

    /**
     * Writes the sensorLedState characteristic to the associated remote device. It will turn the
     * LED on if the operation is carried out successfully.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> turnLedOn() {
        return write(new byte[] {0x01}, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_LED_STATE);
    }

    /**
     * Writes the sensorThreshold characteristic to the associated remote device. This is the
     * value that must be exceeded for a sensor to register a change.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background
     *
     * @param sensorThreshold A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorThreshold(byte[] sensorThreshold) {
        return write(sensorThreshold, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_THRESHOLD);
    }

    /**
     * Writes the sensorConfig characteristic to the associated remote device.
     *
     * <p>See {@link BluetoothGatt#writeCharacteristic} for details as to the actions performed in
     * the background.
     *
     * @param configuration A number represented in Bytes to be written the remote device
     * @return Observable<BluetoothGattCharacteristic>, an observable of what will be written to the
     * device
     */
    public Observable<BluetoothGattCharacteristic> writeSensorConfig(byte[] configuration) {
        return write(configuration, SERVICE_DIRECT_CONNECTION, CHARACTERISTIC_SENSOR_CONFIGURATION);
    }

}
