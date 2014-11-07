package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.UUID;

import io.relayr.ble.BleDevice;
import io.relayr.ble.BleDeviceType;
import rx.Observer;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static io.relayr.ble.service.TestValues.EXPECTED_SENSOR_ID_AS_BYTE_ARRAY;
import static java.util.UUID.fromString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class OnBoardingServiceTest {

    private BluetoothGattCharacteristic characteristic;
    private OnBoardingService service;
    private BluetoothGattReceiver receiver = new BluetoothGattReceiver();

    @Before public void initialise() {
        BleDevice bleDevice = mock(BleDevice.class);
        when(bleDevice.getType()).thenReturn(BleDeviceType.WunderbarMIC);
        BluetoothGatt gatt = mock(BluetoothGatt.class);
        BluetoothGattService gattService = mock(BluetoothGattService.class);
        when(gatt.getServices()).thenReturn(Arrays.asList(gattService));
        when(gattService.getUuid()).thenReturn(fromString("00002001-0000-1000-8000-00805f9b34fb"));
        characteristic = mock(BluetoothGattCharacteristic.class);
        when(gattService.getCharacteristics()).thenReturn(Arrays.asList(characteristic));

        service = new OnBoardingService(bleDevice, gatt, receiver);
    }

    @Test public void writeSensorIdTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeSensorId(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeSensorPassKeyTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002018-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeSensorPassKey(new byte[0])
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeOnBoardingFlagForDirectConnectionTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeOnBoardingFlagForDirectConnection()
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeOnBoardingFlagToConnectToMasterModuleTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));

        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);

        service
                .writeOnBoardingFlagToConnectToMasterModule()
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void getSensorIdTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));
        when(characteristic.getValue()).thenReturn(EXPECTED_SENSOR_ID_AS_BYTE_ARRAY);

        @SuppressWarnings("unchecked")
        Observer<? super UUID> observer = mock(Observer.class);
        service
                .getSensorId()
                .subscribe(observer);

        receiver.onCharacteristicRead(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);
        verify(observer).onNext(TestValues.EXPECTED_SENSOR_ID);
    }

    @Test public void getOnBoardingFlagTest() {
        when(characteristic.getUuid()).thenReturn(fromString("00002019-0000-1000-8000-00805f9b34fb"));
        when(characteristic.getValue()).thenReturn(new byte[] {0x01});

        @SuppressWarnings("unchecked")
        Observer<? super Integer> observer = mock(Observer.class);
        service
                .getOnBoardingFlag()
                .subscribe(observer);

        receiver.onCharacteristicRead(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);
        verify(observer).onNext(1);
    }

    @Test public void getSensorPassKeyTest() {
        String expected = "819683";
        when(characteristic.getUuid()).thenReturn(fromString("00002018-0000-1000-8000-00805f9b34fb"));
        when(characteristic.getStringValue(0)).thenReturn(expected);

        @SuppressWarnings("unchecked")
        Observer<? super String> observer = mock(Observer.class);
        service
                .getSensorPassKey()
                .subscribe(observer);

        receiver.onCharacteristicRead(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);
        verify(observer).onNext(expected);
    }

}
