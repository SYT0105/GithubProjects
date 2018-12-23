package com.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.providers.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.google.android.gms.common.ConnectionResult.SERVICE_MISSING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_UPDATING;
import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class LocationActivity extends AppCompatActivity implements LocationListener {


    private static final int REQUEST_CODE_PERMISSION_LOCATION = 101;
    private static final int REQUEST_CODE_GOOGLE_SERVICES = 102;
    private static final int REQUEST_CODE_CHECK_SETTINGS_LOCATION = 103;
    private TextView locationText;
    private AddressResultReceiver addressResultReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        init();
        checkGoogleServices();
    }

    private void init() {
        locationText = findViewById(R.id.text_view_location_infor);
        addressResultReceiver = new AddressResultReceiver(new Handler());
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    }


    private void checkGoogleServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        switch (googleApiAvailability.isGooglePlayServicesAvailable(this)) {

            case SERVICE_MISSING:
                break;
            case SUCCESS:
                checkLocationStatus();
                break;
            case SERVICE_UPDATING:
                break;
            case SERVICE_VERSION_UPDATE_REQUIRED:
                GoogleApiAvailability.getInstance().getErrorDialog(this
                        , googleApiAvailability.isGooglePlayServicesAvailable(this), REQUEST_CODE_GOOGLE_SERVICES);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_GOOGLE_SERVICES:
                if (resultCode == RESULT_OK) {
                    checkGoogleServices();
                }
                break;

            case REQUEST_CODE_CHECK_SETTINGS_LOCATION:
                if (resultCode == RESULT_OK) {
                    checkLocationStatus();
                }
                break;
        }


    }

//    private void requestLocationUsingAndroidFramework() {
//        try {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
//                }
//            } else {
//
//                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                locationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER,
//                        1000,
//                        1,
//                        this);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void checkLocationStatus() {
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
                }
                return;
            }

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(getDefaultLocationRequest());
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);

            final Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build());

            locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(LocationActivity.this,
                                    REQUEST_CODE_CHECK_SETTINGS_LOCATION);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });

            locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                    LocationSettingsStates locationSettingsStates = locationSettingsResponse.getLocationSettingsStates();
                    /*if (!locationSettingsStates.isLocationPresent()
                            || !locationSettingsStates.isLocationUsable()
                            || !locationSettingsStates.isGpsPresent()
                            || !locationSettingsStates.isGpsUsable()
                            || !locationSettingsStates.isNetworkLocationPresent()
                            || !locationSettingsStates.isNetworkLocationUsable()) {

                    } else {
                        requestLocationUsingGoogleServices();
                    }*/
                    requestLocationUsingGoogleServices();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocationRequest getDefaultLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        return locationRequest;
    }

    private void requestLocationUsingGoogleServices() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION);
                }
                return;
            }
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(getDefaultLocationRequest(),
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            updateLocationInfoUI(locationResult.getLastLocation());
                        }
                    }, Looper.myLooper());
            fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    Log.d("available ", "available");
                    updateLocationInfoUI(location);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        updateLocationInfoUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        try {

            if (status == LocationProvider.AVAILABLE) {
                Toast.makeText(this, provider + " available", Toast.LENGTH_SHORT).show();
            } else if (status == LocationProvider.OUT_OF_SERVICE) {
                Toast.makeText(this, provider + " out of service", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, provider + " temporarily un-available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

        try {
            switch (provider) {
                case LocationManager.GPS_PROVIDER:
                    Toast.makeText(this, provider + " enabled", Toast.LENGTH_SHORT).show();
                    break;
                case LocationManager.NETWORK_PROVIDER:
                    Toast.makeText(this, provider + " enabled", Toast.LENGTH_SHORT).show();
                    break;
                case LocationManager.PASSIVE_PROVIDER:
                    Toast.makeText(this, provider + " enabled", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

        try {
            switch (provider) {
                case LocationManager.GPS_PROVIDER:
                    Toast.makeText(this, provider + " disabled", Toast.LENGTH_SHORT).show();
                    break;
                case LocationManager.NETWORK_PROVIDER:
                    Toast.makeText(this, provider + " disabled", Toast.LENGTH_SHORT).show();
                    break;
                case LocationManager.PASSIVE_PROVIDER:
                    Toast.makeText(this, provider + " disabled", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Permisson Not Granted", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkLocationStatus();
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationInfoUI(Location location) {
        try {
            if (location != null) {

                //This below service is for getting the address from location
                Intent intent =
                        new Intent(LocationActivity.this, FetchAddressIntentService.class);
                intent.putExtra("location", location);
                intent.putExtra("rr", addressResultReceiver);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Toast.makeText(getApplicationContext(), "Result Received", Toast.LENGTH_SHORT).show();
            locationText.setText(resultData.toString());
        }
    }
}