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

public class MayViewPdf extends AppCompatActivity {
    private PDFView pdfViewMay;
    Button conf_May;
    SimpleDateFormat may_datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_may_view_pdf);

        pdfViewMay = findViewById(R.id.may_view_Pdf);
        conf_May = findViewById(R.id.conf_May_btn);


        //path of file and name
        File file = new File(this.getExternalFilesDir("/Maynilad Bills"),may_datePatternFormat.format(new Date()) + "-bill.pdf");

        pdfViewMay.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();


    }

    public void conf_May_btn(View v) {

        Intent k = new Intent(this, DashboardTenant.class);
        startActivity(k);
        finish();
    }
}