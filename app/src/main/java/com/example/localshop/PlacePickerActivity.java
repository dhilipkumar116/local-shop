package com.example.localshop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.localshop.Prevalent.productType;
import com.example.localshop.shopActivity.HomeActivityS;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;

public class PlacePickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayout linearLayout;
    private Button set_location;
    private int PERMISSION_ID = 44;
    private FusedLocationProviderClient mfusedLocation;
    private Location mLastLocation;
    private Double lat , lang ;
    private String shopname , totalprice;
    private ProgressDialog progressDialog;
    private Double shopLat, shopLong ,distance,Pricein20km,Priceoout20km;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);


        linearLayout = findViewById(R.id.place_picker_layout);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        linearLayout.setAnimation(animation);


        shopname = getIntent().getStringExtra("shopname");

        set_location = (Button) findViewById(R.id.pick_set_location_btn);
        mfusedLocation = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkItUserOrAdmin();

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition =mMap.getCameraPosition();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                lat = cameraPosition.target.latitude;
                lang = cameraPosition.target.longitude;

            }
        });

        if(checkPermissions()){
            if(isLocationEnabled()){
                enableUserLocation();
                zoomToUserLocation();
            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        }else {
            requestPermissions();
        }

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void zoomToUserLocation() {
        Task<Location> locationTask = mfusedLocation.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                }else {
                    zoomToUserLocation();
                }

            }
        });
    }

    private void checkItUserOrAdmin() {
            if(lang.equals(0.0) && lat.equals(0.0)){
                Toast.makeText(this, "choose location", Toast.LENGTH_LONG).show();
            }else{
                adminAction();
            }
    }

    private void adminAction() {
        DatabaseReference adminlocationref = FirebaseDatabase.getInstance()
                .getReference().child("Admins").child(shopname);
        adminlocationref.child("latitude").setValue(lat);
        adminlocationref.child("longtitude").setValue(lang)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(PlacePickerActivity.this, 
                                    HomeActivityS.class);
                            intent.putExtra("code","");
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(PlacePickerActivity.this, (CharSequence)
                                    task.getException(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void enableUserLocation() {
        mMap.setMyLocationEnabled(true);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    enableUserLocation();
                    zoomToUserLocation();
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }

            } else {
                requestPermissions();
            }
        }
    }


}
