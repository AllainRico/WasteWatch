    package com.example.loginandregister.internet;

    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.widget.Toast;

    public class InternetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = CheckInternet.getNetworkInfo(context);
            if (status.equals("connected")){
                Toast.makeText(context.getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
            }
            else if(status.equals("disconnected")){
                //Toast.makeText(context.getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                Intent noInternetIntent = new Intent(context, NoInternetActivity.class);
                noInternetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(noInternetIntent);
            }
        }
    }
