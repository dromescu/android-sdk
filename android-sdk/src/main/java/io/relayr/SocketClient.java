package io.relayr;

import io.relayr.model.TransmitterDevice;
import rx.Subscriber;
import rx.Subscription;

public interface SocketClient {

    /**
     * Subscribes an app to a device channel. Enables the app to receive data from the device.
     * @param device The device object to be subscribed to.
     * @param subscriber The app which subscribes to the device channel
     */
    public Subscription subscribe(TransmitterDevice device, Subscriber<Object> subscriber);

    /**
     * Unsubscribes an app from a device channel, stopping and cleaning up the connection.
     * @param sensorId the Id of {@link io.relayr.model.TransmitterDevice}
     */
    public void unSubscribe(final String sensorId);

}
