package io.relayr.ble;

import io.relayr.model.DeviceModel;

/**
 * A class representing the type of a relayr BLE Device.
 * Currently the available device types are the different type or sensors which are part of the
 * WunderBar: WunderbarHTU, WunderbarGYRO, WunderbarLIGHT, WunderbarMIC, WunderbarBRIDG,
 * WunderbarIR, WunderbarApp, Unknown
 */
public enum BleDeviceType {
	WunderbarHTU,
	WunderbarGYRO,
	WunderbarLIGHT,
	WunderbarMIC,
	WunderbarBRIDG,
	WunderbarIR,
	WunderbarApp,
	Unknown;

    /** Convert the sensor name advertised in ble that into a device type */
	public static BleDeviceType getDeviceType(String deviceName) {
		if (deviceName != null) {
			if (deviceName.equals("WunderbarHTU")) {
				return WunderbarHTU;
			}
			if (deviceName.equals("WunderbarGYRO")) {
				return WunderbarGYRO;
			}
			if (deviceName.equals("WunderbarLIGHT")) {
				return WunderbarLIGHT;
			}
			if (deviceName.equals("WunderbarMIC")) {
				return WunderbarMIC;
			}
			if (deviceName.equals("WunderbarBRIDG")) {
				return WunderbarBRIDG;
			}
			if (deviceName.equals("WunderbarIR")) {
				return WunderbarIR;
			}
			if (deviceName.equals("WunderbarApp")) {
				return WunderbarApp;
			}
		}
		return Unknown;
	}

    public static BleDeviceType from(DeviceModel model) {
        if (model.equals(DeviceModel.TEMPERATURE_HUMIDITY)) return WunderbarHTU;
        if (model.equals(DeviceModel.ACCELEROMETER_GYROSCOPE)) return WunderbarGYRO;
        if (model.equals(DeviceModel.LIGHT_PROX_COLOR)) return WunderbarLIGHT;
        if (model.equals(DeviceModel.MICROPHONE)) return WunderbarMIC;
        if (model.equals(DeviceModel.GROVE)) return WunderbarBRIDG;
        if (model.equals(DeviceModel.IR_TRANSMITTER)) return WunderbarIR;
        return Unknown;
    }

    public static boolean isKnownDevice(String deviceName) {
        return !getDeviceType(deviceName).equals(Unknown);
    }
}
