package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //Hide Navigation bar variable
    private View decorView;

    //Inputs
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView register;

    FirebaseDatabase database;
    DatabaseReference reference, reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);

        database = FirebaseDatabase.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Barangay.class);
                startActivity(intent);
                finish();
            }
        });


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCredentials()) {
                        loginUser();
                }
            }
        });
    }

    private boolean validateCredentials() {
        String username = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            editTextEmail.setError("Username is required.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
            return false;
        }

        return true;
    }




    private void loginUser() {
        final String username = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        Log.d("LoginActivity", "Username: " + username);
        Log.d("LoginActivity", "Password: " + password);

        reference = database.getReference("Database");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ("admin".equals(username) && snapshot.child("collectors").child("admin").exists()) {
                  String adminPasswordFromDB = snapshot.child("collectors").child(username).child("password").getValue(String.class);

                        if(adminPasswordFromDB.equals(password)) {
                            // Admin login successful
                            Intent adminIntent = new Intent(Login.this, AdminMainActivity.class);

                            SharedPreferences preferences2 = getSharedPreferences("AdminHomeFragment", MODE_PRIVATE);

                            String username1 = snapshot.child("collectors").child(username).child("username").getValue(String.class);


                            preferences2.edit().putString("adminFragment", username1).apply();

                            startActivity(adminIntent);
                            finish();
                            editTextPassword.setError(null);
                        }

                }
                else if (snapshot.child("users").child(username).exists()) {

                    // ... (existing user login logic)
                    String passwordFromDB = snapshot.child("users").child(username).child("password").getValue(String.class);
                    if (passwordFromDB.equals(password)) {
                        // User login successful
                        // ... (your existing user login code)
                        Intent intent = new Intent(Login.this, UserMainActivity.class);

                        SharedPreferences preferences = getSharedPreferences("HomeFragment", MODE_PRIVATE);
                        String firstname = snapshot.child("firstName").getValue(String.class);
                        preferences.edit().putString("firstname", firstname).apply();

                        SharedPreferences preferences2 = getSharedPreferences("ProfileFragment", MODE_PRIVATE);
                        String firstName = snapshot.child("users").child(username).child("firstName").getValue(String.class);
                        String lastName = snapshot.child("users").child(username).child("lastName").getValue(String.class);
                        String email = snapshot.child("users").child(username).child("email").getValue(String.class);
                        String username1 = snapshot.child("users").child(username).child("username").getValue(String.class);

                        preferences2.edit().putString("firstname", firstName).apply();
                        preferences2.edit().putString("lastname", lastName).apply();
                        preferences2.edit().putString("email", email).apply();
                        preferences2.edit().putString("ProfileUsername", username1).apply();

                        startActivity(intent);
                        finish();
                        editTextPassword.setError(null);
                    } else {
                        // Incorrect user password
                        editTextPassword.setError("Invalid password");
                    }
                } else {
                    // User doesn't exist
                    editTextEmail.setError("User doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
                // Handle the error if needed
            }
        });
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