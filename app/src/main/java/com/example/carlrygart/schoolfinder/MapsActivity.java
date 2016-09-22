package com.example.carlrygart.schoolfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Full screen map activity to show the selected school.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final static String LOG_TAG = "MAPSACTIVITY";

    private GoogleMap mMap;
    private School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Fetch the school object from the model for showing the right text in the marker text.
        Intent intent = getIntent();
        school = SchoolFinder.getSchoolByName(intent.getStringExtra("school_name"));
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enables my location if it's allowed.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Add marker for the school in the map and move the map view there.
        mMap.addMarker(new MarkerOptions().position(school.getLocation()).title(school.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(school.getLocation(), 16));
    }
}
