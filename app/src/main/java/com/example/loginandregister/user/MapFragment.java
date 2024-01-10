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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.LocationData;
import com.example.loginandregister.garbageBin.GarbageBinStatusModel;
import com.example.loginandregister.requestCollection.userRequestCollectionFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final long RUNNABLE_INTERVAL = 5000;
    private ProgressBar progressBar;
    private ImageView mapPlaceholder;
    private GoogleMap googleMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DatabaseReference adminNameReference;
    DatabaseReference adminLatLongReference;
    private static final int INTERVAL = 2000;

    FloatingActionButton requestCollectionBTN;
    private LocationRequest locationRequest;
    SharedPreferences preferences2;
    String barname;
    String collectorname = "";
    private Marker adminMarker;
    private Handler handler;
    private Runnable periodicTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = database.getReference();
        preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        barname = preferences2.getString("barName", "");


        handler = new Handler();
        periodicTask = new Runnable() {
            @Override
            public void run() {
                // Log "hello" or perform any other background task here
                Log.d("AdminMapFragment", "hello");
//                setCollectorLocation(AdminLocationService.this);
//                getLongValueFromDB();
//                getLatValueFromDB();
                getCollectorLocation();
                handler.postDelayed(this, RUNNABLE_INTERVAL);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initWidgets(view);

        handler.postDelayed(periodicTask, RUNNABLE_INTERVAL);

        requestCollectionBTN = view.findViewById(R.id.requestCollectionbtn);
        requestCollectionBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserCollectionFragmentUI();
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.userMap);
        supportMapFragment.getMapAsync(this);

        return view;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);

        reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double barangayLat = (Double) snapshot.child("Barangay").child(barname).child("Map").child("Latitude").getValue();
                Double barangayLong = (Double) snapshot.child("Barangay").child(barname).child("Map").child("Longitude").getValue();

                if (barangayLat != null && barangayLong != null) {
                    LatLng brgyMap = new LatLng(barangayLat, barangayLong);
                    float zoomLevel = 15.3f;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));
//                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setZoomGesturesEnabled(false);
                    googleMap.getUiSettings().setAllGesturesEnabled(false);

                    displayAllBinsOnMap();

                    onMapLoaded();
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

    private void getUserCollectionFragmentUI() {
        userRequestCollectionFragment requestCollectionFragment = new userRequestCollectionFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, requestCollectionFragment)
                .addToBackStack(null) // This allows the user to navigate back to the previous fragment
                .commit();
    }

    private void getCollectorLocation() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object latValueObj = snapshot.child("collectors").child(barname + "-Collector").child("latitude").getValue();
                Object longValueObj = snapshot.child("collectors").child(barname + "-Collector").child("longitude").getValue();

                Double collectorLatValue = convertToDouble(latValueObj);
                Double collectorLongValue = convertToDouble(longValueObj);

                if (collectorLatValue != null && collectorLongValue != null) {
                    displayAdminLocation(collectorLatValue, collectorLongValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("onCancelled: ", "ERROR ON getCollectorLocation");
            }
        });
    }

    private void moveCameraToLocation(double latitude, double longitude, float zoomLevel) {
        LatLng location = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
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


    public void displayAdminLocation(Double collectorlatvalue, Double collectorlongvalue) {
        double _latvalue, _longvalue;
        _latvalue = collectorlatvalue;
        _longvalue = collectorlongvalue;

        if (googleMap != null) {
            LatLng adminLocation = new LatLng(_latvalue, _longvalue);

            BitmapDescriptor truckIcon = BitmapDescriptorFactory.fromResource(R.drawable.truck_icon);

            if (adminMarker == null) {
                // If the marker is null, it means it's the first time, so add a new marker
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(adminLocation)
                        .title("Collector's Location")
                        .icon(truckIcon);

                adminMarker = googleMap.addMarker(markerOptions);
            } else {
                // If the marker already exists, update its position
                adminMarker.setPosition(adminLocation);
            }

            adminMarker.showInfoWindow();
        }
    }

    private void displayAllBinsOnMap() {
        reference.child("Barangay").child("Looc").child("Bins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GarbageBinStatusModel> bins = new ArrayList<>();

                for (DataSnapshot binSnapshot : dataSnapshot.getChildren()) {
                    GarbageBinStatusModel latestBin = getLatestBin(binSnapshot);

                    if (latestBin != null) {
                        bins.add(latestBin);
                    }
                }

                displayBinsOnMap(bins);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminMapFragment", "Failed to read bin data from Firebase", databaseError.toException());
            }
        });
    }

    private GarbageBinStatusModel getLatestBin(DataSnapshot binSnapshot) {
        GarbageBinStatusModel latestBin = null;
        String binName = binSnapshot.getKey();
        String latestDate = "";

        for (DataSnapshot yearSnapshot : binSnapshot.getChildren()) {
            for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                    if (daySnapshot.hasChild("Latitude") && daySnapshot.hasChild("Longitude")) {
                        String date = yearSnapshot.getKey() + monthSnapshot.getKey() + daySnapshot.getKey();

                        if (date.compareTo(latestDate) > 0) {
                            latestDate = date;
                            int fillLevel = daySnapshot.child("FillLevel").getValue(Integer.class);
                            double latitude = daySnapshot.child("Latitude").getValue(Double.class);
                            double longitude = daySnapshot.child("Longitude").getValue(Double.class);

                            latestBin = new GarbageBinStatusModel();
                            latestBin.setBin(binName);
                            latestBin.setFillLevel(fillLevel);
                            latestBin.setLatitude(latitude);
                            latestBin.setLongitude(longitude);
                        }
                    }
                }
            }
        }

        return latestBin;
    }

    private void displayBinsOnMap(List<GarbageBinStatusModel> bins) {
        if (googleMap != null && bins != null && !bins.isEmpty()) {
            for (GarbageBinStatusModel bin : bins) {
                double binLatitude = bin.getLatitude();
                double binLongitude = bin.getLongitude();
                String binName = bin.getBin();

                LatLng binLocation = new LatLng(binLatitude, binLongitude);

                BitmapDescriptor binIcon = BitmapDescriptorFactory.fromResource(R.drawable.bin_icon);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(binLocation)
                        .title(binName)
                        .icon(binIcon);

                googleMap.addMarker(markerOptions);
            }
        }
    }

//    public void displayBinLocation(double binLatidue, double binLongitude) {
//        if (googleMap != null) {
//
//            LatLng binLocation = new LatLng(binLatidue, binLongitude);
//
//            BitmapDescriptor binIcon = BitmapDescriptorFactory.fromResource(R.drawable.bin_icon);
//
//            MarkerOptions markerOptions = new MarkerOptions()
//                    .position(binLocation)
//                    .title("Bin Location")
//                    .icon(binIcon);
//
//            googleMap.addMarker(markerOptions);
//        }
//    }

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


    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(periodicTask);

        super.onDestroyView();
    }

    private Double convertToDouble(Object value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        return null; // Handle other cases or return a default value as needed
    }
}
