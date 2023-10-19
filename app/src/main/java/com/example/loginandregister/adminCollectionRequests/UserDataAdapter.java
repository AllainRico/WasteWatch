package com.example.loginandregister.adminCollectionRequests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;

import java.util.List;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder> {
    private List<UserDataModel> userDataList;

    public UserDataAdapter(List<UserDataModel> userDataList) {
        this.userDataList = userDataList;
    }

    @NonNull
    @Override
    public UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_item, parent, false);
        return new UserDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDataViewHolder holder, int position) {
        UserDataModel userData = userDataList.get(position);
        holder.bind(userData);
    }

    @Override
    public int getItemCount() {
        return userDataList.size();
    }

    public static class UserDataViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, latTextView, lonTextView;

        public UserDataViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            latTextView = itemView.findViewById(R.id.latTextView);
            lonTextView = itemView.findViewById(R.id.lonTextView);
        }

        public void bind(UserDataModel userData) {
            usernameTextView.setText(userData.getUsername());
            latTextView.setText(String.valueOf(userData.getLat()));
            lonTextView.setText(String.valueOf(userData.getLon()));
        }
    }
}
