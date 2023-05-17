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

public class CalculateActivity4 extends AppCompatActivity {
    //database connection
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("ManRecord");

    //constructor
    DataObj3 dataObj3 = new DataObj3();
    EditText man_num1,man_num2,man_num3,man_num4;
    Button man_calc;
    TextView man_res;
    boolean paid = true;
    long invoiceNo3 = 0;
    DecimalFormat man_decimalFormat = new DecimalFormat("#.##");
    SimpleDateFormat man_datePatternFormat = new SimpleDateFormat("dd-MM-yyyy");

    //ipasok
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate4);

        //calling the methods
        callFindViewByIdMan();
        callOnClickListenerMan();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                invoiceNo3 = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        String rc = getIntent().getStringExtra("CR2");
        man_num1.setText(rc);
    }

    public void ocr_Man_btn(View c){
        Intent l = new Intent(CalculateActivity4.this, OcrActivity2.class);
        startActivity(l);


    }

    private void callOnClickListenerMan() {
        man_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //incrementation of no. of invoices
                dataObj3.invoiceNo3 = invoiceNo3 +1;
                //getting the number on the edit text widget ()

                dataObj3.currentReadMan = Float.parseFloat(String.valueOf(man_num1.getText()));
                dataObj3.previousReadMan = Float.parseFloat(String.valueOf(man_num2.getText()));
                dataObj3.totalBillMan = Float.parseFloat(String.valueOf(man_num3.getText()));
                dataObj3.totalConsMan = Float.parseFloat(String.valueOf(man_num4.getText()));

                // to get date
                dataObj3.dateMan = new Date().getTime();

                //isPaid
                if (paid){
                    dataObj3.isPaid3 =  new String("Not Paid");
                }
                else {
                    dataObj3.isPaid3 =  new String("Paid");
                }
                dialog();


            }
        });
    }


    private void calculation(){
        //calculation
        dataObj3.amountMan = Double.valueOf(dataObj3.getCurrentReadMan() - dataObj3.getPreviousReadMan() ) * (dataObj3.totalBillMan/ dataObj3.totalConsMan);

        //get the value of the calculated result
//        man_res.setText("" + (dataObj3.getAmountMan()));

        //insertion in database
        myRef.child(String.valueOf(invoiceNo3+1)).setValue(dataObj3);

        printPDF();
        previewFile();
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

    private void previewFile(){
        //preview of pdf
        Intent pdfViewMan = new Intent(CalculateActivity4.this, ManViewPdf.class);
        Toast.makeText(getApplicationContext(),"PDF has been saved and downloaded" ,Toast.LENGTH_SHORT).show();
        startActivity(pdfViewMan);
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
        canvas.drawText("Current Reading: " + man_num1.getText(), 20,125,paint);
        canvas.drawText("Previous Reading: " + man_num2.getText(), 20,135,paint);
        //date
        canvas.drawText("Date: " + man_datePatternFormat.format(new Date()), 20, 200, paint);

        //to get price rate
        double priceRateMan = Double.valueOf(dataObj3.getTotalBillMan() / dataObj3.getTotalConsMan());
        canvas.drawText("Maynilad Bill: " + (dataObj3.getTotalBillMan()), 20,145,paint);
        canvas.drawText("Total Consumption: " + (dataObj3.getTotalConsMan()), 20,155,paint);
        canvas.drawText("Price Rate: " + (priceRateMan), 20,165,paint);

        //to get the same result for calculation
        double amountMan = Double.valueOf(dataObj3.getCurrentReadMan() - dataObj3.getPreviousReadMan() ) * (priceRateMan);


        //total bill

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);
        canvas.drawText("Total Bill: " + man_decimalFormat.format(amountMan),135,250,paint);


        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8f);
        canvas.drawText("Status: " + dataObj3.getIsPaid3(),200,300,paint);

        //invoice count
        paint.setTextSize(12f);
        canvas.drawText(String.valueOf(invoiceNo3+1), 230 , 15, paint);




        myPdfDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir("/ManilaWater Bills"),man_datePatternFormat.format(new Date()) + "-bill.pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();
    }

    private void callFindViewByIdMan() {
        //buttons
        man_num1 = findViewById(R.id.CRead_Man_tb);
        man_num2 = findViewById(R.id.PRead_Man_tb);
        man_num3 = findViewById(R.id.TBill_Man_tb2);
        man_num4 = findViewById(R.id.TCons_Man_tb2);
        man_calc = findViewById(R.id.calc_Man_btn);

    }
}

