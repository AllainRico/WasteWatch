package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Login extends AppCompatActivity {

    // Inputs
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView register;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inputs
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);

        database = FirebaseDatabase.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Barangay.class);
                startActivity(intent);
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

        reference = database.getReference("Database").child("users").child(username);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String passwordFromDB = snapshot.child("password").getValue(String.class);
                    if (passwordFromDB.equals(password)) {
                        // Password is correct, login successful
                        editTextPassword.setError(null);

                        Intent intent = new Intent(Login.this, MainActivity.class);

                        startActivity(intent);
                        finish(); // Optional: Finish the login activity so the user can't go back to it after logging in
                    } else {
                        // Password is incorrect
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
}

