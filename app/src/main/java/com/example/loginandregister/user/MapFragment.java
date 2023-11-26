package com.example.loginandregister.user;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.LocationData;
import com.example.loginandregister.requestCollection.userRequestCollectionFragment;
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

import java.util.Calendar;

public class MapFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView mapPlaceholder;
    private GoogleMap googleMap;
    double adminLatitude = LocationData.getInstance().getAdminLatitude();
    double adminLongitude = LocationData.getInstance().getAdminLongitude();
    //dummy bin Latitude, Longitude
    double binLatidue = 10.305827;
    double binLongitude = 123.944845;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DatabaseReference adminNameReference;
    DatabaseReference adminLatLongReference;
    private Handler handler = new Handler();
    private static final int INTERVAL = 2000;

    FloatingActionButton requestCollectionBTN;
    private LocationRequest locationRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initWidgets(view);

        requestCollectionBTN = view.findViewById(R.id.requestCollectionbtn);
        requestCollectionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserCollectionFragmentUI();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.userMap);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                String username = preferences2.getString("ProfileUsername", "");

                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                String year = String.valueOf(currentYear); //setYear();
                String month = String.valueOf(currentMonth); //setMonth();
                String day = String.valueOf(currentDay); //setDay();

                reference = database.getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bar = snapshot.child("users").child(username).child("barName").getValue(String.class);
                        if ("Looc".equals(bar)) { // Compare strings using .equals()
                            Double lat = snapshot.child("Barangay").child(bar).child("Map").child("Latitude").getValue(Double.class);
                            Double longi = snapshot.child("Barangay").child(bar).child("Map").child("Longitude").getValue(Double.class);
                            Double binLat = snapshot.child("Barangay").child(bar).child("Bins").child("bin1").child(year).child(month).child(day).child("Latitude").getValue(Double.class);
                            Double binLong = snapshot.child("Barangay").child(bar).child("Bins").child("bin1").child(year).child(month).child(day).child("Longitude").getValue(Double.class);

                            if (lat != null && longi != null) {
                                LatLng brgyMap = new LatLng(lat, longi);
                                float zoomLevel = 15.3f;
                                //googleMap.addMarker(new MarkerOptions().position(brgyMap).title(bar));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                                googleMap.getUiSettings().setZoomControlsEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);
                                onMapLoaded();

                                Log.d("DBLatitude: ", String.valueOf(lat));
                                Log.d("DBLongitude: ", String.valueOf(longi));

                                realtimeLocation();

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Call your method to retrieve data here
                                        realtimeLocation();
                                        handler.postDelayed(this, INTERVAL);
                                    }
                                }, INTERVAL);

//                                displayAdminLocation(adminLatitude, adminLongitude);
                                displayBinLocation(binLatidue,binLongitude);



                            }
                        } else if ("Basak".equals(bar)) { // Compare strings using .equals()
                            Double lat = snapshot.child("Barangay").child(bar).child("Map").child("Latitude").getValue(Double.class);
                            Double longi = snapshot.child("Barangay").child(bar).child("Map").child("Longitude").getValue(Double.class);

                            if (lat != null && longi != null) {
                                LatLng brgyMap = new LatLng(lat, longi);
                                float zoomLevel = 15.3f;
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                                googleMap.getUiSettings().setZoomControlsEnabled(false);
                                googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                googleMap.getUiSettings().setAllGesturesEnabled(false);

//                                displayAdminLocation(lat, longi);
//                                displayBinLocation(binLatidue,binLongitude);

                                onMapLoaded();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Call your method to retrieve data here
                                        realtimeLocation();
                                        handler.postDelayed(this, INTERVAL);
                                    }
                                }, INTERVAL);
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

    private void getUserCollectionFragmentUI() {
        userRequestCollectionFragment requestCollectionFragment = new userRequestCollectionFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, requestCollectionFragment)
                .addToBackStack(null) // This allows the user to navigate back to the previous fragment
                .commit();
    }

    public void realtimeLocation(){
        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername", "");

        adminNameReference = database.getReference();

        adminNameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bar = snapshot.child("users").child(username).child("barName").getValue(String.class);
                if ("Looc".equals(bar))
                {
                    String latlongpath = "/collectors/admin";
                    adminLatLongReference = database.getReference(latlongpath);
                    adminLatLongReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Object adminlat = snapshot.child("latitude").getValue();
                            Object adminlong = snapshot.child("longitude").getValue();

                            displayAdminLocation((Double) adminlat, (Double) adminlong);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if ("Basak".equals(bar))
                {
                    String latlongpath = "/collectors/basakAdmin";
                    adminLatLongReference = database.getReference(latlongpath);
                    adminLatLongReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Object adminlat = snapshot.child("latitude").getValue();
                            Object adminlong = snapshot.child("longitude").getValue();

                            displayAdminLocation((Double) adminlat, (Double) adminlong);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void displayAdminLocation(double adminLatitude, double adminLongitude) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
            });
        }
    }

    public void displayBinLocation(double binLatidue, double binLongitude) {
        if (googleMap != null) {

            LatLng binLocation = new LatLng(binLatidue, binLongitude);

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

    private void initWidgets(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        mapPlaceholder = view.findViewById(R.id.mapPlaceholder);
    }

    public void onMapLoaded() {
        progressBar.setVisibility(View.GONE);
        mapPlaceholder.setVisibility(View.GONE);
    }
}
