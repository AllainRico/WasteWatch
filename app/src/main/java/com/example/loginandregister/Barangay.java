package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Barangay extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private View decorView;
    private Button buttonBrgySignUp;
    private Spinner brgySpinner, districtSpinner;
    private FirebaseDatabase database;
    private DatabaseReference barangayRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barangay);

        //Hide the Navigation Bar
        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupSystemBarsForAndroid12AndHigher(decorView);
        } else {
            hideSystemBars();
        }

        initWidgets();

        database = FirebaseDatabase.getInstance();
        barangayRef = database.getReference("Database").child("Barangay");

        ArrayList<String> barangayList = new ArrayList<>();
        barangayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot barangaySnapshot : dataSnapshot.getChildren()) {
                    String barangayName = barangaySnapshot.getKey();
                    barangayList.add(barangayName);
                }
                //for default spinner value
                barangayList.add(0, "Select a Barangay...");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Barangay.this, android.R.layout.simple_spinner_item, barangayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                brgySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Barangay.this, "Error fetching barangays: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonBrgySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedBarangay = brgySpinner.getSelectedItem().toString();
                String selectedDistrict = districtSpinner.getSelectedItem().toString();  // Get selected district

                if (selectedBarangay != null && !selectedBarangay.isEmpty()) {
                    if (!selectedDistrict.equals("Select a District...")) {  // Check if district is selected
                        SharedPreferences preferences3 = getSharedPreferences("MyPrefsBarangay", MODE_PRIVATE);
                        preferences3.edit().putString("barangay", selectedBarangay).apply();
                        SharedPreferences preferences4 = getSharedPreferences("MyPrefsBarangayDistrict", MODE_PRIVATE);
                        preferences4.edit().putString("district", selectedDistrict).apply();
                        Intent intent = new Intent(Barangay.this, Register.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Barangay.this, "Please select a district", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Barangay.this, "Please select a barangay", Toast.LENGTH_SHORT).show();
                }
            }
        });


        brgySpinner.setOnItemSelectedListener(this);

        onWindowFocusChanged(true);
    }

    private void initWidgets() {
        buttonBrgySignUp = findViewById(R.id.btn_bgry_signup);
        brgySpinner = findViewById(R.id.brgySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedBarangay = adapterView.getItemAtPosition(i).toString();
        if (!selectedBarangay.equals("Select a Barangay...")) {
            populateDistrictSpinner(selectedBarangay);
            districtSpinner.setEnabled(true);
        }
        else{
            // If "Select a Barangay..." is selected, disable the districtSpinner
            districtSpinner.setEnabled(false);

            // Set a default value in districtSpinner
            ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(Barangay.this, android.R.layout.simple_spinner_item, new String[]{"Select a District..."});
            districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(districtAdapter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Implement as needed
    }

    private void populateDistrictSpinner(String selectedBarangay) {
        ArrayList<String> districtList = new ArrayList<>();

        DatabaseReference districtRef = database.getReference("Database")
                .child("Barangay")
                .child(selectedBarangay)
                .child("District");

        districtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot districtSnapshot : dataSnapshot.getChildren()) {
                    String districtName = districtSnapshot.getKey();
                    districtList.add(districtName);
                }
                //for default spinner value
                districtList.add(0, "Select a District...");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Barangay.this, android.R.layout.simple_spinner_item, districtList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Barangay.this, "Error fetching districts: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Barangay.this, Login.class);
        startActivity(intent);
        finish();
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
