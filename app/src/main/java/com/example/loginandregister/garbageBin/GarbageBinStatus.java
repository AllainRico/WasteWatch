package com.example.loginandregister.garbageBin;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loginandregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
public class GarbageBinStatus extends Fragment implements DialogCloseListener {

    private Button backButton;
    private FloatingActionButton addGarbageBin;
    private RecyclerView garbageBinRecyclerView;
    private GarbageBinStatusAdapter garbageBinAdapter;
    private List<GarbageBinStatusModel> garbageBinList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_garbage_bin_status, container, false);

        initWidgets(view);

        garbageBinList = new ArrayList<>();
        garbageBinRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        garbageBinAdapter = new GarbageBinStatusAdapter();
        garbageBinRecyclerView.setAdapter(garbageBinAdapter);

        reference = database.getReference("Database")
                .child("Barangay")
                .child("Looc")
                .child("Bins");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Use a HashMap to keep track of the latest date for each bin
                HashMap<String, String> latestBinDates = new HashMap<>();

                for (DataSnapshot binSnapshot : dataSnapshot.getChildren()) {
                    String binName = binSnapshot.getKey();
                    updateBinForCurrentDate(binName);
                    String latestDate = ""; // Initialize to an empty string

                    for (DataSnapshot yearSnapshot : binSnapshot.getChildren()) {
                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                            for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                if (daySnapshot.hasChild("FillLevel") &&
                                        daySnapshot.hasChild("Latitude") &&
                                        daySnapshot.hasChild("Longitude")) {
                                    String date = yearSnapshot.getKey() + monthSnapshot.getKey() + daySnapshot.getKey();

                                    // Check if this date is newer than the existing one (if any)
                                    if (date.compareTo(latestDate) > 0) {
                                        latestDate = date; // Update the latest date
                                    }
                                }
                            }
                        }
                    }

                    // Now, you have the latest date for this bin
                    latestBinDates.put(binName, latestDate);
                }

                // Create a new list to store the latest bin entries
                List<GarbageBinStatusModel> latestBinEntries = new ArrayList<>();

                // Iterate through the bins and get the latest entry for each bin
                for (DataSnapshot binSnapshot : dataSnapshot.getChildren()) {
                    String binName = binSnapshot.getKey();
                    String latestDate = latestBinDates.get(binName); // Get the latest date

                    for (DataSnapshot yearSnapshot : binSnapshot.getChildren()) {
                        for (DataSnapshot monthSnapshot : yearSnapshot.getChildren()) {
                            for (DataSnapshot daySnapshot : monthSnapshot.getChildren()) {
                                if (daySnapshot.hasChild("FillLevel") &&
                                        daySnapshot.hasChild("Latitude") &&
                                        daySnapshot.hasChild("Longitude")) {
                                    String date = yearSnapshot.getKey() + monthSnapshot.getKey() + daySnapshot.getKey();

                                    // Check if this entry corresponds to the latest date for the bin
                                    if (date.equals(latestDate)) {
                                        int fillLevel = daySnapshot.child("FillLevel").getValue(Integer.class);
                                        double latitude = daySnapshot.child("Latitude").getValue(Double.class);
                                        double longitude = daySnapshot.child("Longitude").getValue(Double.class);

                                        GarbageBinStatusModel model = new GarbageBinStatusModel();
                                        model.setBin(binName);
                                        model.setFillLevel(fillLevel);
                                        model.setLatitude(latitude);
                                        model.setLongitude(longitude);

                                        // Add the latest entry to the list
                                        latestBinEntries.add(model);
                                    }
                                }
                            }
                        }
                    }
                }

                // Clear the old data and set the new data in garbageBinList
                garbageBinList.clear();
                garbageBinList.addAll(latestBinEntries);

                garbageBinAdapter.setBin(garbageBinList);
                garbageBinAdapter.notifyDataSetChanged();
            }

                @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        addGarbageBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddBinDialog();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        garbageBinAdapter.setOnItemLongClickListener(new GarbageBinStatusAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showDeleteBinDialog(position);
            }
        });

        return view;
    }

    private void showAddBinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add New Bin");

        final EditText binNameInput = new EditText(requireContext());
        binNameInput.setHint("Enter Bin Name");
        binNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(binNameInput);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String binName = binNameInput.getText().toString().trim();

                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                String year = String.valueOf(currentYear); //setYear();
                String month = String.valueOf(currentMonth); //setMonth();
                String day = String.valueOf(currentDay); //setDay();

                HashMap<String, Object> bin = new HashMap<>();
                bin.put("FillLevel", 0); //getFillLevel();
                bin.put("Latitude", 0); //getLatitude();
                bin.put("Longitude", 0); //getLongitude();

                database.getReference("Database")
                        .child("Barangay")
                        .child("Looc")
                        .child("Bins")
                        .child(binName)
                        .child(year)
                        .child(month)
                        .child(day)
                        .updateChildren(bin);

                if (!binName.isEmpty()) {
                    GarbageBinStatusModel newBin = new GarbageBinStatusModel();
                    newBin.setBin(binName);
                    newBin.setFillLevel(0);

                    garbageBinList.add(newBin);
                    garbageBinAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Bin Name is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteBinDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete Bin");
        builder.setMessage("Are you sure you want to delete this bin?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d("DeleteBin", "Deleting bin at position: " + position);

                // Ensure that position is valid
                if (position >= 0 && position < garbageBinList.size()) {

                    GarbageBinStatusModel binToDelete = garbageBinList.get(position);
                    String binName = binToDelete.getBin();
                    Log.d("DeleteBin", "binName: " + binName);


                    deleteBinFromDatabase(binName, position);
                } else {

                    Log.e("DeleteBin", "Invalid position: " + position);
                    Toast.makeText(requireContext(), "Invalid position for deletion", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateBinForCurrentDate(final String binName) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        final String year = String.valueOf(currentYear);
        final String month = String.valueOf(currentMonth);
        final String day = String.valueOf(currentDay);

        DatabaseReference currentBinRef = database.getReference("Database")
                .child("Barangay")
                .child("Looc")
                .child("Bins")
                .child(binName)
                .child(year)
                .child(month)
                .child(day);

        currentBinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // If the bin for the current date doesn't exist, create it

                    // Get the data from the previous day (assuming it exists)
                    DatabaseReference previousDayRef = database.getReference("Database")
                            .child("Barangay")
                            .child("Looc")
                            .child("Bins")
                            .child(binName)
                            .child(year)
                            .child(month)
                            .child(String.valueOf(currentDay - 1)); // Get data from the previous day

                    previousDayRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot previousDaySnapshot) {
                            // Check if the previous day's data exists
                            if (previousDaySnapshot.exists()) {
                                // Get the data from the previous day
                                int fillLevel = previousDaySnapshot.child("FillLevel").getValue(Integer.class);
                                double latitude = previousDaySnapshot.child("Latitude").getValue(Double.class);
                                double longitude = previousDaySnapshot.child("Longitude").getValue(Double.class);

                                // Create the new entry for the current day and set it to the previous day's values
                                DatabaseReference binRef = currentBinRef;
                                binRef.child("FillLevel").setValue(fillLevel);
                                binRef.child("Latitude").setValue(latitude);
                                binRef.child("Longitude").setValue(longitude);
                            } else {
                                // If there is no data for the previous day, set default values
                                DatabaseReference binRef = currentBinRef;
                                binRef.child("FillLevel").setValue(0); // Set FillLevel as needed
                                binRef.child("Latitude").setValue(0.0); // Set Latitude as needed
                                binRef.child("Longitude").setValue(0.0); // Set Longitude as needed
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Failed to read database", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to read database", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void deleteBinFromDatabase(String binName, final int position) {
        if (position >= 0 && position < garbageBinList.size()) {
            DatabaseReference binRef = database.getReference("Database")
                    .child("Barangay")
                    .child("Looc")
                    .child("Bins")
                    .child(binName);

            binRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (position >= 0 && position < garbageBinList.size()) {
                            garbageBinList.remove(position);
                            garbageBinAdapter.notifyItemRemoved(position);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete bin from Firebase", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e("DeleteBin", "Invalid position: " + position);
            Toast.makeText(requireContext(), "Invalid position for deletion", Toast.LENGTH_SHORT).show();
        }
    }


    private void initWidgets(View view){
        backButton = view.findViewById(R.id.backButton);
        garbageBinRecyclerView = view.findViewById(R.id.garbageBinRecyclerView);
        addGarbageBin = view.findViewById(R.id.addGarbageBin);
    }
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        Collections.reverse(garbageBinList);
        garbageBinAdapter.setBin(garbageBinList);
        garbageBinAdapter.notifyDataSetChanged();
    }
}