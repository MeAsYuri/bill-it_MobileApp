package com.example.billit_all.Bill_history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class BillsTenant extends AppCompatActivity {

    Button allBtn, paidBtn, unpaidBtn, penaltyBtn;

    Spinner select_date_spinner;

    RecyclerView billsRecycler, billsRecycler2, billsRecycler3;

    TextView selected_calendar, eReceiptID;

    BillAdapter billAdapter;
    ArrayList<Bill> bills = new ArrayList<>();

    BillAdapter2 billAdapter2;
    ArrayList<Bill2> bills2 = new ArrayList<>();

    BillAdapter3 billAdapter3;
    ArrayList<Bill3> bills3 = new ArrayList<>();

    ImageView rentImg;
    TextView rentTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_tenant);

        allBtn = findViewById(R.id.allBtn);
        paidBtn = findViewById(R.id.paidBtn);
        unpaidBtn = findViewById(R.id.unpaidBtn);
        penaltyBtn = findViewById(R.id.penaltyBtn);

        select_date_spinner = findViewById(R.id.select_date_spinner);
        billsRecycler = findViewById(R.id.billsRecycler);
        billsRecycler2 = findViewById(R.id.billsRecycler2);
        billsRecycler3 = findViewById(R.id.billsRecycler3);

        selected_calendar = findViewById(R.id.selected_calendar);

        eReceiptID = findViewById(R.id.eReceiptID);

        rentImg = findViewById(R.id.rentImg);
        rentTextView = findViewById(R.id.rentTextView);

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
                                String user_id = user.getString("user_id");
                                Log.d("USER_ID", user_id);



                                String[] DATE = {"all", "month", "year"};
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(BillsTenant.this, android.R.layout.simple_spinner_item, DATE);

                                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                select_date_spinner.setAdapter(spinnerAdapter);

                                select_date_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedItem = parent.getItemAtPosition(position).toString();
                                        Toast.makeText(getApplicationContext(), "You selected " + selectedItem, Toast.LENGTH_SHORT).show();
                                        billsRecycler3.setVisibility(View.VISIBLE);
                                        rentImg.setVisibility(View.VISIBLE);
                                        rentTextView.setVisibility(View.VISIBLE);



//                                        Electricity receipts
                                        RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                        BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                Request.Method.GET,
                                                "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            JSONArray billsJson = response.getJSONArray("data");
                                                            bills.clear();
                                                            Log.d("data", String.valueOf(billsJson));

                                                            if (billsJson.length() == 0) {
                                                                billsRecycler.setVisibility(View.GONE);
                                                                Log.d("data", String.valueOf(billsJson));
                                                            } else {
                                                                Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                billsRecycler.setVisibility(View.VISIBLE);
                                                                for (int i = 0; i < billsJson.length(); i++) {
                                                                    JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                    billsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                    billsRecycler.setAdapter(billAdapter);
                                                                    billAdapter = new BillAdapter(bills);

                                                                    String elec_id = recordsJson.getString("elec_id");
                                                                    Integer elec_paid = recordsJson.getInt("elec_paid");
                                                                    Double elec_amount = recordsJson.getDouble("elec_amount");
                                                                    Double elec_penalty = recordsJson.getDouble("elec_penalty");
                                                                    Double elec_balance = recordsJson.getDouble("elec_balance");

                                                                    String elec_due_date = recordsJson.getString("elec_due_date");
                                                                    String elec_date = recordsJson.getString("elec_date");
                                                                    Integer elec_reading = recordsJson.getInt("elec_reading");
                                                                    Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                    Integer elec_total_bill = recordsJson.getInt("elec_total_bill");
                                                                    Integer elec_total_consumption = recordsJson.getInt("elec_total_consumption");
                                                                    String elec_unit_id = recordsJson.getString("elec_unit_id");
                                                                    Log.d("elec_id", elec_id);

                                                                    double elec_price_rate = 0.0;
                                                                    elec_price_rate = (double) elec_total_bill / elec_total_consumption;
                                                                    String formattedPrice = String.format("%.2f", elec_price_rate);
                                                                    elec_price_rate = Double.parseDouble(formattedPrice);

                                                                    String elecPaidString = "";

                                                                    if (elec_paid == 1) {
                                                                        elecPaidString = "PAID";
                                                                    }
                                                                    if (elec_paid == 0) {
                                                                        elecPaidString = "UNPAID";
                                                                    }


                                                                    Bill bill = new Bill(elec_id, elecPaidString, elec_amount, elec_penalty,elec_due_date,elec_reading,elec_consumption,elec_total_bill,elec_total_consumption, elec_price_rate, elec_unit_id, elec_date, elec_balance);
                                                                    bills.add(bill);
                                                                }

                                                                }
                                                            if (billAdapter != null) {
                                                                billAdapter.notifyDataSetChanged();
                                                            }



                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                           Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        );

                                        request2.authenticated(getApplicationContext());
                                        queue2.add(request2);

//                                        Water Receipts
                                        RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                        BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                Request.Method.GET,
                                                "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            JSONArray billsJson = response.getJSONArray("data");
                                                            bills2.clear();

                                                            if (response.getJSONArray("data").length() == 0) {
                                                                billsRecycler2.setVisibility(View.GONE);
                                                            } else {
                                                                Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                billsRecycler2.setVisibility(View.VISIBLE);
                                                                for (int i = 0; i < billsJson.length(); i++) {
                                                                    JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                    billsRecycler2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                    billsRecycler2.setAdapter(billAdapter2);
                                                                    billAdapter2 = new BillAdapter2(bills2);

                                                                    String water_id = recordsJson.getString("water_id");
                                                                    Integer water_paid = recordsJson.getInt("water_paid");
                                                                    Double water_amount = recordsJson.getDouble("water_amount");
                                                                    Double water_penalty = recordsJson.getDouble("water_penalty");
                                                                    Double water_balance = recordsJson.getDouble("water_balance");

                                                                    String water_due_date = recordsJson.getString("water_due_date");
                                                                    String water_date = recordsJson.getString("water_date");
                                                                    Integer water_reading = recordsJson.getInt("water_reading");
                                                                    Integer water_consumption = recordsJson.getInt("water_consumption");
                                                                    Integer water_total_bill = recordsJson.getInt("water_total_bill");
                                                                    Integer water_total_consumption = recordsJson.getInt("water_total_consumption");
                                                                    String water_unit_id = recordsJson.getString("water_unit_id");

                                                                    double water_price_rate = 0.0;
                                                                    water_price_rate = (double) water_total_bill / water_total_consumption;
                                                                    String formattedPrice = String.format("%.2f", water_price_rate);
                                                                    water_price_rate = Double.parseDouble(formattedPrice);

                                                                    String waterPaidString = "";

                                                                    if (water_paid == 1) {
                                                                        waterPaidString = "PAID";
                                                                    }
                                                                    if (water_paid == 0) {
                                                                        waterPaidString = "UNPAID";
                                                                    }


                                                                    Bill2 bill2 = new Bill2(water_id, waterPaidString, water_amount, water_penalty,water_due_date,water_reading,water_consumption,water_total_bill,water_total_consumption, water_price_rate, water_unit_id, water_date, water_balance);
                                                                    bills2.add(bill2);


                                                                }
                                                            }
                                                            if (billAdapter2 != null) {
                                                                billAdapter2.notifyDataSetChanged();
                                                            }


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        );

                                        request3.authenticated(getApplicationContext());
                                        queue3.add(request3);


//                                        Rent Receipts
                                        RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                        BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                Request.Method.GET,
                                                "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
                                                            JSONArray billsJson = response.getJSONArray("data");
                                                            bills3.clear();

                                                            if (response.getJSONArray("data").length() == 0) {
                                                                billsRecycler3.setVisibility(View.GONE);
                                                            } else {
                                                                Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                billsRecycler3.setVisibility(View.VISIBLE);
                                                                for (int i = 0; i < billsJson.length(); i++) {
                                                                    JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                    billsRecycler3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                    billsRecycler3.setAdapter(billAdapter3);
                                                                    billAdapter3 = new BillAdapter3(bills3);

                                                                    String rent_id = recordsJson.getString("rent_id");
                                                                    Integer rent_amount = recordsJson.getInt("rent_amount");
                                                                    Integer rent_paid = recordsJson.getInt("rent_paid");
                                                                    String rent_date = recordsJson.getString("rent_date");
                                                                    Integer rent_unit_id = recordsJson.getInt("rent_unit_id");
                                                                    Double rent_penalty = recordsJson.getDouble("rent_penalty");
                                                                    Double rent_balance = recordsJson.getDouble("rent_balance");
                                                                    String elec_due_date = recordsJson.getString("elec_due_date");

                                                                    String rentPaidString = "";

                                                                    if (rent_paid == 1) {
                                                                        rentPaidString = "PAID";
                                                                    }
                                                                    if (rent_paid == 0) {
                                                                        rentPaidString = "UNPAID";
                                                                    }


                                                                    Bill3 bill3 = new Bill3(rent_id, rentPaidString, rent_date, rent_amount, rent_unit_id, rent_penalty,rent_balance, elec_due_date);
                                                                    bills3.add(bill3);


                                                                }
                                                            }
                                                            if (billAdapter3 != null) {
                                                                billAdapter3.notifyDataSetChanged();
                                                            }


                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        );

                                        request4.authenticated(getApplicationContext());
                                        queue4.add(request4);


                                        allBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                billsRecycler3.setVisibility(View.VISIBLE);
                                                rentImg.setVisibility(View.VISIBLE);
                                                rentTextView.setVisibility(View.VISIBLE);
//                                                Electricity receipts
                                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler.setAdapter(billAdapter);
                                                                            billAdapter = new BillAdapter(bills);

                                                                            String elec_id = recordsJson.getString("elec_id");
                                                                            Integer elec_paid = recordsJson.getInt("elec_paid");
                                                                            Double elec_amount = recordsJson.getDouble("elec_amount");
                                                                            Double elec_penalty = recordsJson.getDouble("elec_penalty");
                                                                            Double elec_balance = recordsJson.getDouble("elec_balance");

                                                                            String elec_due_date = recordsJson.getString("elec_due_date");
                                                                            String elec_date = recordsJson.getString("elec_date");
                                                                            Integer elec_reading = recordsJson.getInt("elec_reading");
                                                                            Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                            Integer elec_total_bill = recordsJson.getInt("elec_total_bill");
                                                                            Integer elec_total_consumption = recordsJson.getInt("elec_total_consumption");
                                                                            String elec_unit_id = recordsJson.getString("elec_unit_id");

                                                                            double elec_price_rate = 0.0;
                                                                            elec_price_rate = (double) elec_total_bill / elec_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", elec_price_rate);
                                                                            elec_price_rate = Double.parseDouble(formattedPrice);

                                                                            String elecPaidString = "";

                                                                            if (elec_paid == 1) {
                                                                                elecPaidString = "PAID";
                                                                            }
                                                                            if (elec_paid == 0) {
                                                                                elecPaidString = "UNPAID";
                                                                            }


                                                                            Bill bill = new Bill(elec_id, elecPaidString, elec_amount, elec_penalty,elec_due_date,elec_reading,elec_consumption,elec_total_bill,elec_total_consumption, elec_price_rate, elec_unit_id, elec_date, elec_balance);
                                                                            bills.add(bill);


                                                                        }
                                                                    }
                                                                    if (billAdapter != null) {
                                                                        billAdapter.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request2.authenticated(getApplicationContext());
                                                queue2.add(request2);

//                                        Water Receipts
                                                RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills2.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler2.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler2.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler2.setAdapter(billAdapter2);
                                                                            billAdapter2 = new BillAdapter2(bills2);

                                                                            String water_id = recordsJson.getString("water_id");
                                                                            Integer water_paid = recordsJson.getInt("water_paid");
                                                                            Double water_amount = recordsJson.getDouble("water_amount");
                                                                            Double water_penalty = recordsJson.getDouble("water_penalty");
                                                                            Double water_balance = recordsJson.getDouble("water_balance");

                                                                            String water_due_date = recordsJson.getString("water_due_date");
                                                                            String water_date = recordsJson.getString("water_date");
                                                                            Integer water_reading = recordsJson.getInt("water_reading");
                                                                            Integer water_consumption = recordsJson.getInt("water_consumption");
                                                                            Integer water_total_bill = recordsJson.getInt("water_total_bill");
                                                                            Integer water_total_consumption = recordsJson.getInt("water_total_consumption");
                                                                            String water_unit_id = recordsJson.getString("water_unit_id");

                                                                            double water_price_rate = 0.0;
                                                                            water_price_rate = (double) water_total_bill / water_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", water_price_rate);
                                                                            water_price_rate = Double.parseDouble(formattedPrice);

                                                                            String waterPaidString = "";

                                                                            if (water_paid == 1) {
                                                                                waterPaidString = "PAID";
                                                                            }
                                                                            if (water_paid == 0) {
                                                                                waterPaidString = "UNPAID";
                                                                            }


                                                                            Bill2 bill2 = new Bill2(water_id, waterPaidString, water_amount, water_penalty,water_due_date,water_reading,water_consumption,water_total_bill,water_total_consumption, water_price_rate, water_unit_id, water_date, water_balance);
                                                                            bills2.add(bill2);


                                                                        }
                                                                    }
                                                                    if (billAdapter2 != null) {
                                                                        billAdapter2.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request3.authenticated(getApplicationContext());
                                                queue3.add(request3);


//                                        Rent Receipts
                                                RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills3.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler3.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler3.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler3.setAdapter(billAdapter3);
                                                                            billAdapter3 = new BillAdapter3(bills3);

                                                                            String rent_id = recordsJson.getString("rent_id");
                                                                            Integer rent_amount = recordsJson.getInt("rent_amount");
                                                                            Integer rent_paid = recordsJson.getInt("rent_paid");
                                                                            String rent_date = recordsJson.getString("rent_date");
                                                                            Integer rent_unit_id = recordsJson.getInt("rent_unit_id");
                                                                            Double rent_penalty = recordsJson.getDouble("rent_penalty");
                                                                            Double rent_balance = recordsJson.getDouble("rent_balance");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");

                                                                            String rentPaidString = "";

                                                                            if (rent_paid == 1) {
                                                                                rentPaidString = "PAID";
                                                                            }
                                                                            if (rent_paid == 0) {
                                                                                rentPaidString = "UNPAID";
                                                                            }


                                                                            Bill3 bill3 = new Bill3(rent_id, rentPaidString, rent_date, rent_amount, rent_unit_id, rent_penalty,rent_balance, elec_due_date);
                                                                            bills3.add(bill3);


                                                                        }
                                                                    }
                                                                    if (billAdapter3 != null) {
                                                                        billAdapter3.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request4.authenticated(getApplicationContext());
                                                queue4.add(request4);
                                            }
                                        });

                                        paidBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                billsRecycler3.setVisibility(View.VISIBLE);
                                                rentImg.setVisibility(View.VISIBLE);
                                                rentTextView.setVisibility(View.VISIBLE);


//                                        Electricity receipts
                                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler.setAdapter(billAdapter);
                                                                            billAdapter = new BillAdapter(bills);

                                                                            String elec_id = recordsJson.getString("elec_id");
                                                                            Integer elec_paid = recordsJson.getInt("elec_paid");
                                                                            Double elec_amount = recordsJson.getDouble("elec_amount");
                                                                            Double elec_penalty = recordsJson.getDouble("elec_penalty");
                                                                            Double elec_balance = recordsJson.getDouble("elec_balance");

                                                                            String elec_due_date = recordsJson.getString("elec_due_date");
                                                                            String elec_date = recordsJson.getString("elec_date");
                                                                            Integer elec_reading = recordsJson.getInt("elec_reading");
                                                                            Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                            Integer elec_total_bill = recordsJson.getInt("elec_total_bill");
                                                                            Integer elec_total_consumption = recordsJson.getInt("elec_total_consumption");
                                                                            String elec_unit_id = recordsJson.getString("elec_unit_id");

                                                                            double elec_price_rate = 0.0;
                                                                            elec_price_rate = (double) elec_total_bill / elec_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", elec_price_rate);
                                                                            elec_price_rate = Double.parseDouble(formattedPrice);



//
                                                                            String elecPaidString = "";
                                                                            if (elec_paid == 1) {
                                                                                elecPaidString = "PAID";
                                                                                Bill bill = new Bill(elec_id, elecPaidString, elec_amount, elec_penalty,elec_due_date,elec_reading,elec_consumption,elec_total_bill,elec_total_consumption, elec_price_rate, elec_unit_id, elec_date, elec_balance);
                                                                                bills.add(bill);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter != null) {
                                                                        billAdapter.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request2.authenticated(getApplicationContext());
                                                queue2.add(request2);


//                                        Water Receipts
                                                RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills2.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler2.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler2.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler2.setAdapter(billAdapter2);
                                                                            billAdapter2 = new BillAdapter2(bills2);

                                                                            String water_id = recordsJson.getString("water_id");
                                                                            Integer water_paid = recordsJson.getInt("water_paid");
                                                                            Double water_amount = recordsJson.getDouble("water_amount");
                                                                            Double water_penalty = recordsJson.getDouble("water_penalty");
                                                                            Double water_balance = recordsJson.getDouble("water_balance");

                                                                            String water_due_date = recordsJson.getString("water_due_date");
                                                                            String water_date = recordsJson.getString("water_date");
                                                                            Integer water_reading = recordsJson.getInt("water_reading");
                                                                            Integer water_consumption = recordsJson.getInt("water_consumption");
                                                                            Integer water_total_bill = recordsJson.getInt("water_total_bill");
                                                                            Integer water_total_consumption = recordsJson.getInt("water_total_consumption");
                                                                            String water_unit_id = recordsJson.getString("water_unit_id");

                                                                            double water_price_rate = 0.0;
                                                                            water_price_rate = (double) water_total_bill / water_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", water_price_rate);
                                                                            water_price_rate = Double.parseDouble(formattedPrice);

                                                                            String waterPaidString = "";

                                                                            if (water_paid == 1) {
                                                                                waterPaidString = "PAID";
                                                                                Bill2 bill2 = new Bill2(water_id, waterPaidString, water_amount, water_penalty,water_due_date,water_reading,water_consumption,water_total_bill,water_total_consumption, water_price_rate, water_unit_id, water_date, water_balance);
                                                                                bills2.add(bill2);

                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter2 != null) {
                                                                        billAdapter2.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request3.authenticated(getApplicationContext());
                                                queue3.add(request3);


//                                        Rent Receipts
                                                RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills3.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler3.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler3.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler3.setAdapter(billAdapter3);
                                                                            billAdapter3 = new BillAdapter3(bills3);

                                                                            String rent_id = recordsJson.getString("rent_id");
                                                                            Integer rent_amount = recordsJson.getInt("rent_amount");
                                                                            Integer rent_paid = recordsJson.getInt("rent_paid");
                                                                            String rent_date = recordsJson.getString("rent_date");
                                                                            Integer rent_unit_id = recordsJson.getInt("rent_unit_id");
                                                                            Double rent_penalty = recordsJson.getDouble("rent_penalty");
                                                                            Double rent_balance = recordsJson.getDouble("rent_balance");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");

                                                                            String rentPaidString = "";

                                                                            if (rent_paid == 1) {
                                                                                rentPaidString = "PAID";
                                                                                Bill3 bill3 = new Bill3(rent_id, rentPaidString, rent_date, rent_amount, rent_unit_id, rent_penalty, rent_balance,elec_due_date);
                                                                                bills3.add(bill3);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter3 != null) {
                                                                        billAdapter3.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request4.authenticated(getApplicationContext());
                                                queue4.add(request4);

                                            }
                                        });

                                        unpaidBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                billsRecycler3.setVisibility(View.VISIBLE);
                                                rentImg.setVisibility(View.VISIBLE);
                                                rentTextView.setVisibility(View.VISIBLE);


//                                        Electricity receipts
                                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler.setAdapter(billAdapter);
                                                                            billAdapter = new BillAdapter(bills);

                                                                            String elec_id = recordsJson.getString("elec_id");
                                                                            Integer elec_paid = recordsJson.getInt("elec_paid");
                                                                            Double elec_amount = recordsJson.getDouble("elec_amount");
                                                                            Double elec_penalty = recordsJson.getDouble("elec_penalty");
                                                                            Double elec_balance = recordsJson.getDouble("elec_balance");

                                                                            String elec_date = recordsJson.getString("elec_date");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");
                                                                            Integer elec_reading = recordsJson.getInt("elec_reading");
                                                                            Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                            Integer elec_total_bill = recordsJson.getInt("elec_total_bill");
                                                                            Integer elec_total_consumption = recordsJson.getInt("elec_total_consumption");
                                                                            String elec_unit_id = recordsJson.getString("elec_unit_id");

                                                                            double elec_price_rate = 0.0;
                                                                            elec_price_rate = (double) elec_total_bill / elec_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", elec_price_rate);
                                                                            elec_price_rate = Double.parseDouble(formattedPrice);



//
                                                                            String elecPaidString = "";
                                                                            if (elec_paid == 0) {
                                                                                elecPaidString = "UNPAID";
                                                                                Bill bill = new Bill(elec_id, elecPaidString, elec_amount, elec_penalty,elec_due_date,elec_reading,elec_consumption,elec_total_bill,elec_total_consumption, elec_price_rate, elec_unit_id, elec_date, elec_balance);
                                                                                bills.add(bill);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter != null) {
                                                                        billAdapter.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request2.authenticated(getApplicationContext());
                                                queue2.add(request2);


//                                        Water Receipts
                                                RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills2.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler2.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler2.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler2.setAdapter(billAdapter2);
                                                                            billAdapter2 = new BillAdapter2(bills2);

                                                                            String water_id = recordsJson.getString("water_id");
                                                                            Integer water_paid = recordsJson.getInt("water_paid");
                                                                            Double water_amount = recordsJson.getDouble("water_amount");
                                                                            Double water_penalty = recordsJson.getDouble("water_penalty");
                                                                            Double water_balance = recordsJson.getDouble("water_balance");

                                                                            String water_due_date = recordsJson.getString("water_due_date");
                                                                            String water_date = recordsJson.getString("water_date");
                                                                            Integer water_reading = recordsJson.getInt("water_reading");
                                                                            Integer water_consumption = recordsJson.getInt("water_consumption");
                                                                            Integer water_total_bill = recordsJson.getInt("water_total_bill");
                                                                            Integer water_total_consumption = recordsJson.getInt("water_total_consumption");
                                                                            String water_unit_id = recordsJson.getString("water_unit_id");

                                                                            double water_price_rate = 0.0;
                                                                            water_price_rate = (double) water_total_bill / water_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", water_price_rate);
                                                                            water_price_rate = Double.parseDouble(formattedPrice);

                                                                            String waterPaidString = "";

                                                                            if (water_paid == 0) {
                                                                                waterPaidString = "UNPAID";
                                                                                Bill2 bill2 = new Bill2(water_id, waterPaidString, water_amount, water_penalty,water_due_date,water_reading,water_consumption,water_total_bill,water_total_consumption, water_price_rate, water_unit_id, water_date,water_balance);
                                                                                bills2.add(bill2);

                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter2 != null) {
                                                                        billAdapter2.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request3.authenticated(getApplicationContext());
                                                queue3.add(request3);


//                                        Rent Receipts
                                                RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills3.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler3.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler3.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler3.setAdapter(billAdapter3);
                                                                            billAdapter3 = new BillAdapter3(bills3);

                                                                            String rent_id = recordsJson.getString("rent_id");
                                                                            Integer rent_amount = recordsJson.getInt("rent_amount");
                                                                            Integer rent_paid = recordsJson.getInt("rent_paid");
                                                                            String rent_date = recordsJson.getString("rent_date");
                                                                            Integer rent_unit_id = recordsJson.getInt("rent_unit_id");
                                                                            Double rent_penalty = recordsJson.getDouble("rent_penalty");
                                                                            Double rent_balance = recordsJson.getDouble("rent_balance");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");

                                                                            String rentPaidString = "";

                                                                            if (rent_paid == 0) {
                                                                                rentPaidString = "UNPAID";
                                                                                Bill3 bill3 = new Bill3(rent_id, rentPaidString, rent_date, rent_amount, rent_unit_id, rent_penalty, rent_balance, elec_due_date);
                                                                                bills3.add(bill3);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter3 != null) {
                                                                        billAdapter3.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request4.authenticated(getApplicationContext());
                                                queue4.add(request4);

                                            }
                                        });

                                        penaltyBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

//                                                Electricity receipts
                                                RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler.setVisibility(View.GONE);
                                                                    } else {


                                                                        billsRecycler.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler.setAdapter(billAdapter);
                                                                            billAdapter = new BillAdapter(bills);

                                                                            String elec_id = recordsJson.getString("elec_id");
                                                                            Integer elec_paid = recordsJson.getInt("elec_paid");
                                                                            Double elec_amount = recordsJson.getDouble("elec_amount");
                                                                            Double elec_penalty = recordsJson.getDouble("elec_penalty");
                                                                            Double elec_balance = recordsJson.getDouble("elec_balance");

                                                                            String elec_date = recordsJson.getString("elec_date");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");
                                                                            Integer elec_reading = recordsJson.getInt("elec_reading");
                                                                            Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                            Integer elec_total_bill = recordsJson.getInt("elec_total_bill");
                                                                            Integer elec_total_consumption = recordsJson.getInt("elec_total_consumption");
                                                                            String elec_unit_id = recordsJson.getString("elec_unit_id");

                                                                            double elec_price_rate = 0.0;
                                                                            elec_price_rate = (double) elec_total_bill / elec_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", elec_price_rate);
                                                                            elec_price_rate = Double.parseDouble(formattedPrice);

                                                                            String elecPaidString = "";

                                                                            if (elec_paid == 1) {
                                                                                elecPaidString = "PAID";
                                                                            }
                                                                            if (elec_paid == 0) {
                                                                                elecPaidString = "UNPAID";
                                                                            }

                                                                            if (elec_penalty == 0) {
//
                                                                            }
                                                                            else {
                                                                                Bill bill = new Bill(elec_id, elecPaidString, elec_amount, elec_penalty,elec_due_date,elec_reading,elec_consumption,elec_total_bill,elec_total_consumption, elec_price_rate, elec_unit_id, elec_date, elec_balance);
                                                                                bills.add(bill);
                                                                            }



                                                                        }
                                                                    }
                                                                    if (billAdapter != null) {
                                                                        billAdapter.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request2.authenticated(getApplicationContext());
                                                queue2.add(request2);

//                                        Water Receipts
                                                RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills2.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler2.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler2.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler2.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler2.setAdapter(billAdapter2);
                                                                            billAdapter2 = new BillAdapter2(bills2);

                                                                            String water_id = recordsJson.getString("water_id");
                                                                            Integer water_paid = recordsJson.getInt("water_paid");
                                                                            Double water_amount = recordsJson.getDouble("water_amount");
                                                                            Double water_penalty = recordsJson.getDouble("water_penalty");
                                                                            Double water_balance = recordsJson.getDouble("water_balance");

                                                                            String water_due_date = recordsJson.getString("water_due_date");
                                                                            String water_date = recordsJson.getString("water_date");
                                                                            Integer water_reading = recordsJson.getInt("water_reading");
                                                                            Integer water_consumption = recordsJson.getInt("water_consumption");
                                                                            Integer water_total_bill = recordsJson.getInt("water_total_bill");
                                                                            Integer water_total_consumption = recordsJson.getInt("water_total_consumption");
                                                                            String water_unit_id = recordsJson.getString("water_unit_id");

                                                                            double water_price_rate = 0.0;
                                                                            water_price_rate = (double) water_total_bill / water_total_consumption;
                                                                            String formattedPrice = String.format("%.2f", water_price_rate);
                                                                            water_price_rate = Double.parseDouble(formattedPrice);

                                                                            String waterPaidString = "";

                                                                            if (water_paid == 1) {
                                                                                waterPaidString = "PAID";
                                                                            }
                                                                            if (water_paid == 0) {
                                                                                waterPaidString = "UNPAID";
                                                                            }
                                                                            Log.d("water_penalty", String.valueOf(water_penalty));
                                                                            if (water_penalty == 0) {

                                                                            } else {
                                                                                Bill2 bill2 = new Bill2(water_id, waterPaidString, water_amount, water_penalty,water_due_date,water_reading,water_consumption,water_total_bill,water_total_consumption, water_price_rate, water_unit_id, water_date, water_balance);
                                                                                bills2.add(bill2);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter2 != null) {
                                                                        billAdapter2.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request3.authenticated(getApplicationContext());
                                                queue3.add(request3);


//                                        Rent Receipts
                                                RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                        Request.Method.GET,
                                                        "/api/bills/all/mobile/" + user_id + "/" + selectedItem,
                                                        null,
                                                        new Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                                try {
                                                                    JSONArray billsJson = response.getJSONArray("data");
                                                                    bills3.clear();

                                                                    if (response.getJSONArray("data").length() == 0) {
                                                                        billsRecycler3.setVisibility(View.GONE);
                                                                    } else {
                                                                        Log.d("selectedTenantId", String.valueOf(billsJson));

                                                                        billsRecycler3.setVisibility(View.VISIBLE);
                                                                        for (int i = 0; i < billsJson.length(); i++) {
                                                                            JSONObject recordsJson = billsJson.getJSONObject(i);
                                                                            billsRecycler3.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                                            billsRecycler3.setAdapter(billAdapter3);
                                                                            billAdapter3 = new BillAdapter3(bills3);

                                                                            String rent_id = recordsJson.getString("rent_id");
                                                                            Integer rent_amount = recordsJson.getInt("rent_amount");
                                                                            Integer rent_paid = recordsJson.getInt("rent_paid");
                                                                            String rent_date = recordsJson.getString("rent_date");
                                                                            Integer rent_unit_id = recordsJson.getInt("rent_unit_id");
                                                                            Double rent_penalty = recordsJson.getDouble("rent_penalty");
                                                                            Double rent_balance = recordsJson.getDouble("rent_balance");
                                                                            String elec_due_date = recordsJson.getString("elec_due_date");

                                                                            String rentPaidString = "";

                                                                            if (rent_paid == 1) {
                                                                                rentPaidString = "PAID";
                                                                            }
                                                                            if (rent_paid == 0) {
                                                                                rentPaidString = "UNPAID";
                                                                            }

                                                                            if (rent_penalty == 0) {

                                                                            }else {
                                                                                Bill3 bill3 = new Bill3(rent_id, rentPaidString, rent_date, rent_amount, rent_unit_id, rent_penalty, rent_balance, elec_due_date);
                                                                                bills3.add(bill3);
                                                                            }


                                                                        }
                                                                    }
                                                                    if (billAdapter3 != null) {
                                                                        billAdapter3.notifyDataSetChanged();
                                                                    }


                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                );

                                                request4.authenticated(getApplicationContext());
                                                queue4.add(request4);

                                            }
                                        });



                                    }
//                                    end of ItemSelect

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT).show();
                                    }
                                });


//                                end of user_id
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
//        end of user_id


    }
}