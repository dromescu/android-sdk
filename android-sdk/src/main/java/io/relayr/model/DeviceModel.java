package io.relayr.model;

/** Holds information about the ids of the available devices in the relayr platform */
public enum DeviceModel {

    LIGHT_PROX_COLOR("a7ec1b21-8582-4304-b1cf-15a1fc66d1e8"),
    ACCELEROMETER_GYROSCOPE("173c44b5-334e-493f-8eb8-82c8cc65d29f"),
    GROVE("ebd828dd-250c-4baf-807d-69d85bed065b"),
    TEMPERATURE_HUMIDITY("ecf6cf94-cb07-43ac-a85e-dccf26b48c86"),
    IR_TRANSMITTER("bab45b9c-1c44-4e71-8e98-a321c658df47"),
    MICROPHONE("4f38b6c6-a8e9-4f93-91cd-2ac4064b7b5a"),
    UNKNOWN("-1");

    private final String id;

    DeviceModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static DeviceModel from(TransmitterDevice device) {
        return from(device.model);
    }

    public static DeviceModel from(String id) {
        return LIGHT_PROX_COLOR.id.equals(id) ? LIGHT_PROX_COLOR:
               ACCELEROMETER_GYROSCOPE.id.equals(id) ? ACCELEROMETER_GYROSCOPE:
               GROVE.id.equals(id) ? GROVE:
               TEMPERATURE_HUMIDITY.id.equals(id) ? TEMPERATURE_HUMIDITY:
               IR_TRANSMITTER.id.equals(id) ? IR_TRANSMITTER:
               MICROPHONE.id.equals(id) ? MICROPHONE:
               UNKNOWN;
    }
}