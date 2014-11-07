package io.relayr.api;

import java.util.List;

import io.relayr.model.App;
import io.relayr.model.Command;
import io.relayr.model.CreateWunderBar;
import io.relayr.model.Device;
import io.relayr.model.Transmitter;
import io.relayr.model.TransmitterDevice;
import io.relayr.model.User;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;

/** This class incorporates a wrapped version of the relayr API calls. */
public interface RelayrApi {

    /** @return an {@link rx.Observable} of a list of devices registered under a user. */
    @GET("/users/{userId}/devices")
    Observable<List<Device>> getUserDevices(@Path("userId") String userId);

    /** @return an {@link rx.Observable} to the information about the app initiating the request. */
    @GET("/oauth2/app-info")
    Observable<App> getAppInfo();

    /** @return an {@link rx.Observable} information about the user initiating the request. */
    @GET("/oauth2/user-info")
    Observable<User> getUserInfo();

    @POST("/devices/{device_id}/cmd/{command_name}")
    Observable<Void> sendCommand(@Path("device_id") String deviceId,
                                 @Path("command_name") String commandName,
                                 @Body Command command);

    /** Api call to tell the backend to create WunderBar.
     * @return an {@link rx.Observable} to a WunderBar that contains the IDs and Secrets of the
     * Master Module and Sensor Modules. */
    @POST("/users/{userId}/wunderbar")
    Observable<CreateWunderBar> createWunderBar(@Path("userId") String userId);

    /** @return an {@link rx.Observable} with a list all Transmitters listed under a user. */
    @GET("/users/{userId}/transmitters")
    Observable<List<Transmitter>> getTransmitters(@Path("userId") String userId);

    /** @return an {@link rx.Observable} of a specific transmitter */
    @GET("/transmitters/{transmitter}")
    Observable<Transmitter> getTransmitter(@Path("transmitter") String transmitter);

    /** Updates a transmitter.
     * @param transmitter updated transmitter with the new details
     * @param transmitterId id of the transmitter to update
     * @return an {@link rx.Observable} to the updated Transmitter */
    @PATCH("/transmitters/{transmitter}")
    Observable<Transmitter> updateTransmitter(@Body Transmitter transmitter,
                                              @Path("transmitter") String transmitterId);

    /**
     * @param transmitter the id of the transmitter to get the devices from
     * @return an {@link rx.Observable} with a list of devices that belong to the specific
     * transmitter. */
    @GET("/transmitters/{transmitter}/devices")
    Observable<List<TransmitterDevice>> getTransmitterDevices(
            @Path("transmitter") String transmitter);

}
