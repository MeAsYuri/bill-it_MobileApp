package com.example.billit_all.Calculate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.billit_all.DashboardTenant;
import com.example.billit_all.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManViewPdfTenant extends AppCompatActivity {
    private PDFView pdfViewMan;
    Button conf_Man;
    SimpleDateFormat man_datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    String billName = "Manila Water";
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_view_pdf_tenant);

        pdfViewMan = findViewById(R.id.man_view_Pdf_tenant);
        conf_Man = findViewById(R.id.conf_Man_btn_tenant);
        Intent intent = getIntent();
        String sessionId = intent.getStringExtra("tenant_id");

        //path of file and name
        File file = new File(dir,"Rm#"+sessionId+ billName + "-" + man_datePatternFormat.format(new Date()) + "-bill.pdf");
        pdfViewMan.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();

    }
    public void conf_Man_btn_tenant(View v) {

        Intent j = new Intent(this, DashboardTenant.class);
        startActivity(j);
        finish();
    }
}