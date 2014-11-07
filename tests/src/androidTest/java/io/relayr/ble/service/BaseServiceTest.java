package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.List;

import io.relayr.ble.BleDevice;
import io.relayr.ble.service.error.CharacteristicNotFoundException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static java.util.UUID.fromString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BaseServiceTest {

    private static final int EXPECTED_BATTERY = 100;
    private static final String EXPECTED_FIRMWARE = "1.2.3";
    private static final String EXPECTED_HARDWARE = "1.0.0";
    private static final String EXPECTED_MANUFACTURER = "Relayr";

    @Mock private BleDevice bleDevice;
    @Mock private BluetoothDevice device;
    @Mock private BluetoothGattCharacteristic batteryCharacteristic;
    @Mock private BluetoothGattCharacteristic firmwareCharacteristic;
    @Mock private BluetoothGattCharacteristic hardwareCharacteristic;
    @Mock private BluetoothGattCharacteristic manufacturerCharacteristic;
    @Mock private BluetoothGatt gatt;
    private BluetoothGattReceiver receiver = new BluetoothGattReceiver();
    private BaseService baseService;

    @Before public void initialise() {
        MockitoAnnotations.initMocks(this);

        BluetoothGattService batteryService = mock(BluetoothGattService.class);
        when(batteryService.getUuid()).thenReturn(fromString("0000180f-0000-1000-8000-00805f9b34fb"));

        when(batteryCharacteristic.getValue()).thenReturn(new byte[] {EXPECTED_BATTERY});
        when(batteryCharacteristic.getUuid()).thenReturn(fromString("00002a19-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> batteryCharacteristics = Arrays.asList(batteryCharacteristic);
        when(batteryService.getCharacteristics()).thenReturn(batteryCharacteristics);


        BluetoothGattService deviceInfoService = mock(BluetoothGattService.class);
        when(deviceInfoService.getUuid()).thenReturn(fromString("0000180A-0000-1000-8000-00805f9b34fb"));

        when(firmwareCharacteristic.getStringValue(0)).thenReturn(EXPECTED_FIRMWARE);
        when(firmwareCharacteristic.getValue()).thenReturn(EXPECTED_FIRMWARE.getBytes());
        when(firmwareCharacteristic.getUuid()).thenReturn(fromString("00002A26-0000-1000-8000-00805f9b34fb"));

        when(hardwareCharacteristic.getStringValue(0)).thenReturn(EXPECTED_HARDWARE);
        when(hardwareCharacteristic.getUuid()).thenReturn(fromString("00002a27-0000-1000-8000-00805f9b34fb"));

        when(manufacturerCharacteristic.getStringValue(0)).thenReturn(EXPECTED_MANUFACTURER);
        when(manufacturerCharacteristic.getUuid()).thenReturn(fromString("00002a29-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> characteristics = Arrays.asList(
                firmwareCharacteristic, hardwareCharacteristic, manufacturerCharacteristic);
        when(deviceInfoService.getCharacteristics()).thenReturn(characteristics);

        List<BluetoothGattService> services = Arrays.asList(deviceInfoService, batteryService);

        when(gatt.getDevice()).thenReturn(device);
        when(gatt.getServices()).thenReturn(services);
        baseService = new BaseService(bleDevice, gatt, receiver);
    }

    @Test public void connectTest() {
        @SuppressWarnings("unchecked")
        Observer<BaseService> observer = mock(Observer.class);

        BaseService
                .doConnect(device, receiver)
                .map(new Func1<BluetoothGatt, BaseService>() {
                    @Override
                    public BaseService call(BluetoothGatt gatt) {
                        return new BaseService(bleDevice, gatt, receiver);
                    }
                })
                .subscribe(observer);

        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(mock(BluetoothGatt.class), GATT_SUCCESS);

        verify(observer, times(1)).onNext(any(BaseService.class));
    }

    @Test public void disconnectTest() {
        @SuppressWarnings("unchecked")
        Observer<BleDevice> observer = mock(Observer.class);

        BaseService
                .doConnect(device, receiver)
                .map(new Func1<BluetoothGatt, BaseService>() {
                    @Override
                    public BaseService call(BluetoothGatt g) {
                        return new BaseService(bleDevice, gatt, receiver);
                    }
                })
                .flatMap(new Func1<BaseService, Observable<BleDevice>>() {
                    @Override
                    public Observable<BleDevice> call(BaseService baseService) {
                        return baseService.disconnect();
                    }

                })
                .subscribe(observer);

        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(mock(BluetoothGatt.class), GATT_SUCCESS);
        receiver.onConnectionStateChange(mock(BluetoothGatt.class), GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(bleDevice);
    }

    @Test public void readCharacteristic_shouldThrow_CharacteristicNotFoundException() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);
        baseService
                .readCharacteristic("non_existing_service", "non_existing_characteristic", "")
                .subscribe(observer);
        verify(observer, times(1)).onError(any(CharacteristicNotFoundException.class));
    }

    @Test public void getBatteryLevelTest() {
        @SuppressWarnings("unchecked")
        Observer<? super Integer> observer = mock(Observer.class);
        baseService
                .getBatteryLevel()
                .subscribe(observer);
        receiver.onCharacteristicRead(gatt, batteryCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_BATTERY);
    }

    @Test public void getFirmwareVersionTest() {
        @SuppressWarnings("unchecked")
        Observer<? super String> observer = mock(Observer.class);
        baseService
                .getFirmwareVersion()
                .subscribe(observer);
        receiver.onCharacteristicRead(gatt, firmwareCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_FIRMWARE);
    }

    @Test public void getHardwareVersionTest() {
        @SuppressWarnings("unchecked")
        Observer<? super String> observer = mock(Observer.class);
        baseService
                .getHardwareVersion()
                .subscribe(observer);
        receiver.onCharacteristicRead(gatt, hardwareCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_HARDWARE);
    }

    @Test public void getManufacturerTest() {
        @SuppressWarnings("unchecked")
        Observer<? super String> observer = mock(Observer.class);
        baseService
                .getManufacturer()
                .subscribe(observer);
        receiver.onCharacteristicRead(gatt, manufacturerCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_MANUFACTURER);
    }

}
