package io.relayr.ble.parser;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AdvertisementPacketParser {

    private static final String TAG = "AdvertisementPacketParser";
    private static final int SERVICES_MORE_AVAILABLE_16_BIT = 0x02;
    private static final int SERVICES_COMPLETE_LIST_16_BIT = 0x03;
    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;

    /**
     * Decodes the services uuid from the advertisement packet since
     * {@link android.bluetooth.BluetoothDevice#getUuids()} returns null most of the times.
     */
    public static List<String> decodeServicesUuid(byte[] data) {
        List<String> serviceUuids = new ArrayList<>();
        if (data != null) {
            int packetLength = data.length;
            for (int index = 0; index < packetLength-1; index++) {
                int fieldLength = data[index];
                int fieldName = data[++index];

                if (fieldName == SERVICES_MORE_AVAILABLE_16_BIT || fieldName == SERVICES_COMPLETE_LIST_16_BIT) {
                    for (int i = index + 1; i < index + fieldLength - 1; i += 2)
                        serviceUuids.add(decodeService16BitUUID(data, i));
                }
                index += fieldLength - 1;
            }
        }
        return serviceUuids;
    }

    /**
     * Decodes the device name from the Complete Local Name or Shortened Local Name field in the
     * Advertisement packet. {@link android.bluetooth.BluetoothDevice#getName()} does it already,
     * although some phones skip it. i.e. Sony Xperia Z1 (C6903) with Android 4.3 where getName()
     * always returns <code>null</code>.
     */
    public static String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength-1; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    private static String decodeLocalName(final byte[] data, final int start, final int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "Unable to encode the complete local name to UTF-8", e);
            return "";
        } catch (final IndexOutOfBoundsException e) {
            Log.e(TAG, "Error when reading complete local name", e);
            return "";
        }
    }

    private static String decodeService16BitUUID(byte[] data, int startPosition) {
        return Integer.toHexString(decodeUuid16(data, startPosition));
    }

    private static int decodeUuid16(byte[] data, int start) {
        int b1 = data[start] & 0xff;
        int b2 = data[start + 1] & 0xff;

        return (b2 << 8 | b1);
    }
}
