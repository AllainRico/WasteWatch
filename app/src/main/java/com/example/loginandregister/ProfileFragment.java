package com.example.loginandregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;


public class ProfileFragment extends Fragment {

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private ImageView editFirstName;
    private ImageView editLastName;
    private ImageView editEmail;
    private Button buttonLogout;
    private TextView txtUsername;

    //Profile image
    private ImageView editProfile;

    // Boolean flag to track if the EditText is in edit mode
    private boolean isEditMode = false;
    FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstName = view.findViewById(R.id.txt_first_name);
        txtLastName = view.findViewById(R.id.txt_last_name);
        txtEmail = view.findViewById(R.id.txt_email);
        txtUsername = view.findViewById(R.id.username);

        editFirstName = view.findViewById(R.id.edit_first_name);
        editLastName = view.findViewById(R.id.edit_last_name);
        editEmail = view.findViewById(R.id.edit_email);

        buttonLogout = view.findViewById(R.id.btn_logout);

        //change profile
        //editProfile = view.findViewById(R.id.editprofile);

        // Set EditTexts initially as read-only
        txtFirstName.setInputType(InputType.TYPE_NULL);
        txtLastName.setInputType(InputType.TYPE_NULL);
        txtEmail.setInputType(InputType.TYPE_NULL);

        // Set OnClickListener for the edit ImageViews
        editFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditMode(editFirstName, txtFirstName);
            }
        });

        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditMode(editLastName, txtLastName);
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEditMode(editEmail, txtEmail);
            }
        });

        reference = database.getInstance().getReference("Database").child("users");

        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");
        txtUsername.setText(username);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName2 = snapshot.child(username).child("firstName").getValue(String.class);
                String lastName2 = snapshot.child(username).child("lastName").getValue(String.class);
                String email2 = snapshot.child(username).child("email").getValue(String.class);
                txtFirstName.setText(firstName2);
                txtLastName.setText(lastName2);
                txtEmail.setText(email2);

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

        // Add a touch listener to the root ViewGroup of the fragment layout
        View rootView = view.findViewById(R.id.fragment_profile);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Check if the root view is touched, and if in edit mode, return EditText fields to read-only mode
                if (isEditMode) {
                    toggleEditMode(editFirstName, txtFirstName);
                    toggleEditMode(editLastName, txtLastName);
                    toggleEditMode(editEmail, txtEmail);
                    return true;
                }
                return false;
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


    private void toggleEditMode(ImageView editView, EditText editText) {
        if (isEditMode) {

            String first = txtFirstName.getText().toString().trim();
            String last = txtLastName.getText().toString().trim();
            String email3 = txtEmail.getText().toString().trim();
            String user = txtUsername.getText().toString().trim();

            reference = database.getInstance().getReference("Database").child("users").child(user);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    reference.child("firstName").setValue(first);
                    reference.child("lastName").setValue(last);
                    reference.child("email").setValue(email3);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Set EditText to read-only mode
            editText.setInputType(InputType.TYPE_NULL);
            editView.setImageResource(R.drawable.ic_edit);
            // Hide the keyboard when leaving edit mode
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        } else {
            // Set EditText to edit mode
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.requestFocus();
            editText.setSelection(editText.getText().length()); // Set cursor to the end
            editView.setImageResource(R.drawable.ic_check);
            // Show the keyboard when entering edit mode
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        isEditMode = !isEditMode;
    }
}
