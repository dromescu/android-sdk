package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import java.util.UUID;

import io.relayr.ble.BleUtils;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SFLOAT;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static io.relayr.ble.service.Utils.getCharacteristicInServices;
import static rx.Observable.error;
import static rx.Observable.just;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class Service {

    protected final BluetoothGatt mBluetoothGatt;
    protected final BluetoothGattReceiver mBluetoothGattReceiver;

    protected Service(BluetoothGatt gatt, BluetoothGattReceiver receiver) {
        mBluetoothGatt = gatt;
        mBluetoothGattReceiver = receiver;
    }

    protected static Observable<? extends BluetoothGatt> doConnect(
            final BluetoothDevice bluetoothDevice, final BluetoothGattReceiver receiver) {
        return receiver
                .connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.discoverDevices(gatt);
                    }
                });
    }

    protected Observable<BluetoothGattCharacteristic> write(byte[] bytes,
                                                            String serviceUuid,
                                                            String characteristicUuid) {
        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(characteristicUuid));
        }
        characteristic.setValue(bytes);
        return mBluetoothGattReceiver.writeCharacteristic(mBluetoothGatt, characteristic);
    }

    protected Observable<BluetoothGattCharacteristic> readCharacteristic(String serviceUuid,
                                                                         String characteristicUuid,
                                                                         final String what) {

        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                mBluetoothGatt.getServices(), serviceUuid, characteristicUuid);
        if (characteristic == null) {
            return error(new CharacteristicNotFoundException(what));
        }
        return mBluetoothGattReceiver.readCharacteristic(mBluetoothGatt, characteristic);
    }

    protected Observable<Float> readFloatCharacteristic(String serviceUuid,
                                                        String characteristicUuid,
                                                        final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Float>>() {
                    @Override
                    public Observable<Float> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just(charac.getFloatValue(FORMAT_SFLOAT, 0));
                    }
                });
    }

    protected Observable<Integer> readIntegerCharacteristic(String serviceUuid,
                                                            String characteristicUuid,
                                                            final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just(charac.getIntValue(FORMAT_UINT16, 0));
                    }
                });
    }

    protected Observable<Integer> readByteAsAnIntegerCharacteristic(String serviceUuid,
                                                                    String characteristicUuid,
                                                                    final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(BluetoothGattCharacteristic charac) {
                        if (charac.getValue() == null || charac.getValue().length == 0) {
                            error(new CharacteristicNotFoundException(what));
                        }
                        return just((int) charac.getValue()[0]);
                    }
                });
    }


    protected Observable<String> readStringCharacteristic(String serviceUuid,
                                                          String characteristicUuid,
                                                          final String what) {
        return readCharacteristic(serviceUuid, characteristicUuid, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<String>>() {
                    @Override
                    public Observable<String> call(BluetoothGattCharacteristic charac) {
                        String value = charac.getStringValue(0);
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(what));
                        }
                        return just(value);
                    }
                });
    }

    protected Observable<UUID> readUuidCharacteristic(String service, String characteristic,
                                                      final String what) {
        return readCharacteristic(service, characteristic, what)
                .flatMap(new Func1<BluetoothGattCharacteristic, Observable<UUID>>() {
                    @Override
                    public Observable<UUID> call(BluetoothGattCharacteristic characteristic) {
                        byte[] value = characteristic.getValue();
                        if (value == null) {
                            return error(new CharacteristicNotFoundException(what));
                        }
                        return just(BleUtils.fromBytes(value));
                    }
                });
    }

}
