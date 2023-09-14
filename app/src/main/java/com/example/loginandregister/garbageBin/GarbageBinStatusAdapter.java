package com.example.loginandregister.garbageBin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;

import java.util.List;

public class GarbageBinStatusAdapter extends RecyclerView.Adapter<GarbageBinStatusAdapter.ViewHolder> {

    private List<GarbageBinStatusModel> binStatusModel;

    private GarbageBinStatus fragmentGarbageBinStatus;

    public GarbageBinStatusAdapter(GarbageBinStatus fragmentGarbageBinStatus){
        this.fragmentGarbageBinStatus = fragmentGarbageBinStatus;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bin_layout, parent, false);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        GarbageBinStatusModel item = binStatusModel.get(position);
        holder.bin.setText(item.getBin());
        holder.place.setText(item.getPlace());

        int fillLevel = item.getStatus();

        int fillLevelImageResource = 0;
        if (fillLevel == 0) {
            fillLevelImageResource = R.drawable.ic_error; // empty
        } else if (fillLevel >= 1 && fillLevel <= 49) {
            fillLevelImageResource = R.drawable.ic_error; // half-full
        } else {
            fillLevelImageResource = R.drawable.ic_error; // full
        }

        holder.fillLevel.setImageResource(fillLevelImageResource);
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
        TextView bin, place;
        ImageView fillLevel;

        ViewHolder(View view){
            super(view);
            bin = view.findViewById(R.id.bin);
            place = view.findViewById(R.id.place);
            fillLevel = view.findViewById(R.id.fillLevel);
        }
    }
}
