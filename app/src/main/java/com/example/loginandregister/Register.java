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
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class Register extends AppCompatActivity {
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

        decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setupSystemBarsForAndroid12AndHigher(decorView);
        } else {
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int i) {
                    if(i == 0){
                        decorView.setSystemUiVisibility(hideSystemBars());
                    }
                }
            });
        }

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateCredentials()) {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference().child("users");

                    editTextFirstName.setError(null);
                    editTextLastName.setError(null);
                    editTextUsername.setError(null);
                    editTextEmail.setError(null);
                    editTextPassword.setError(null);

                    String username = editTextUsername.getText().toString().trim(); // Declare username here

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.child(username).exists()) {
                                if (username.equals("admin")) {
                                    editTextUsername.setError("Choose another username");
                                } else {
                                    String firstName = editTextFirstName.getText().toString().trim();
                                    String lastName = editTextLastName.getText().toString().trim();
                                    String email = editTextEmail.getText().toString().trim();
                                    String password = editTextPassword.getText().toString().trim();
                                    SharedPreferences preferences = getSharedPreferences("MyPrefsBarangay", Context.MODE_PRIVATE);
                                    String barangay = preferences.getString("barangay", " ");
                                    SharedPreferences preferences1 = getSharedPreferences("MyPrefsBarangayDistrict", Context.MODE_PRIVATE);
                                    String district = preferences1.getString("district", " ");
                                    boolean isVerify = false;

                                    User user = new User(firstName, lastName, username, email, password, barangay, district, isVerify);

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("isVerify", user.isVerify());
                                    userMap.put("firstName", user.getFirstName());
                                    userMap.put("lastName", user.getLastName());
                                    userMap.put("username", user.getUsername());
                                    userMap.put("email", user.getEmail());
                                    userMap.put("password", user.getPassword());
                                    userMap.put("barName", user.getBarName());
                                    userMap.put("district", user.getDistrict());

                                    reference.child(username).setValue(userMap);

                                    Toast.makeText(Register.this, "Account Registered Successfully", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(Register.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                editTextUsername.setError("Username already exists");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });


    }
    private boolean validateCredentials() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        boolean isValid = true;

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("First Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Last Name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Username is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            hidePasswordToggle();
            isValid = false;
        } else if (password.length() < 8 || password.length() > 20) {
            editTextPassword.setError("Password must be 8-20 characters long");
            hidePasswordToggle();
            isValid = false;
        } else {
            showPasswordToggle();
        }

        return isValid;
    }

    private void hidePasswordToggle() {
        passwordToggle.setVisibility(View.GONE);
    }
    private void showPasswordToggle() {
        passwordToggle.setVisibility(View.VISIBLE);
    }

    private void initWidgets() {
        editTextFirstName = findViewById(R.id.fName);
        editTextLastName = findViewById(R.id.lName);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        passwordToggle = findViewById(R.id.passwordToggle);
        buttonReg = findViewById(R.id.btn_register);
        editTextPassword.addTextChangedListener(passwordWatcher);
    }

    TextWatcher passwordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                showPasswordToggle();
            }
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
                return insets;
            }
        });
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