package io.relayr.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Observer;

import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleDeviceType.WunderbarBRIDG;
import static io.relayr.ble.BleDeviceType.WunderbarGYRO;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
@RunWith(RobolectricTestRunner.class)
public class RelayrSdkImplTest {

    private BleDevice mDevice;

    @Before public void init() {
        BluetoothDevice bleDevice = mock(BluetoothDevice.class);
        when(bleDevice.getAddress()).thenReturn("random");
        mDevice = new BleDevice(bleDevice, WunderbarGYRO.name(), DIRECT_CONNECTION,
                mock(BleDeviceManager.class));
    }

    @Test public void scan_shouldCall_onNext_whenMatchingDevicesHaveBeenDiscovered_beforeThisScan() {
        BleDeviceManager manager = new BleDeviceManager();
        manager.addDiscoveredDevice(mDevice);
        RelayrBleSdk sdk = new RelayrBleSdkImpl(mock(BluetoothAdapter.class), manager);
        Observable<List<BleDevice>> observable = sdk.scan(new HashSet<>(Arrays.asList(WunderbarGYRO)));
        @SuppressWarnings("unchecked")
        Observer<List<BleDevice>> observer = mock(Observer.class);
        observable.subscribe(observer);
        verify(observer).onNext(anyListOf(BleDevice.class));
    }

    @Test public void scan_shouldCall_onNext_whenMatchingDevicesAreDiscovered() {
        BleDeviceManager manager = new BleDeviceManager();
        RelayrBleSdk sdk = new RelayrBleSdkImpl(mock(BluetoothAdapter.class), manager);
        Observable<List<BleDevice>> observable = sdk.scan(new HashSet<>(Arrays.asList(WunderbarGYRO)));
        @SuppressWarnings("unchecked")
        Observer<List<BleDevice>> observer = mock(Observer.class);
        observable.subscribe(observer);
        manager.addDiscoveredDevice(mDevice);
        verify(observer).onNext(anyListOf(BleDevice.class));
    }

    @Test public void scan_shouldNot_interactWithTheObservable_whenNoDevicesAreDiscovered() {
        BleDeviceManager manager = new BleDeviceManager();
        RelayrBleSdk sdk = new RelayrBleSdkImpl(mock(BluetoothAdapter.class), manager);
        Observable<List<BleDevice>> observable = sdk.scan(new HashSet<>(Arrays.asList(WunderbarGYRO)));
        @SuppressWarnings("unchecked")
        Observer<List<BleDevice>> observer = mock(Observer.class);
        observable.subscribe(observer);
        verify(observer, never()).onNext(anyListOf(BleDevice.class));
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Exception.class));
    }

    @Test public void scan_shouldNot_interactWithTheObservable_whenADeviceIAmNotInterestedInIsDiscovered() {
        BleDeviceManager manager = new BleDeviceManager();
        RelayrBleSdk sdk = new RelayrBleSdkImpl(mock(BluetoothAdapter.class), manager);
        Observable<List<BleDevice>> observable = sdk.scan(new HashSet<>(Arrays.asList(WunderbarBRIDG)));
        @SuppressWarnings("unchecked")
        Observer<List<BleDevice>> observer = mock(Observer.class);
        observable.subscribe(observer);
        manager.addDiscoveredDevice(mDevice);
        verify(observer, never()).onNext(anyListOf(BleDevice.class));
        verify(observer, never()).onCompleted();
        verify(observer, never()).onError(any(Exception.class));
    }

}
