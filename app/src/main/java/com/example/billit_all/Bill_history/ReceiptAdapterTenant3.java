package com.example.billit_all.Bill_history;


import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billit_all.Calculate.CalculateActivity2;
import com.example.billit_all.Payment.PaymentElectricity;
import com.example.billit_all.Payment.PaymentRent;
import com.example.billit_all.Payment.PaymentWater;
import com.example.billit_all.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ReceiptAdapterTenant3 extends RecyclerView.Adapter<ReceiptAdapterTenant3.myViewHolder> {

    protected ArrayList<Receipt2> receipts2;
    String billName = "Rent";
    String selectedTenantID;
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public ReceiptAdapterTenant3(ArrayList<Receipt2> receipts2){
        this.receipts2=receipts2;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_list_tenant2,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
        myViewHolder.unit_no.setText(String.valueOf(receipts2.get(position).getUnit_no()));
        myViewHolder.date.setText(String.valueOf(receipts2.get(position).getDate()));
        myViewHolder.statusR.setText(String.valueOf(receipts2.get(position).getPaidString()));
        myViewHolder.receipt_rentFee.setText(String.valueOf(receipts2.get(position).getReceipt_rentFee()));

        if (myViewHolder.statusR.getText().toString() == "PAID"){
            myViewHolder.pay_btn3.setVisibility(View.INVISIBLE);

        }else {
            myViewHolder.statusR.setTextColor(Color.RED);
            myViewHolder.pay_btn3.setVisibility(View.VISIBLE);
            myViewHolder.download_btn3.setVisibility(View.VISIBLE);
        }

        myViewHolder.tenantID.setText(String.valueOf(receipts2.get(position).getTenant_id()));
        if (myViewHolder.tenantID.getText().equals("null") || myViewHolder.tenantID.getText() == "null")
        {
            myViewHolder.previousTenant.setVisibility(View.VISIBLE);
        }
        else {

        }



        final int tenantID_position= receipts2.get(position).getUnit_id();
        final String qrCode = receipts2.get(position).getQrCode();
        final double tenantAmount_position= receipts2.get(position).getReceipt_rentFee();
        myViewHolder.pay_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(myViewHolder.pay_btn.getContext(),"this is payment acitivty",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(myViewHolder.pay_btn3.getContext(), PaymentRent.class);
                intent.putExtra("id", tenantID_position);
                intent.putExtra("amount", tenantAmount_position);
                intent.putExtra("qr", qrCode);
                myViewHolder.pay_btn3.getContext().startActivity(intent);
                myViewHolder.pay_btn3.setVisibility(View.INVISIBLE);
            }
        });
        myViewHolder.download_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), billName +" Receipt Downloaded", Toast.LENGTH_SHORT).show();
                PdfDocument myPdfDocument = new PdfDocument();
                Paint paint = new Paint();
                Paint forLinePaint = new Paint();
                //creation of PDF
                PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
                PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
                Canvas canvas = myPage.getCanvas();

                paint.setTextSize(15.5f);
                paint.setColor(Color.rgb(0,0,0));

                //texts on the receipt and texts on the receipt and
                canvas.drawText("Bill-It Receipt" +"  "+"("+ billName +")" ,20,20, paint);
                paint.setTextSize(12f);
                canvas.drawText("Room number: " + "# "+ myViewHolder.unit_no.getText(),20,60 ,paint);


                //the broken line
                forLinePaint.setStyle(Paint.Style.STROKE);
                forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
                forLinePaint.setStrokeWidth(2);
                paint.setTextSize(8.5f);
                canvas.drawText("Date: " + myViewHolder.date.getText(), 20, 80, paint);
                canvas.drawLine(20,65,230,65, forLinePaint);
                canvas.drawText("Rent: " + myViewHolder.receipt_rentFee.getText(), 20, 120, paint);

                canvas.drawLine(20,90,230,90,forLinePaint);



                canvas.drawLine(40,230,210,230, forLinePaint);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(15f);
                canvas.drawText("Total Bill: "+ "₱ "+myViewHolder.receipt_rentFee.getText(),120,250,paint);
                paint.setTextSize(20f);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(8f);
                canvas.drawText("Status: " + myViewHolder.statusR.getText(),200,340,paint);


                //invoice count
                paint.setTextSize(12f);
                canvas.drawText(String.valueOf(+1), 230 , 15, paint);



                myPdfDocument.finishPage(myPage);

                File file = new File(dir,"Rm#"+myViewHolder.unit_no.getText()+ billName + "_" + myViewHolder.date.getText() + "-bill.pdf");

                try {
                    myPdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                myPdfDocument.close();

            }


        });

    }

    @Override
    public int getItemCount() {
        return receipts2.size();
    }




    public class myViewHolder extends RecyclerView.ViewHolder {


        Button pay_btn3, download_btn3;
        TextView receipt_rentFee,date , statusR, unit_no, tenantID, previousTenant;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            receipt_rentFee = itemView.findViewById(R.id.receipt_rentFee);
            date = itemView.findViewById(R.id.date);
            statusR = itemView.findViewById(R.id.statusR);
            unit_no = itemView.findViewById(R.id.unit_no);

            pay_btn3 = itemView.findViewById(R.id.pay_btn3);
            download_btn3 = itemView.findViewById(R.id.download_btn3);

            tenantID  = itemView.findViewById(R.id.tenantID);
            previousTenant  = itemView.findViewById(R.id.previousTenant);




        }


    }

}
