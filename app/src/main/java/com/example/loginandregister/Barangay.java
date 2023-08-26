package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Barangay extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Hide Navigation bar variable
    private View decorView;
    //Inputs
    private Button buttonBrgySignUp;
    private Spinner brgySpinner, districtSpinner;

    //database
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //To show the item selected in the toast
        //String brgy_name = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(this, brgy_name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //if null
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barangay);

        //Hide the Navigation Bar
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0){
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });


        //Barangay Spinner and District Spinner
        initWidgets();

        ArrayAdapter<CharSequence> brgyAdapter = ArrayAdapter.createFromResource(this, R.array.barangays, android.R.layout.simple_spinner_item);
        brgyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brgySpinner.setAdapter(brgyAdapter);
        brgySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(this, R.array.district, android.R.layout.simple_spinner_item);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtAdapter);
        districtSpinner.setOnItemSelectedListener(this);

        buttonBrgySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedBarangay = brgySpinner.getSelectedItem().toString();
                if (selectedBarangay != null && !selectedBarangay.isEmpty()) {
                    database = FirebaseDatabase.getInstance();
                    SharedPreferences preferences3 = getSharedPreferences("MyPrefsBarangay", MODE_PRIVATE);
                    preferences3.edit().putString("barangay", selectedBarangay).apply();
                        Intent intent = new Intent(Barangay.this,Register.class);
                        startActivity(intent);
                        finish();

                } else {
                    Toast.makeText(Barangay.this, "Please select a barangay", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initWidgets() {
        buttonBrgySignUp = findViewById(R.id.btn_bgry_signup);
        brgySpinner = findViewById(R.id.brgySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to Login activity
        Intent intent = new Intent(Barangay.this, Login.class);
        startActivity(intent);
        finish();
    }

    //Hide the Navigation Bar Method
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

}