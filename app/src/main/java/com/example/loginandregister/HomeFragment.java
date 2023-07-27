package com.example.loginandregister;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomeFragment extends Fragment {

    private Button buttonMap, buttonProfile, buttonSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        buttonMap = view.findViewById(R.id.btnMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment container with MapFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new MapFragment())
                        .addToBackStack(null) // Optional: Add to back stack so pressing back button returns to this fragment
                        .commit();
            }
        });

        buttonProfile = view.findViewById(R.id.btnProfile);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment container with ProfileFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ProfileFragment())
                        .addToBackStack(null) // Optional: Add to back stack so pressing back button returns to this fragment
                        .commit();

            }
        });

        buttonSchedule = view.findViewById(R.id.btnSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Replace the current fragment container with ScheduleFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new ScheduleFragment())
                        .addToBackStack(null) // Optional: Add to back stack so pressing back button returns to this fragment
                        .commit();
            }
        });

        return view;
    }

}
