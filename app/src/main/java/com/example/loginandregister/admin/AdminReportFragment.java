package com.example.loginandregister.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginandregister.Login;
import com.example.loginandregister.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminReportFragment extends Fragment {
    private Button buttonLogout;
    private Button reportbtn;
    private TextView buttonReport, barangay;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DatabaseReference iotdatareference;
    private SharedPreferences sharedPreferences;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private String currentDate = sdf.format(new Date());

    private String barrangayName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_report, container, false);

        // Initialize SharedPreferences for Location Permission
        sharedPreferences = getActivity().getSharedPreferences("LocationPermission", Context.MODE_PRIVATE);

        reportbtn = view.findViewById(R.id.btn_report);
        buttonLogout = view.findViewById(R.id.btn_logout);
        barangay = view.findViewById(R.id.barangay);



        SharedPreferences preferences2 = getActivity().getSharedPreferences("AdminHomeFragment", Context.MODE_PRIVATE);
        String username = preferences2.getString("adminFragment","");

        reference = database.getReference("Database").child("collectors").child(username);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String barName = snapshot.child("barName").getValue(String.class);
                barrangayName = barName;
                barangay.setText("Barangay " + barName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Set the button logout click listener
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearLocationPermissionStatus();
                Intent intent = new Intent(getActivity(), Login.class);
                showLogoutConfirmationDialog();
            }
        });

        //set the button report

        reportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> iotdatastring = new ArrayList<>();
                iotdatareference =  FirebaseDatabase.getInstance().getReference("/Database/Barangay/Looc/Bins");
                iotdatareference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        iotdatastring.clear();
                            for(DataSnapshot snapshot1: snapshot.getChildren())
                            {
                                String binName = snapshot1.getKey();
                                iotdatastring.add(binName);
                                //iotdatastring.add(snapshot.getValue().toString()); //this shit gets all the data under the referenced path in firebase
                            }
                        Log.d("FirebaseData", String.valueOf(iotdatastring));
                        try {
                            createPdf(barrangayName, currentDate, iotdatastring);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout for the dialog content
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout_confirmation, null);
        builder.setView(dialogView);

        // Set the background and text color for the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here (e.g., start LoginActivity)
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
                ((AdminMainActivity) getActivity()).setOnlineStatus(false);
                alertDialog.dismiss(); // Close the dialog after clicking "Yes"
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Close the dialog after clicking "No"
            }
        });
        alertDialog.show();
    }

    private void clearLocationPermissionStatus() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("location_permission_granted", false);
        editor.apply();
    }

    // Check if location permission needs to be requested
    private boolean shouldRequestLocationPermission() {
        // Check the location permission status in SharedPreferences
        return !sharedPreferences.getBoolean("location_permission_granted", false);
    }

    // Method to request location permission
    private void requestLocationPermission() {
        if (shouldRequestLocationPermission()) {
            // Request location permission
            // Your existing code for requesting location permission
        }
    }

    //createpdf
    private void createPdf(String barName, String currentDate, ArrayList iotdatastring ) throws FileNotFoundException {
        String barangayName = barName;
        String date = currentDate;
        ArrayList binsList = iotdatastring;
        //lets create a WasteWatchReports directory to hold all the reports
        File directory = new File(this.getActivity().getExternalFilesDir("/WasteWatchReports").getPath());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfDocument mypdfdoc = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint(); //for the lines brov
        PdfDocument.PageInfo mypageinfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
        PdfDocument.Page myPage = mypdfdoc.startPage(mypageinfo);
        Canvas canvas = myPage.getCanvas();

        paint.setTextSize(14f);
        paint.setColor(Color.rgb(0,50,250));
        canvas.drawText("WEEKLY REPORT", 20, 40, paint);

        paint.setTextSize(5.5f);
        canvas.drawText("Waste Watch - Garbage Management System", 20, 47, paint);
        canvas.drawText("Barangay: "+barangayName, 20, 65, paint);
        canvas.drawText("Date:     "+date, 20, 75, paint);

        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{2,2}, 0));
        forLinePaint.setStrokeWidth(1);
        forLinePaint.setColor(Color.rgb(0, 50, 250));
        canvas.drawLine(20, 85, 230, 85, forLinePaint);

        canvas.drawText("Bin fill level Report ", 20, 92, paint);
        //make the days
        canvas.drawText("MON", 67, 109, paint);
        canvas.drawText("TUE", 87, 109, paint);
        canvas.drawText("WED", 107, 109, paint);
        canvas.drawText("THU", 127, 109, paint);
        canvas.drawText("FRI", 147, 109, paint);
        canvas.drawText("SAT", 167, 109, paint);
        canvas.drawText("SUN", 187, 109, paint);
        canvas.drawText("Average", 207, 109, paint);


        //data from the bins
        double lastspace;
        for (int i = 0; i < binsList.size(); i++) {
            String binName = (String) binsList.get(i);
            canvas.drawText("" + binName, 20, 126 + i * 17, paint);
            lastspace = 126 + i * 17;
        }


        mypdfdoc.finishPage(myPage);
        File file = new File(directory, "" + currentDate + "_Weekly_Report.pdf");

        try {
            mypdfdoc.writeTo(new FileOutputStream(file));
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(), "PDF saved", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            mypdfdoc.close();
        }
    }

}