package com.example.loginandregister.requestCollection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userRequestCollectionFragment extends Fragment {

    private EditText user_username_editText;
    private EditText user_barangayName;
    private EditText user_userRequestMessage_editText;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment_user_request_collection, container, false);

        displayDataToScreen(view);



        return view;
    }

    private void displayDataToScreen(View view) {
        //this is to display the data to editTexts

        user_username_editText = view.findViewById(R.id.user_username);
        user_barangayName = view.findViewById(R.id.user_barname);
        user_userRequestMessage_editText = view.findViewById(R.id.user_request_message);

        user_username_editText.setInputType(InputType.TYPE_NULL);
        user_barangayName.setInputType(InputType.TYPE_NULL);

        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");

        user_username_editText.setText(username);
        getBarName();
    }

    private void getBarName() {
        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");

        reference = database.getReference("Database");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String barName = snapshot.child("users").child(username).child("barName").getValue(String.class);
                user_barangayName.setText(barName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}