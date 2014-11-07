package io.relayr.api;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.relayr.storage.DataStorage;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

import static io.relayr.BuildConfig.APPLICATION_ID;
import static io.relayr.BuildConfig.VERSION_NAME;

@Module(
        complete = false,
        library = true
)
public class ApiModule {

    public static final String API_ENDPOINT = "https://api.relayr.io";
    private static final String USER_AGENT = APPLICATION_ID + ".sdk.android/" + VERSION_NAME;
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    private final Context app;

    public ApiModule(Context context) {
        app = context;
    }

    @Provides @Singleton Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(API_ENDPOINT);
    }

    @Provides @Singleton Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    private static final RequestInterceptor apiRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", USER_AGENT);
            request.addHeader("Authorization", DataStorage.getUserToken());
            request.addHeader("Content-Type", "application/json; charset=UTF-8");
        }
    };

    private static final RequestInterceptor oauthRequestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("User-Agent", USER_AGENT);
        }
    };

    @Provides @Singleton @Named("api") RestAdapter provideApiRestAdapter(Endpoint endpoint,
                                                                         Client client) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setRequestInterceptor(apiRequestInterceptor)
                .build();
    }

    @Provides @Singleton @Named("oauth") RestAdapter provideOauthRestAdapter(Endpoint endpoint,
                                                                             Client client) {
        return new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint)
                .setRequestInterceptor(oauthRequestInterceptor)
                .build();
    }

    @Provides @Singleton RelayrApi provideRelayrApi(@Named("api") RestAdapter restAdapter) {
        return restAdapter.create(RelayrApi.class);
    }

    @Provides @Singleton OauthApi provideOauthApi(@Named("oauth") RestAdapter restAdapter) {
        return restAdapter.create(OauthApi.class);
    }

    @Provides @Singleton SubscriptionApi provideSubscriptionApi(@Named("api")
                                                                RestAdapter restAdapter) {
        return restAdapter.create(SubscriptionApi.class);
    }

    @Provides @Singleton OkHttpClient provideOkHttpClient() {
        return createOkHttpClient(app);
    }

    private static OkHttpClient createOkHttpClient(Context app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "https");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            Log.e(ApiModule.class.getSimpleName(), "Unable to install disk cache.");
        }

        return client;
    }

}
