package com.example.loginandregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Barangay extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Inputs
    Button buttonBrgySignUp;
    TextView  loginTextView;
    Spinner brgySpinner;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String brgy_name = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, brgy_name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //if null
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barangay);

        //Inputs
        loginTextView = findViewById(R.id.loginNow);
        buttonBrgySignUp = findViewById(R.id.btn_bgry_signup);
        brgySpinner = findViewById(R.id.brgySpinner);

        ArrayAdapter<CharSequence>  adapter = ArrayAdapter.createFromResource(this,R.array.barangays, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        brgySpinner.setAdapter(adapter);

        brgySpinner.setOnItemSelectedListener(this);






        buttonBrgySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedBarangay = brgySpinner.getSelectedItem().toString();
                if (selectedBarangay != null && !selectedBarangay.isEmpty()) {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("Database").child("Barangay").child(selectedBarangay);
                    reference.setValue(true); // Save the selected barangay to the database
                    Toast.makeText(Barangay.this, "Successful", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Barangay.this, "Please select a barangay", Toast.LENGTH_SHORT).show();
                }
            }
        });


        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class );
                startActivity(intent);
                finish();
            }
        });



    }
}