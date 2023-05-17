package com.example.billit_all.Bill_history;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.myViewHolder> {

    protected ArrayList<Bill> bills;
    String billName = "Electricity";
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public BillAdapter (ArrayList<Bill> bills){
        this.bills=bills;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_list,parent,false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder myViewHolder, int position) {
        myViewHolder.statusR.setText(String.valueOf(bills.get(position).getElecPaidString()));
        if (myViewHolder.statusR.getText().toString() == "PAID"){

        }else {
            myViewHolder.statusR.setTextColor(Color.RED);
        }

        myViewHolder.date.setText(String.valueOf(bills.get(position).getElec_due_date()));
        myViewHolder.date_created.setText(String.valueOf(bills.get(position).getElec_date()));
        myViewHolder.total_consumption.setText(String.valueOf(bills.get(position).getElec_total_consumption()));
        myViewHolder.total_bill.setText(String.valueOf(bills.get(position).getElec_total_bill()));
        myViewHolder.unit_no.setText(String.valueOf(bills.get(position).getElec_unit_no()));
        myViewHolder.price_rate.setText(String.valueOf(bills.get(position).getElec_price_rate()));
        myViewHolder.consumption.setText(String.valueOf(bills.get(position).getElec_consumption()));
        myViewHolder.amount.setText(String.valueOf(bills.get(position).getElec_amount()));
        myViewHolder.elec_reading.setText(String.valueOf(bills.get(position).getElec_reading()));

        myViewHolder.penalty.setText(String.valueOf(bills.get(position).getElec_penalty()));
        myViewHolder.balance.setText(String.valueOf(bills.get(position).getElec_balance()));

        myViewHolder.comp_total_consumption.setText(String.valueOf(bills.get(position).getElec_total_consumption()));
        myViewHolder.comp_total_bill.setText(String.valueOf(bills.get(position).getElec_total_bill()));

        myViewHolder.comp_consumption.setText(String.valueOf(bills.get(position).getElec_consumption()));
        myViewHolder.comp_price_rate.setText(String.valueOf(bills.get(position).getElec_price_rate()));


        myViewHolder.download_btn1.setOnClickListener(new View.OnClickListener() {
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
                canvas.drawText("Date: " + myViewHolder.date_created.getText(), 20, 75, paint);
                canvas.drawText("Due Date: " +  myViewHolder.date.getText(), 20, 85, paint);
                canvas.drawLine(20,65,230,65, forLinePaint);


                canvas.drawLine(20,90,230,90,forLinePaint);

                //readings
                paint.setTextSize(8f);
                paint.setTextSize(8f);
                canvas.drawText("Current Reading: " + myViewHolder.elec_reading.getText(), 20,105,paint);

                int prev_reading = Integer.parseInt(myViewHolder.elec_reading.getText().toString()) - Integer.parseInt(myViewHolder.consumption.getText().toString());
                canvas.drawText("Previous Reading: " + prev_reading, 20,115,paint);
                canvas.drawText("Current Reading - Previous Reading = Tenant Consumption", 30,130,paint);
                paint.setTextSize(10f);
                canvas.drawText("Tenant Consumption: " + " " +myViewHolder.consumption.getText() +" kWh", 55,145,paint);
                paint.setTextSize(8f);
                canvas.drawText("Main Total Bill: " + "₱ "+myViewHolder.total_bill.getText(), 20,165,paint);
                canvas.drawText("Main Meter Consumption: " + myViewHolder.total_consumption.getText() +" kWh", 20,175,paint);
                canvas.drawText("Main Total Bill ÷ Main Meter Consumption = Price per kWh", 27,190,paint);

                paint.setTextSize(10f);
                canvas.drawText("Price per kWh: " + myViewHolder.price_rate.getText(), 80,205,paint);

                paint.setTextSize(8f);
                canvas.drawText("Price per kWh x Tenant Consumption = Total bill Amount", 27,240,paint);
                //to get the same result for calculation

                //to get the same result for calculation

                //total bill
                canvas.drawLine(40,250,210,250, forLinePaint);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(15f);
                canvas.drawText("Amount: "+ "₱ "+ myViewHolder.amount.getText(),120,270,paint);
                paint.setTextSize(20f);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(8f);
                canvas.drawText("Status: " + myViewHolder.statusR.getText(),200,340,paint);

                //invoice count
                paint.setTextSize(12f);
                canvas.drawText(String.valueOf(+1), 230 , 15, paint);
                paint.setTextSize(8f);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Balance: "+ "₱ "+myViewHolder.balance.getText(),50,290,paint);
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
        return bills.size();
    }



    public class myViewHolder extends RecyclerView.ViewHolder {


        Button pay_btn,download_btn1;
        TextView date, date_created, total_consumption,total_bill, price_rate, unit_no,consumption, amount, statusR, elec_reading, water_reading, penalty, balance, comp_total_bill, comp_total_consumption, comp_consumption, comp_price_rate;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            total_consumption = itemView.findViewById(R.id.total_consumption);
            total_bill = itemView.findViewById(R.id.total_bill);
            price_rate = itemView.findViewById(R.id.price_rate);
            consumption = itemView.findViewById(R.id.consumption);
            amount = itemView.findViewById(R.id.amount);
            statusR = itemView.findViewById(R.id.statusR);
            unit_no = itemView.findViewById(R.id.unit_no);

//            pay_btn = itemView.findViewById(R.id.pay_btn);
            download_btn1 = itemView.findViewById(R.id.download_btn1);

            elec_reading = itemView.findViewById(R.id.elec_reading);
            water_reading = itemView.findViewById(R.id.water_reading);
            date_created = itemView.findViewById(R.id.date_created);

            penalty = itemView.findViewById(R.id.penalty);
            balance = itemView.findViewById(R.id.balance);

            comp_total_consumption = itemView.findViewById(R.id.comp_total_consumption);
            comp_total_bill = itemView.findViewById(R.id.comp_total_bill);

            comp_consumption = itemView.findViewById(R.id.comp_consumption);
            comp_price_rate = itemView.findViewById(R.id.comp_price_rate);
        }


    }
}
