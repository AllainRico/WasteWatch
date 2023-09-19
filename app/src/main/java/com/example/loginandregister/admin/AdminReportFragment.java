package com.example.loginandregister.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.loginandregister.Login;
import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminReportFragment extends Fragment {
    private Button buttonLogout;
    private Button reportbtn;
    private TextView buttonReport, barangay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DatabaseReference iotdatareference;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_report, container, false);

        // Initialize SharedPreferences for Location Permission
        sharedPreferences = getActivity().getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);

        reportbtn = view.findViewById(R.id.btn_report);
        buttonLogout = view.findViewById(R.id.btn_logout);
        barangay = view.findViewById(R.id.barangay);

        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment","");

        reference = database.getReference("Database").child("collectors").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String barName = snapshot.child("barName").getValue(String.class);
                barangay.setText("Barangay " + barName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set the button logout click listener
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLocationPermissionStatus();
                Intent intent = new Intent(getActivity(), Login.class);
                showLogoutConfirmationDialog();
            }
        });

        //set the button report

        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> iotdatastring = new ArrayList<>();
                iotdatareference =  database.getReference("Database").child("Barangay").child("Looc").child("Bins");
                iotdatareference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        iotdatastring.clear();
                            for(DataSnapshot snapshot1: snapshot.getChildren())
                            {
                                iotdatastring.add(snapshot.getValue().toString());
                            }
                        Log.d("FirebaseData", String.valueOf(iotdatastring));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout for the dialog content
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout_confirmation, null);
        builder.setView(dialogView);

        // Set the background and text color for the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here (e.g., start LoginActivity)
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
                ((AdminMainActivity) getActivity()).setOnlineStatus(false);
                alertDialog.dismiss(); // Close the dialog after clicking "Yes"
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Close the dialog after clicking "No"
            }
        });
        alertDialog.show();
    }

    private void clearLocationPermissionStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("location_permission_granted", false);
        editor.apply();
    }

    // Check if location permission needs to be requested
    private boolean shouldRequestLocationPermission() {
        // Check the location permission status in SharedPreferences
        return !sharedPreferences.getBoolean("location_permission_granted", false);
    }

    // Method to request location permission
    private void requestLocationPermission() {
        if (shouldRequestLocationPermission()) {
            // Request location permission
            // Your existing code for requesting location permission
        }
    }
}