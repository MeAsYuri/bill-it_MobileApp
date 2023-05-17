package com.example.billit_all.Calculate;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.billit_all.DashboardTenant;
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
public class MayViewPdfTenant extends AppCompatActivity {
    private PDFView pdfView;
    Button conf_Mer_btn;
    String billName = "Water";
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    TextView test;
    String currentReadString, previousReadString, totalBillString, mainConsumptionString, consumptionString, pRateString, amountString, unitIdString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_may_view_pdf_tenant);

        pdfView = findViewById(R.id.view_Pdf);
        conf_Mer_btn = findViewById(R.id.conf_Mer_btn);
        test = findViewById(R.id.test);


        Intent a = getIntent();
        currentReadString = a.getStringExtra("currentReadString");
        previousReadString = a.getStringExtra("previousReadString");
        totalBillString = a.getStringExtra("totalBillString");
        mainConsumptionString = a.getStringExtra("mainConsumptionString");

        consumptionString = a.getStringExtra("consumptionString");
        pRateString = a.getStringExtra("pRateString");
        amountString = a.getStringExtra("amountString");
        unitIdString = a.getStringExtra("unitIdString");


        test.setText(unitIdString);

        File file = new File(dir, "Rm#" + unitIdString + billName + "-" + datePatternFormat.format(new Date()) + "-bill.pdf");
        pdfView.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();


        conf_Mer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent g = new Intent(MayViewPdfTenant.this, DashboardTenant.class);
                startActivity(g);
                finish();
            }
        });
    }
}