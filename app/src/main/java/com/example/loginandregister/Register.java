package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {
    //Hide Navigation bar variable
    private View decorView;

    //Inputs
    TextInputEditText editTextFirstName, editTextLastName, editTextUsername, editTextEmail, editTextPassword;
    Button buttonReg;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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


        //Inputs
        editTextFirstName = findViewById(R.id.fName);
        editTextLastName = findViewById(R.id.lName);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);



        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Database").child("users");
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                SharedPreferences preferences = getSharedPreferences("MyPrefsBarangay", Context.MODE_PRIVATE);
                String barangay = preferences.getString("barangay", " ");


                User user = new User(firstName, lastName, username, email, password, barangay);
                reference.child(username).setValue(user);
                Toast.makeText(Register.this, "Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to Barangay activity
        Intent intent = new Intent(Register.this, Barangay.class);
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