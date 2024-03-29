package com.example.loginandregister.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private Button buttonMap, buttonProfile, buttonSchedule;
    TextView usernameTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        usernameTxt = view.findViewById(R.id.username);


        // this is the get sharedPreference for the first name from the login
        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");
        reference = database.getReference().child("users").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String first = snapshot.child("firstName").getValue(String.class);
                usernameTxt.setText(first);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonMap = view.findViewById(R.id.user_btnMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new MapFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "map" item
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.map);
            }
        });

        buttonProfile = view.findViewById(R.id.user_btnProfile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "profile" item
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.profile);
            }
        });

        buttonSchedule = view.findViewById(R.id.user_btnSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ScheduleFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "schedule" item
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.schedule);
            }
        });

        return view;
    }
}
