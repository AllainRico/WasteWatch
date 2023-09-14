package com.example.loginandregister.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdminMapFragment extends Fragment {

    private Button garbageBinStatusButton;
    private ProgressBar progressBar;
    private ImageView mapPlaceholder;
    private GoogleMap googleMap;
    double adminLatitude = LocationData.getInstance().getAdminLatitude();
    double adminLongitude = LocationData.getInstance().getAdminLongitude();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

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
                        .addToBackStack(null) // This allows the user to navigate back to the previous fragment
                        .commit();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.adminMap);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
                String username = preferences2.getString("adminFragment", "");

                reference = database.getReference("Database");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bar = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                        if ("Looc".equals(bar)) { // Compare strings using .equals()
                            Double lat = snapshot.child("Barangay").child("Looc").child("Map").child("Latitude").getValue(Double.class);
                            Double longi = snapshot.child("Barangay").child("Looc").child("Map").child("Longitude").getValue(Double.class);

                            if (lat != null && longi != null) {
                                LatLng brgyMap = new LatLng(lat, longi);
                                float zoomLevel = 15.3f;
                                //googleMap.addMarker(new MarkerOptions().position(brgyMap).title(bar));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));


                                googleMap.getUiSettings().setZoomControlsEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);

                                displayAdminLocation();

                                onMapLoaded();
                            }
                        }else   if ("Basak".equals(bar)) { // Compare strings using .equals()
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

    public void displayAdminLocation() {
        if (googleMap != null) {

            LatLng adminLocation = new LatLng(adminLatitude, adminLongitude);

            googleMap.addMarker(new MarkerOptions().position(adminLocation).title("Admin Location"));
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

}