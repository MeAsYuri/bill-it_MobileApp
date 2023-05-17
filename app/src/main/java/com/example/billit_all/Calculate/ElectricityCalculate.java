package com.example.billit_all.Calculate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.Bill_history.Tenant;
import com.example.billit_all.Bill_history.TenantAdapter;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ElectricityCalculate extends AppCompatActivity {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    EditText CRead_tb,PRead_tb,TBill_tb2,MConsump;
    Button calc_btn, ocr_btn, datePickerButton;
    String paid = "UNPAID";
    String billName = "Electricity";
    TextView result, datePickerString;
    String selectedTenantId, selectedTenantName;
    Spinner select_tenant, selectTenantName;
    String ocrRead;
    String selectedRoomNoId;
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

//    private static final int REQUEST_PERMISSIONS = 1;
//    private static final String[] PERMISSIONS = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_calculate);





        CRead_tb = findViewById(R.id.CRead_tb);
        PRead_tb = findViewById(R.id.PRead_tb);
        TBill_tb2 = findViewById(R.id.TBill_tb2);
        MConsump = findViewById(R.id.MConsump);
        calc_btn = findViewById(R.id.calc_btn);
        ocr_btn = findViewById(R.id.ocr_btn);
        select_tenant = findViewById(R.id.select_tenant);
        datePickerButton = findViewById(R.id.datePickerButton);
        datePickerString = findViewById(R.id.datePickerString);

        //get OCR read
        ocrRead = getIntent().getStringExtra("CR");
        CRead_tb.setText(ocrRead);

        //spinner
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext().getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.updateProfile(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                Log.d("USER_ID", id);

                                //    dapat ilagay sa onItemSelected para naka refer sa selected Tenant
                                RequestQueue queue1 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request1 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/calculate/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {


                                                try {
//                                                    binago
                                                    JSONObject recordsJson = response.getJSONObject("current_total_record");
                                                    if (recordsJson == null){
                                                        Toast.makeText(getApplicationContext(), "There is no data", Toast.LENGTH_SHORT).show();
                                                    }
                                                    //                                                    binago
                                                    Integer elec_bill = recordsJson.getInt("elec_bill");
                                                    Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                    TBill_tb2.setText(elec_bill.toString());
                                                    MConsump.setText(elec_consumption.toString());

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getApplicationContext(), "Calculate this month to have data", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                );

                                request1.authenticated(getApplicationContext());
                                queue1.add(request1);

                                //        spinner button
                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/calculate/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray tenantsJson = response.getJSONArray("elec_unit_no");
                                                    ArrayList<String> IDs = new ArrayList<>();
                                                    HashMap<String, String> UnitMap = new HashMap<>();

                                                    for (int i = 0; i < tenantsJson.length(); i++) {
                                                        JSONObject tenant = tenantsJson.getJSONObject(i);
                                                        String unit_no = tenant.getString("unit_no");
                                                        String unit_id = tenant.getString("unit_id");
                                                        Log.d("unit_id", unit_id);

                                                        Log.d("selectedUnitID", "this is hashmap" + unit_no);
                                                        Log.d("UnitMap", "this is hashmap" + UnitMap);
                                                        IDs.add(unit_no);
                                                        UnitMap.put(unit_no, unit_id);

                                                    }
                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(ElectricityCalculate.this, android.R.layout.simple_spinner_item, IDs);
                                                    select_tenant.setAdapter(spinnerAdapter);
                                                    spinnerAdapter.insert("select tenant", 0);
                                                    select_tenant.setSelection(-1);

                                                    select_tenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                            selectedTenantId = select_tenant.getSelectedItem().toString();

//                                                          getLastPrevious();

                                                            String selectedUnitNo = select_tenant.getSelectedItem().toString();
                                                            selectedTenantId = select_tenant.getSelectedItem().toString();
                                                            Log.d("selectedUnitID", "this is hashmap" + selectedUnitNo);

                                                            String selectedId = UnitMap.get(selectedUnitNo);
                                                            Log.d("selectedUnitID", "this is hashmap" + selectedId);

                                                            if (selectedId == null) {
                                                                selectedRoomNoId = "";
                                                            } else {
                                                                selectedRoomNoId = selectedId;
                                                            }

//                                                            Toast.makeText(getApplicationContext(), selectedRoomNoId, Toast.LENGTH_LONG).show();
                                                            RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                            BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/calculate/unit/" + selectedId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {


                                                                            try {
                                                                                //binago
                                                                                JSONObject recordsJson = response.getJSONObject("prev_elec_read");
                                                                                if (recordsJson == null){
                                                                                    Toast.makeText(getApplicationContext(), "There is no data", Toast.LENGTH_SHORT).show();
                                                                                    PRead_tb.setText("");
                                                                                }
                                                                                //binago
                                                                                Integer elec_reading = recordsJson.getInt("reading");

                                                                                PRead_tb.setText(elec_reading.toString());
                                                                                Log.d("elec_reading", String.valueOf(elec_reading));


                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                                PRead_tb.setText("");
                                                                            }
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            Toast.makeText(getApplicationContext(), "Add Tenant to calculate", Toast.LENGTH_SHORT).show();
                                                                            PRead_tb.setText("");

                                                                        }
                                                                    }
                                                            );

                                                            request4.authenticated(getApplicationContext());
                                                            queue4.add(request4);



                                                        }

                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });


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

                                request2.authenticated(getApplicationContext());
                                queue2.add(request2);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null

            );
        } catch (JSONException e) {
            e.printStackTrace();
        }


        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);

                // Create DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ElectricityCalculate.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                // Do something with the selected date
                                datePickerString.setText(year + "-" + (month + 1 )+ "-" + dayOfMonth);
//                                Log.d(TAG, "Selected date: " + year + "/" + (month + 1) + "/" + dayOfMonth);

//                                // Show TimePickerDialog
//                                TimePickerDialog timePickerDialog = new TimePickerDialog(
//                                        ElectricityCalculate.this,
//                                        new TimePickerDialog.OnTimeSetListener() {
//                                            @Override
//                                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
//                                                // Do something with the selected time
//                                                datePickerString.append(" " + hourOfDay + ":" + minute + ":" + second);
//                                            }
//                                        },
//                                        hourOfDay, minute, true);
//                                timePickerDialog.show();

                            }
                        },
                        year, month, dayOfMonth);

                // Show DatePickerDialog
                datePickerDialog.show();
            }
        });


        calc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentReadString = CRead_tb.getText().toString();
                String previousReadString = PRead_tb.getText().toString();
                String totalBillString = TBill_tb2.getText().toString();
                String mainConsumptionString = MConsump.getText().toString();

                //     dinagdag
                String dueDateString = datePickerString.getText().toString();

                int currentReadInt = Integer.parseInt(currentReadString);
                int previousReadInt = Integer.parseInt(previousReadString);
                int totalBillInt = Integer.parseInt(totalBillString);
                int mainConsumptionInt= Integer.parseInt(mainConsumptionString);

//                for date


                //rules
                if(currentReadString.isEmpty()){
                    CRead_tb.setError("Fill the Current Reading");
                    CRead_tb.requestFocus();
                    return;
                }
                if(currentReadInt <= previousReadInt){
                    CRead_tb.setError("Current Reading should be Higher than Previous reading");
                    CRead_tb.requestFocus();
                    return;
                }
                if(previousReadString.isEmpty()){
                    PRead_tb.setError("Fill the Previous Reading");
                    PRead_tb.requestFocus();
                    return;
                }
                if(previousReadInt >= currentReadInt){
                    PRead_tb.setError("Previous Reading should be Lower than Current reading");
                    PRead_tb.requestFocus();
                    return;
                }
                if(totalBillString.isEmpty()){
                    TBill_tb2.setError("Fill the Total Bill");
                    TBill_tb2.requestFocus();
                    return;
                }
                if(mainConsumptionString.isEmpty()){
                    MConsump.setError("Fill the Main Consumption");
                    MConsump.requestFocus();
                    return;
                }
//     dinagdag
                if(dueDateString.isEmpty()){
                    datePickerString.setError("Pick a date");
                    datePickerString.requestFocus();
                    return;
                }

                double num3Double = Double.parseDouble(totalBillString);

                //Calculating Consumption Int
                int consumption = currentReadInt - previousReadInt;
                String consumptionText = Double.toString(consumption);

                //formula for price rate
                double pRate = (double) totalBillInt / (double) mainConsumptionInt ;
                String priceRateText = String.format("%.2f",pRate);
                //Initializing and Calculating amount double
                double amount = (double) consumption * pRate;
                String amountText = String.format("%.2f",amount);
                int amountInteger = (int) Math.round(Double.parseDouble(amountText));

                Log.d("calculated", String.valueOf(currentReadInt));
                Log.d("calculated", String.valueOf(previousReadInt));
                Log.d("calculated", String.valueOf(totalBillInt));
                Log.d("calculated", String.valueOf(mainConsumptionInt));
                Log.d("results", String.valueOf(consumption));
                Log.d("results", String.valueOf(pRate));
                Log.d("results", String.valueOf(amountInteger));



                //create PDF
                PdfDocument myPdfDocument = new PdfDocument();
                Paint paint = new Paint();
                Paint forLinePaint = new Paint();
                //creation of PDF
                PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
                PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
                Canvas canvas = myPage.getCanvas();

                //color of the font
                paint.setTextSize(15.5f);
                paint.setColor(Color.rgb(0,0,0));

                //texts on the receipt and texts on the receipt and
                canvas.drawText("Bill-It Receipt" +"  " ,20,20, paint);
                paint.setTextSize(12f);
                canvas.drawText("Room number: " + "# "+ selectedTenantId,20,60 ,paint);


                //the broken line
                forLinePaint.setStyle(Paint.Style.STROKE);
                forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
                forLinePaint.setStrokeWidth(2);
                paint.setTextSize(8.5f);
                canvas.drawText("Date: " + datePatternFormat.format(new Date()), 20, 75, paint);
                canvas.drawText("Due Date: " + dueDateString, 20, 85, paint);
                canvas.drawLine(20,65,230,65, forLinePaint);

                //suppose to be for getting the tenant name
                UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
                String tenantName = userSharedPreferenceDataSource.getBackendName();

//                canvas.drawLine(20,90,230,90,forLinePaint);
                canvas.drawLine(20,90,230,90,forLinePaint);
                //readings
                paint.setTextSize(8f);
                canvas.drawText("Current Reading: " + currentReadString, 20,105,paint);
                canvas.drawText("Previous Reading: " + previousReadString, 20,115,paint);
                canvas.drawText("Current Reading - Previous Reading = Tenant Consumption", 30,130,paint);

                paint.setTextSize(10f);
                canvas.drawText("Tenant Consumption: " + " " +consumptionText +" kWh", 55,145,paint);
                paint.setTextSize(8f);
                canvas.drawText("Main Total Bill: " + "₱ "+totalBillString, 20,165,paint);
                canvas.drawText("Main Meter Consumption: " + mainConsumptionString +" kWh", 20,175,paint);
                canvas.drawText("Main Total Bill ÷ Main Meter Consumption = Price per kWh", 27,190,paint);



                paint.setTextSize(10f);
                canvas.drawText("Price per kWh: " + priceRateText, 80,205,paint);


                paint.setTextSize(8f);
                canvas.drawText("Price per kWh x Tenant Consumption = Total bill Amount", 27,240,paint);

                //to get the same result for calculation

                //total bill
                canvas.drawLine(40,250,210,250, forLinePaint);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(15f);
                canvas.drawText("Amount: "+ "₱ "+amountText,120,270,paint);
                paint.setTextSize(20f);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(8f);
                canvas.drawText("Status: " + paid,200,340,paint);


                //invoice count
                paint.setTextSize(12f);
                canvas.drawText(String.valueOf(+1), 230 , 15, paint);



                myPdfDocument.finishPage(myPage);




                File file = new File(dir,"Rm#"+selectedTenantId+ billName + "-" + datePatternFormat.format(new Date()) + "-bill.pdf");


                try {
                    myPdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                myPdfDocument.close();

                //go to the Pdf Viewer
                Toast.makeText(getApplicationContext(),"PDF has been saved and downloaded",Toast.LENGTH_SHORT).show();
                Intent a = new Intent(ElectricityCalculate.this, ViewPdf.class);
                String consumptionString = String.valueOf(consumption);
                String pRateString = String.valueOf(pRate);
                String amountString = String.valueOf(amount);
                String unitIdString = selectedTenantId;
                String unitId = selectedRoomNoId;

                a.putExtra("currentReadString", currentReadString);
                a.putExtra("previousReadString", previousReadString);
                a.putExtra("totalBillString", totalBillString);
                a.putExtra("mainConsumptionString", mainConsumptionString);

                a.putExtra("consumptionString", consumptionString);
                a.putExtra("pRateString", pRateString);
                a.putExtra("amountString", amountString);
                a.putExtra("unitIdString", unitIdString);
                a.putExtra("unitId", unitId);
                a.putExtra("dueDateString", dueDateString);
//     dinagdag
                Log.d("unitno", unitIdString);
                startActivity(a);
            }
        });

        ocr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(ElectricityCalculate.this, OcrActivity.class);
                startActivity(l);

            }
        });


    }


}

