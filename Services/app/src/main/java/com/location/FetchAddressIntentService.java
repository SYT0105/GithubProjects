package com.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    public FetchAddressIntentService() {
        super(FetchAddressIntentService.class.getName());
    }

    private ResultReceiver addressResultReceiver;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        if (intent != null) {
            Location location = intent.getParcelableExtra("location");
            addressResultReceiver = intent.getParcelableExtra("rr");

            List<Address> addresses = null;
            String errorMessage = null;

            try {
                addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = "service not available";
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = "invalid lat long Latitude = " + location.getLatitude() +
                        ", Longitude = " + location.getLongitude();
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "No address found";
                }
                deliverResultToReceiver(0, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                deliverResultToReceiver(1,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments));
            }
        }
    }

    private void deliverResultToReceiver(int result, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putString("key", errorMessage);
        addressResultReceiver.send(result, bundle);
    }
}
