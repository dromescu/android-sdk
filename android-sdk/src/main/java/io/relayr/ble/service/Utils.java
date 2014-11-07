package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import java.util.List;

import static io.relayr.ble.BleUtils.getShortUUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
abstract class Utils {

    static BluetoothGattService getServiceForUuid(List<BluetoothGattService> services,
                                                  String shortUuid) {
        for (BluetoothGattService service: services) {
            String serviceUuid = getShortUUID(service.getUuid());
            if (shortUuid.equals(serviceUuid)) return service;
        }
        return null;
    }

    static BluetoothGattCharacteristic getCharacteristicForUuid(
            List<BluetoothGattCharacteristic> characteristics,
            String shortUuid) {
        for (BluetoothGattCharacteristic characteristic: characteristics) {
            String serviceUuid = getShortUUID(characteristic.getUuid());
            if (shortUuid.equals(serviceUuid)) return characteristic;
        }
        return null;
    }

    static BluetoothGattCharacteristic getCharacteristicInServices(
            List<BluetoothGattService> services,
            String serviceUuid,
            String characteristicUuid) {
        BluetoothGattService service = getServiceForUuid(services, serviceUuid);
        if (service == null) return null;

        return getCharacteristicForUuid(service.getCharacteristics(), characteristicUuid);
    }

    static BluetoothGattDescriptor getDescriptorInCharacteristic(
            BluetoothGattCharacteristic characteristic,
            String shortUuid) {
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        for (BluetoothGattDescriptor descriptor: descriptors) {
            String descriptorUuid = getShortUUID(descriptor.getUuid());
            if (shortUuid.equals(descriptorUuid)) return  descriptor;
        }
        return null;
    }

    static String getCharacteristicInServicesAsString(List<BluetoothGattService> services,
                                                      String serviceUuid,
                                                      String characteristicUuid) {

        BluetoothGattCharacteristic characteristic = getCharacteristicInServices(
                services, serviceUuid, characteristicUuid);
        if (characteristic == null) return "";
        return characteristic.getStringValue(0);
    }

}
