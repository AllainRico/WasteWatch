package com.example.loginandregister;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.inputmethod.EditorInfo;
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
    boolean locationPermissionGranted = false;
    private View decorView;
    private TextInputEditText editTextEmail, editTextPassword;
    private ImageView passwordToggle;
    private Button buttonLogin;
    private TextView register;
    private TextView btnLoginWith;
    private boolean isReceiverRegistered = false;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();

        broadcastReceiver = new InternetReceiver();
        InternetStatus();

        database = FirebaseDatabase.getInstance();

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

        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // When the "Done" action is triggered, click the Login button
                    buttonLogin.performClick();
                    return true;
                }
                return false;
            }
        });

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

        editTextPassword.addTextChangedListener(new TextWatcher() {
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
    }

    private boolean validateCredentials() {
        String username = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        boolean isValid = true; // Assume both fields are valid

        if (TextUtils.isEmpty(username)) {
            editTextEmail.setError("Username is required.");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required.");
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

    private void loginUser() {
        final String username = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        reference = database.getReference("Database");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("collectors").child(username).exists()) {
                    // User with username "admin" exists, check password
                    String adminPasswordFromDB = snapshot.child("collectors").child(username).child("password").getValue(String.class);
                    if (adminPasswordFromDB.equals(password)) {
                        // Admin login successful
                        if (locationPermissionGranted) {
                            // Store admin details in SharedPreferences
                            SharedPreferences adminPreferences = getSharedPreferences("AdminPreferences", MODE_PRIVATE);
                            adminPreferences.edit().putString("adminUsername", username).apply();

                            Intent adminIntent = new Intent(Login.this, AdminMainActivity.class);
                            adminIntent.putExtra("isOnline", true);
                            startActivity(adminIntent);
                            finish();
                        } else {
                            requestLocationPermission();
                        }
                        editTextPassword.setError(null);
                    } else {
                        editTextPassword.setError("Invalid password");
                        hidePasswordToggle();
                    }
                } else if (snapshot.child("users").child(username).exists()) {
                    // User with given username exists, check password
                    String passwordFromDB = snapshot.child("users").child(username).child("password").getValue(String.class);
                    if (passwordFromDB.equals(password)) {
                        // User login successful
                        // Store user details in SharedPreferences
                        SharedPreferences userPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

                        // Store user-specific data such as username, name, or any other relevant information
                        String firstName = snapshot.child("users").child(username).child("firstName").getValue(String.class);
                        String lastName = snapshot.child("users").child(username).child("lastName").getValue(String.class);
                        // Add other user data as needed

                        userPreferences.edit()
                                .putString("username", username)
                                .putString("firstName", firstName)
                                .putString("lastName", lastName)
                                .apply();

                        // Launch the main user activity or perform other actions as needed
                        Intent userIntent = new Intent(Login.this, UserMainActivity.class);
                        startActivity(userIntent);
                        finish();

                        editTextPassword.setError(null);
                    } else {
                        editTextPassword.setError("Invalid password");
                        hidePasswordToggle();
                    }
                } else {
                    editTextEmail.setError("User doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }

    private void requestLocationPermission() {
    Log.d("LocationPermission", "Requesting location permission");
    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            LOCATION_PERMISSION_REQUEST_CODE);
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                locationPermissionGranted = true;
                loginUser(); // Now, you can proceed with login
            } else {
                Log.d("LocationPermission", "Location permission denied");
                showLocationPermissionDeniedDialog();
            }
        }
    }

    private void showLocationPermissionDeniedDialog() {
        if (!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("WasteWatch requires location access to proceed.")
                    .setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
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
    }

    private void showLoginWithOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_with, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        int dialogWidth = getResources().getDimensionPixelSize(R.dimen.login_dialog_width); // Use a dimension resource

        // Set the layout parameters for the dialog's root view
        dialogView.setLayoutParams(new ViewGroup.LayoutParams(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView btnGoogle = dialogView.findViewById(R.id.btnGoogle);
        ImageView btnFacebook = dialogView.findViewById(R.id.btnFacebook);


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Google login here
                dialog.dismiss();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Facebook login here
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void InternetStatus(){
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(broadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private void initWidgets() {
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        passwordToggle = findViewById(R.id.passwordToggle);
        buttonLogin = findViewById(R.id.btn_login);
        register = findViewById(R.id.register);
        btnLoginWith = findViewById(R.id.btnLoginWith);
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

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}