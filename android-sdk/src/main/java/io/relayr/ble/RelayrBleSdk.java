package io.relayr.ble;

import java.util.Collection;
import java.util.List;

import rx.Observable;

/**
 * This class handles all methods related to BLE (Bluetooth Low Energy) communication.
 */
public abstract class RelayrBleSdk {

    /**
     * Starts a scan for BLE devices.
     * Since the sensor mode may change, the cache of all found devices is refreshed
     * and they will be discovered again upon a following scan.
     * @param deviceTypes a collection containing all ble type devices you are interested in
     */
    public abstract Observable<List<BleDevice>> scan(Collection<BleDeviceType> deviceTypes);

    /**
     * Stops an ongoing BLE device scan.
     */
    public abstract void stop();

    /**
     * Checks whether a scan for BLE devices is taking place.
     * Returns true in case it is, false otherwise.
     */
    public abstract boolean isScanning();

    /**
     * Used as an access point to the class {@link io.relayr.ble.BleSocketClient}
     * @return the handler of the BleSocket client
     */
    //public abstract SocketClient getBleSocketClient();

}
