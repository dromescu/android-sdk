package io.relayr.websocket;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.relayr.RelayrSdk;
import io.relayr.SocketClient;
import io.relayr.api.SubscriptionApi;
import io.relayr.model.App;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.WebSocketConfig;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

@Singleton
public class WebSocketClient implements SocketClient {

    private static final String TAG = WebSocketClient.class.getSimpleName();
    private final SubscriptionApi mSubscriptionApi;
    private final WebSocketFactory mWebSocketFactory;
    private final Map<String, PublishSubject<Object>> mWebSocketConnections = new HashMap<>();
    private WebSocket mWebSocket = new UnInitializedWebSocket();

    private static class UnInitializedWebSocket extends WebSocket {

        public UnInitializedWebSocket() {
            super(new WebSocketConfig("", "", "", ""));
        }
    }

    @Inject WebSocketClient(SubscriptionApi subscriptionApi, WebSocketFactory factory) {
        mSubscriptionApi = subscriptionApi;
        mWebSocketFactory = factory;
    }

    private Subscription subscribe(PublishSubject<Object> subject, Subscriber<Object> subscriber) {
        return subject.observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    public Subscription subscribe(TransmitterDevice device, Subscriber<Object> subscriber) {
        if (mWebSocketConnections.containsKey(device.id)) {
            return subscribe(mWebSocketConnections.get(device.id), subscriber);
        } else {
            return start(device, subscriber);
        }
    }

    private Subscription start(final TransmitterDevice device, final Subscriber<Object> subscriber) {
        final PublishSubject<Object> subject = PublishSubject.create();

        // if mWebSocket.isSubscribedToAnyone: subscribeToChannel(device.getId(), subject);
        // else: mRelayrSDK.subscribe(...)

        RelayrSdk.getRelayrApi()
                .getAppInfo()
                .flatMap(new Func1<App, Observable<WebSocketConfig>>() {
                    @Override
                    public Observable<WebSocketConfig> call(App app) {
                        return mSubscriptionApi.subscribe(app.id, device.id);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WebSocketConfig>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, error.getMessage());
                        mWebSocketConnections.remove(device.id);
                    }

                    @Override
                    public void onNext(WebSocketConfig webSocketConfig) {
                        initWebSocket(webSocketConfig);
                        subscribeToChannel(webSocketConfig.channel, device.id, subject);
                    }
                });

        mWebSocketConnections.put(device.id, subject);
        return subscribe(subject, subscriber);
    }

    /*public void unSubscribeAll() {
        mWebSocket.unSubscribeAll();
        // mSubscriptionApi.unSubscribeAll();
        Observable
                .from(mWebSocketConnections.keySet())
                .flatMap(new Func1<String, Observable<?>>() {
                    @Override
                    public Observable<?> call(String deviceId) {
                        return mSubscriptionApi.unSubscribe(appId, deviceId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }*/

    public void unSubscribe(final String sensorId) {
        if (mWebSocketConnections.containsKey(sensorId)) {
            mWebSocketConnections.get(sensorId).onCompleted();
            mWebSocketConnections.remove(sensorId);
        }

        // mWebSocket.unSubscribe(sensorId);

        RelayrSdk.getRelayrApi()
                .getAppInfo()
                .flatMap(new Func1<App, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(App app) {
                        return mSubscriptionApi.unSubscribe(app.id, sensorId);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void pubnubConfig) {
                        /* success! */
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable error) {
                    }
                });
    }

    private void initWebSocket(WebSocketConfig config) {
        /* It should just be one instance since the authKey and cipherKey will always be the same */
        mWebSocket = mWebSocketFactory.createWebSocket(config);
    }

    private void subscribeToChannel(String channel, final String deviceId,
                                    final PublishSubject<Object> subject) {
        mWebSocket.subscribe(channel, new WebSocketCallback() {
            @Override
            public void connectCallback(Object message) {

            }

            @Override
            public void disconnectCallback(Object message) {
                subject.onCompleted();
                mWebSocketConnections.remove(deviceId);
            }

            @Override
            public void reconnectCallback(Object message) {

            }

            @Override
            public void successCallback(Object message) {
                subject.onNext(message);
            }

            @Override
            public void errorCallback(Throwable error) {
                subject.onError(error);
                mWebSocketConnections.clear();
            }
        });
    }
}
