package com.example.loginandregister.adminCollectionRequests;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder> {

    private static Geocoder geocoder;

    private List<UserDataModel> userDataList;

    private OnItemClickListener mListener;

    public static void initGeocoder(Context context) {
        if (geocoder == null) {
            geocoder = new Geocoder(context, Locale.getDefault());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int postition);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public UserDataAdapter(List<UserDataModel> userDataList, Geocoder geocoder) {
        this.userDataList = userDataList;
        this.geocoder = geocoder;
    }

    @NonNull
    @Override
    public UserDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_data_item, parent, false);
        return new UserDataViewHolder(view, mListener);
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
        TextView usernameTextView, addressTextView;

        public UserDataViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);

            //onclick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);

                        }//if no position
                    }//if listener !=null
                }
            });
        }

        public void bind(UserDataModel userData) {
            usernameTextView.setText(userData.getUsername());

            // Convert latitude and longitude to address
            String address = getAddress(userData.getLat(), userData.getLon());
            addressTextView.setText(address);
        }

        public static String getAddress(double lat, double lon) {
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append(" ");
                    }
                    return sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Address not available";
        }
    }
}
