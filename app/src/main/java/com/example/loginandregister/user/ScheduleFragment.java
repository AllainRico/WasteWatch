        package com.example.loginandregister.user;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.loginandregister.schedule.CalendarAdapter;
        import com.example.loginandregister.R;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.time.DayOfWeek;
        import java.time.LocalDate;
        import java.time.YearMonth;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.Locale;

        public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {

            private TextView monthYearText;
            private Button btnPrevious;
            private Button btnNext;
            private RecyclerView calendarRecyclerView;
            private LocalDate selectedDate;
            private TextView dayTextView;
            private ArrayList<String> daysWithSchedule = new ArrayList<>();
            private TextView barangayTextView;
            private TextView timeTextView;
            private CalendarAdapter calendarAdapter;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_schedule, container, false);

                calendarAdapter = new CalendarAdapter(this, new ArrayList<>(), daysWithSchedule, selectedDate);
                selectedDate = LocalDate.now();

                initWidgets(view);
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


                SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                String username = preferences2.getString("ProfileUsername","");

                reference = database.getReference();
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String barName = snapshot.child("users").child(username).child("barName").getValue(String.class);
                        String district = snapshot.child("users").child(username).child("district").getValue(String.class);
                        String day = dayTextView.getText().toString();

                        daysWithSchedule.clear();

                        for (DataSnapshot scheduleSnapshot : snapshot.child("Barangay").child(barName).child("Schedule").getChildren()) {
                            String scheduleDay = scheduleSnapshot.getKey();

                            String[] dayParts = scheduleDay.split(", ");
                            if (dayParts.length >= 2) {
                                String[] dayNumberParts = dayParts[1].split(" ");
                                if (dayNumberParts.length >= 2) {
                                    daysWithSchedule.add(dayNumberParts[1]);
                                }
                            }
                        }

                        barangayTextView.setText(district + ", " + barName + " Barangay Hall");
                        String timeString = snapshot.child("Barangay").child(barName).child("Schedule").child(day).getValue(String.class);
                        if (timeString != null && !timeString.isEmpty()) {
                            timeTextView.setText("Starts at: " + timeString);
                        } else {
                            timeTextView.setText("No Schedule");
                        }

                        calendarAdapter.notifyDataSetChanged();

                        setMonthView();

                        Log.d("DaysWithSchedule", "Days with Schedule: " + daysWithSchedule.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return view;
            }

            private void initWidgets(View view) {
                calendarRecyclerView = view.findViewById(R.id.calendarRecycleView);
                monthYearText = view.findViewById(R.id.monthYearTV);
                barangayTextView = view.findViewById(R.id.barangay);
                timeTextView = view.findViewById(R.id.time);
                dayTextView = view.findViewById(R.id.day);
                btnPrevious = view.findViewById(R.id.btnPrevious);
                btnNext = view.findViewById(R.id.btnNext);
            }

            private void setMonthView() {
                monthYearText.setText(monthYearFromDate((selectedDate)));
                ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
                calendarAdapter = new CalendarAdapter(this, daysInMonth, daysWithSchedule, selectedDate);
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

                //This Calculates the day number for the first day of the calendar grid
                int startDay = DayOfWeek.from(firstOfMonth).getValue() % 7;

                for (int i = 1; i <= 42; i++) {
                    int dayNumber = i - startDay;
                    if (dayNumber >= 1 && dayNumber <= daysInMonth) {
                        daysInMonthArray.add(String.valueOf(dayNumber));
                    } else {
                        daysInMonthArray.add("");
                    }
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

                    LocalDate clickedDate = selectedDate.withDayOfMonth(Integer.parseInt(dayText));
                    String formattedDate = formatDateForDisplay(clickedDate);
                    dayTextView.setText(formattedDate);

                    String message = "Selected Date: " + formattedDate;
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                    String username = preferences2.getString("ProfileUsername","");

                    reference = database.getReference();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String barName = snapshot.child("users").child(username).child("barName").getValue(String.class);
                            String district = snapshot.child("users").child(username).child("district").getValue(String.class);
                            String day = dayTextView.getText().toString(); // You can set day here based on your logic

                            barangayTextView.setText(district + ", " + barName + " Barangay Hall");
                            String timeString = snapshot.child("Barangay").child(barName).child("Schedule").child(day).getValue(String.class);
                            if (timeString != null && !timeString.isEmpty()) {
                                timeTextView.setText("Starts at: " + timeString);
                            } else {
                                timeTextView.setText("No Schedule");
                            }

                            daysWithSchedule.clear();

                            // This updates daysWithSchedule based on the retrieved schedule data
                            for (DataSnapshot scheduleSnapshot : snapshot.child("Barangay").child(barName).child("Schedule").getChildren()) {
                                String scheduleDay = scheduleSnapshot.getKey();
                                daysWithSchedule.add(scheduleDay);
                            }

                            calendarAdapter.updateData(daysInMonthArray(selectedDate), daysWithSchedule, selectedDate);

                            setMonthView();
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
        }