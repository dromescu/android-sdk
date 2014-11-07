package io.relayr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import javax.inject.Inject;

import io.relayr.LoginEventListener;
import io.relayr.R;
import io.relayr.RelayrApp;
import io.relayr.RelayrSdk;
import io.relayr.api.ApiModule;
import io.relayr.api.OauthApi;
import io.relayr.api.RelayrApi;
import io.relayr.model.OauthToken;
import io.relayr.model.User;
import io.relayr.storage.DataStorage;
import io.relayr.storage.RelayrProperties;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {

    @Inject OauthApi mOauthApi;
    @Inject RelayrApi mRelayrApi;
    private volatile boolean isObtainingAccessToken;
    private WebView mWebView;
    private View mLoadingView;
    private static final String REDIRECT_URI = "http://localhost";

    @SuppressLint("setJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelayrApp.inject(this);
        View view = View.inflate(this, R.layout.login_layout, null);
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mLoadingView = view.findViewById(R.id.loading_spinner);
        setContentView(view);

        // Initially hide the loading view.
        mLoadingView.setVisibility(View.GONE);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setVerticalScrollBarEnabled(false);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setVisibility(View.VISIBLE);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("Login_Activity", "WebView opening: " + url);
                final String code = getCode(url);
                if (code != null && !isObtainingAccessToken) {
                    Log.d("Relayr_LoginActivity", "onPageStarted code: " + code);
                    isObtainingAccessToken = true;
                    mLoadingView.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    mOauthApi
                            .authoriseUser(code,
                                    RelayrProperties.get().clientId,
                                    RelayrProperties.get().clientSecret,
                                    "authorization_code",
                                    REDIRECT_URI,
                                    "")
                            .flatMap(new Func1<OauthToken, Observable<User>>() {
                                @Override
                                public Observable<User> call(OauthToken token) {
                                    DataStorage.saveUserToken(token.type + " " + token.token);
                                    return mRelayrApi.getUserInfo();
                                }
                            })
                            .map(new Func1<User, User>() {
                                @Override
                                public User call(User user) {
                                    DataStorage.saveUserId(user.id);
                                    return user;
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<User>() {

                                @Override
                                public void onCompleted() {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null) listener.onSuccessUserLogIn();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    finish();
                                    LoginEventListener listener = RelayrSdk.getLoginEventListener();
                                    if (listener != null) listener.onErrorLogin(e);
                                }

                                @Override
                                public void onNext(User user) {

                                }
                            });
                }
            }
        });
        mWebView.loadUrl(getLoginUrl());
    }

    private String getLoginUrl() {
        Uri.Builder uriBuilder = Uri.parse(ApiModule.API_ENDPOINT).buildUpon();
        uriBuilder.path("/oauth2/auth");

        uriBuilder.appendQueryParameter("client_id", RelayrProperties.get().clientId);
        uriBuilder.appendQueryParameter("redirect_uri", REDIRECT_URI);
        uriBuilder.appendQueryParameter("response_type", "code");
        uriBuilder.appendQueryParameter("scope", "access-own-user-info");

        return uriBuilder.build().toString();
    }


    static String getCode(String url) {
        String codeParam = "?code=";
        if (url.contains(REDIRECT_URI) && url.contains(codeParam)) {
            int tokenPosition = url.indexOf(codeParam);
            String code = url.substring(tokenPosition + codeParam.length());
            if (code.contains("&")) code = code.substring(0, code.indexOf("&"));
            Log.d("Login_Activity", "Access code: " + code);
            return code;
        } else {
            return null;
        }
    }

    public static void startActivity(Activity currentActivity) {
        Intent loginActivity = new Intent(currentActivity, LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        currentActivity.startActivity(loginActivity);
    }
}
