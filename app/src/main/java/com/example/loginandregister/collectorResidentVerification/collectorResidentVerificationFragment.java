package com.example.loginandregister.collectorResidentVerification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginandregister.R;
import com.example.loginandregister.adminCollectionRequests.UserDataModel;
import com.example.loginandregister.garbageBin.GarbageBinStatusAdapter;
import com.example.loginandregister.garbageBin.GarbageBinStatusModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class collectorResidentVerificationFragment extends Fragment {

    private Button backButton;
    public static String barName;
    private RecyclerView verifyListRecyclerView;
    private ResidentAdapter residentAdapter;
    private List<ResidentModel> residentList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collector_resident_verification, container, false);

        initWidgets(view);

        residentList = new ArrayList<>();
        verifyListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        residentAdapter = new ResidentAdapter(residentList);
        verifyListRecyclerView.setAdapter(residentAdapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("users");

        Query query = reference.orderByChild("barName").equalTo(barName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ResidentModel> residents = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String firstName = userSnapshot.child("firstName").getValue(String.class);
                    String lastName = userSnapshot.child("lastName").getValue(String.class);

                    ResidentModel resident = new ResidentModel(firstName, lastName);
                    residents.add(resident);
                }

                residentList.clear();
                residentList.addAll(residents);

                residentAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initWidgets(View view){
        backButton = view.findViewById(R.id.backButton);
        verifyListRecyclerView = view.findViewById(R.id.verifyListRecyclerView);
    }
}