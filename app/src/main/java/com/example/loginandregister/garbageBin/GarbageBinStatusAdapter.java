package com.example.loginandregister.garbageBin;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginandregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GarbageBinStatusAdapter extends RecyclerView.Adapter<GarbageBinStatusAdapter.ViewHolder> {
    private Button collectBinButton;
    private static Geocoder geocoder;
    private List<GarbageBinStatusModel> binStatusModel;
    private OnItemLongClickListener longClickListener;
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public GarbageBinStatusAdapter(){
        this.binStatusModel = new ArrayList<>();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bin_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position){
        GarbageBinStatusModel item = binStatusModel.get(position);
        holder.bin.setText("Bin Name: " + item.getBin());

        int fillLevel = binStatusModel.get(holder.getAdapterPosition()).getFillLevel();

        int fillLevelImageResource;

        if (fillLevel == 0) {
            fillLevelImageResource = R.drawable.empty;
        } else if (fillLevel >= 1 && fillLevel <= 49) {
            fillLevelImageResource = R.drawable.half;
        } else if (fillLevel >= 50 && fillLevel <= 100){
            fillLevelImageResource = R.drawable.full;
            holder.collectBinButton.setVisibility(View.VISIBLE);
            holder.collectBinButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call a method to update the isCollected value in Firebase
                    updateIsCollectedValue(position);
                }
            });

        } else {
            fillLevelImageResource = R.drawable.full;
        }

        holder.fillLevel.setImageResource(fillLevelImageResource);

        //for button visible or gone
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, holder.itemView.getResources().getDisplayMetrics()), // Width
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, holder.itemView.getResources().getDisplayMetrics())  // Height
        );

        if (holder.collectBinButton.getVisibility() == View.VISIBLE) {
            params.addRule(RelativeLayout.START_OF, holder.collectBinButton.getId());
            params.setMarginEnd((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, holder.itemView.getResources().getDisplayMetrics())); // Add margin
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        }

        holder.fillLevel.setLayoutParams(params);


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(position);
                    return true;
                }
                return false;
            }
        });


        String address = "District Camia";
        holder.place.setText("Location: " + address);
    }
    private void updateIsCollectedValue(int position) {
        String binName = binStatusModel.get(position).getBin();

        // Update the isCollected value in Firebase
        DatabaseReference binRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Barangay")
                .child(GarbageBinStatus.barName)  // Assuming barName is a class variable
                .child("Bins")
                .child(binName)
                .child(String.valueOf(getYear()))
                .child(String.valueOf(getMonth()))
                .child(String.valueOf(getDate()));

        // Assuming isCollected is a boolean value in your data structure
        binRef.child("isCollected").setValue(true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update successful
                            Log.d("isCollectedUpdate" , "done");

                            addEntryToCollectionHistory(binRef);
                        } else {
                            // Update failed
                            Log.d("isCollectedUpdate" , "failed");
                        }
                    }
                });

    }

    private void addEntryToCollectionHistory(DatabaseReference binRef) {
        String currentTime = (String) currentTime();

        // Add a new entry to the "Collection History"
        binRef.child("Collection History").child(currentTime).setValue("N/A")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Entry added successfully
                            Log.d("CollectionHistory", "Entry added successfully");
                        } else {
                            // Entry addition failed
                            Log.d("CollectionHistory", "Entry addition failed");
                        }
                    }
                });
    }

    private Object currentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Get the current date and time
        Date currentTime = new Date(System.currentTimeMillis());

        // Format the date and time using the specified format
        return dateFormat.format(currentTime);
    }

    @Override
    public int getItemCount() {
        return binStatusModel.size();
    }

    public void setBin(List<GarbageBinStatusModel> binStatusModel){
        this.binStatusModel = binStatusModel;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView bin;
        ImageView fillLevel;
        Button collectBinButton;
        TextView place;

        ViewHolder(View view){
            super(view);
            bin = view.findViewById(R.id.bin);
            fillLevel = view.findViewById(R.id.fillLevel);
            collectBinButton = view.findViewById(R.id.collectBinButton);
            place = view.findViewById(R.id.place);
            collectBinButton.setVisibility(View.GONE);
        }
    }

    private String getAddress(double lat, double lon) {
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

    private int getYear(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        return year;
    }

    private int getMonth(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;

        return month;
    }

    private int getDate(){
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DATE);
        return date;
    }
}