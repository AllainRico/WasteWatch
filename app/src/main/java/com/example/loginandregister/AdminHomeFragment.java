package com.example.loginandregister;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeFragment extends Fragment {

    private Button buttonMap, buttonProfile, buttonSchedule;
    TextView adminTxt;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;





    public AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);


        adminTxt = view.findViewById(R.id.admin);


        SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("ProfileUsername","");
        reference = database.getReference("Database").child("users").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String first = snapshot.child("firstName").getValue(String.class);
                adminTxt.setText(first);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




/*


        reference = database.getInstance().getReference("Database").child("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String first = snapshot.child(username).child("firstName").getValue(String.class);
                usernameTxt.setText(first);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







 */
        // Retrieve the username from arguments


        buttonMap = view.findViewById(R.id.admin_btnMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AdminHomeFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "map" item
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.map);
            }
        });

        buttonProfile = view.findViewById(R.id.admin_btnReport);
        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new AdminScheduleFragment())
                        .addToBackStack(null)
                        .commit();
                // Set the selected item in the BottomNavigationView to the "profile" item
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.profile);
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
                ((UserMainActivity) requireActivity()).setBottomNavigationSelectedItem(R.id.schedule);
            }
        });

        return view;
    }
}
