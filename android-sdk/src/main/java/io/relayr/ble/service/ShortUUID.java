package io.relayr.ble.service;

public interface ShortUUID {

    // General services & characteristics
    static String SERVICE_BATTERY_LEVEL = "180f";
    static String CHARACTERISTIC_BATTERY_LEVEL = "2a19";

    static String SERVICE_DEVICE_INFO = "180a";
    static String CHARACTERISTIC_FIRMWARE_VERSION = "2a26";
    static String CHARACTERISTIC_HARDWARE_VERSION = "2a27";
    static String CHARACTERISTIC_MANUFACTURER = "2a29";

    // Relayr related services
    static String SERVICE_CONNECTED_TO_MASTER_MODULE = "2000";

    static String SERVICE_ON_BOARDING = "2001";
    static String CHARACTERISTIC_SENSOR_ID = "2010";
    static String CHARACTERISTIC_PASS_KEY = "2018";
    static String CHARACTERISTIC_ON_BOARDING_FLAG = "2019";

    static String SERVICE_DIRECT_CONNECTION = "2002";
    //static String CHARACTERISTIC_SENSOR_BEACON_FREQUENCY = "2011";
    static String CHARACTERISTIC_SENSOR_FREQUENCY = "2012";
    static String CHARACTERISTIC_SENSOR_LED_STATE = "2013";
    static String CHARACTERISTIC_SENSOR_THRESHOLD = "2014";
    static String CHARACTERISTIC_SENSOR_CONFIGURATION = "2015";
    static String CHARACTERISTIC_SENSOR_DATA = "2016";

    static String DESCRIPTOR_DATA_NOTIFICATIONS = "2902";

}
