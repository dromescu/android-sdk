package io.relayr.ble.service;

import java.util.UUID;

import io.relayr.ble.BleUtils;

public abstract class TestValues {

    public static final byte[] EXPECTED_SENSOR_ID_AS_BYTE_ARRAY = new byte[] {100, 56, 98, 48, 56,
            51, 55, 55, 45, 98, 99, 102, 99, 45, 52, 57, 98, 100, 45, 57, 97, 50, 102, 45, 50, 99,
            97, 48, 98, 49, 98, 48, 48, 54, 98, 50};
    static final UUID EXPECTED_SENSOR_ID = BleUtils.fromBytes(EXPECTED_SENSOR_ID_AS_BYTE_ARRAY);

}
