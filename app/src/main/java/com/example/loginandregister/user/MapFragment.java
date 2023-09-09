        package com.example.loginandregister.user;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;

        import android.provider.ContactsContract;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.ProgressBar;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;

        import com.example.loginandregister.R;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.ktx.Firebase;


        public class MapFragment extends Fragment{

            private ProgressBar progressBar;
            private ImageView mapPlaceholder;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_map, container, false);

                progressBar = view.findViewById(R.id.progressBar);
                mapPlaceholder = view.findViewById(R.id.mapPlaceholder);

                SupportMapFragment supportMapFragment=(SupportMapFragment)
                        getChildFragmentManager().findFragmentById(R.id.map);

                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        double latitude, longitude;
                        String barangay;

                        barangay = "Looc";
                        latitude = 10.305712;
                        longitude = 123.941780;
                        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                        String username = preferences2.getString("ProfileUsername","");



                        LatLng brgyMap = new LatLng(latitude, longitude);

                        googleMap.addMarker(new MarkerOptions().position(brgyMap).title(barangay));

                        float zoomLevel = 15.3f;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                        googleMap.getUiSettings().setAllGesturesEnabled(false);

                        reference = database.getReference("Database").child("users").child(username);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String bar = snapshot.child("barName").getValue(String.class);
                                if(bar == "Looc"){
                                    saveLocationToFirebaseLooc(latitude, longitude);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        onMapLoaded();
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
//                                // When clicked on map
//                                // Initialize marker options
//                                MarkerOptions markerOptions=new MarkerOptions();
//                                // Set position of marker
//                                markerOptions.position(latLng);
//                                // Set title of marker
//                                markerOptions.title(latLng.latitude+" : "+latLng.longitude);
//                                // Remove all marker
//                                googleMap.clear();
//                                // Animating to zoom the marker
//                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
//                                // Add marker on map
//                                googleMap.addMarker(markerOptions);
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
            private void saveLocationToFirebaseLooc(double latitude, double longitude) {
                // Initialize Firebase Database reference
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                // Specify the path to the "Looc" barangay in your database
                DatabaseReference loocReference = databaseReference.child("Database").child("Barangay").child("Looc");

                // Update the latitude and longitude
                loocReference.child("Map").child("Latitude").setValue(latitude);
                loocReference.child("Map").child("Longitude").setValue(longitude);
            }
        }
