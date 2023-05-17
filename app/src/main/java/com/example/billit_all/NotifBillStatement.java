package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotifBillStatement extends AppCompatActivity {

    TextView idTextView, amountValue, userConsValue, unitValue, totalBillValue, totalConsValue, elecTitle, waterTitle, rentTitle, userConsText, totalConsText, totalBillText, amountText;
    ImageView settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_bill_statement);

        idTextView = (TextView) findViewById(R.id.id);
        amountValue = (TextView) findViewById(R.id.amountValue);
        userConsValue = (TextView) findViewById(R.id.userConsValue);
        unitValue = (TextView) findViewById(R.id.unitValue);
        totalBillValue = (TextView) findViewById(R.id.totalBillValue);
        totalConsValue = (TextView) findViewById(R.id.totalConsValue);
        elecTitle = (TextView) findViewById(R.id.elecTitle);
        waterTitle = (TextView) findViewById(R.id.waterTitle);
        rentTitle = (TextView) findViewById(R.id.rentTitle);
        userConsText = (TextView) findViewById(R.id.userConsTxt);
        totalConsText = (TextView) findViewById(R.id.totalConsTxt);
        totalBillText = (TextView) findViewById(R.id.totalBillTxt);
        amountText = (TextView) findViewById(R.id.amountTxt);

        settings = (ImageView) findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifBillStatement.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        int userId = userSharedPreferenceDataSource.getBackendId();
        idTextView.setText(String.valueOf(userId));


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
                        JSONObject data = jsonObject.getJSONObject("data");
                        Log.d("USER_DATA", String.valueOf(data));

                        if (type.equals("bill statement")) {
                            // Handle tenant's verification data

                            if (typeOfId.equals("water_id")) {
                                String totalConsumption = data.getString("total_consumption");
                                String totalAmount = data.getString("total_bill");
                                String unit = data.getString("unit_no");
                                String consumption = data.getString("consumption");
                                String amount = data.getString("amount");
                                totalConsValue.setText(totalConsumption);
                                totalBillValue.setText(totalAmount);
                                unitValue.setText(unit);
                                userConsValue.setText(consumption);
                                amountValue.setText(amount);

                                waterTitle.setVisibility(View.VISIBLE);

                            } else if (typeOfId.equals("electricity_id")) {
                                String totalConsumption = data.getString("total_consumption");
                                String totalAmount = data.getString("total_bill");
                                String unit = data.getString("unit_no");
                                String consumption = data.getString("consumption");
                                String amount = data.getString("amount");
                                totalConsValue.setText(totalConsumption);
                                totalBillValue.setText(totalAmount);
                                unitValue.setText(unit);
                                userConsValue.setText(consumption);
                                amountValue.setText(amount);

                                elecTitle.setVisibility(View.VISIBLE);

                            } else if (typeOfId.equals("rent_unit_id")) {
                                String rent = data.getString("rent_fee");
                                userConsValue.setText(rent);
                                userConsText.setText("Rent Fee:");

                                totalConsText.setVisibility(View.GONE);
                                totalBillText.setVisibility(View.GONE);
                                amountText.setVisibility(View.GONE);
                                totalConsValue.setVisibility(View.GONE);
                                totalBillValue.setVisibility(View.GONE);
                                amountValue.setVisibility(View.GONE);

                                rentTitle.setVisibility(View.VISIBLE);

                            }
                            else {
                                elecTitle.setVisibility(View.INVISIBLE);
                                waterTitle.setVisibility(View.INVISIBLE);
                                rentTitle.setVisibility(View.INVISIBLE);
                            }

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