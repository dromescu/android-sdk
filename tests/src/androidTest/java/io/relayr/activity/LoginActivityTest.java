package io.relayr.activity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    @Test public void getCode_withNoRedirectUrl_shouldBeNull() {
        Assert.assertEquals(LoginActivity.getCode(""), null);
    }

    @Test public void getCode_withRedirectUrl() {
        String expected = "code";
        Assert.assertEquals(LoginActivity.getCode("http://localhost?code=" + expected), expected);
    }

    @Test public void getCode_withRedirectUrl_fromSocialAuth() {
        String expected = "code";
        String otherParams = expected + "&otherParamether=param";
        Assert.assertEquals(LoginActivity.getCode("http://localhost?code=" + otherParams), expected);
    }

}