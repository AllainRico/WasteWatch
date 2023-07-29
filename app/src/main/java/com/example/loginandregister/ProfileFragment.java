package com.example.loginandregister;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ProfileFragment extends Fragment {

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private ImageView editFirstName;
    private ImageView editLastName;
    private ImageView editEmail;
    private Button buttonLogout;

    //Profile image
    private ImageView editProfile;

    // Boolean flag to track if the EditText is in edit mode
    private boolean isEditMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstName = view.findViewById(R.id.txt_first_name);
        txtLastName = view.findViewById(R.id.txt_last_name);
        txtEmail = view.findViewById(R.id.txt_email);

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

        // Set the button logout click listener
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle logout logic here
            }
        });

        return view;
    }

    private void toggleEditMode(ImageView editView, EditText editText) {
        if (isEditMode) {
            // Set EditText to read-only mode
            editText.setInputType(InputType.TYPE_NULL);
            editView.setImageResource(R.drawable.ic_edit);
        } else {
            // Set EditText to edit mode
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.requestFocus();
            editText.setSelection(editText.getText().length()); // Set cursor to the end
            editView.setImageResource(R.drawable.ic_check);
        }
        isEditMode = !isEditMode;
    }
}
