package com.example.a41011561p.fotomojon;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SharedViewModel extends AndroidViewModel {
    private final Application app;
    private final MutableLiveData<String> currentAddress = new MutableLiveData<>();
    private final MutableLiveData<String> checkPermission = new MutableLiveData<>();
    private final MutableLiveData<String> buttonText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBar = new MutableLiveData<>();
    private final MutableLiveData<LatLng> currentLatLng = new MutableLiveData<>();

    private boolean mTrackingLocation;
    FusedLocationProviderClient mFusedLocationClient;

    public SharedViewModel(@NonNull Application application) {
        super(application);

        this.app = application;
    }

    void setFusedLocationClient(FusedLocationProviderClient mFusedLocationClient) {
        this.mFusedLocationClient = mFusedLocationClient;
    }
    public MutableLiveData<LatLng> getCurrentLatLng() {
        return currentLatLng;
    }

    LiveData<String> getCurrentAddress() {
        return currentAddress;
    }

    MutableLiveData<String> getButtonText() {
        return buttonText;
    }

    MutableLiveData<Boolean> getProgressBar() {
        return progressBar;
    }

    LiveData<String> getCheckPermission() {
        return checkPermission;
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (mTrackingLocation) {
                new FetchAddressTask(
                        getApplication().getApplicationContext()
                ).execute(
                        locationResult.getLastLocation()
                );
            }
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    void switchTrackingLocation() {
        if (!mTrackingLocation) {
            startTrackingLocation(true);
        } else {
            stopTrackingLocation();
        }

    }

    @SuppressLint("MissingPermission")
    void startTrackingLocation(boolean needsChecking) {
        if (needsChecking) {
            checkPermission.postValue("check");
        } else {
            mFusedLocationClient.requestLocationUpdates(
                    getLocationRequest(),
                    mLocationCallback,
                    null
            );

            currentAddress.postValue("Carregant...");

            progressBar.postValue(true);
            mTrackingLocation = true;
            buttonText.setValue("break;");
        }
    }


    private void stopTrackingLocation() {
        if (mTrackingLocation) {
            mFusedLocationClient.removeLocationUpdates (mLocationCallback);
            mTrackingLocation = false;
            progressBar.postValue(false);
            buttonText.setValue("while(true) {Get Location}");
        }
    }

    private class FetchAddressTask extends AsyncTask<Location, Void, String> {
        private final String TAG = FetchAddressTask.class.getSimpleName();
        private Context mContext;

        FetchAddressTask(Context applicationContext) {
            mContext = applicationContext;
        }

        @Override
        protected String doInBackground(Location... locations) {
            Geocoder geocoder = new Geocoder(mContext,
                    Locale.getDefault());
            Location location = locations[0];

            List<Address> addresses = null;
            String resultMessage = "";

            try {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                currentLatLng.postValue(latlng);

                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // En aquest cas, sols volem una ??nica adre??a:
                        1);

                if (addresses == null || addresses.size() == 0) {
                    if (resultMessage.isEmpty()) {
                        resultMessage = "No s'ha trobat cap adre??a";
                        Log.e(TAG, resultMessage);
                    }
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressParts = new ArrayList<>();

                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressParts.add(address.getAddressLine(i));
                    }

                    resultMessage = TextUtils.join("\n", addressParts);
                }
            } catch (IOException ioException) {
                resultMessage = "Servei no disponible";
                Log.e(TAG, resultMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                resultMessage = "Coordenades no v??lides";
                Log.e(TAG, resultMessage + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), illegalArgumentException);
            }
            return resultMessage;
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            currentAddress.postValue(address);
        }
    }
}