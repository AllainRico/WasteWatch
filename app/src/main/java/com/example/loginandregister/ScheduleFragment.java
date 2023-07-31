        package com.example.loginandregister;

        import android.os.Bundle;

        import androidx.fragment.app.Fragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.time.LocalDate;
        import java.time.YearMonth;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.util.Locale;

        public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener{

            private TextView monthYearText;
            private RecyclerView calendarRecyclerView;
            private LocalDate selectedDate;
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                View view = inflater.inflate(R.layout.fragment_schedule, container, false);

                initWidgets(view);
                selectedDate = LocalDate.now();
                setMonthView();

                Button btnPrevious = view.findViewById(R.id.btnPrevious);
                Button btnNext = view.findViewById(R.id.btnNext);
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

                return view;
            }

            private void initWidgets(View view) {
                calendarRecyclerView = view.findViewById(R.id.caledarRecycleView);
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
                    String message = "Selected Date: " + dayText + " " + monthYearFromDate(selectedDate);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }

        }