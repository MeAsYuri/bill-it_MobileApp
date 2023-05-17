package com.example.apptenants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPdf extends AppCompatActivity {

    private PDFView pdfView;
    Button conf_Mer;
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("MerRecord");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = findViewById(R.id.view_Pdf);
        conf_Mer = findViewById(R.id.conf_Mer_btn);



        //path of file and name
        File file = new File(this.getExternalFilesDir("/Meralco Bills"),datePatternFormat.format(new Date()) + "-bill.pdf");

        pdfView.fromFile(file)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAnnotationRendering(true)
                .defaultPage(0)
                .scrollHandle(null)
                .password(null)
                .load();




    }
    public void conf_Mer_btn(View v) {

        Intent g = new Intent(this, CalculateActivity1.class);
        startActivity(g);
        finish();
    }
    }