package io.relayr.ble.service;

import android.annotation.TargetApi;
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
import java.util.UUID;

import io.relayr.ble.BleDevice;
import io.relayr.ble.BleDeviceType;
import rx.Observer;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SFLOAT;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT16;
import static io.relayr.ble.service.TestValues.*;
import static java.util.UUID.fromString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DirectConnectionServiceTest {

    private static final int EXPECTED_FREQUENCY = 1000; // in milliseconds
    private static final float EXPECTED_THRESHOLD = 3.3f;

    @Mock private BluetoothGattCharacteristic frequencyCharacteristic;
    @Mock private BluetoothGattCharacteristic thresholdCharacteristic;
    @Mock private BluetoothGattCharacteristic sensorIdCharacteristic;
    @Mock private BluetoothGatt gatt;
    private BluetoothGattReceiver receiver = new BluetoothGattReceiver();
    private DirectConnectionService service;

    @Before public void initialise() {
        MockitoAnnotations.initMocks(this);
        BleDevice bleDevice = mock(BleDevice.class);
        when(bleDevice.getType()).thenReturn(BleDeviceType.WunderbarMIC);

        BluetoothGattService batteryService = mock(BluetoothGattService.class);
        when(batteryService.getUuid()).thenReturn(fromString("00002002-0000-1000-8000-00805f9b34fb"));

        when(frequencyCharacteristic.getIntValue(FORMAT_UINT16, 0)).thenReturn(EXPECTED_FREQUENCY);
        when(frequencyCharacteristic.getValue()).thenReturn(new byte[]{0x38, 0x03, 0x00, 0x00});
        when(frequencyCharacteristic.getUuid()).thenReturn(fromString("00002012-0000-1000-8000-00805f9b34fb"));

        when(thresholdCharacteristic.getFloatValue(FORMAT_SFLOAT, 0)).thenReturn(EXPECTED_THRESHOLD);
        when(thresholdCharacteristic.getValue()).thenReturn(("" + FORMAT_SFLOAT).getBytes());
        when(thresholdCharacteristic.getUuid()).thenReturn(fromString("00002014-0000-1000-8000-00805f9b34fb"));

        when(sensorIdCharacteristic.getValue()).thenReturn(EXPECTED_SENSOR_ID_AS_BYTE_ARRAY);
        when(sensorIdCharacteristic.getUuid()).thenReturn(fromString("00002010-0000-1000-8000-00805f9b34fb"));

        List<BluetoothGattCharacteristic> batteryCharacteristics =
                Arrays.asList(frequencyCharacteristic, sensorIdCharacteristic, thresholdCharacteristic);
        when(batteryService.getCharacteristics()).thenReturn(batteryCharacteristics);

        List<BluetoothGattService> services = Arrays.asList(batteryService);

        when(gatt.getServices()).thenReturn(services);
        service = new DirectConnectionService(bleDevice, gatt, receiver);
    }

    @Test public void getSensorFrequencyTest() {
        @SuppressWarnings("unchecked")
        Observer<? super Integer> observer = mock(Observer.class);
        service.getSensorFrequency()
               .subscribe(observer);
        receiver.onCharacteristicRead(gatt, frequencyCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_FREQUENCY);
    }

    @Test public void getSensorThresholdTest() {
        @SuppressWarnings("unchecked")
        Observer<? super BluetoothGattCharacteristic> observer = mock(Observer.class);
        service.getSensorThreshold()
                .subscribe(observer);
        receiver.onCharacteristicRead(gatt, thresholdCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(any(BluetoothGattCharacteristic.class));
    }

    @Test public void getSensorIdTest() {
        @SuppressWarnings("unchecked")
        Observer<? super UUID> observer = mock(Observer.class);
        service.getSensorId()
               .subscribe(observer);
        receiver.onCharacteristicRead(gatt, sensorIdCharacteristic, GATT_SUCCESS);
        verify(observer).onNext(EXPECTED_SENSOR_ID);
    }

}
