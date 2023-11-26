package com.example.loginandregister.requestCollection;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.loginandregister.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class userRequestCollectionFragment extends Fragment {

    private EditText user_username_editText;
    private EditText user_barangayName;
    private Button user_checklocationButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 200;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_fragment_user_request_collection, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        user_checklocationButton = view.findViewById(R.id.user_checklocationButton);

        displayDataToScreen(view);
        getRealtimeLocation();


        return view;
    }

    private void getRealtimeLocation() {
        if (user_checklocationButton != null) {
            user_checklocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(getActivity(), "Button is clicked~!", Toast.LENGTH_SHORT).show();
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if(requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        {

                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null)
                                    {

                                        Double user_lat_value = location.getLatitude();
                                        Double user_long_value = location.getLongitude();
                                        Toast.makeText(getActivity(), "Latitude = "+ user_lat_value + " Longitude = " + user_long_value, Toast.LENGTH_SHORT).show();
                                        sendLocationToDB(user_lat_value, user_long_value);

                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), "!!!!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "!!!", Toast.LENGTH_SHORT).show();
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "ELSE Button is clicked~!", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            Log.e("ButtonError", "user_check_location_button is null");
        }
    }



    private void displayDataToScreen(View view) {
        //this is to display the data to editTexts

        user_username_editText = view.findViewById(R.id.user_username);
        user_barangayName = view.findViewById(R.id.user_barname);

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

        reference = database.getReference();
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
    private void sendLocationToDB(Double _lat, Double _long) {
        ///Database/Barangay/getBarName()/Requests/Pending
        String barname = String.valueOf(user_barangayName.getText());
        String username = String.valueOf(user_username_editText.getText());
        String path = "/Barangay/"+ barname +"/Requests/Pending"+"/" + username;
        Log.d("PATH CHECK~~", path);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Create a new entry with lat and long values
                Map<String, Object> locationData = new HashMap<>();
                locationData.put("lat", _lat);
                locationData.put("long", _long);

                // Push the data to the "Pending" node
                reference.setValue(locationData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            // There was an error adding the data
                            Log.e("Firebase", "Data could not be saved. " + databaseError.getMessage());
                        } else {
                            // Data added successfully
                            Log.d("Firebase", "Data saved successfully.");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}