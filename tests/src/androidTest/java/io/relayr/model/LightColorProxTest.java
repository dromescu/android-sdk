package io.relayr.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LightColorProxTest {

    @Test public void toRgbTest() {
        LightColorProx.Color color = new LightColorProx.Color();
        color.r = 1000;
        color.g = 300;
        color.b = 200;
        Assert.assertEquals(-36276, color.toRgb());
    }

}
