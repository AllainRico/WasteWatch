package com.example.loginandregister.garbageBin;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginandregister.R;

import java.util.ArrayList;
import java.util.List;

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
}