package com.example.loginandregister.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.loginandregister.Login;
import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminReportFragment extends Fragment {


    private Button buttonLogout;
    private TextView buttonReport, barangay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_report, container, false);


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
                Intent intent = new Intent(getActivity(), Login.class);
                showLogoutConfirmationDialog();
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

        // Get references to the "Yes" and "No" buttons
        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        // Set click listeners for the buttons
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here (e.g., start LoginActivity)
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
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
}