// AdminMapFragment.java

package com.example.loginandregister.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.example.loginandregister.adminCollectionRequests.adminCollectionRequestsFragment;
import com.example.loginandregister.garbageBin.GarbageBinStatus;
import com.example.loginandregister.garbageBin.GarbageBinStatusModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import java.util.List;

public class AdminMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final long RUNNABLE_INTERVAL = 5000;
    private FloatingActionButton fabOptionMenu;
    private ProgressBar progressBar;
    private ImageView mapPlaceholder;
    private GoogleMap googleMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    double requestLat;
    double requestLon;
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static String adminusername;
    public static String requesteeName;
    private Handler handler;
    private Runnable periodicTask;
    private Marker adminMarker;
    SharedPreferences preferences2;
    String username;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reference = database.getReference();

        preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        username = preferences2.getString("adminFragment", "");

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
        View view = inflater.inflate(R.layout.fragment_admin_map, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        initWidgets(view);

        handler.postDelayed(periodicTask, RUNNABLE_INTERVAL);

        fabOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFabOptionsMenu(view);
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.adminMap);
        supportMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnInfoWindowClickListener(this);

        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment", "");

        reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double collectorLatValue = (Double) snapshot.child("collectors").child(username).child("latitude").getValue();
                Double collectorLongValue = (Double) snapshot.child("collectors").child(username).child("longitude").getValue();

                    if (collectorLatValue != null && collectorLongValue != null) {
                        LatLng brgyMap = new LatLng(collectorLatValue, collectorLongValue);
                        float zoomLevel = 15.3f;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));
//                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.getUiSettings().setZoomGesturesEnabled(true);
                        googleMap.getUiSettings().setAllGesturesEnabled(true);

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

        if (requestLat != 0 && requestLon != 0) {
            displayRequesteeLocationOnMap(requestLat, requestLon);
        }
    }

    private void showFabOptionsMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.fab_options_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_garbage_bin) {
                    openGarbageBinStatusFragment();
                    return true;
                } else if (itemId == R.id.action_collection_requests) {
                    openCollectionRequestsFragment();
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private void openGarbageBinStatusFragment() {
        GarbageBinStatus garbageBinStatusFragment = new GarbageBinStatus();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, garbageBinStatusFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openCollectionRequestsFragment() {
        adminCollectionRequestsFragment collectionrequestsfragment = new adminCollectionRequestsFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, collectionrequestsfragment)
                .addToBackStack(null)
                .commit();
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
                for (DataSnapshot yearSnapshot : binSnapshot.getChildren()) {
                    for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                        for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                            if (daySnapshot.hasChild("Latitude") && daySnapshot.hasChild("Longitude")) {
                                double latitude = daySnapshot.child("Latitude").getValue(Double.class);
                                double longitude = daySnapshot.child("Longitude").getValue(Double.class);

                                GarbageBinStatusModel bin = new GarbageBinStatusModel();
                                bin.setLatitude(latitude);
                                bin.setLongitude(longitude);

                                bins.add(bin);
                            }
                        }
                    }
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


    private void initWidgets(@NonNull View view) {
        fabOptionMenu = view.findViewById(R.id.fabOptionMenu);
        progressBar = view.findViewById(R.id.progressBar);
        mapPlaceholder = view.findViewById(R.id.mapPlaceholder);
    }

    public void onMapLoaded() {
        progressBar.setVisibility(View.GONE);
        mapPlaceholder.setVisibility(View.GONE);
    }

    public void setRequestLocations(double request_lat_value, double request_longi_value) {
        requestLat = request_lat_value;
        requestLon = request_longi_value;
    }

    public void displayRequesteeLocationOnMap(double _lat, double _long) {
        if (googleMap != null) {
            LatLng requesteeLocation = new LatLng(_lat, _long);

            BitmapDescriptor binIcon = BitmapDescriptorFactory.fromResource(R.drawable.request_icon);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(requesteeLocation)
                    .title("Request")
                    .icon(binIcon);

            googleMap.addMarker(markerOptions);

            // Move the camera to center on the requestee's location
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(requesteeLocation));
        }
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        if(marker.getTitle() == "Current Location"){
            marker.hideInfoWindow();

        }//if marker kay dili request
        else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Remove this request from " + requesteeName + "?")
                    .setCancelable(true)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialogInterface, @SuppressWarnings("unused") final int id) {
                            deleteThisRequest(requesteeName);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, @SuppressWarnings("unused") final int id) {
                            dialogInterface.cancel();
                        }
                    });
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void deleteThisRequest(String _username) {
        String path = "/Barangay/Looc/Requests/Pending";

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(path);
        reference.child(_username).removeValue();
    }

    public interface OnMapReadyListener {
        void onMapReady();
    }

    public void getLatValueFromDB(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double collectorLatValue = (Double) snapshot.child("collectors").child(username).child("latitude").getValue();
                setLatvalue(collectorLatValue);
                Log.d("Collectorvalue", collectorLatValue.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("onCancelled: ", "ERROR ON getLatValueFromDB");
            }
        });

    }
    public double setLatvalue(double latvalue){
        double _latvalue;
        _latvalue = latvalue;
        return _latvalue;
    }

    public void getLongValueFromDB(){
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double collectorLongValue = (Double) snapshot.child("collectors").child(username).child("longitude").getValue();
                setLongvalue(collectorLongValue);
                Log.d("Collectorvalue", collectorLongValue.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public double setLongvalue(double longvalue){
        double _longvalue;
        _longvalue = longvalue;
        return _longvalue;
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(periodicTask);

        super.onDestroyView();
    }

    private void getCollectorLocation() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double collectorLatValue = (Double) snapshot.child("collectors").child(username).child("latitude").getValue();
                Double collectorLongValue = (Double) snapshot.child("collectors").child(username).child("longitude").getValue();

                if (collectorLatValue != null && collectorLongValue != null) {
                    setLatvalue(collectorLatValue);
                    setLongvalue(collectorLongValue);

                    // Update the marker on the map
                    displayAdminLocation(collectorLatValue, collectorLongValue);
                    moveCameraToLocation(collectorLatValue, collectorLongValue, googleMap.getCameraPosition().zoom);
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

}
