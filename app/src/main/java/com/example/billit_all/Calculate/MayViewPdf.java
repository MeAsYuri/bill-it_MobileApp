package com.example.billit_all.Calculate;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.DashboardLandlord;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MayViewPdf extends AppCompatActivity {
    private PDFView pdfView;
    Button conf_May_btn;
    String billName = "Water";
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    Switch SMS_btn;
    Button backSetButton;
    TextView phone_number,tenant_name ;
    TextView test;
    SmsManager smsManager = SmsManager.getDefault();
    String currentReadString, previousReadString, totalBillString, mainConsumptionString, consumptionString, pRateString, amountString, unitIdString, unitId, dueDate;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_may_view_pdf);

        pdfView = findViewById(R.id.view_Pdf);
        conf_May_btn = findViewById(R.id.conf_May_btn);
        SMS_btn = findViewById(R.id.SMS_button);
        test = findViewById(R.id.test);
        phone_number = findViewById(R.id.phone_number);
        tenant_name = findViewById(R.id.tenant_name);
        backSetButton = findViewById(R.id.backSetButton);

        backSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent a = getIntent();
        currentReadString = a.getStringExtra("currentReadString");
        previousReadString = a.getStringExtra("previousReadString");
        totalBillString = a.getStringExtra("totalBillString");
        mainConsumptionString = a.getStringExtra("mainConsumptionString");

        consumptionString = a.getStringExtra("consumptionString");
        pRateString = a.getStringExtra("pRateString");
        amountString = a.getStringExtra("amountString");
        unitIdString = a.getStringExtra("unitIdString");
        unitId = a.getStringExtra("unitId");
        dueDate = a.getStringExtra("dueDateString");
//        Log.d("ViewPdfdata", currentReadString);
//        Log.d("ViewPdfdata", previousReadString);
//        Log.d("ViewPdfdata", totalBillString);
//        Log.d("ViewPdfdata", mainConsumptionString);
//        Log.d("ViewPdfdata", consumptionString);
//        Log.d("ViewPdfdata", amountString);
//        Log.d("ViewPdfdata", unitIdString);



        test.setText(unitIdString);



//get tenant details
        RequestQueue queue0 = AppVolleyRequestQueue.getInstance(getApplicationContext().getApplicationContext());
        BackendJsonObjectRequest request0 = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/bill-history/get-record/" + unitId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


//                            method for formatting the date
//                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//                            Date date = null;
//                            try {
//                                date = inputFormat.parse(date_from_db);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            // Format the date to the desired output format
//                            String formattedDate = outputFormat.format(date);

                            JSONObject responseJSON = response.getJSONObject("tenant");

                            if (responseJSON == null && responseJSON.equals("")) {
                                Toast.makeText(getApplicationContext(), "No Name and Phone for this Tenant", Toast.LENGTH_SHORT).show();
                            }

                            String name = responseJSON.getString("name");
                            String phone = responseJSON.getString("phone");

                            Log.d("profile", name);
                            Log.d("profile", phone);
                            tenant_name.setText(name);
                            phone_number.setText(phone);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        request0.authenticated(getApplicationContext().getApplicationContext());
        queue0.add(request0);


        File file = new File(dir, "Rm#"+unitIdString+ billName + "-" + datePatternFormat.format(new Date()) + "-bill.pdf");
        pdfView.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();


        SMS_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    //send SMS
                    try {
                        // Set the phone number
                        String phoneNumber =  (String) phone_number.getText();
                        String Name = (String)  tenant_name.getText();
                        String Date = dueDate;
                        Log.d("phone_number", phoneNumber);
                        String message = "(Bill It Notice) Hello! "+ Name +", your Electricity Bill has been issued by your Landlord, Check the Bill-It app for more Info. Thank you";

                        String message2 = "(Bill It Notice) Hi! "+ Name +", your Electricity bill is due on "+( Date )+  ",Pay Online through Bill It App and Gcash or in Cash. Thank you";

                        // Get the default SMS manager
                        SmsManager smsManager = SmsManager.getDefault();

                        // Create a PendingIntent to send the SMS message
                        Intent intent = new Intent(MayViewPdf.this, SmsReceiver.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("message", message);
                        intent.putExtra("requestCode", 1);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MayViewPdf.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


                        Intent intent2 = new Intent(MayViewPdf.this, SmsReceiver.class);
                        intent2.putExtra("phoneNumber", phoneNumber);
                        intent2.putExtra("message", message2);
                        intent2.putExtra("requestCode", 2);
                        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(MayViewPdf.this, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date dueDate = dateFormat.parse(Date);

                        //time for message1
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.add(Calendar.SECOND, 10);

                        //time for message2
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTime(dueDate);
                        calendar2.add(Calendar.DATE, -3); // Subtract 3 days from the due date

//                        parse date to Int to sched the time
//                        calendar2.add(Calendar.MINUTE, 1);

                        // Schedule the SMS message using AlarmManager
                        AlarmManager alarmManager = (AlarmManager) MayViewPdf.this.getSystemService(Context.ALARM_SERVICE);
                        //first intent
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        //second intent and calendar
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);

                        // Display a Toast message to confirm the message was scheduled
                        Toast.makeText(MayViewPdf.this, "SMS scheduled", Toast.LENGTH_SHORT).show();

//                        //put datas
//                        RequestQueue queue1 = AppVolleyRequestQueue.getInstance(getApplicationContext());
//                        JSONObject formdata = new JSONObject();
//                        try {
//                            formdata.put("messages", message);
////                            formdata.put("unit_id", 2);
//
//                            BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                                    Request.Method.POST,
//                                    "/api/sms/message",
//                                    formdata,
//                                    new Response.Listener<JSONObject>() {
//                                        @Override
//                                        public void onResponse(JSONObject response) {
//                                            try {
//                                                String message = response.getString("message");
//                                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    },
//                                    new Response.ErrorListener() {
//                                        @Override
//                                        public void onErrorResponse(VolleyError error) {
//                                            Toast.makeText(MayViewPdf.this, "SMS has not been sent", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                            );
//
//
//                            request.authenticated(getApplicationContext());
//                            queue1.add(request);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        //put data end

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //end of send SMS
                } else {


                }
            }
        });


        conf_May_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //put datas
                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                JSONObject formdata = new JSONObject();
                try {
                    formdata.put("prev_read", previousReadString);
                    formdata.put("current_read", currentReadString);
                    formdata.put("total_bill", totalBillString);
                    formdata.put("total_consumption",mainConsumptionString);
                    formdata.put("unit_id", unitId);

                    //     dinagdag
                    formdata.put("due_date", dueDate);



                    BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                            Request.Method.POST,
                            "/api/water",
                            formdata,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String message = response.getString("message");
                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    );


                    request.authenticated(getApplicationContext());
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ///end here

                Intent g = new Intent(MayViewPdf.this, DashboardLandlord.class);
                startActivity(g);
                finish();
            }
        });
    }
}