package io.relayr;

import android.app.Activity;
import android.content.Context;

import javax.inject.Inject;

import io.relayr.activity.LoginActivity;
import io.relayr.api.RelayrApi;
import io.relayr.ble.BleUtils;
import io.relayr.ble.RelayrBleSdk;
import io.relayr.storage.DataStorage;
import io.relayr.websocket.WebSocketClient;

/**
 * The RelayrSdk Class serves as the access point to all endpoints in the Android SDK.
 * It includes basic calls such as user login validation and can also call the handlers of the
 * other classes- {@link io.relayr.api.RelayrApi}, {@link io.relayr.ble.RelayrBleSdk} and
 * {@link io.relayr.websocket.WebSocketClient}.
 */
public class RelayrSdk {

    @Inject static RelayrApi mRelayrApi;
    @Inject static WebSocketClient mWebSocketClient;
    @Inject static BleUtils mBleUtils;
    @Inject static RelayrBleSdk mRelayrBleSdk;

    private static LoginEventListener loginEventListener;

    /**
     * Initializes the SDK. Should be called when the {@link android.app.Application} is
     * created.
     */
    public static void init(Context context) {
        RelayrApp.init(context, false);
    }

    /**
     * Initializes the SDK in Mock Mode.
     * In this mode, mock reading values are generated.
     * Used for testing purposes, without the need of a WunderBar or an internet connection.
     * Should be called when the *{@link android.app.Application}* is created.
     */
    public static void initInMockMode(Context context) {
        RelayrApp.init(context, true);
    }

    /**
     * Resets the SDK throwing away the graph holding all the dependencies. Make sure to
     * call {@link #init(android.content.Context)} before trying to do anything else.
     */
    public static void reset() {
        RelayrApp.reset();
    }

    /**
     * Returns the version of the SDK
     * @return the version String
     */
	public static String getVersion() {
        return BuildConfig.VERSION_NAME;
	}

    /** @return the handler of the Relayr API.
     * Used as an access point to class {@link RelayrApi} */
    public static RelayrApi getRelayrApi() {
        return mRelayrApi;
    }

    /** Launches the login activity. Enables the user to log in to the relayr platform. */
	public static void logIn(Activity currentActivity, LoginEventListener listener) {
        loginEventListener = listener;
        LoginActivity.startActivity(currentActivity);
	}

    /**
     * Checks whether or not a user is logged in to the relayr platform.
     * @return true if the user is logged in, false otherwise.
     */
	public static boolean isUserLoggedIn() {
		return DataStorage.isUserLoggedIn();
	}

    /** Logs the user out of the relayr platform. */
	public static void logOut() {
		DataStorage.logOut();
	}

    /**
     * Used as an access point to the class {@link WebSocketClient}
     * @return the handler of the WebSocket client
     */
    public static WebSocketClient getWebSocketClient() {
        return mWebSocketClient;
    }

    /**
     * Provides the relayr sdk with a BLE implementation or an empty implementation, in case
     * bluetooth is not available on the device.
     * An empty implementation is one in which the methods do not function
     * This call should be preceded by {@link io.relayr.RelayrSdk#isBleSupported}
     * to check whether BLE is supported
     * and by {@link io.relayr.RelayrSdk#isBleAvailable} to check whether BLE is activated
     * @return the handler of the Relayr BLE SDK
     */
     public static RelayrBleSdk getRelayrBleSdk() {
        return mRelayrBleSdk;
    }

    /**
     * Checks whether or not Bluetooth is supported.
     * Should be called before the RelayrBleSdk handler {@link #getRelayrBleSdk}
     * @return true if Bluetooth is supported, false otherwise.
     */
    public static boolean isBleSupported() {
        return mBleUtils.isBleSupported();
    }

    /**
     * Checks whether Bluetooth is turned on or not.
     * @return true if Bluetooth is turned on, false otherwise.
     * Should be called before calling the RelayrBleSdk handler {@link #getRelayrBleSdk}.
     * The user can be prompted to activate their Bluetooth using
     * {@link #promptUserToActivateBluetooth}
     */
    public static boolean isBleAvailable() {
        return mBleUtils.isBleAvailable();
    }

    /**
     * Prompts the user to activate Bluetooth.
     * The method will not perform any action in case Bluetooth is not supported,
     * i.e. if {@link #isBleSupported()} returns true.
     * @param activity an instance of {@link android.app.Activity}
     */
    public static void promptUserToActivateBluetooth(Activity activity) {
        if (isBleSupported()) mBleUtils.promptUserToActivateBluetooth(activity);
    }

    /**
     * Listener indicating a 'login' event
     * @return the listener or null if doesn't exist
     */
	public static LoginEventListener getLoginEventListener() {
		return loginEventListener;
	}
}
