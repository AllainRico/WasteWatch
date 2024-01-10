package com.example.loginandregister.collectorResidentVerification;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private List<ResidentModel> residentList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference;

    public ResidentAdapter(List<ResidentModel> residentList, DatabaseReference reference) {
        this.residentList = residentList;
        this.databaseReference = database.getReference().child("users");
    }

    @NonNull
    @Override
    public ResidentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.verify_resident_item, parent, false);
        return new ResidentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidentViewHolder holder, int position) {
        ResidentModel resident = residentList.get(position);

        holder.firstNameTextView.setText(resident.getFirstName());
        holder.lastNameTextView.setText(resident.getLastName());

        // Set click listeners for verify and deny buttons
        holder.verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleVerification(resident);
                Toast.makeText(v.getContext(),"Verified "+resident.getFirstName()+" "+resident.getLastName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDeny(resident);
                Toast.makeText(v.getContext(),"Denied "+resident.getFirstName()+" "+resident.getLastName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleVerification(ResidentModel resident) {
        databaseReference.child(resident.getUsername()).child("isVerify").setValue(true);
        residentList.remove(resident);
        notifyDataSetChanged();
    }

    private void handleDeny(ResidentModel resident) {
        databaseReference.child(resident.getUsername()).removeValue();
        residentList.remove(resident);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;
        Button verifyButton;
        Button denyButton;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            verifyButton = itemView.findViewById(R.id.verify_btn);
            denyButton = itemView.findViewById(R.id.deny_btn);
        }
    }
}