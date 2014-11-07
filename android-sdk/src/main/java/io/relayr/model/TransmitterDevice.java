package io.relayr.model;

/**
 * The transmitter device object holds the same information as the {@link io.relayr.model.Device}
 * The difference is that the model attribute in the former is an ID rather than an object.
 */
public class TransmitterDevice extends Transmitter {

    public final String model;

    public TransmitterDevice(String id, String secret, String owner, String name,
                             String model) {
        super(id, secret, owner, name);
        this.model = model;
    }

    public DeviceModel getModel() {
        return DeviceModel.from(model);
    }

}
