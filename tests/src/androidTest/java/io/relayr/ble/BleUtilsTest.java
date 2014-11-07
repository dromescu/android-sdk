package io.relayr.ble;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;

import io.relayr.ble.service.TestValues;

import static io.relayr.ble.BleUtils.fromBytes;
import static io.relayr.ble.BleUtils.getShortUUID;

@RunWith(RobolectricTestRunner.class)
public class BleUtilsTest {

    @Test public void fromBytesTest() {
        Assert.assertEquals(fromBytes(TestValues.EXPECTED_SENSOR_ID_AS_BYTE_ARRAY),
                UUID.fromString("64386230-3833-3737-2d62-6366632d3439"));
    }

    @Test public void getShortUuid_fromString_Test() {
        Assert.assertEquals(getShortUUID("00002002-0000-1000-8000-00805f9b34fb"), "2002");
    }

    @Test public void getShortUuid_fromUuid_Test() {
        Assert.assertEquals(getShortUUID(UUID.fromString("00002002-0000-1000-8000-00805f9b34fb")),
                "2002");
    }

}
