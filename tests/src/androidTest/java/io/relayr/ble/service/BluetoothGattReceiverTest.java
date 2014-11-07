package io.relayr.ble.service;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import io.relayr.ble.service.error.DisconnectionException;
import io.relayr.ble.service.error.WriteCharacteristicException;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import static android.bluetooth.BluetoothGatt.GATT_FAILURE;
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
public class BluetoothGattReceiverTest {

    @Mock private BluetoothGatt bluetoothGatt;
    @Mock private BluetoothDevice bluetoothDevice;
    private final BluetoothGattReceiver receiver = new BluetoothGattReceiver();

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(bluetoothGatt.getDevice()).thenReturn(bluetoothDevice);
    }

    @Test public void connect_shouldSuccessfullyConnect() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        receiver.connect(bluetoothDevice)
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void connect_shouldSuccessfullyConnectAndThenThrowADisconnectError() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        receiver.connect(bluetoothDevice)
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
        verify(observer, times(1)).onError(any(DisconnectionException.class));
    }

    @Test public void discoverServices_shouldCallOnNext() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        receiver.connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.discoverDevices(gatt);
                    }
                })
                .subscribe(observer);

        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onServicesDiscovered(bluetoothGatt, GATT_SUCCESS);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void disconnect_shouldCallOnComplete() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGatt> observer = mock(Observer.class);

        receiver.connect(bluetoothDevice)
                .flatMap(new Func1<BluetoothGatt, Observable<BluetoothGatt>>() {
                    @Override
                    public Observable<BluetoothGatt> call(BluetoothGatt gatt) {
                        return receiver.disconnect(gatt);
                    }
                })
                .subscribe(observer);


        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_CONNECTED);
        receiver.onConnectionStateChange(bluetoothGatt, GATT_SUCCESS, STATE_DISCONNECTED);

        verify(observer, times(1)).onNext(bluetoothGatt);
    }

    @Test public void writeCharacteristic_shouldWork() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002a19-0000-1000-8000-00805f9b34fb"));

        receiver.writeCharacteristic(mock(BluetoothGatt.class), characteristic)
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_SUCCESS);

        verify(observer, times(1)).onNext(characteristic);
    }

    @Test public void writeCharacteristic_shouldThrowAnError() {
        @SuppressWarnings("unchecked")
        Observer<BluetoothGattCharacteristic> observer = mock(Observer.class);
        BluetoothGattCharacteristic characteristic = mock(BluetoothGattCharacteristic.class);
        when(characteristic.getUuid()).thenReturn(fromString("00002a19-0000-1000-8000-00805f9b34fb"));

        receiver.writeCharacteristic(mock(BluetoothGatt.class), characteristic)
                .subscribe(observer);

        receiver.onCharacteristicWrite(mock(BluetoothGatt.class), characteristic, GATT_FAILURE);

        verify(observer, times(1)).onError(any(WriteCharacteristicException.class));
    }

}
