package com.example.loginandregister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtFirstName = view.findViewById(R.id.txt_first_name);
        txtLastName = view.findViewById(R.id.txt_last_name);
        txtEmail = view.findViewById(R.id.txt_email);

        editFirstName = view.findViewById(R.id.edit_first_name);
        editLastName = view.findViewById(R.id.edit_last_name);
        editEmail = view.findViewById(R.id.edit_email);

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
                txtFirstName.setInputType(InputType.TYPE_CLASS_TEXT);
                txtFirstName.requestFocus();
                txtFirstName.setSelection(txtFirstName.getText().length()); // Set cursor to the end
            }
        });

        editLastName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtLastName.setInputType(InputType.TYPE_CLASS_TEXT);
                txtLastName.requestFocus();
                txtLastName.setSelection(txtLastName.getText().length()); // Set cursor to the end
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                txtEmail.requestFocus();
                txtEmail.setSelection(txtEmail.getText().length()); // Set cursor to the end
            }
        });

        // Set OnFocusChangeListener to reset input type to read-only when EditText loses focus
        txtFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    txtFirstName.setInputType(InputType.TYPE_NULL);
                }
            }
        });

        txtLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    txtLastName.setInputType(InputType.TYPE_NULL);
                }
            }
        });

        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    txtEmail.setInputType(InputType.TYPE_NULL);
                }
            }
        });

        SharedPreferences preferences2 = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String firstName = preferences2.getString("firstname", " ");
        String lastName = preferences2.getString("lastname", " ");
        String email = preferences2.getString("email", " ");

        txtFirstName.setText(firstName);
        txtLastName.setText(lastName);
        txtEmail.setText(email);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}