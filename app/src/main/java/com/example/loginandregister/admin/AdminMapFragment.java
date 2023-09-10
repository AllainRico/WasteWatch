package com.example.loginandregister.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.loginandregister.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class AdminMapFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView mapPlaceholder;

    private GoogleMap googleMap;
    private double adminLatitude;
    private double adminLongitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_map, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        mapPlaceholder = view.findViewById(R.id.mapPlaceholder);

        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                double latitude, longitude;
                String barangay;

                float zoomLevel = 15.3f;

                barangay = "Looc";
                latitude = 10.305712;
                longitude = 123.941780;

                LatLng brgyMap = new LatLng(latitude, longitude);

                googleMap.addMarker(new MarkerOptions().position(brgyMap).title(barangay));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                displayAdminLocation();

                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                googleMap.getUiSettings().setAllGesturesEnabled(false);

                onMapLoaded();
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // Do nothing when clicked on map, effectively disabling any action
                    }
                });

            }
        });

        return view;
    }
    public void onMapLoaded() {
        progressBar.setVisibility(View.GONE);
        mapPlaceholder.setVisibility(View.GONE);
    }

    public void displayAdminLocation() {
        if (googleMap != null) {
            //Temp
            adminLatitude = 10.305627;
            adminLongitude = 123.946517;

            LatLng adminLocation = new LatLng(adminLatitude, adminLongitude);

            googleMap.addMarker(new MarkerOptions().position(adminLocation).title("Admin Location"));
        }
    }

    public void updateAdminLocation(double latitude, double longitude) {
        if (googleMap != null) {
            LatLng adminLocation = new LatLng(latitude, longitude);

            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(adminLocation).title("Admin Location"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(adminLocation));
        }
    }

}