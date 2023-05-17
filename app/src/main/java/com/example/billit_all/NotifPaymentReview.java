package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotifPaymentReview extends AppCompatActivity {

    TextView idTextView, amountValue, elecTitle, waterTitle, rentTitle;
    EditText paidValue;
    Button statusPaidValue, statusUnpaidValue, receiptBtn;
    ImageView settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_payment_review);

        settings = (ImageView) findViewById(R.id.settings);
        idTextView = (TextView) findViewById(R.id.id);
        amountValue = (TextView) findViewById(R.id.amountValue);
        elecTitle = (TextView) findViewById(R.id.elecTitle);
        waterTitle = (TextView) findViewById(R.id.waterTitle);
        rentTitle = (TextView) findViewById(R.id.rentTitle);
        paidValue = (EditText) findViewById(R.id.paidValue);
        statusPaidValue = (Button) findViewById(R.id.statusPaidValue);
        statusUnpaidValue = (Button) findViewById(R.id.statusUnpaidValue);
        receiptBtn = (Button) findViewById(R.id.receipt);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifPaymentReview.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        receiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifPaymentReview.this, ReceiptViewing.class);
                startActivity(intent);
            }
        });


        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        int userId = userSharedPreferenceDataSource.getBackendId();
        idTextView.setText(String.valueOf(userId));
        Log.d("userId", String.valueOf(userId));


        retrieveProfile();
    }


    public void retrieveProfile() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        String url = "https://bill-it.online/api/get/notification/page";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String type = jsonObject.getString("type");
                        String typeOfId = jsonObject.getString("type_of_id");
                        JSONObject data = jsonObject.getJSONObject("data").getJSONObject("data");
                        Log.d("USER_DATA", String.valueOf(data));
                        String prov = jsonObject.getJSONObject("data").getString("provider");

                        if (prov.equals("Electricity")) {
                            // Handle tenant's verification data
                            String amount = data.getString("amount");
                            String paid = data.getString("amount_paid");
                            String isPaid = data.getString("isPaid");
                            String balance = data.getString("balance");

                            amountValue.setText(amount);
                            paidValue.setText(paid);

                            elecTitle.setVisibility(View.VISIBLE);

                            if (isPaid.equals("1") && balance.equals("0")){
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else if (isPaid.equals("0") && balance.equals("0")) {
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else {
                                statusUnpaidValue.setVisibility(View.VISIBLE);
                            }

                        } else if (prov.equals("Rent")) {
                            String amount = data.getString("amount");
                            String paid = data.getString("amount_paid");
                            String isPaid = data.getString("isPaid");
                            String balance = data.getString("balance");

                            amountValue.setText(amount);
                            paidValue.setText(paid);

                            rentTitle.setVisibility(View.VISIBLE);

                            if (isPaid.equals("1") && balance.equals("0")){
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else if (isPaid.equals("0") && balance.equals("0")) {
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else {
                                statusUnpaidValue.setVisibility(View.VISIBLE);
                            }

                        } else if (prov.equals("Water")) {
                            String amount = data.getString("amount");
                            String paid = data.getString("amount_paid");
                            String isPaid = data.getString("isPaid");
                            String balance = data.getString("balance");

                            amountValue.setText(amount);
                            paidValue.setText(paid);

                            waterTitle.setVisibility(View.VISIBLE);

                            if (isPaid.equals("1") && balance.equals("0")){
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else if (isPaid.equals("0") && balance.equals("0")) {
                                statusPaidValue.setVisibility(View.VISIBLE);
                            } else {
                                statusUnpaidValue.setVisibility(View.VISIBLE);
                            }

                        }
                        else {
                            elecTitle.setVisibility(View.INVISIBLE);
                            waterTitle.setVisibility(View.INVISIBLE);
                            rentTitle.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", idTextView.getText().toString());
                return params;
            }
        };
        queue.add(request);
    }
}