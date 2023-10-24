package com.example.loginandregister.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.loginandregister.R;
import com.example.loginandregister.garbageBin.GarbageBinStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


public class AdminMapFragment extends Fragment {
    private Button garbageBinStatusButton;
    private ProgressBar progressBar;
    private ImageView mapPlaceholder;
    private GoogleMap googleMap;
    double adminLatitude = LocationData.getInstance().getAdminLatitude();
    double adminLongitude = LocationData.getInstance().getAdminLongitude();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private final Handler mapUpdateHandler = new Handler();
    private final Handler mapRefreshHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_map, container, false);

        initWidgets(view);

        garbageBinStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GarbageBinStatus garbageBinStatusFragment = new GarbageBinStatus();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, garbageBinStatusFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.adminMap);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                startMapUpdates();
                SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
                String username = preferences2.getString("adminFragment", "");

                reference = database.getReference("Database");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bar = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                        if (bar != null) {
                            Double lat = snapshot.child("Barangay").child(bar).child("Map").child("Latitude").getValue(Double.class);
                            Double longi = snapshot.child("Barangay").child(bar).child("Map").child("Longitude").getValue(Double.class);

                            if (lat != null && longi != null) {
                                LatLng brgyMap = new LatLng(lat, longi);
                                float zoomLevel = 15.3f;
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));
                                googleMap.getUiSettings().setZoomControlsEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);

                                displayAdminLocation();

                                // Fetch and display all the bins
                                displayAllBins(snapshot.child("Barangay").child(bar).child("Bins"));
                                onMapLoaded();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
    public void displayAllBins(DataSnapshot binsSnapshot) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String year = String.valueOf(currentYear); //setYear();
        String month = String.valueOf(currentMonth); //setMonth();
        String day = String.valueOf(currentDay); //setDay();

        for (DataSnapshot binSnapshot : binsSnapshot.getChildren()) {
            Double binLatitude = binSnapshot.child(year).child(month).child(day).child("Latitude").getValue(Double.class);
            Double binLongitude = binSnapshot.child(year).child(month).child(day).child("Longitude").getValue(Double.class);

            if (binLatitude != null && binLongitude != null) {
                displayBinLocation(binLatitude, binLongitude);
            }
        }
    }
    public void displayAdminLocation() {
        Log.d("displayAdminLocation-lat", String.valueOf(adminLatitude));
        if (googleMap != null) {
            LatLng adminLocation = new LatLng(adminLatitude, adminLongitude);

            BitmapDescriptor truckIcon = BitmapDescriptorFactory.fromResource(R.drawable.truck_icon);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(adminLocation)
                    .title("Admin Location")
                    .icon(truckIcon);

            googleMap.addMarker(markerOptions);
        }
    }

    public void displayBinLocation(Double binLatitude, Double binLongitude){
        if (googleMap != null) {
            //dummy bin Latitude, Longitude

            LatLng binLocation = new LatLng(binLatitude, binLongitude);

            BitmapDescriptor binIcon = BitmapDescriptorFactory.fromResource(R.drawable.bin_icon);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(binLocation)
                    .title("Bin Location")
                    .icon(binIcon);

            googleMap.addMarker(markerOptions);
        }
    }

//    public void updateAdminLocation(double latitude, double longitude) {
//        if (googleMap != null) {
//            LatLng adminLocation = new LatLng(latitude, longitude);
//
//            googleMap.clear();
//            googleMap.addMarker(new MarkerOptions().position(adminLocation).title("Admin Location"));
//
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(adminLocation));
//        }
//    }

    private void initWidgets(@NonNull View view) {
        garbageBinStatusButton = view.findViewById(R.id.garbageBinStatusButton);
        progressBar = view.findViewById(R.id.progressBar);
        mapPlaceholder = view.findViewById(R.id.mapPlaceholder);
    }
    public void onMapLoaded() {
        progressBar.setVisibility(View.GONE);
        mapPlaceholder.setVisibility(View.GONE);
    }

    private void refreshMap() {
        googleMap.clear(); // Clear existing markers
        displayAdminLocation(); // Display admin's location marker
     //   displayBinLocation();   // Display bin's location marker
        // Add any other code to update the map here
    }
    private final Runnable mapUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // Code to refresh the map
            refreshMap();
            // Schedule the next update after 2 seconds
            mapUpdateHandler.postDelayed(this, 2000);
        }
    };
    private void startMapUpdates() {
        mapUpdateHandler.postDelayed(mapUpdateRunnable, 2000); // Start updates
    }

    private void stopMapUpdates() {
        mapUpdateHandler.removeCallbacks(mapUpdateRunnable); // Stop updates
    }

    @Override
    public void onStart() {
        super.onStart();
        startMapUpdates(); // Start updates when the fragment is visible
    }

    @Override
    public void onResume() {
        super.onResume();
        mapRefreshHandler.postDelayed(mapRefreshRunnable, 2000); // Start refreshing immediately
    }
    @Override
    public void onPause() {
        super.onPause();
        mapRefreshHandler.removeCallbacks(mapRefreshRunnable); // Stop refreshing
    }
    @Override
    public void onStop() {
        super.onStop();
        stopMapUpdates(); // Stop updates when the fragment is not visible
    }
    private final Runnable mapRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            // Code for refreshing the map goes here

            // Schedule the next refresh after 2 seconds
            mapRefreshHandler.postDelayed(this, 2000); // 2000 milliseconds = 2 seconds
        }
    };

}