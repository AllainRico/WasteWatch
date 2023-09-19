package com.example.loginandregister.admin;

import android.content.Context;
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

import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeFragment extends Fragment {

    private Button buttonMap, buttonReport, buttonSchedule;
    TextView adminTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);


        adminTxt = view.findViewById(R.id.admin);


        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment","");
        Log.d("username", username);
        reference = database.getReference("Database").child("collectors").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String first = snapshot.child("username").getValue(String.class);
                adminTxt.setText(first);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonMap = view.findViewById(R.id.admin_btnMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AdminMapFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "map" item
                ((AdminMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.map);
            }
        });

        buttonReport = view.findViewById(R.id.admin_btnReport);
        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AdminReportFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "profile" item
                ((AdminMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.profile);
            }
        });

        buttonSchedule = view.findViewById(R.id.admin_btnSchedule);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AdminScheduleFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "schedule" item
                ((AdminMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.schedule);
            }
        });

        return view;
    }
}
