package io.relayr.ble;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.relayr.model.DeviceModel;

import static io.relayr.ble.BleDeviceType.*;
import static io.relayr.model.DeviceModel.IR_TRANSMITTER;
import static io.relayr.model.DeviceModel.UNKNOWN;

@RunWith(RobolectricTestRunner.class)
public class BleDeviceTypeTest {

    @Test public void testGetDeviceTypeFromNullDevice() {
        Assert.assertEquals(getDeviceType(null), Unknown);
    }

    @Test public void testGetDeviceTypeFromRandomDevice() {
        Assert.assertEquals(getDeviceType("random text"), Unknown);
    }

    @Test public void testGetDeviceTypeFromNullText() {
        Assert.assertEquals(getDeviceType(null), Unknown);
    }

    @Test public void testGetDeviceTypeFromRandomText() {
        Assert.assertEquals(getDeviceType("random text"), Unknown);
    }

    @Test public void testGetDeviceTypeFromThermometerText() {
        Assert.assertEquals(getDeviceType("WunderbarHTU"), WunderbarHTU);
    }

    @Test public void testGetDeviceTypeFromGyroscopeText() {
        Assert.assertEquals(getDeviceType("WunderbarGYRO"), WunderbarGYRO);
    }

    @Test public void testGetDeviceTypeFromLightText() {
        Assert.assertEquals(getDeviceType("WunderbarLIGHT"), WunderbarLIGHT);
    }

    @Test public void testGetDeviceTypeFromMicrophoneText() {
        Assert.assertEquals(getDeviceType("WunderbarMIC"), WunderbarMIC);
    }

    @Test public void testGetDeviceTypeFromBridgeText() {
        Assert.assertEquals(getDeviceType("WunderbarBRIDG"), WunderbarBRIDG);
    }

    @Test public void testGetDeviceTypeFromInfraredText() {
        Assert.assertEquals(getDeviceType("WunderbarIR"), WunderbarIR);
    }

    @Test public void testGetDeviceTypeFromApplicationText() {
        Assert.assertEquals(getDeviceType("WunderbarApp"), WunderbarApp);
    }

    @Test public void getBleDeviceType_from_DeviceModel() {
        Assert.assertEquals(WunderbarIR, from(IR_TRANSMITTER));
    }

    @Test public void getBleDeviceType_from_DeviceModelUnknown() {
        Assert.assertEquals(Unknown, from(UNKNOWN));
    }

    @Test public void isKnownDevice_shouldBeTrue() {
        Assert.assertTrue(BleDeviceType.isKnownDevice("WunderbarIR"));
    }

    @Test public void isKnownDevice_shouldBeFalse() {
        Assert.assertFalse(isKnownDevice("random device"));
    }

}
