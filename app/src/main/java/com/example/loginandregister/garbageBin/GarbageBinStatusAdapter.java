package com.example.loginandregister.garbageBin;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GarbageBinStatusAdapter extends RecyclerView.Adapter<GarbageBinStatusAdapter.ViewHolder> {
    private Button collectBinButton;
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
        holder.bin.setText(item.getBin());

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
        //butang ta collection size ari para mahibaloan unsa sunod nga collection
        // Add a new entry to the "Collection History"
        binRef.child("Collection History").child("Collection1").setValue(currentTime)
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

        ViewHolder(View view){
            super(view);
            bin = view.findViewById(R.id.bin);
            fillLevel = view.findViewById(R.id.fillLevel);
            collectBinButton = view.findViewById(R.id.collectBinButton);

            collectBinButton.setVisibility(View.GONE);

        }
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