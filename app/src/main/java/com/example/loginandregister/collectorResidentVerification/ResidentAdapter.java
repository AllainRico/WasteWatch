package com.example.loginandregister.collectorResidentVerification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;

import java.util.List;

public class ResidentAdapter extends RecyclerView.Adapter<ResidentAdapter.ResidentViewHolder> {

    private List<ResidentModel> residentList;

    public ResidentAdapter(List<ResidentModel> residentList) {
        this.residentList = residentList;
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

    }

    @Override
    public int getItemCount() {
        return residentList.size();
    }

    public static class ResidentViewHolder extends RecyclerView.ViewHolder {
        TextView firstNameTextView;
        TextView lastNameTextView;

        public ResidentViewHolder(@NonNull View itemView) {
            super(itemView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
        }
    }
}