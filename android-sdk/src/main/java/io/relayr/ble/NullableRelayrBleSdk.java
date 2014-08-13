package io.relayr.ble;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

class NullableRelayrBleSdk extends RelayrBleSdk {

    public Observable<List<BleDevice>> scan() {
        return Observable.create(new Observable.OnSubscribe<List<BleDevice>>() {
            @Override
            public void call(Subscriber<? super List<BleDevice>> subscriber) {

            }
        });
    }

    public void stop() { }

    public boolean isScanning() { return false; }

}