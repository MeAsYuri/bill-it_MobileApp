package com.example.apptenants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManViewPdf extends AppCompatActivity {
    private PDFView pdfViewMan;
    Button conf_Man;
    SimpleDateFormat man_datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_view_pdf);


        pdfViewMan = findViewById(R.id.man_view_Pdf);
        conf_Man = findViewById(R.id.conf_Man_btn);


        //path of file and name
        File file = new File(this.getExternalFilesDir("/ManilaWater Bills"),man_datePatternFormat.format(new Date()) + "-bill.pdf");

        pdfViewMan.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();


    }
    public void conf_Man_btn(View v) {

        Intent j = new Intent(this, DashboardTenant.class);
        startActivity(j);
        finish();
    }
}