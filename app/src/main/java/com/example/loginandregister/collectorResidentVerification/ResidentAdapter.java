package com.example.loginandregister.collectorResidentVerification;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private List<ResidentModel> residentList;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Activity activity;

    private DatabaseReference databaseReference;

    public ResidentAdapter(List<ResidentModel> residentList, DatabaseReference reference, Activity activity) {
        this.residentList = residentList;
        this.databaseReference = database.getReference().child("users");
        this.activity = activity;
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
                showConfirmationDialog("Are you sure you want to verify "+ resident.getFirstName() + " " + resident.getLastName() + "?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            handleVerification(resident);
                            Toast.makeText(v.getContext(), "Verified " + resident.getFirstName() + " " + resident.getLastName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        holder.denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog("Are you sure you want to deny "+ resident.getFirstName() + " " + resident.getLastName() + "?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            handleDeny(resident);
                            Toast.makeText(v.getContext(), "Denied " + resident.getFirstName() + " " + resident.getLastName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void showConfirmationDialog(String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity); // Pass your activity reference
        builder.setMessage(message)
                .setPositiveButton("Yes", onClickListener)
                .setNegativeButton("No", onClickListener)
                .show();
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