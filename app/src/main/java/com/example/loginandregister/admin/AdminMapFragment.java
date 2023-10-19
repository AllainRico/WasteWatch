package com.example.loginandregister.admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.example.loginandregister.adminCollectionRequests.adminCollectionRequestsFragment;
import com.example.loginandregister.garbageBin.GarbageBinStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private FloatingActionButton collectionrequestsfab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_map, container, false);

        initWidgets(view);

        collectionrequestsfab = view.findViewById(R.id.admin_requestCollectionbtn);
        collectionrequestsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAdminRequestsFragmentUI();
            }
        });

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
                SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
                String username = preferences2.getString("adminFragment", "");

                reference = database.getReference("Database");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bar = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                        if ("Looc".equals(bar)) {
                            Double lat = snapshot.child("Barangay").child("Looc").child("Map").child("Latitude").getValue(Double.class);
                            Double longi = snapshot.child("Barangay").child("Looc").child("Map").child("Longitude").getValue(Double.class);

                            if (lat != null && longi != null) {
                                LatLng brgyMap = new LatLng(lat, longi);
                                float zoomLevel = 15.3f;
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));
                                googleMap.getUiSettings().setZoomControlsEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);

                                displayAdminLocation();
                                displayBinLocation();

                                onMapLoaded();
                            }
                        } else if ("Basak".equals(bar)) {
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
                                displayBinLocation();

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

    private void getAdminRequestsFragmentUI() {
        adminCollectionRequestsFragment collectionrequestsfragment = new adminCollectionRequestsFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, collectionrequestsfragment)
                .addToBackStack(null)
                .commit();
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

    public void displayBinLocation() {
        if (googleMap != null) {
            double binLatidue = 10.305827;
            double binLongitude = 123.944845;

            LatLng binLocation = new LatLng(binLatidue, binLongitude);

            BitmapDescriptor binIcon = BitmapDescriptorFactory.fromResource(R.drawable.bin_icon);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(binLocation)
                    .title("Bin Location")
                    .icon(binIcon);

            googleMap.addMarker(markerOptions);
        }
    }

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
