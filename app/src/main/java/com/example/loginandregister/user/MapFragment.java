        package com.example.loginandregister.user;

        import android.os.Bundle;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.ProgressBar;

        import androidx.fragment.app.Fragment;

        import com.example.loginandregister.R;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        public class MapFragment extends Fragment{

            private ProgressBar progressBar;
            private ImageView mapPlaceholder;

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
                        latitude = 10.3023;
                        longitude = 123.9469;

                        LatLng brgyMap = new LatLng(latitude, longitude);

                        googleMap.addMarker(new MarkerOptions().position(brgyMap).title(barangay));

                        float zoomLevel = 15;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brgyMap, zoomLevel));

                        onMapLoaded();
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                // When clicked on map
                                // Initialize marker options
                                MarkerOptions markerOptions=new MarkerOptions();
                                // Set position of marker
                                markerOptions.position(latLng);
                                // Set title of marker
                                markerOptions.title(latLng.latitude+" : "+latLng.longitude);
                                // Remove all marker
                                googleMap.clear();
                                // Animating to zoom the marker
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                // Add marker on map
                                googleMap.addMarker(markerOptions);
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
        }
