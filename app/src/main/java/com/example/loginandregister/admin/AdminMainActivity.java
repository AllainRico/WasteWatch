    package com.example.loginandregister.admin;

    import androidx.annotation.NonNull;
    import androidx.annotation.RequiresApi;
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
    import android.os.Build;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.view.WindowInsets;
    import android.view.WindowInsetsController;
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
            setContentView(R.layout.activity_admin_main);

            // Retrieve the isOnline value from the Intent
            isOnline = getIntent().getBooleanExtra("isOnline", false);

            //Hide the Navigation Bar
            decorView = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setupSystemBarsForAndroid12AndHigher(decorView);
            } else {
                hideSystemBars();
            }

            initializeLayout();

            if (isOnline) {
                // Access GPS here
                initializeLocationService();
            } else {
                // User is not online, handle this case accordingly
                showLocationPermissionDeniedDialog();
            }
        }

        private boolean isLocationPermissionGranted() {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
        }

        private void requestLocationPermission() {
            Log.d("LocationPermission", "Requesting location permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location permission granted, initialize location service
                    initializeLocationService();
                } else {
                    showLocationPermissionDeniedDialog();
                }
            }
        }

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

                                        LocationData.getInstance().setAdminLatitude(adminLatitude);
                                        LocationData.getInstance().setAdminLongitude(adminLongitude);

                                        Log.d("AdminLocation", "Latitude: " + adminLatitude);
                                        Log.d("AdminLocation", "Longitude: " + adminLongitude);
                                    } else {
                                        Toast.makeText(AdminMainActivity.this, "Location is not available", Toast.LENGTH_SHORT).show();

                                        double defaultLatitude = 10.305627;
                                        double defaultLongitude = 123.946517;

                                        LocationData.getInstance().setAdminLatitude(defaultLatitude);
                                        LocationData.getInstance().setAdminLongitude(defaultLongitude);
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



        private void initializeLayout() {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Fragment shown first at start
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

        @RequiresApi(api = Build.VERSION_CODES.R)
        private void setupSystemBarsForAndroid12AndHigher(View decorView) {
            decorView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    WindowInsetsController controller = v.getWindowInsetsController();
                    if (controller != null) {
                        // Hide system bars using the new API
                        controller.hide(WindowInsets.Type.systemBars());
                        controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    }
                    return insets;
                }
            });
        }

        private void hideSystemBars() {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }