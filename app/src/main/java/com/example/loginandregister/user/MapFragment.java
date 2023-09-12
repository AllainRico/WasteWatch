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
            private GoogleMap googleMap;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_map, container, false);

                progressBar = view.findViewById(R.id.progressBar);
                mapPlaceholder = view.findViewById(R.id.mapPlaceholder);

                SupportMapFragment supportMapFragment = (SupportMapFragment)
                        getChildFragmentManager().findFragmentById(R.id.userMap);

                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;

                        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                        String username = preferences2.getString("ProfileUsername", "");

                        reference = database.getReference("Database");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String bar = snapshot.child("users").child(username).child("barName").getValue(String.class);
                                if ("Looc".equals(bar)) { // Compare strings using .equals()
                                    Double lat = snapshot.child("Barangay").child(bar).child("Map").child("Latitude").getValue(Double.class);
                                    Double longi = snapshot.child("Barangay").child(bar).child("Map").child("Longitude").getValue(Double.class);

                                    if (lat != null && longi != null) {
                                        LatLng brgyMap = new LatLng(lat, longi);
                                        float zoomLevel = 15.3f;
                                        //googleMap.addMarker(new MarkerOptions().position(brgyMap).title(bar));
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                        googleMap.getUiSettings().setAllGesturesEnabled(false);
                                        onMapLoaded();
                                    }
                                }else  if ("Basak".equals(bar)) { // Compare strings using .equals()
                                    Double lat = snapshot.child("Barangay").child(bar).child("Map").child("Latitude").getValue(Double.class);
                                    Double longi = snapshot.child("Barangay").child(bar).child("Map").child("Longitude").getValue(Double.class);

                                    if (lat != null && longi != null) {
                                        LatLng brgyMap = new LatLng(lat, longi);
                                        float zoomLevel = 15.3f;
                                        //googleMap.addMarker(new MarkerOptions().position(brgyMap).title(bar));
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                                        googleMap.getUiSettings().setZoomControlsEnabled(false);
                                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                        googleMap.getUiSettings().setAllGesturesEnabled(false);
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
              loocReference.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      String lat = snapshot.child("Latitude").getValue(String.class);
                      String longi = snapshot.child("Longitude").getValue(String.class);
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
            }
        }
