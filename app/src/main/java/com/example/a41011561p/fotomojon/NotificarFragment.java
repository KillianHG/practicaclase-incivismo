package com.example.a41011561p.fotomojon;


import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificarFragment extends Fragment {
    ProgressBar mLoading;
    private TextInputEditText txtLatitud;
    private TextInputEditText txtLongitud;
    private TextInputEditText txtDireccio;
    private TextInputEditText txtDescripcio;
    private ImageView Foto;
    private Button buttonFoto;

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private Uri photoURI;
    private ImageView foto;

    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(
                getContext().getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private Button buttonNotificar;
    private SharedViewModel model;



    public NotificarFragment() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(this).load(photoURI).into(foto);
            } else {
                Toast.makeText(getContext(),
                        "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notificar, container, false);

        mLoading = view.findViewById(R.id.loading);

        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        txtLatitud = view.findViewById(R.id.txtLatitud);
        txtLongitud = view.findViewById(R.id.txtLongitud);
        txtDireccio = view.findViewById(R.id.txtDireccio);
        txtDescripcio = view.findViewById(R.id.txtDescripcio);
        buttonNotificar = view.findViewById(R.id.button_notificar);
        Foto = view.findViewById(R.id.foto);
        buttonFoto = view.findViewById(R.id.button_foto);

        model.getCurrentAddress().observe(this, address -> {
            txtDireccio.setText(getString(R.string.address_text,
                    address, System.currentTimeMillis()));
        });
        model.getCurrentLatLng().observe(this, latlng -> {
            txtLatitud.setText(String.valueOf(latlng.latitude));
            txtLongitud.setText(String.valueOf(latlng.longitude));
        });


        model.getProgressBar().observe(this, visible -> {
            if (visible)
                mLoading.setVisibility(ProgressBar.VISIBLE);
            else
                mLoading.setVisibility(ProgressBar.INVISIBLE);
        });

        model.switchTrackingLocation();

        buttonFoto.setOnClickListener(button -> {
            dispatchTakePictureIntent();
        });

        buttonNotificar.setOnClickListener(button -> {
            Incidencia incidencia = new Incidencia();
            incidencia.setDireccio(txtDireccio.getText().toString());
            incidencia.setLatitud(txtLatitud.getText().toString());
            incidencia.setLongitud(txtLongitud.getText().toString());
            incidencia.setProblema(txtDescripcio.getText().toString());

            FirebaseAuth auth = FirebaseAuth.getInstance();
            DatabaseReference base = FirebaseDatabase.getInstance().getReference();

            if (auth.getUid() != null) {
                DatabaseReference users = base.child("users");
                DatabaseReference uid = users.child(auth.getUid());
                DatabaseReference incidencies = uid.child("incidencies");

                DatabaseReference reference = incidencies.push();
                reference.setValue(incidencia);

                Toast.makeText(getContext(), "Avís donat", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
