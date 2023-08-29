        package com.example.loginandregister;

        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;
        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.time.Duration;
        import java.time.LocalDate;
        import java.time.LocalTime;
        import java.time.YearMonth;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.Locale;

        public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener{

            private TextView monthYearText;
            private RecyclerView calendarRecyclerView;
            private LocalDate selectedDate;
            private TextView dayTextView;
            private TextView barangayTextView;
            private TextView timeTextView;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_schedule, container, false);

                barangayTextView = view.findViewById(R.id.barangay);
                timeTextView = view.findViewById(R.id.time);

                initWidgets(view);
                selectedDate = LocalDate.now();
                setMonthView();

                dayTextView = view.findViewById(R.id.day); // Initialize dayTextView
                updateDayTextView();

                Button btnPrevious = view.findViewById(R.id.btnPrevious);
                Button btnNext = view.findViewById(R.id.btnNext);
                dayTextView = view.findViewById(R.id.day);
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

                reference = database.getReference("Database");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String barName = snapshot.child("users").child(username).child("barName").getValue(String.class);
                        String district = snapshot.child("users").child(username).child("district").getValue(String.class);
                        String day = dayTextView.getText().toString(); // You can set day here based on your logic

                        // Update UI based on retrieved data
                        barangayTextView.setText(district+", "+barName + " Barangay Hall");
                        if (barName.equals("Looc")) {
                            String timeString = snapshot.child("Barangay").child("Looc").child("Schedule").child(day).getValue(String.class);
                            timeTextView.setText("Starts at: " + timeString);

//                            // Schedule Notification
//                            ScheduleNotificationManager notificationManager = new ScheduleNotificationManager(getContext());
//
//                            // Parse the time string into a LocalTime object
//                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);
//                            LocalTime collectionTime = LocalTime.parse(timeString, formatter);
//
//                            // Calculate the time until collection in milliseconds
//                            LocalTime currentTime = LocalTime.now();
//                            long timeUntilCollection = Duration.between(currentTime, collectionTime).toMillis();
//
//                            // Create an intent for the BroadcastReceiver that will handle the notification
//                            Intent alarmIntent = new Intent(getContext(), NotificationReceiver.class);
//                            alarmIntent.putExtra("title", "Garbage Collection");
//                            alarmIntent.putExtra("message", "Make sure your garbage is ready to collect!");
//
//                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//
//                            // Schedule the notification
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeUntilCollection, pendingIntent);
//                            } else {
//                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeUntilCollection, pendingIntent);
//                            }
                        }
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
            }

            private void setMonthView() {
                monthYearText.setText(monthYearFromDate((selectedDate)));
                ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
                CalendarAdapter calendarAdapter = new CalendarAdapter(this, daysInMonth);
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
                        daysInMonthArray.add(String.valueOf(i));
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

                    LocalDate clickedDate = selectedDate.withDayOfMonth(Integer.parseInt(dayText));
                    String formattedDate = formatDateForDisplay(clickedDate);
                    dayTextView.setText(formattedDate);

                    String message = "Selected Date: " + formattedDate;
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences2 = getActivity().getSharedPreferences("ProfileFragment", Context.MODE_PRIVATE);
                    String username = preferences2.getString("ProfileUsername","");

                    reference = database.getReference("Database");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String barName = snapshot.child("users").child(username).child("barName").getValue(String.class);
                            String day = dayTextView.getText().toString();
                            if(barName.equals("Basak")){
                                String time = snapshot.child("Barangay").child("Basak").child("Schedule").child(day).getValue(String.class);
                                timeTextView.setText("Starts at: " + time);
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
        }




