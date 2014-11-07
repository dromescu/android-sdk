package io.relayr.ble;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Subscriber;

class BleDeviceManager {

    private final Map<String, BleDevice> mDiscoveredDevices = new ConcurrentHashMap<>();
    private final Map<Long, Subscriber<? super List<BleDevice>>> mDevicesSubscriberMap = new ConcurrentHashMap<>();

    void addSubscriber(Long key, Subscriber<? super List<BleDevice>> devicesSubscriber) {
        mDevicesSubscriberMap.put(key, devicesSubscriber);
        if (!mDiscoveredDevices.isEmpty()) devicesSubscriber.onNext(getDiscoveredDevices());
    }

    void addDiscoveredDevice(BleDevice device) {
        if (mDiscoveredDevices.containsKey(device.getAddress()))
            mDiscoveredDevices.remove(device.getAddress());
        mDiscoveredDevices.put(device.getAddress(), device);
        for (Subscriber<? super List<BleDevice>> mDevicesSubscriber : mDevicesSubscriberMap.values())
            mDevicesSubscriber.onNext(getDiscoveredDevices());
    }

    boolean isDeviceDiscovered(String address) {
        return mDiscoveredDevices.containsKey(address);
    }

    boolean isDeviceDiscovered(BleDevice device) {
        return isDeviceDiscovered(device.getAddress());
    }

    void clear() {
        mDiscoveredDevices.clear();
    }

    List<BleDevice> getDiscoveredDevices() {
        return new ArrayList<>(mDiscoveredDevices.values());
    }

    void removeDevice(BleDevice device) {
        mDiscoveredDevices.remove(device.getAddress());
    }

    boolean isDeviceDiscovered(BluetoothDevice device, BleDeviceMode mode) {
        if (!isDeviceDiscovered(device.getAddress())) return false;
        BleDevice bleDevice = mDiscoveredDevices.get(device.getAddress());
        return bleDevice.getMode().equals(mode);
    }

    void removeSubscriber(Long key) {
        mDevicesSubscriberMap.remove(key);
    }

    boolean isThereAnySubscriber() {
        return !mDevicesSubscriberMap.isEmpty();
    }
}
