package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CashPayment extends AppCompatActivity {

    LinearLayout cashContainer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);

        cashContainer = findViewById(R.id.cashContainer);

        retrieveBills();

    }

    public void retrieveBills(){
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.updateProfile(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/cash/payment/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray cashPayment = response.getJSONArray("combinedData");

                                                    cashContainer.removeAllViews();

                                                    for (int i = 0; i < cashPayment.length(); i++) {
                                                        JSONObject cash = cashPayment.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.cash_card, null);

                                                        CircularImageView billTypePic = view.findViewById(R.id.billTypePic);
                                                        TextView billType = view.findViewById(R.id.billType);
                                                        TextView billDate = view.findViewById(R.id.billDate);
                                                        TextView unitNum = view.findViewById(R.id.unitNum);
                                                        TextView billAmount = view.findViewById(R.id.billAmount);
                                                        TextView billPenaltyTitle = view.findViewById(R.id.billPenaltyTitle);
                                                        TextView billPenalty = view.findViewById(R.id.billPenalty);
                                                        TextView billBalance = view.findViewById(R.id.billBalance);
                                                        CheckBox cashCheckbox = view.findViewById(R.id.cashCheckbox);
                                                        EditText cashReceived = view.findViewById(R.id.cashReceived);
                                                        Button confirmCash = view.findViewById(R.id.confirmCash);

                                                        String typeBill = cash.getString("source_table");
                                                        if (typeBill.equals("elec")) {
                                                            String typeBillText = "<font color='#000000'><b>Electricity</b></font>";
                                                            billType.setText(Html.fromHtml(typeBillText));
                                                            billTypePic.setImageResource(R.drawable.electricity_icon);
                                                        } else if (typeBill.equals("water")) {
                                                            String typeBillText = "<font color='#000000'><b>Water</b></font>";
                                                            billType.setText(Html.fromHtml(typeBillText));
                                                            billTypePic.setImageResource(R.drawable.water_icon);
                                                        } else if (typeBill.equals("rent")) {
                                                            String typeBillText = "<font color='#000000'><b>Rent</b></font>";
                                                            billType.setText(Html.fromHtml(typeBillText));
                                                            billTypePic.setImageResource(R.drawable.house);
                                                        }

//                                                        String dateBill = cash.getString("date");
//                                                        billDate.setText(dateBill);
                                                        String dateBill = cash.getString("date");
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                        Date date = dateFormat.parse(dateBill);
                                                        dateFormat.applyPattern("MMMM yyyy");
                                                        String formattedDate = dateFormat.format(date);
                                                        billDate.setText(formattedDate);
                                                        String numUnit = cash.getString("unit_no");
                                                        unitNum.setText(numUnit);
                                                        String amountBill = cash.getString("amount");
                                                        billAmount.setText(amountBill);
                                                        String penaltyBill = cash.getString("penalty");
                                                        if (penaltyBill.equals("0")) {
//                                                            billPenalty.setText(penaltyBill);
                                                            billPenaltyTitle.setVisibility(View.GONE);
                                                            billPenalty.setVisibility(View.GONE);
                                                        } else {
                                                            billPenaltyTitle.setVisibility(View.VISIBLE);
                                                            billPenalty.setVisibility(View.VISIBLE);
                                                            billPenalty.setText(penaltyBill);
                                                        }
                                                        String balanceBill = cash.getString("balance");
                                                        billBalance.setText(balanceBill);

                                                        String providerId = cash.getString("id");
                                                        String provider = cash.getString("provider_type");
//                                                        String provider = billType.getText().toString();

                                                        //checkbox
                                                        cashCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                if (isChecked) {
                                                                    cashReceived.setVisibility(View.VISIBLE);
                                                                    confirmCash.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    cashReceived.setVisibility(View.GONE);
                                                                    confirmCash.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }); //end of checkbox

                                                        //click confirm cash payment button
                                                        confirmCash.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
//                                                                String url = "http://10.0.2.2:8000/api/cash-payment/" + providerId + "/" + provider;

                                                                final String amount_paid = cashReceived.getText().toString();
                                                                final String unit_id = unitNum.getText().toString();

                                                                Calendar calendar = Calendar.getInstance();
                                                                // Create a SimpleDateFormat object and set the desired date format
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                                // Format the current date and time as a string
                                                                String createdAt = dateFormat.format(calendar.getTime());

                                                                final JSONObject form = new JSONObject();
                                                                try {
                                                                    form.put("amount", amount_paid);
//                                                                    if (typeBill.equals("elec")) {
//                                                                        form.put("electricity_id", providerId);
//                                                                        Log.d("Data Sent", "Electricity ID: " + providerId);
//                                                                    } else if (typeBill.equals("water")) {
//                                                                        form.put("water_id", providerId);
//                                                                    } else if (typeBill.equals("rent")) {
//                                                                        form.put("rent_unit_id", providerId);
//                                                                    }
////                                                                    form.put("electricity_id", providerId);
////                                                                    form.put("water_id", providerId);
////                                                                    form.put("rent_unit_id", providerId);
//                                                                    form.put("unit_id", unit_id);
//                                                                    Log.d("Data Sent", "Unit ID: " + unit_id);
//                                                                    form.put("created_at", createdAt);
//                                                                    form.put("updated_at", createdAt);
                                                                } catch (JSONException e) {
                                                                    Toast.makeText(CashPayment.this, "No data", Toast.LENGTH_SHORT).show();
                                                                    e.printStackTrace();
                                                                }

                                                                RequestQueue queueConfirm = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                                                BackendJsonObjectRequest requestConfirm = new BackendJsonObjectRequest(
                                                                        Request.Method.POST,
                                                                        "/api/cash/payment/" + providerId + "/" + provider, form,
//                                                                        "/api/cash/payment/" + providerId + "/" + provider, form,
                                                                        new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                try {
                                                                                    String message = response.getString("message");
                                                                                    Log.d("Data Sent", "Provider: " + provider);
                                                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                                                                                    Toast.makeText(CashPayment.this, "Received!!!", Toast.LENGTH_SHORT).show();
                                                                                    loginPreferenceDataSource.getBackendToken();
                                                                                    cashContainer.removeView(view);
                                                                                    recreate();

                                                                                } catch (JSONException e) {
                                                                                    Toast.makeText(CashPayment.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(CashPayment.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                );
                                                                queueConfirm.add(requestConfirm);
//                                                                Log.d("response", response);
                                                            }
                                                        }); //end of cash payment button

                                                        cashContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                request.authenticated(getApplicationContext());
                                queue.add(request);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}