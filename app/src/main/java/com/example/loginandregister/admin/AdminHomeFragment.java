package com.example.loginandregister.admin;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandregister.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
    AdminMainActivity mainActivity;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setCollectorLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        adminTxt = view.findViewById(R.id.admin);


        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment","");
        Log.d("username", username);
        reference = database.getReference().child("collectors").child(username);
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

    public boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

    }

    public void setCollectorLocation() {
        if (isLocationPermissionGranted(getActivity())) {
            Log.d("setCollectorLocation: ", "location permission granted");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.d("onSuccess: ", location.toString());
                    if (location != null) {

                        Double user_lat_value = location.getLatitude();
                        Double user_long_value = location.getLongitude();
                        Toast.makeText(getActivity(), "Latitude = " + user_lat_value + " Longitude = " + user_long_value, Toast.LENGTH_SHORT).show();
                        sendLocationToDB(user_lat_value, user_long_value);
//                        displayAdminLocation(user_lat_value, user_long_value);

                    } else {
                        Toast.makeText(getActivity(), "!!!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            Log.d("setCollectorLocation: ", "location permission not granted");
        }
    }


    private void sendLocationToDB(Double _lat, Double _long) {

        String path = "/collectors/"+ mainActivity.globalusername;
        Log.d("PATH CHECK~~", path);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reference.child("latitude").setValue(_lat);
                reference.child("longitude").setValue(_long);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
