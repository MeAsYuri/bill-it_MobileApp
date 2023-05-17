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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

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


public class CalculateActivityTenant4 extends AppCompatActivity {
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    EditText num1,num2,num3,num4;
    Button calc;
    String paid = "NOT PAID";
    TextView result;
    String selectedTenantId, selectedTenantName;
    TextView selectTenant, selectTenantName;
    String billName = "Manila Water";
    double previousRead;
    double currentRead;
    double priceRate;
    double amount;


    //    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");


    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tenant4);

        num1 = findViewById(R.id.CRead_Man_tb_tenant);
        num2 = findViewById(R.id.PRead_Man_tb_tenant);
        num3 = findViewById(R.id.TBill_Man_tb2_tenant);
        num4 = findViewById(R.id.MConsump_tenant);
        calc = (Button) findViewById(R.id.calc_Man_btn_tenant);
        selectTenant = findViewById(R.id.select_Man_tenant_tenant);

        String rc = getIntent().getStringExtra("CR");
        num1.setText(rc);

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initializing num1... to String
                String num1Text = num1.getText().toString();
                String num2Text = num2.getText().toString();
                String num3Text = num3.getText().toString();
                String num4Text = num4.getText().toString();

                //rules
                if(num1Text.isEmpty()){
                    num1.setError("Fill the Current Reading");
                    num1.requestFocus();
                    return;
                }
                if(num2Text.isEmpty()){
                    num2.setError("Fill the Previous Reading");
                    num2.requestFocus();
                    return;
                }
                if(num3Text.isEmpty()){
                    num3.setError("Fill the Total Bill");
                    num3.requestFocus();
                    return;
                }
                if(num4Text.isEmpty()){
                    num4.setError("Fill the Main Consumption");
                    num4.requestFocus();
                    return;
                }

                //initializing num1.... to Int
                int num1Parse = Integer.parseInt(num1Text);
                int num2Parse = Integer.parseInt(num2Text);
                int num3Parse = Integer.parseInt(num3Text);
                int num4Parse = Integer.parseInt(num4Text);

                //initializing num3 to Double
                double num3Double = Double.parseDouble(num3Text);

                //Calculating Consumption Int
                int consumption = num1Parse - num2Parse;
                String consumptionText = Double.toString(consumption);

                //formula for price rate
                double pRate = (double) num3Parse / (double) num4Parse ;
                String priceRateText = Double.toString(pRate);
                //Initializing and Calculating amount double
                double amount = (double) consumption * (double) pRate;
                String amountText = Double.toString(amount);
                int amountInteger = (int) Math.round(Double.parseDouble(amountText));


                //parameters to avoid multiple getText();
                calc_ini(num1Parse, num2Parse, num3Double,consumption,num4Parse,amountInteger, pRate);
                printPDF(num1Text, num2Text, num3Text,consumptionText,amountText,num4Text,priceRateText);

                previewFile();
            }
        });
        //calling the methods

        populateTenantDropDown();
    }

    public void ocr_Man_btn(View c){
        Intent l = new Intent(CalculateActivityTenant4.this, OcrActivityTenant3.class);
        startActivity(l);

    }



    private void calc_ini(int cRead, int pRead, double tBill, int consump, int mConsump, int total, double pRate){
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());

        JSONObject form = new JSONObject();
        try {

            form.put("prev_read", pRead);
            form.put("current_read", cRead);
            form.put("total_bill", tBill);
            form.put("main_consumption",mConsump);
            form.put("tenant_id", selectedTenantId);
            //will be ignored
            form.put("price_rate", pRate);
            form.put("consumption", consump);
            form.put("amount", total);


            BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                    Request.Method.POST,
                    "/api/water",
                    form,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
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
    }

    public void populateTenantDropDown(){
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.fetchUserFromBackend(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                String name = user.getString("name");
                                String id = user.getString("user_id");
                                selectTenant.setText(id);
                                selectedTenantId = selectTenant.getText().toString();

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
    }



    private void previewFile(){
        Intent pdfView = new Intent(CalculateActivityTenant4.this, ManViewPdfTenant.class);
        Toast.makeText(getApplicationContext(),"PDF has been saved and downloaded",Toast.LENGTH_SHORT).show();
        String id = selectedTenantId;
        pdfView.putExtra("tenant_id", id);
        startActivity(pdfView);
    }


    public void printPDF(String currentRead, String previousRead, String totalBill, String amount, String total, String mainConsumption, String priceRateText) {


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

        canvas.drawText("Bill-It Receipt" +"  "+"("+ billName +")" ,20,20, paint);


        //the broken line
        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);
        paint.setTextSize(12f);
        canvas.drawText("Room number: " + "# "+ selectedTenantId,20,60 ,paint);
        paint.setTextSize(8.5f);
        canvas.drawText("Date: " + datePatternFormat.format(new Date()), 20, 80, paint);
        canvas.drawLine(20,65,230,65, forLinePaint);

        //suppose to be for getting the tenant name
        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        String tenantName = userSharedPreferenceDataSource.getBackendName();

        canvas.drawLine(20,90,230,90,forLinePaint);

        //readings
        paint.setTextSize(8f);
        canvas.drawText("Current Reading: " + currentRead, 20,125,paint);
        canvas.drawText("Previous Reading: " + previousRead, 20,135,paint);
        canvas.drawLine(20,90,230,90,forLinePaint);
        paint.setTextSize(10f);
        canvas.drawText("Tenant Consumption: " + " " +amount +" cu.m.", 55,155,paint);
        paint.setTextSize(8f);
        canvas.drawText("Main Total Bill: " + "₱ "+totalBill, 20,175,paint);
        canvas.drawText("Main Meter Consumption: " + mainConsumption +" cu.m.", 20,185,paint);
        paint.setTextSize(10f);
        canvas.drawText("Price Rate: " + priceRateText, 80,205,paint);
        paint.setTextSize(8f);

        //to get the same result for calculation

        //total bill
        canvas.drawLine(40,230,210,230, forLinePaint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15f);
        canvas.drawText("Total Bill: "+ "₱ "+total,120,250,paint);
        paint.setTextSize(20f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8f);
        canvas.drawText("Status: " + paid,200,340,paint);


        //invoice count
        paint.setTextSize(12f);
        canvas.drawText(String.valueOf(+1), 230 , 15, paint);



        myPdfDocument.finishPage(myPage);
//        path of file and name

        File file = new File(dir,"Rm#"+selectedTenantId+ billName + "-" + datePatternFormat.format(new Date()) + "-bill.pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();

    }

    private void getSpecificTenant(String userid) {
        BillBackendDataResource billBackendDataResource = BillBackendDataResource.getInstance(getApplicationContext());
        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        userSharedPreferenceDataSource.getBackendId();
        try {
            billBackendDataResource.retrieveUserFromBackend(
                    userid,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                String name = user.getString("name");
                                userSharedPreferenceDataSource.storeBackendName(name);

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
    }
}