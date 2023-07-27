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

public class Barangay extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Inputs
    Button buttonBrgySignUp;
    Spinner brgySpinner;

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
        buttonBrgySignUp = findViewById(R.id.btn_bgry_signup);
        brgySpinner = findViewById(R.id.brgySpinner);

        ArrayAdapter<CharSequence>  adapter = ArrayAdapter.createFromResource(this,R.array.barangays, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        brgySpinner.setAdapter(adapter);

        brgySpinner.setOnItemSelectedListener(this);






        buttonBrgySignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class );
                startActivity(intent);
                finish();
            }
        });

    }
}