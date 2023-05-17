package com.example.apptenants;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateActivity3 extends AppCompatActivity {
    //database connection
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("MayRecord");

    //constructor
    DataObj2 dataObj2 = new DataObj2();
    EditText may_num1,may_num2,may_num3,may_num4;
    Button may_calc;
    TextView may_res;
    long invoiceNo2 = 0;
    boolean paid = true;
    DecimalFormat may_decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat may_datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate3);

        //calling the methods
        callFindViewByIdMay();
        callOnClickListenerMay();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoiceNo2 = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        String rc = getIntent().getStringExtra("CR");
        may_num1.setText(rc);
    }

    public void ocr_May_btn(View c){
        Intent l = new Intent(CalculateActivity3.this, OcrActivity2.class);
        startActivity(l);


    }
    private void callOnClickListenerMay() {

        may_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //incrementation of no. of invoices
                dataObj2.invoiceNo2 = invoiceNo2 +1;
                //getting the number on the edit text widget ()

                dataObj2.currentReadMay = Float.parseFloat(String.valueOf(may_num1.getText()));
                dataObj2.previousReadMay = Float.parseFloat(String.valueOf(may_num2.getText()));
                dataObj2.totalBillMay = Float.parseFloat(String.valueOf(may_num3.getText()));
                dataObj2.totalConsMay = Float.parseFloat(String.valueOf(may_num4.getText()));
                // to get date
                dataObj2.dateMay = new Date().getTime();

                //isPaid
                if (paid){
                    dataObj2.isPaid2 =  new String("Not Paid");
                }
                else {
                    dataObj2.isPaid2 =  new String("Paid");
                }
                dialog();



            }
        });
    }

    private void dialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice!!")
                .setMessage("Are you sure you want to proceed?")
                .setPositiveButton("proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ipasok
                        calculation();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    private void previewFile() {
        //preview of pdf
        Intent pdfViewMay = new Intent(CalculateActivity3.this, MayViewPdf.class);
        Toast.makeText(getApplicationContext(),"PDF has been saved and downloaded",Toast.LENGTH_SHORT).show();
        startActivity(pdfViewMay);
    }

    private void calculation(){
        //calculation
        dataObj2.amountMay = Double.valueOf(dataObj2.getCurrentReadMay() - dataObj2.getPreviousReadMay() ) * (dataObj2.totalBillMay/ dataObj2.totalConsMay);

        //get the value of the calculated result
//        may_res.setText("" + (dataObj2.getAmountMay()));

        //insertion in database
        myRef.child(String.valueOf(invoiceNo2+1)).setValue(dataObj2);

        printPDF();

        previewFile();
    }
    private void printPDF() {
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
        canvas.drawText("Bill-It Receipt", 20,20, paint);
        paint.setTextSize(8.5f);


        //the broken line
        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);
        canvas.drawLine(20,65,230,65, forLinePaint);
        //suppose to be for getting the tenant name
        canvas.drawText("Customer:" + "Berndette",20,80 ,paint);
        canvas.drawLine(20,90,230,90,forLinePaint);

        //readings
        canvas.drawText("Current Reading: " + may_num1.getText(), 20,125,paint);
        canvas.drawText("Previous Reading: " + may_num2.getText(), 20,135,paint);
        //date
        canvas.drawText("Date: " + may_datePatternFormat.format(new Date()), 20, 200, paint);

        //to get price rate
        double priceRateMay = Double.valueOf(dataObj2.getTotalBillMay() / dataObj2.getTotalConsMay());
        canvas.drawText("Maynilad Bill: " + (dataObj2.getTotalBillMay()), 20,145,paint);
        canvas.drawText("Total Consumption: " + (dataObj2.getTotalConsMay()), 20,155,paint);
        canvas.drawText("Price Rate: " + (priceRateMay), 20,165,paint);

        //to get the same result for calculation
        double amountMay = Double.valueOf(dataObj2.getCurrentReadMay() - dataObj2.getPreviousReadMay() ) * (priceRateMay);


        //total bill

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText("Total Bill: " + may_decimalFormat.format(amountMay),135,250,paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8f);
        canvas.drawText("Status: " + dataObj2.getIsPaid2(),200,300,paint);

        //invoice count
        paint.setTextSize(12f);
        canvas.drawText(String.valueOf(invoiceNo2+1), 230 , 15, paint);




        myPdfDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir("/Maynilad Bills"),may_datePatternFormat.format(new Date()) + "-bill.pdf");


        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
    }

    private void callFindViewByIdMay(){
        //buttons
        may_num1 = findViewById(R.id.CRead_May_tb);
        may_num2 = findViewById(R.id.PRead_May_tb);
        may_num3 = findViewById(R.id.TBill_May_tb2);
        may_num4 = findViewById(R.id.TCons_May_tb2);
        may_calc = findViewById(R.id.calc_May_btn);


    }


}