package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandregister.admin.AdminMainActivity;
import com.example.loginandregister.internet.InternetReceiver;
import com.example.loginandregister.user.UserMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    boolean isOnline = false;
    private View decorView;
    private TextInputEditText editTextEmail, editTextPassword;
    private ImageView passwordToggle;
    private Button buttonLogin;
    private TextView register;
    private TextView btnLoginWith;
    FirebaseDatabase database;
    DatabaseReference reference, reference2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();

        //internet
        broadcastReceiver = new InternetReceiver();
        InternetStatus();

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int inputType = editTextPassword.getInputType();

                if (inputType == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                if (TextUtils.isEmpty(editTextPassword.getError())) {
                    // If no error, set the standard margin
                    updateToggleMargin(4);
                } else {
                    // If there's an error, set a larger margin to avoid overlap
                    updateToggleMargin(18);
                }

                editTextPassword.setTypeface(Typeface.DEFAULT);
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });

        // Set a text change listener for the password EditText
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString().trim();
                if (!TextUtils.isEmpty(password) && TextUtils.isEmpty(editTextPassword.getError())) {
                    // If password is not empty and no error, set the standard margin
                    updateToggleMargin(4);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

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

        database = FirebaseDatabase.getInstance();

        //Making Register string to have a underline
        SpannableString spannableString = new SpannableString(getString(R.string.register));
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        register.setText(spannableString);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Barangay.class);
                startActivity(intent);
                finish();
            }
        });

        // Making "Login with" string have an underline
        SpannableString loginWithSpannable = new SpannableString(getString(R.string.login_with));
        loginWithSpannable.setSpan(new UnderlineSpan(), 0, loginWithSpannable.length(), 0);
        btnLoginWith.setText(loginWithSpannable);
        btnLoginWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginWithOptionsDialog();
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

    private void initWidgets() {
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        passwordToggle = findViewById(R.id.passwordToggle);
        buttonLogin = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);
        btnLoginWith = findViewById(R.id.btnLoginWith);
    }

    //internet
    public void InternetStatus(){
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    //internet
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
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
            updateToggleMargin(18); // Increase margin for error message
            return false;
        } else {
            // If password is not empty and no error, set the standard margin
            updateToggleMargin(4);
        }

        return true;
    }

    private void loginUser() {
        final String username = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        //Log.d("LoginActivity", "Username: " + username);
        //Log.d("LoginActivity", "Password: " + password);

        reference = database.getReference("Database");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ("admin".equals(username) && snapshot.child("collectors").child("admin").exists()) {
                    //Log.d("LoginActivity", "Admin login detected");
                    String adminPasswordFromDB = snapshot.child("collectors").child(username).child("password").getValue(String.class);
                        if(adminPasswordFromDB.equals(password)) {
                            // Admin login successful
                            //Log.d("LoginActivity", "Admin password is correct");
                            // Request location permission
                            requestLocationPermission();

                            Intent adminIntent = new Intent(Login.this, AdminMainActivity.class);
                            adminIntent.putExtra("isOnline", true); // Set isOnline to true

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
                        updateToggleMargin(18);
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

    private void requestLocationPermission() {
        Log.d("LocationPermission", "Requesting location permission");
        // Request location permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Log.d("LocationPermission", "Location permission request result received.");
            // Log grantResults here to see what it contains
            for (int result : grantResults) {
                Log.d("LocationPermission", "Grant Result: " + result);
            }

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, admin can proceed
                isOnline = true;
                // Log success here
                Log.d("LocationPermission", "Location permission granted");
            } else {
                // Location permission denied
                // Log denial here
                Log.d("LocationPermission", "Location permission denied");
                showLocationPermissionDeniedDialog();
            }
        }
    }

    private void showLocationPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("WasteWatch requires location access to proceed.")
                .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Request location permission again
                        requestLocationPermission();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Login.this, "Location is needed", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }


    private void showLoginWithOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_with, null);
        builder.setView(dialogView);

        // Set the background and text color for the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Set the desired width of the dialog
        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.login_dialog_width); // Use a dimension resource

        // Set the layout parameters for the dialog's root view
        dialogView.setLayoutParams(new ViewGroup.LayoutParams(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView btnGoogle = dialogView.findViewById(R.id.btnGoogle);
        ImageView btnFacebook = dialogView.findViewById(R.id.btnFacebook);

        // Set the click listeners for the Google and Facebook buttons
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Google login here
                dialog.dismiss(); // Close the dialog after selection
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Facebook login here
                dialog.dismiss(); // Close the dialog after selection
            }
        });

        dialog.show();
    }

    //Moves TogglePassword when Error occurs
    private void updateToggleMargin(int marginDp) {
        ImageView passwordToggle = findViewById(R.id.passwordToggle);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) passwordToggle.getLayoutParams();
        params.setMarginEnd(dpToPx(marginDp)); // Convert dp to pixels
        passwordToggle.setLayoutParams(params);
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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