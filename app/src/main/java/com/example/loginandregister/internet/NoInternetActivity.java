package com.example.loginandregister.internet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.loginandregister.R;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        Button retryButton = findViewById(R.id.btn_retry);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connectivity again and take action accordingly
                if (isInternetConnected()) {
                    // If internet is available, close this activity and return to the login activity
                    finish();
                } else {
                    // Show a message or retry mechanism
                    Toast.makeText(NoInternetActivity.this, "Still no internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isInternetConnected() {
        // You can use your InternetReceiver code here to check internet connectivity.
        // Return true if internet is connected, otherwise return false.
        return CheckInternet.getNetworkInfo(this).equals("connected");
    }
}