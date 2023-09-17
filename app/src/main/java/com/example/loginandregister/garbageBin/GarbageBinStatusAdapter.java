package com.example.loginandregister.garbageBin;

import android.os.Bundle;
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

        int fillLevel = item.getStatus(); //fill Level to image

        int fillLevelImageResource;

        if (fillLevel == 0) {
            fillLevelImageResource = R.drawable.empty;
        } else if (fillLevel >= 1 && fillLevel <= 49) {
            fillLevelImageResource = R.drawable.half;
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
    public void editItem(int position) {
        GarbageBinStatusModel item = binStatusModel.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("Bin", item.getBin());
        bundle.putString("Place", item.getPlace());
        bundle.putInt("Status", item.getStatus());
        AddNewGarbageBin fragment = new AddNewGarbageBin();
        fragment.setArguments(bundle);
        fragment.show(fragment.getParentFragmentManager(), AddNewGarbageBin.TAG);
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
