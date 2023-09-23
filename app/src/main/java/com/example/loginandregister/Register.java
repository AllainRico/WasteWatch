package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Register extends AppCompatActivity {
    //Hide Navigation bar variable
    private View decorView;
    private TextInputEditText editTextFirstName, editTextLastName, editTextUsername, editTextEmail, editTextPassword;
    private ImageView passwordToggle;
    private Button buttonReg;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initWidgets();
        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inputType = editTextPassword.getInputType();

                if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                editTextPassword.setTypeface(Typeface.DEFAULT);
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });

        //Hide the Navigation Bar
        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupSystemBarsForAndroid12AndHigher(decorView);
        } else {
            hideSystemBars();
        }

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString().trim();
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Database").child("users");

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child(username).exists()) {
                            if(username.equals("admin")){
                                Toast.makeText(Register.this, "Choose another username", Toast.LENGTH_SHORT).show();
                            }else{
                                String firstName = editTextFirstName.getText().toString().trim();
                                String lastName = editTextLastName.getText().toString().trim();
                                String email = editTextEmail.getText().toString().trim();
                                String password = editTextPassword.getText().toString().trim();
                                SharedPreferences preferences = getSharedPreferences("MyPrefsBarangay", Context.MODE_PRIVATE);
                                String barangay = preferences.getString("barangay", " ");
                                SharedPreferences preferences1 = getSharedPreferences("MyPrefsBarangayDistrict", Context.MODE_PRIVATE);
                                String district = preferences1.getString("district", " ");
                                User user = new User(firstName, lastName, username, email, password, barangay, district);
                                reference.child(username).setValue(user);
                                Toast.makeText(Register.this, "Successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(Register.this, "Username already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

    }

    private void initWidgets() {
        editTextFirstName = findViewById(R.id.fName);
        editTextLastName = findViewById(R.id.lName);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        passwordToggle = findViewById(R.id.passwordToggle);
        buttonReg = findViewById(R.id.btn_register);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to Barangay activity
        Intent intent = new Intent(Register.this, Barangay.class);
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