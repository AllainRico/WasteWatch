package com.example.loginandregister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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


public class ProfileFragment extends Fragment {

    Button buttonLogout;
    TextView txtFirstName, txtLastName, txtEmail;
    FirebaseDatabase database;
    DatabaseReference reference;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        buttonLogout = view.findViewById(R.id.btn_logout);
        txtFirstName = view.findViewById(R.id.txt_first_name);
        txtLastName = view.findViewById(R.id.txt_last_name);
        txtEmail   = view.findViewById(R.id.txt_email);


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