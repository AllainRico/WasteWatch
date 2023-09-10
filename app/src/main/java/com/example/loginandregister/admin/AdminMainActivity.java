    package com.example.loginandregister.admin;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import android.Manifest;
    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Toast;

    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;

    import com.example.loginandregister.R;
    import com.example.loginandregister.databinding.ActivityMainBinding;
    import com.google.android.gms.tasks.OnSuccessListener;


    public class AdminMainActivity extends AppCompatActivity {
        private View decorView;
        ActivityMainBinding binding;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        boolean isOnline = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //setContentView(R.layout.activity_main);

            // Retrieve the isOnline value from the Intent
            isOnline = getIntent().getBooleanExtra("isOnline", false);

            if (isLocationPermissionGranted()) {
                Log.d("AdminLocation", "Initializing location service");
                initializeLocationService();
            } else {
                requestLocationPermission();
            }

            decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int i) {
                    if(i == 0){
                        decorView.setSystemUiVisibility(hideSystemBars());
                    }
                }
            });

            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            //Fragment shown first at start
            replaceAdminFragment(new AdminHomeFragment());

            binding.bottomNavigationView.setOnItemSelectedListener(item -> {

                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    replaceAdminFragment(new AdminHomeFragment());
                } else if (itemId == R.id.schedule) {
                    replaceAdminFragment(new AdminScheduleFragment());
                } else if (itemId == R.id.map) {
                    replaceAdminFragment(new AdminMapFragment());
                } else if (itemId == R.id.profile) {
                    replaceAdminFragment(new AdminReportFragment());
                }

                return true;
            });

            if (isOnline) {
                // Access GPS here
                // You can use location services because the user is online
            } else {
                // User is not online, handle this case accordingly
                // You might want to show a message or restrict access to GPS features
            }
        }

        // Check if the location permission is granted
        private boolean isLocationPermissionGranted() {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        // Request location permission
        private void requestLocationPermission() {
            Log.d("LocationPermission", "Requesting location permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                Log.d("LocationPermission", "Location permission request result received.");
                // Log grantResults here to see what it contains
                for (int result : grantResults) {
                    Log.d("LocationPermission", "Grant Result: " + result);
                }
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted, initialize location service
                    initializeLocationService();
                } else {
                    showLocationPermissionDeniedDialog();
                }
            }
        }

        // Initialize location service
        private void initializeLocationService() {
            if (isLocationPermissionGranted()) {

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double adminLatitude = location.getLatitude();
                                        double adminLongitude = location.getLongitude();

                                        Log.d("AdminLocation", "Latitude: " + adminLatitude);
                                        Log.d("AdminLocation", "Longitude: " + adminLongitude);

                                        // Pass the adminLatitude and adminLongitude to your AdminMapFragment to display on the map
                                        AdminMapFragment adminMapFragment = (AdminMapFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.map);
                                        adminMapFragment.updateAdminLocation(adminLatitude, adminLongitude);
                                    } else {
                                        Toast.makeText(AdminMainActivity.this, "Location is not available", Toast.LENGTH_SHORT).show();

                                        double defaultLatitude = 10.305627;
                                        double defaultLongitude = 123.946517;

                                        AdminMapFragment adminMapFragment = (AdminMapFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.map);

                                        if (adminMapFragment != null) {
                                            adminMapFragment.updateAdminLocation(defaultLatitude, defaultLongitude);
                                        } else {
                                            // Handle the case where the fragment is null
                                            Toast.makeText(AdminMainActivity.this, "Map fragment not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                } else {
                    showLocationPermissionDeniedDialog();
                }
            } else {
                requestLocationPermission();
            }
        }



        private void showLocationPermissionDeniedDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("WasteWatch requires location access to proceed.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Request location permission again
                            requestLocationPermission();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(AdminMainActivity.this, "Location is needed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();
        }

        public void setOnlineStatus(boolean onlineStatus) {
            isOnline = onlineStatus;
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            if(hasFocus){
                decorView.setSystemUiVisibility(hideSystemBars());
            }
        }

        private int hideSystemBars(){
            return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        private void replaceAdminFragment(Fragment fragment){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            fragmentTransaction.commit();
        }

        public void setBottomNavigationSelectedItem(int itemId) {
            binding.bottomNavigationView.setSelectedItemId(itemId);
        }
    }