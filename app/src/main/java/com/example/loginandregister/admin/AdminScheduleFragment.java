package com.example.loginandregister.admin;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandregister.schedule.CalendarAdapter;
import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdminScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private TextView dayTextView;
    private ArrayList<String> daysWithSchedule = new ArrayList<>();
    private TextView barangayTextView;
    private EditText timeTextView;
    private Button btnPrevious, btnNext;
    //private Button btnEditTime;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    private ImageView editTimeImageView;
    private LocalDate selectedDate;
    private Map<LocalDate, String> dayTimeMap = new HashMap<>();
    private boolean isEditMode = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_schedule, container, false);

        initWidgets(view);

        selectedDate = LocalDate.now();
        setMonthView();

        updateDayTextView();

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonth(v);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth(v);
            }
        });


        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment","");

        reference = database.getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String day = dayTextView.getText().toString();
                String barName = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                String timeString = snapshot.child("Barangay").child(barName).child("Schedule").child(day).getValue(String.class);

                daysWithSchedule.clear(); // Clear existing data
                for (DataSnapshot scheduleSnapshot : snapshot.child("Barangay").child(barName).child("Schedule").getChildren()) {
                    String scheduleDay = scheduleSnapshot.getKey();
                    daysWithSchedule.add(scheduleDay);
                }

                barangayTextView.setText(barName+" Barangay Hall cc");
                if (timeString != null && !timeString.isEmpty()) {
                    timeTextView.setText("Starts at: " + timeString);
                } else {
                    timeTextView.setText("No Schedule");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        timeTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    // Toggle the edit mode when the Enter key is pressed
                    toggleEditMode();

                    return true;
                }
                return false;
            }
        });

        editTimeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if the keyboard is open and the touch event is outside of the EditText
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isEditMode && timeTextView.isEnabled()) {
                        Rect outRect = new Rect();
                        timeTextView.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            // Hide the keyboard and exit edit mode
                            hideKeyboard();
                            toggleEditMode();
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;

        if (isEditMode) {
            // Check if the selected date is today or a future date
            if (!selectedDate.isBefore(LocalDate.now())) {
                // Enter edit mode
                timeTextView.setEnabled(true);
                timeTextView.requestFocus();
                // Show the keyboard when entering edit mode
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(timeTextView, InputMethodManager.SHOW_IMPLICIT);

                editTimeImageView.setImageResource(R.drawable.ic_check); // Change to check icon

                // Show the time picker when entering edit mode
                showTimePickerDialog();
            } else {
                // If the selected date is in the past, show a message and disable editing
                Toast.makeText(getActivity(), "Cannot edit past dates", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Exit edit mode
            timeTextView.setEnabled(false);
            editTimeImageView.setImageResource(R.drawable.ic_edit); // Change back to edit icon
            if (!timeTextView.getText().toString().isEmpty()) {
                dayTimeMap.put(selectedDate, timeTextView.getText().toString());

                reference = database.getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
                        String username = preferences2.getString("adminFragment","");
                        String day = dayTextView.getText().toString();
                        String time = timeTextView.getText().toString();
                        String barName = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                        reference.child("Barangay").child(barName).child("Schedule").child(day).setValue(time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    // Format the selected time to hh:mm a (12-hour format with AM/PM)
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    String formattedTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.getTime());
                    timeTextView.setText(formattedTime);
                },
                // Set the current time as the default selection
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false // Set to true for 24-hour format
        );
        timePickerDialog.show();
    }


    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecycleView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        timeTextView = view.findViewById(R.id.time);
        barangayTextView = view.findViewById(R.id.barangay);
        dayTextView = view.findViewById(R.id.day);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        editTimeImageView = view.findViewById(R.id.edit_time);
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate((selectedDate)));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(this, daysInMonth, daysWithSchedule, selectedDate);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
        return date.format(formatter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);

        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        // Calculate the day number for the first day of the calendar grid
        int startDay = 1 - dayOfWeek;

        for (int i = startDay; i <= daysInMonth; i++) {
            if (i <= 0 || i > daysInMonth) {
                daysInMonthArray.add("");
            } else {
                LocalDate day = selectedDate.withDayOfMonth(i);
                String savedTime = dayTimeMap.get(day);
                if (savedTime != null) {
                    daysInMonthArray.add(i + "\n" + savedTime);
                } else {
                    daysInMonthArray.add(String.valueOf(i));
                }
            }
        }

        // Add extra empty cells at the end to fill the calendar grid
        while (daysInMonthArray.size() < 42) {
            daysInMonthArray.add("");
        }

        return daysInMonthArray;
    }

    public void previousMonth(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonth(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (!dayText.isEmpty() && !dayText.equals("null")) {
            int clickedDay = Integer.parseInt(dayText);
            selectedDate = selectedDate.withDayOfMonth(clickedDay); // Update the selected date

            String formattedDate = formatDateForDisplay(selectedDate);
            dayTextView.setText(formattedDate);

            String message = "Selected Date: " + formattedDate;
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

            String savedTime = dayTimeMap.get(selectedDate);
            timeTextView.setText(savedTime);
            timeTextView.setEnabled(false); // Disable the timeTextView for previous dates

            // Set the edit mode based on whether there's a saved time
            isEditMode = savedTime != null;
            editTimeImageView.setImageResource(isEditMode ? R.drawable.ic_check : R.drawable.ic_edit);


            SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
            String username = preferences2.getString("adminFragment","");

            reference = database.getReference();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String day = dayTextView.getText().toString();
                    String barName = snapshot.child("collectors").child(username).child("barName").getValue(String.class);
                    String timeString = snapshot.child("Barangay").child(barName).child("Schedule").child(day).getValue(String.class);
                    barangayTextView.setText(barName+" Barangay Hall cc");
                    if (timeString != null && !timeString.isEmpty()) {
                        timeTextView.setText("Starts at: " + timeString);
                    } else {
                        timeTextView.setText("No Schedule");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void updateDayTextView() {
        String currentDate = formatDateForDisplay(LocalDate.now());
        dayTextView.setText(currentDate);
    }

    private String formatDateForDisplay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault());
        return date.format(formatter);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(timeTextView.getWindowToken(), 0);
    }
}