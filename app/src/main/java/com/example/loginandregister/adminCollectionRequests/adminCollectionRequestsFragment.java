package com.example.loginandregister.adminCollectionRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.example.loginandregister.admin.AdminMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adminCollectionRequestsFragment extends Fragment implements UserDataAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private UserDataAdapter adapter;
    ArrayList<UserDataModel> userDataList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_admin_collection_requests, container, false);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/Database/Barangay/Looc/Requests/Pending");



        recyclerView = view.findViewById(R.id.requestsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserDataAdapter(userDataList);

        adapter.setOnItemClickListener(this);

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


    @Override
    public void onItemClick(int position) {
        UserDataModel clickedItem = userDataList.get(position);
        //Toast.makeText(getContext(), "Clicked: " + clickedItem.getUsername(), Toast.LENGTH_SHORT).show();

        AdminMapFragment adminMapFragment = new AdminMapFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, adminMapFragment)
                .addToBackStack(null)
                .commit();

    }//onItemClick
}//adminCollectionRequestsFragment