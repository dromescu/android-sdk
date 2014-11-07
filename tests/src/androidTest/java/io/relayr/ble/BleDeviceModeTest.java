package io.relayr.ble;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.relayr.ble.BleDeviceMode.CONNECTED_TO_MASTER_MODULE;
import static io.relayr.ble.BleDeviceMode.DIRECT_CONNECTION;
import static io.relayr.ble.BleDeviceMode.ON_BOARDING;
import static io.relayr.ble.BleDeviceMode.UNKNOWN;
import static io.relayr.ble.BleDeviceMode.containsService;
import static io.relayr.ble.BleDeviceMode.fromServiceUuids;
import static io.relayr.ble.BleDeviceMode.fromUuid;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceModeTest {

    @Test public void fromUuid_shouldReturn_connectedToMasterModule() {
        Assert.assertEquals(CONNECTED_TO_MASTER_MODULE, fromUuid("2000"));
    }

    @Test public void fromUuid_shouldReturn_onBoarding() {
        Assert.assertEquals(ON_BOARDING, fromUuid("2001"));
    }

    @Test public void fromUuid_shouldReturn_directConnection() {
        Assert.assertEquals(DIRECT_CONNECTION, fromUuid("2002"));
    }

    @Test public void fromParcelUuidArrayTest_fromNullValue_shouldReturnUnknown() {
        Assert.assertEquals(UNKNOWN, fromServiceUuids(null));
    }

    @Test public void fromParcelUuidArrayTest_fromEmptyArray_shouldReturnUnknown() {
        Assert.assertEquals(UNKNOWN, fromServiceUuids(new ArrayList<String>()));
    }

    @Test public void fromParcelUuidArrayTest_fromRandomUuidArray_shouldReturnUnknown() {
        List<String> uuids = Arrays.asList("2902");
        Assert.assertEquals(UNKNOWN, fromServiceUuids(uuids));
    }

    @Test public void fromParcelUuidArrayTest_containingOnBoardingService_shouldReturnOnBoarding() {
        List<String> uuids = Arrays.asList("2902", "2001");
        Assert.assertEquals(ON_BOARDING, fromServiceUuids(uuids));
    }

    @Test public void containsService_shouldReturnFalse() {
        Assert.assertFalse(containsService("1"));
    }

    @Test public void containsService_shouldReturnTrue() {
        Assert.assertTrue(containsService("2000"));
    }

}
