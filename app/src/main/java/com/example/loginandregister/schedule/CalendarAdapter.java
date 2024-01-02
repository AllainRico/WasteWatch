package com.example.loginandregister.schedule;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final ArrayList<String> daysInMonth;
    private final ArrayList<String> daysWithSchedule;
    private final OnItemListener onItemListener;
    private LocalDate selectedDate;

    public CalendarAdapter(OnItemListener onItemListener, ArrayList<String> daysInMonth, ArrayList<String> daysWithSchedule, LocalDate selectedDate) {
        this.onItemListener = onItemListener;
        this.daysInMonth = daysInMonth;
        this.daysWithSchedule = daysWithSchedule;
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysInMonth.get(position);
        holder.dayTextView.setText(day);

        holder.itemView.setBackgroundColor(Color.WHITE);

        if (!day.isEmpty() && !day.equals("null")) {
            LocalDate currentDate = selectedDate.withDayOfMonth(1);
            int currentYear = currentDate.getYear();
            int currentMonth = currentDate.getMonthValue();

            LocalDate clickedDate = selectedDate.withDayOfMonth(Integer.parseInt(day));
            int clickedYear = clickedDate.getYear();
            int clickedMonth = clickedDate.getMonthValue();

            if (currentYear == clickedYear && currentMonth == clickedMonth) {
                if (containsDayWithSchedule(day)) {
                    holder.itemView.setBackgroundColor(Color.GREEN);
                }
            }
        }
    }

    private boolean containsDayWithSchedule(String day) {
        if (!day.isEmpty() && !day.equals("null")) {
            return daysWithSchedule.contains(day);
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }

    public void updateData(ArrayList<String> daysInMonth, ArrayList<String> daysWithSchedule, LocalDate selectedDate) {
        this.daysInMonth.clear();
        this.daysInMonth.addAll(daysInMonth);
        this.daysWithSchedule.clear();
        this.daysWithSchedule.addAll(daysWithSchedule);
        this.selectedDate = selectedDate;
        notifyDataSetChanged();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dayTextView;
        final OnItemListener onItemListener;

        public CalendarViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.cellDayText);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(getAdapterPosition(), dayTextView.getText().toString());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
