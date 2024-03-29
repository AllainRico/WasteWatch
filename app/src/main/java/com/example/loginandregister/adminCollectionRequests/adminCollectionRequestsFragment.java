package com.example.loginandregister.adminCollectionRequests;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.AdminMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class adminCollectionRequestsFragment extends Fragment implements UserDataAdapter.OnItemClickListener, AdminMapFragment.OnMapReadyListener {

    private RecyclerView recyclerView;
    private UserDataAdapter adapter;
    ArrayList<UserDataModel> userDataList = new ArrayList<>();
    private double requestLat;
    private double requestLon;
    private Button backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_admin_collection_requests, container, false);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/Barangay/Looc/Requests/Pending");

        recyclerView = view.findViewById(R.id.requestsRecyclerView);

        initWidgets(view);
        UserDataAdapter.initGeocoder(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDataAdapter(userDataList, new Geocoder(getContext(), Locale.getDefault()));

        adapter.setOnItemClickListener(this);

        recyclerView.setAdapter(adapter);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.getKey();

                    double lat = snapshot.child("lat").getValue(Double.class);
                    double lon = snapshot.child("long").getValue(Double.class);

                    UserDataModel userData = new UserDataModel(username, lat, lon);
                    userDataList.add(userData);
                }

                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        UserDataModel clickedItem = userDataList.get(position);

        AdminMapFragment adminMapFragment = new AdminMapFragment();
        adminMapFragment.setRequestLocations(clickedItem.getLat(), clickedItem.getLon());
        adminMapFragment.requesteeName = clickedItem.getUsername();
        adminMapFragment.setUserLocation(getCollectorLocation());

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, adminMapFragment)
                .addToBackStack(null)
                .commit();
    }
    private LatLng getCollectorLocation() {
        // Retrieve the collector's location from your data source
        // For example, if you have it stored in SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        double collectorLat = preferences.getFloat("collectorLat", 0.0f);
        double collectorLon = preferences.getFloat("collectorLon", 0.0f);

        return new LatLng(collectorLat, collectorLon);
    }
    @Override
    public void onMapReady() {
        AdminMapFragment adminMapFragment = (AdminMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.adminMap);

        if (adminMapFragment != null) {
            adminMapFragment.displayRequesteeLocationOnMap(requestLat, requestLon);
        }
    }

    public void setRequestLocations(double request_lat_value, double request_longi_value) {
        requestLat = request_lat_value;
        requestLon = request_longi_value;
    }
    private void initWidgets(View view){
        backButton = view.findViewById(R.id.backButton);
        recyclerView = view.findViewById(R.id.requestsRecyclerView);
    }
}