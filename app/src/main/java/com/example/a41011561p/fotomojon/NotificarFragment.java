package com.example.a41011561p.fotomojon;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificarFragment extends Fragment {

    Button buttonLocation;
    final int REQUEST_LOCATION_PERMISSION = 1;
    private final String TAG = this.getClass().getSimpleName();
    Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;
    TextView mLocationTextView;


    public NotificarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificar, container, false);

        mLocationTextView = view.findViewById(R.id.localitzacio);

        buttonLocation = view.findViewById(R.id.button_location);

        buttonLocation.setOnClickListener(v -> {
            getLocation();
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        return view;
    }



    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    location -> {
                        if (location != null) {
                            mLastLocation = location;
                            mLocationTextView.setText(
                                    getString(R.string.location_text,
                                            mLastLocation.getLatitude(),
                                            mLastLocation.getLongitude(),
                                            mLastLocation.getTime()));
                        } else {
                            mLocationTextView.setText("Sense localització coneguda");
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // Si es concedeix permís, obté la ubicació,
                // d'una altra manera, mostra un Toast

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getContext(),
                            "Permís denegat",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
