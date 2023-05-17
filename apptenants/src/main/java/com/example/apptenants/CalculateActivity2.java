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

public class CalculateActivity2 extends AppCompatActivity {

    //database connection
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("MerRecord");


    //constructor
    DataObj dataObj = new DataObj();
    EditText num1,num2,num3;
    Button calc;
    boolean paid = true;
    TextView result;
    long invoiceNo = 0;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

    //ipasok
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate2);



        //calling the methods
        callFindViewById();
        callOnClickListener();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoiceNo = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String rc = getIntent().getStringExtra("CR");
        num1.setText(rc);




    }


   public void ocr_btn(View c){
       Intent l = new Intent(CalculateActivity2.this, OcrActivity.class);
       startActivity(l);


   }

    private void callOnClickListener() {
        //calculation button
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //incrementation of no. of invoices
                dataObj.invoiceNo = invoiceNo +1;
                //getting the number on the edit text widget ()

                dataObj.currentRead = Float.parseFloat(String.valueOf(num1.getText()));
                dataObj.previousRead = Float.parseFloat(String.valueOf(num2.getText()));
                dataObj.priceRate = Float.parseFloat(String.valueOf(num3.getText()));

                // to get date
                dataObj.date = new Date().getTime();

                //isPaid
                if (paid){
                    dataObj.isPaid =  new String("Not Paid");
                }
                else {
                    dataObj.isPaid =  new String("Paid");
                }


                //ipasok
                dialog();


            }


        });
    }

    //ipasok
    private void previewFile(){
        Intent pdfView = new Intent(CalculateActivity2.this, ViewPdf.class);
        Toast.makeText(getApplicationContext(),"PDF has been saved and downloaded",Toast.LENGTH_SHORT).show();
        startActivity(pdfView);
    }

    //ipasok
    private void calculation(){
        //calculation
        dataObj.amount = Double.valueOf(dataObj.getCurrentRead() - dataObj.getPreviousRead() ) * dataObj.getPriceRate();


//        res.setText("" + (dataObj.getAmount()));

        //insertion in database
        myRef.child(String.valueOf(invoiceNo+1)).setValue(dataObj);

        printPDF();

        //preview of pdf
        previewFile();
    }

    //ipasok
    private void dialog(){

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
        canvas.drawText("Customer:" + "Bernadette",20,80 ,paint);
        canvas.drawLine(20,90,230,90,forLinePaint);

        //readings
        canvas.drawText("Current Reading: " + num1.getText(), 20,125,paint);
        canvas.drawText("Previous Reading: " + num2.getText(), 20,135,paint);
        //date
        canvas.drawText("Date: " + datePatternFormat.format(new Date()), 20, 200, paint);
        canvas.drawText("Price Rate: " + num3.getText(), 20,165,paint);

        //to get the same result for calculation
        double amount = Double.valueOf(dataObj.getCurrentRead() - dataObj.getPreviousRead() ) * dataObj.getPriceRate();

        //total bill

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText("Total Bill: " + decimalFormat.format(amount),125,250,paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8f);
        canvas.drawText("Status: " + dataObj.getIsPaid(),200,300,paint);


        //invoice count
        paint.setTextSize(12f);
        canvas.drawText(String.valueOf(invoiceNo+1), 230 , 15, paint);




        myPdfDocument.finishPage(myPage);
//        path of file and name
        File file = new File(this.getExternalFilesDir("/Meralco Bills"),datePatternFormat.format(new Date()) + "-bill.pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();

    }

    private void callFindViewById() {
        //buttons
        num1 = findViewById(R.id.CRead_tb);
        num2 = findViewById(R.id.PRead_tb);
        num3 = findViewById(R.id.TBill_tb2);
        calc = findViewById(R.id.calc_btn);




    }
}