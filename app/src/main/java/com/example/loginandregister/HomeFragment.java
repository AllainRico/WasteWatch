package com.example.loginandregister;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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

public class HomeFragment extends Fragment {

    private Button buttonMap, buttonProfile, buttonSchedule;
    TextView usernameTxt;
    FirebaseDatabase database;
    DatabaseReference reference;





    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        usernameTxt = view.findViewById(R.id.username);


        SharedPreferences preferences = getActivity().getSharedPreferences("HomeFragment", Context.MODE_PRIVATE);
        String username = preferences.getString("firstname", " ");

        usernameTxt.setText(username);

        // Retrieve the username from arguments



        buttonMap = view.findViewById(R.id.btnMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new MapFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "map" item
                ((MainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.map);
            }
        });

        buttonProfile = view.findViewById(R.id.btnProfile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "profile" item
                ((MainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.profile);
            }
        });

        buttonSchedule = view.findViewById(R.id.btnSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ScheduleFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "schedule" item
                ((MainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.schedule);
            }
        });

        return view;
    }
}
