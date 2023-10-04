package com.example.loginandregister.garbageBin;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginandregister.R;

import java.util.ArrayList;
import java.util.List;

public class GarbageBinStatusAdapter extends RecyclerView.Adapter<GarbageBinStatusAdapter.ViewHolder> {
    private List<GarbageBinStatusModel> binStatusModel;

    public GarbageBinStatusAdapter(){
        this.binStatusModel = new ArrayList<>();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bin_layout, parent, false);

        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        GarbageBinStatusModel item = binStatusModel.get(position);
        holder.bin.setText(item.getBin());

        int fillLevel = item.getFillLevel();

        int fillLevelImageResource;

        if (fillLevel == 0) {
            fillLevelImageResource = R.drawable.empty;
        } else if (fillLevel >= 1 && fillLevel <= 49) {
            fillLevelImageResource = R.drawable.half;
        } else if (fillLevel >= 50 && fillLevel <= 100){
            fillLevelImageResource = R.drawable.full;
        } else {
            fillLevelImageResource = R.drawable.full;
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
        TextView bin;
        ImageView fillLevel;

        ViewHolder(View view){
            super(view);
            bin = view.findViewById(R.id.bin);
            fillLevel = view.findViewById(R.id.fillLevel);
        }
    }
}