package com.example.billit_all.Calculate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String message = intent.getStringExtra("message");
        int requestCode = intent.getIntExtra("requestCode", -1);



        if (requestCode == 1) {
            // Handle the first message
            try {
                // Get the default SMS manager
                SmsManager smsManager = SmsManager.getDefault();

                // Send the SMS message
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

//            // Display a Toast message to confirm the message was sent
//            Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2) {
            // Handle the second message
            try {
                // Get the default SMS manager
                SmsManager smsManager = SmsManager.getDefault();

                // Send the SMS message
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

//            // Display a Toast message to confirm the message was sent
//            Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
}
