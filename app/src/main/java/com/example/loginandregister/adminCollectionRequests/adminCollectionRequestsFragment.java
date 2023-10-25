package com.example.loginandregister.adminCollectionRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adminCollectionRequestsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserDataAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_admin_collection_requests, container, false);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/Database/Barangay/Looc/Requests/Pending");

        ArrayList<UserDataModel> userDataList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDataAdapter(userDataList);
        recyclerView.setAdapter(adapter);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.getKey();

                    double lat = snapshot.child("lat").getValue(Double.class);
                    double lon = snapshot.child("long").getValue(Double.class);

                    UserDataModel userData = new UserDataModel(username, lat, lon);
                    userDataList.add(userData);
                }

                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });

        return view;
    }//onCreateView

}//adminCollectionRequestsFragment