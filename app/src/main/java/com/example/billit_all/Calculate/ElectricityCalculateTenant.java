package com.example.billit_all.Calculate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ElectricityCalculateTenant extends AppCompatActivity {

    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    EditText CRead_tb,PRead_tb,TBill_tb2,MConsump;
    Button calc_btn, ocr_btn;
    String paid = "UNPAID";
    String billName = "Electricity";
    TextView result;
    String ocrRead;
    String selectedTenantId, selectedTenantName;
    TextView select_tenant;
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_calculate_tenant);


        CRead_tb = findViewById(R.id.CRead_tb);
        PRead_tb = findViewById(R.id.PRead_tb);
        TBill_tb2 = findViewById(R.id.TBill_tb2);
        MConsump = findViewById(R.id.MConsump);
        calc_btn = findViewById(R.id.calc_btn);
        ocr_btn = findViewById(R.id.ocr_btn);
        select_tenant = findViewById(R.id.select_tenant);

        //get OCR read
        ocrRead = getIntent().getStringExtra("CR");
        CRead_tb.setText(ocrRead);

        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.fetchTenantIdFromBackend(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                Log.d("USER_ID_Tenant", String.valueOf(user));
                                String id = user.getString("id");
                                Log.d("USER_ID_Tenant", id);

                                select_tenant.setText(id);
                                selectedTenantId = select_tenant.getText().toString();
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


        calc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentReadString = CRead_tb.getText().toString();
                String previousReadString = PRead_tb.getText().toString();
                String totalBillString = TBill_tb2.getText().toString();
                String mainConsumptionString = MConsump.getText().toString();


                int currentReadInt = Integer.parseInt(currentReadString);
                int previousReadInt = Integer.parseInt(previousReadString);
                int totalBillInt = Integer.parseInt(totalBillString);
                int mainConsumptionInt= Integer.parseInt(mainConsumptionString);

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


                double num3Double = Double.parseDouble(totalBillString);

                //Calculating Consumption Int
                int consumption = currentReadInt - previousReadInt;
                String consumptionText = Double.toString(consumption);

                //formula for price rate
                double pRate = (double) totalBillInt / (double) mainConsumptionInt ;
                String priceRateText = String.format("%.2f",pRate);
                //Initializing and Calculating amount double
                double amount = (double) consumption * pRate;
                String amountText = String.format("%.2f",amount);;
                int amountInteger = (int) Math.round(Double.parseDouble(amountText));

                Log.d("calculated", String.valueOf(currentReadInt));
                Log.d("calculated", String.valueOf(previousReadInt));
                Log.d("calculated", String.valueOf(totalBillInt));
                Log.d("calculated", String.valueOf(mainConsumptionInt));
                Log.d("results", String.valueOf(consumption));
                Log.d("results", String.valueOf(pRate));
                Log.d("results", String.valueOf(amount));



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
                canvas.drawText("Date: " + datePatternFormat.format(new Date()), 20, 80, paint);
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
                Intent a = new Intent(ElectricityCalculateTenant.this, ViewPdfTenant.class);
                String consumptionString = String.valueOf(consumption);
                String pRateString = String.valueOf(pRate);
                String amountString = String.valueOf(amount);
                String unitIdString = selectedTenantId;

                a.putExtra("currentReadString", currentReadString);
                a.putExtra("previousReadString", previousReadString);
                a.putExtra("totalBillString", totalBillString);
                a.putExtra("mainConsumptionString", mainConsumptionString);

                a.putExtra("consumptionString", consumptionString);
                a.putExtra("pRateString", pRateString);
                a.putExtra("amountString", amountString);
                a.putExtra("unitIdString", unitIdString);

                Log.d("unitno", unitIdString);
                startActivity(a);
            }
        });

        ocr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(ElectricityCalculateTenant.this, OcrActivityTenant.class);
                startActivity(l);

            }
        });


    }
    }
