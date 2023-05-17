package com.example.billit_all.Bill_history;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoryFragment extends Fragment {

    RecyclerView tenantRecycler;
    RecyclerView tenantRecycler2;
    RecyclerView tenantRecycler3;
    RecyclerView tenantRecycler4;
    TenantAdapter tenantAdapter;
    ReceiptAdapter receiptAdapter;
    ReceiptAdapter2 receiptAdapter2;
    ReceiptAdapter3 receiptAdapter3;
    ArrayList<Tenant> tenants = new ArrayList<>();
    ArrayList<Receipt> receipts = new ArrayList<>();
    ArrayList<Receipt2> receipts2 = new ArrayList<>();
    String selectedTenantId, selectedTenantId2, selectedUnitID, selectedUnitElecID, selectedUnitWaterID, selectedUnitRentID, selectedRoomNoId;
    Spinner selectTenant, selectElectricityReceipt, selectWaterReceipt, selectRentReceipt;

    TextView name_tenant, phone_tenant, rentFee_tenant;

    BarChart elecConsumptionChart, waterConsumptionChart, rentChart;

    Button waterPdfDownload, elecPdfDownload, recordPdfDownload, rentPdfDownload;

    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

//    Integer meralcoID;
//    TextView idMeralco;

    SimpleDateFormat datePatternFormat = new SimpleDateFormat("yyyy-MM-dd");
    TextView textView5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        tenantRecycler = view.findViewById(R.id.tenantRecycler);
        tenantRecycler2 = view.findViewById(R.id.tenantRecyclerRecord);
        tenantRecycler3 = view.findViewById(R.id.tenantRecyclerWater);
        tenantRecycler4 = view.findViewById(R.id.tenantRecyclerRent);

        selectTenant = view.findViewById(R.id.select_tenant_history);
        selectElectricityReceipt = view.findViewById(R.id.select_tenant_electricity);
        selectWaterReceipt = view.findViewById(R.id.select_tenant_water);
        selectRentReceipt = view.findViewById(R.id.select_tenant_rent);

        name_tenant = view.findViewById(R.id.name_tenant);
        phone_tenant = view.findViewById(R.id.phone_tenant);
        rentFee_tenant = view.findViewById(R.id.rentFee_tenant);

//        elecConsumptionChart = view.findViewById(R.id.elecConsumptionChart);
//        waterConsumptionChart = view.findViewById(R.id.waterConsumptionChart);
//        rentChart = view.findViewById(R.id.rentChart);
//        waterPdfDownload = view.findViewById(R.id.waterPdfDownload);
//        elecPdfDownload = view.findViewById(R.id.elecPdfDownload);
//        rentPdfDownload = view.findViewById(R.id.rentPdfDownload);
        recordPdfDownload = view.findViewById(R.id.recordPdfDownload);

        textView5 = view.findViewById(R.id.textView5);



        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
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

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/bill-history/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray tenantsJson = response.getJSONArray("units");


                                                    ArrayList<String> IDs = new ArrayList<>();
                                                    HashMap<String, String> UnitMap = new HashMap<>();

                                                    for (int i = 0; i < tenantsJson.length(); i++) {
                                                        JSONObject tenant = tenantsJson.getJSONObject(i);
                                                        String unit_id = tenant.getString("unit_id");
                                                        String unit_no = tenant.getString("unit_no");
                                                        IDs.add(unit_no);
                                                        UnitMap.put(unit_no, unit_id);
                                                        Log.d("unit_no", "this is hashmap" + unit_no);
                                                        Log.d("unit_id", "this is hashmap" + UnitMap);
                                                    }


                                                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, IDs);

                                                    spinnerAdapter.insert("select an option", 0);
                                                    selectTenant.setSelection(-1);
                                                    selectTenant.setAdapter(spinnerAdapter);


                                                    selectTenant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                            selectedTenantId = selectTenant.getSelectedItem().toString();
//                                                            Log.d("selectedTenantId", selectedTenantId);
//

                                                            selectedTenantId = selectTenant.getSelectedItem().toString();
                                                            Log.d("selectedTenantId", "selectedUnitNo" + selectedTenantId);
                                                            String selectedUnitNo = selectTenant.getSelectedItem().toString();
                                                            String selectedId = UnitMap.get(selectedUnitNo);
                                                            Log.d("selectedRoomNoId", "this is hashmap" + selectedId);
                                                            if (selectedId == null) {
                                                                selectedRoomNoId = "select an option";
                                                            } else {
                                                                selectedRoomNoId = selectedId;
                                                            }


                                                            if (selectedRoomNoId.equals("select an option") || selectedRoomNoId.equals("0")) {
                                                                // If the selected value is "Nothing", hide the RecyclerView
                                                                tenantRecycler.setVisibility(View.GONE);
                                                                recordPdfDownload.setVisibility(View.GONE);
                                                                name_tenant.setText("");
                                                                phone_tenant.setText("");
                                                                rentFee_tenant.setText("");
                                                            } else {
                                                                // If the selected value is not "Nothing", show the RecyclerView
                                                                tenantRecycler.setVisibility(View.VISIBLE);


                                                            }


//                                                            eletricity spinner
                                                            RequestQueue queue5 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                            BackendJsonObjectRequest request5 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            try {
                                                                                JSONArray tenantsJson1 = response.getJSONArray("elec_receipts");

                                                                                ArrayList<String> ELEC_DATEs = new ArrayList<>();
                                                                                HashMap<String, String> elecMap = new HashMap<>();

//                                                                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                                                for (int j = 0; j < tenantsJson1.length(); j++) {
                                                                                    JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                                                    String elec_id = tenant.getString("id");
                                                                                    String elec_date = tenant.getString("date");

//                                                                                    // Parse the input da te string to a Date object
//                                                                                    Date date = null;
//                                                                                    try {
//                                                                                        date = inputFormat.parse(elec_date);
//                                                                                    } catch (ParseException e) {
//                                                                                        e.printStackTrace();
//                                                                                    }
//                                                                                    // Format the date to the desired output format
//                                                                                    String formattedDate = outputFormat.format(date);

                                                                                    ELEC_DATEs.add(elec_date);
                                                                                    elecMap.put(elec_date, elec_id);
                                                                                    Log.d("elec_date", "this is hashmap" + elec_date);
                                                                                    Log.d("elec_id", "this is hashmap" + elecMap);


                                                                                }
                                                                                ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ELEC_DATEs);
                                                                                selectElectricityReceipt.setAdapter(spinnerAdapter1);

                                                                                //default value
                                                                                spinnerAdapter1.insert("select an option", 0);
                                                                                selectElectricityReceipt.setSelection(-1);

                                                                                selectElectricityReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                    @Override
                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                                                        selectedUnitID = selectElectricityReceipt.getSelectedItem().toString();
//                                                                                        Log.d("selectedUnitID", selectedUnitID);

                                                                                        String selectedDate = selectElectricityReceipt.getSelectedItem().toString();
                                                                                        Log.d("selectedDate", "this is hashmap" + selectedDate);
                                                                                        // Use the date to get the corresponding id from the map
                                                                                        String selectedId = elecMap.get(selectedDate);
                                                                                        Log.d("selectedId", "this is hashmap" + selectedId);
                                                                                        // Set the selectedUnitID to the selected id
                                                                                        selectedUnitElecID = selectedId;
                                                                                        Log.d("selectedUnitElecID", "selectedUnitElecID" + selectedUnitElecID);

                                                                                        if (selectedDate.equals("select an option")) {
                                                                                            // If the selected value is "Nothing", hide the RecyclerView
                                                                                            tenantRecycler2.setVisibility(View.GONE);

                                                                                        } else {
                                                                                            // If the selected value is not "Nothing", show the RecyclerView
                                                                                            tenantRecycler2.setVisibility(View.VISIBLE);
                                                                                        }


//                                                            Electricity Receipt
                                                                                        RequestQueue queue2 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                                                        BackendJsonObjectRequest request2 = new BackendJsonObjectRequest(
                                                                                                Request.Method.GET,
                                                                                                "/api/bill-history/get-receipt/electricity/mobile/" + selectedUnitElecID,
                                                                                                null,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {
                                                                                                        try {

                                                                                                            JSONObject tenantsJson = response.getJSONObject("data");
                                                                                                            receipts.clear();
//                                                                                                            if (tenantsJson == null) {
//                                                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
//                                                                                                            }
                                                                                                            Log.d("Electricitydata", String.valueOf(tenantsJson));
                                                                                                            tenantRecycler2.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                                            tenantRecycler2.setAdapter(receiptAdapter);
                                                                                                            receiptAdapter = new ReceiptAdapter(receipts);
                                                                                                            Integer isPaid = tenantsJson.getInt("isPaid");
                                                                                                            Integer total_consumption = tenantsJson.getInt("total_consumption");
                                                                                                            Double price_rate = tenantsJson.getDouble("price");
                                                                                                            Integer total_bill = tenantsJson.getInt("total_bill");
                                                                                                            Integer unit_consumption = tenantsJson.getInt("unit_consumption");
                                                                                                            Double unit_amount = tenantsJson.getDouble("unit_amount");
                                                                                                            Integer unit_no = tenantsJson.getInt("unit_no");
                                                                                                            Integer elec_reading = tenantsJson.getInt("elec_reading");
                                                                                                            Integer water_reading = tenantsJson.getInt("water_reading");
                                                                                                            Integer unit_id = tenantsJson.getInt("provider_unit_id");
                                                                                                            String qrCode = response.getString("qr");
                                                                                                            String due_date = tenantsJson.getString("due_date");
                                                                                                            String tenant_id = tenantsJson.optString("tenant_id");
                                                                                                            if (tenant_id == null) {
                                                                                                                tenant_id = "null";
                                                                                                            } else {
                                                                                                                // Handle the case where the value is null or not an integer
                                                                                                            }

                                                                                                            String PaidString = "";

                                                                                                            if (isPaid == 1) {
                                                                                                                PaidString = "PAID";
                                                                                                            }
                                                                                                            if (isPaid == 0) {
                                                                                                                PaidString = "UNPAID";
                                                                                                            }

                                                                                                            String date_receipt = tenantsJson.getString("date");
                                                                                                            Date date;
                                                                                                            try {
                                                                                                                date = datePatternFormat.parse(date_receipt);
                                                                                                            } catch (ParseException e) {
                                                                                                                e.printStackTrace();
                                                                                                                return;
                                                                                                            }

                                                                                                            String dateHistoryParsed = datePatternFormat.format(date);
                                                                                                            Receipt receipt = new Receipt(PaidString, dateHistoryParsed, total_consumption, total_bill, price_rate, unit_no, unit_consumption, unit_amount, elec_reading, water_reading,unit_id, qrCode, due_date, tenant_id);
                                                                                                            receipts.add(receipt);


                                                                                                            receiptAdapter.notifyDataSetChanged();


                                                                                                        } catch (JSONException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                    }
                                                                                                },
                                                                                                new Response.ErrorListener() {
                                                                                                    @Override
                                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                        );

                                                                                        request2.authenticated(getActivity().getApplicationContext());
                                                                                        queue2.add(request2);


                                                                                    }

                                                                                    @Override
                                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                                    }
                                                                                });

                                                                            } catch (JSONException e) {
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

                                                            request5.authenticated(getActivity().getApplicationContext());
                                                            queue5.add(request5);

//                                                            water spinner
                                                            RequestQueue queue6 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                            BackendJsonObjectRequest request6 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            try {
                                                                                JSONArray tenantsJson1 = response.getJSONArray("water_receipts");

                                                                                ArrayList<String> WATER_DATEs = new ArrayList<>();
                                                                                HashMap<String, String> waterMap = new HashMap<>();
//                                                                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                                                for (int j = 0; j < tenantsJson1.length(); j++) {
                                                                                    JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                                                    String water_id = tenant.getString("id");
                                                                                    String water_date = tenant.getString("date");

//                                                                                    // Parse the input da te string to a Date object
//                                                                                    Date date = null;
//                                                                                    try {
//                                                                                        date = inputFormat.parse(water_date);
//                                                                                    } catch (ParseException e) {
//                                                                                        e.printStackTrace();
//                                                                                    }
//                                                                                    // Format the date to the desired output format
//                                                                                    String formattedDate = outputFormat.format(date);

                                                                                    WATER_DATEs.add(water_date);
                                                                                    waterMap.put(water_date, water_id);
                                                                                    Log.d("selectedUnitID", "this is hashmap" + water_date);
                                                                                    Log.d("selectedUnitID", "this is hashmap" + waterMap);
                                                                                }
                                                                                ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, WATER_DATEs);
                                                                                selectWaterReceipt.setAdapter(spinnerAdapter1);

                                                                                spinnerAdapter1.insert("select an option", 0);
                                                                                selectWaterReceipt.setSelection(-1);

                                                                                selectWaterReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                    @Override
                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                                                        selectedUnitID = selectElectricityReceipt.getSelectedItem().toString();
//                                                                                        Log.d("selectedUnitID", selectedUnitID);

                                                                                        String selectedDate = selectWaterReceipt.getSelectedItem().toString();
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedDate);
                                                                                        // Use the date to get the corresponding id from the map
                                                                                        String selectedId = waterMap.get(selectedDate);
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedId);
                                                                                        // Set the selectedUnitID to the selected id
                                                                                        selectedUnitWaterID = selectedId;
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedUnitWaterID);

                                                                                        if (selectedDate.equals("select an option")) {
                                                                                            // If the selected value is "Nothing", hide the RecyclerView
                                                                                            tenantRecycler3.setVisibility(View.GONE);
                                                                                        } else {
                                                                                            // If the selected value is not "Nothing", show the RecyclerView
                                                                                            tenantRecycler3.setVisibility(View.VISIBLE);
                                                                                        }


//                                                            WAter Receipt
                                                                                        RequestQueue queue3 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                                                        BackendJsonObjectRequest request3 = new BackendJsonObjectRequest(
                                                                                                Request.Method.GET,
                                                                                                "/api/bill-history/get-receipt/water/mobile/" + selectedUnitWaterID,
                                                                                                null,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {
                                                                                                        try {
                                                                                                            JSONObject tenantsJson = response.getJSONObject("data");
                                                                                                            receipts.clear();
                                                                                                            if (tenantsJson == null && tenantsJson.equals("")) {
                                                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for Water Receipt", Toast.LENGTH_SHORT).show();

                                                                                                            }

                                                                                                            Log.d("Waterdata", String.valueOf(tenantsJson));
                                                                                                            tenantRecycler3.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                                            tenantRecycler3.setAdapter(receiptAdapter2);
                                                                                                            receiptAdapter2 = new ReceiptAdapter2(receipts);
                                                                                                            Integer isPaid = tenantsJson.getInt("isPaid");
                                                                                                            Integer total_consumption = tenantsJson.getInt("total_consumption");
                                                                                                            Double price_rate = tenantsJson.getDouble("price");
                                                                                                            Integer total_bill = tenantsJson.getInt("total_bill");
                                                                                                            Integer unit_consumption = tenantsJson.getInt("unit_consumption");
                                                                                                            Double unit_amount = tenantsJson.getDouble("unit_amount");
                                                                                                            Integer unit_no = tenantsJson.getInt("unit_no");
                                                                                                            Integer water_reading = tenantsJson.getInt("water_reading");
                                                                                                            Integer elec_reading = tenantsJson.getInt("elec_reading");
                                                                                                            Integer unit_id = tenantsJson.getInt("provider_unit_id");
                                                                                                            String qrCode = response.getString("qr");
                                                                                                            String due_date = tenantsJson.getString("due_date");
                                                                                                            String tenant_id = tenantsJson.optString("tenant_id");
                                                                                                            if (tenant_id == null) {
                                                                                                                tenant_id = "null";
                                                                                                            } else {
                                                                                                                // Handle the case where the value is null or not an integer
                                                                                                            }

                                                                                                            String PaidString = "";

                                                                                                            if (isPaid == 1) {
                                                                                                                PaidString = "PAID";
                                                                                                            }
                                                                                                            if (isPaid == 0) {
                                                                                                                PaidString = "UNPAID";
                                                                                                            }

                                                                                                            String date_receipt = tenantsJson.getString("date");
                                                                                                            Date date;
                                                                                                            try {
                                                                                                                date = datePatternFormat.parse(date_receipt);
                                                                                                            } catch (ParseException e) {
                                                                                                                e.printStackTrace();
                                                                                                                return;
                                                                                                            }

                                                                                                            String dateHistoryParsed = datePatternFormat.format(date);
                                                                                                            Receipt receipt = new Receipt(PaidString, dateHistoryParsed, total_consumption, total_bill, price_rate, unit_no, unit_consumption, unit_amount, elec_reading, water_reading, unit_id, qrCode, due_date, tenant_id);
                                                                                                            receipts.add(receipt);


                                                                                                            receiptAdapter2.notifyDataSetChanged();


                                                                                                        } catch (JSONException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                    }
                                                                                                },
                                                                                                new Response.ErrorListener() {
                                                                                                    @Override
                                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Water Receipt", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                        );

                                                                                        request3.authenticated(getActivity().getApplicationContext());
                                                                                        queue3.add(request3);


                                                                                    }

                                                                                    @Override
                                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                                    }
                                                                                });

                                                                                //try catch of water spinner
                                                                            } catch (JSONException e) {
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

                                                            request6.authenticated(getActivity().getApplicationContext());
                                                            queue6.add(request6);


////                                                            rent spinner
                                                            RequestQueue queue7 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                            BackendJsonObjectRequest request7 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            try {
                                                                                JSONArray tenantsJson1 = response.getJSONArray("rent_receipts");

                                                                                ArrayList<String> RENT_DATEs = new ArrayList<>();
                                                                                HashMap<String, String> rentMap = new HashMap<>();
//                                                                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                                                                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                                                for (int j = 0; j < tenantsJson1.length(); j++) {
                                                                                    JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                                                    String rent_id = tenant.getString("id");
                                                                                    String rent_date = tenant.getString("date");

//                                                                                    // Parse the input da te string to a Date object
//                                                                                    Date date = null;
//                                                                                    try {
//                                                                                        date = inputFormat.parse(rent_date);
//                                                                                    } catch (ParseException e) {
//                                                                                        e.printStackTrace();
//                                                                                    }
//                                                                                    // Format the date to the desired output format
//                                                                                    String formattedDate = outputFormat.format(date);

                                                                                    RENT_DATEs.add(rent_date);
                                                                                    rentMap.put(rent_date, rent_id);
                                                                                    Log.d("selectedUnitID", "this is hashmap" + rent_date);
                                                                                    Log.d("selectedUnitID", "this is hashmap" + rentMap);
                                                                                }
                                                                                ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, RENT_DATEs);
                                                                                selectRentReceipt.setAdapter(spinnerAdapter1);

                                                                                spinnerAdapter1.insert("select an option", 0);
                                                                                selectRentReceipt.setSelection(-1);

                                                                                selectRentReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                                    @Override
                                                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                                                        selectedUnitID = selectElectricityReceipt.getSelectedItem().toString();
//                                                                                        Log.d("selectedUnitID", selectedUnitID);

                                                                                        String selectedDate = selectRentReceipt.getSelectedItem().toString();
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedDate);
                                                                                        // Use the date to get the corresponding id from the map
                                                                                        String selectedId = rentMap.get(selectedDate);
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedId);
                                                                                        // Set the selectedUnitID to the selected id
                                                                                        selectedUnitRentID = selectedId;
                                                                                        Log.d("selectedUnitID", "this is hashmap" + selectedUnitRentID);

                                                                                        if (selectedDate.equals("select an option")) {
                                                                                            // If the selected value is "Nothing", hide the RecyclerView
                                                                                            tenantRecycler4.setVisibility(View.GONE);
                                                                                        } else {
                                                                                            // If the selected value is not "Nothing", show the RecyclerView
                                                                                            tenantRecycler4.setVisibility(View.VISIBLE);
                                                                                        }


//                                                            Rent Receipt
                                                                                        RequestQueue queue4 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                                                        BackendJsonObjectRequest request4 = new BackendJsonObjectRequest(
                                                                                                Request.Method.GET,
                                                                                                "/api/bill-history/get-receipt/rent/" + selectedUnitRentID,
                                                                                                null,
                                                                                                new Response.Listener<JSONObject>() {
                                                                                                    @Override
                                                                                                    public void onResponse(JSONObject response) {
                                                                                                        try {
                                                                                                            JSONObject tenantsJson = response.getJSONObject("data");
                                                                                                            receipts2.clear();
                                                                                                            if (tenantsJson == null) {
                                                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for Rent Receipt", Toast.LENGTH_SHORT).show();
                                                                                                            }

                                                                                                            Log.d("Rentdata", String.valueOf(tenantsJson));
                                                                                                            tenantRecycler4.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                                            tenantRecycler4.setAdapter(receiptAdapter3);
                                                                                                            receiptAdapter3 = new ReceiptAdapter3(receipts2);
                                                                                                            Integer isPaid = tenantsJson.getInt("isPaid");
                                                                                                            Integer unit_no = tenantsJson.getInt("unit_no");
                                                                                                            Integer receipt_rentFee = tenantsJson.getInt("rent_fee");
                                                                                                            Integer unit_id = tenantsJson.getInt("provider_unit_id");
                                                                                                            String qrCode = response.getString("qr");
                                                                                                            String tenant_id = tenantsJson.optString("tenant_id");
                                                                                                            if (tenant_id == null) {
                                                                                                                tenant_id = "null";
                                                                                                            } else {
                                                                                                                // Handle the case where the value is null or not an integer
                                                                                                            }

                                                                                                            Log.d("rentData", "this is rendata" + isPaid);
                                                                                                            Log.d("rentData", "this is rendata" + unit_no);
                                                                                                            Log.d("rentData", "this is rendata" + receipt_rentFee);

                                                                                                            String PaidString = "";

                                                                                                            if (isPaid == 1) {
                                                                                                                PaidString = "PAID";
                                                                                                            }
                                                                                                            if (isPaid == 0) {
                                                                                                                PaidString = "UNPAID";
                                                                                                            }

                                                                                                            String date_receipt = tenantsJson.getString("date");
                                                                                                            Date date;
                                                                                                            try {
                                                                                                                date = datePatternFormat.parse(date_receipt);
                                                                                                            } catch (ParseException e) {
                                                                                                                e.printStackTrace();
                                                                                                                return;
                                                                                                            }

                                                                                                            String dateHistoryParsed = datePatternFormat.format(date);
                                                                                                            Receipt2 receipt = new Receipt2(PaidString, dateHistoryParsed, unit_no, receipt_rentFee, unit_id, qrCode, tenant_id);
                                                                                                            receipts2.add(receipt);


                                                                                                            receiptAdapter3.notifyDataSetChanged();


                                                                                                        } catch (JSONException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                    }
                                                                                                },
                                                                                                new Response.ErrorListener() {
                                                                                                    @Override
                                                                                                    public void onErrorResponse(VolleyError error) {
                                                                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Rent Receipt", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                        );

                                                                                        request4.authenticated(getActivity().getApplicationContext());
                                                                                        queue4.add(request4);


                                                                                    }

                                                                                    @Override
                                                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                                                    }
                                                                                });

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }


                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "There are no records for Rent Receipt", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                            );

                                                            request7.authenticated(getActivity().getApplicationContext());
                                                            queue7.add(request7);


//                                                            profile Tenant
                                                            RequestQueue queue0 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                            BackendJsonObjectRequest request0 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {
                                                                            try {
                                                                                JSONObject responseJSON = response.getJSONObject("tenant");
                                                                                String name = responseJSON.getString("name");
                                                                                String phone = responseJSON.getString("phone");
                                                                                String rent_fee = responseJSON.getString("rent_fee");
                                                                                String profile = responseJSON.getString("profile");

                                                                                if (name.equals(null) || name.equals("null") || name.equals("")) {
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "Selected Unit is not yet Verified ", Toast.LENGTH_SHORT).show();
                                                                                    name_tenant.setText("");
                                                                                    phone_tenant.setText("");
                                                                                    rentFee_tenant.setText("");
                                                                                } else {
                                                                                    Log.d("data", name);
                                                                                    Log.d("data", phone);
                                                                                    Log.d("data", rent_fee);
                                                                                    Log.d("data", profile);

                                                                                    name_tenant.setText("Name: " + name);
                                                                                    phone_tenant.setText("Contact: " + phone);
                                                                                    rentFee_tenant.setText("Rent Fee: " + rent_fee);
                                                                                }

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }


                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "Selected Unit is not yet Verified ", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                            );

                                                            request0.authenticated(getActivity().getApplicationContext());
                                                            queue0.add(request0);


                                                            RequestQueue queue1 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                                            BackendJsonObjectRequest request1 = new BackendJsonObjectRequest(
                                                                    Request.Method.GET,
                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
                                                                    null,
                                                                    new Response.Listener<JSONObject>() {
                                                                        @Override
                                                                        public void onResponse(JSONObject response) {


                                                                            try {


                                                                                JSONArray tenantsJson = response.getJSONArray("records");
                                                                                Log.d("selectedTenantId", String.valueOf(tenantsJson));
                                                                                tenants.clear();

                                                                                if (response.getJSONArray("records").length() == 0 || tenantsJson.equals(null) || tenantsJson.equals("") || tenantsJson.equals("[]")) {
                                                                                    // Display a message to the user indicating that there are no records for this tenant
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for this tenant", Toast.LENGTH_SHORT).show();
                                                                                    tenantRecycler.setVisibility(View.GONE);
                                                                                    recordPdfDownload.setVisibility(View.GONE);

                                                                                }
                                                                                else {
                                                                                    recordPdfDownload.setVisibility(View.VISIBLE);
                                                                                    for (int i = 0; i < tenantsJson.length(); i++) {
                                                                                        JSONObject recordsJson = tenantsJson.getJSONObject(i);
                                                                                        tenantRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                        tenantRecycler.setAdapter(tenantAdapter);
                                                                                        tenantAdapter = new TenantAdapter(tenants);


//                                                                                    Integer elec_reading = recordsJson.getInt("elec_reading");
//                                                                                    Integer water_reading = recordsJson.getInt("water_reading");
                                                                                        Integer elec_consumption = recordsJson.getInt("elec_consumption");
                                                                                        Integer water_consumption = recordsJson.getInt("water_consumption");

                                                                                        Integer elec_amount = recordsJson.getInt("elec_bill");
                                                                                        Integer water_amount = recordsJson.getInt("water_bill");

                                                                                        Integer elec_unit_cons = recordsJson.getInt("elec_unit_cons");
                                                                                        Double elec_unit_amount = recordsJson.getDouble("elec_unit_amount");
                                                                                        Integer water_unit_cons = recordsJson.getInt("water_unit_cons");
                                                                                        Double water_unit_amount = recordsJson.getDouble("water_unit_amount");

                                                                                        Integer elec_status = recordsJson.getInt("elec_status");
                                                                                        Integer water_status = recordsJson.getInt("water_status");
                                                                                        Integer rent_status = recordsJson.getInt("rent_status");

                                                                                        Log.d("historyData", String.valueOf(elec_consumption));

                                                                                        String elecPaidString = "";
                                                                                        String waterPaidString = "";
                                                                                        String rentPaidString = "";

                                                                                        if (water_status == 1 || elec_status == 1 || rent_status == 1) {
                                                                                            waterPaidString = "PAID";
                                                                                            rentPaidString = "PAID";
                                                                                            elecPaidString = "PAID";
                                                                                        }
                                                                                        if (water_status == 0) {
                                                                                            waterPaidString = "UNPAID";
                                                                                        }
                                                                                        if (elec_status == 0) {
                                                                                            elecPaidString = "UNPAID";
                                                                                        }
                                                                                        if (rent_status == 0) {
                                                                                            rentPaidString = "UNPAID";
                                                                                        }

                                                                                        String elec_due_date = recordsJson.getString("elec_due_date");
                                                                                        Date date1;
                                                                                        try {
                                                                                            date1 = datePatternFormat.parse(elec_due_date);
                                                                                        } catch (ParseException e) {
                                                                                            e.printStackTrace();
                                                                                            return;
                                                                                        }

                                                                                        String dateHistoryParsed1 = datePatternFormat.format(date1);

                                                                                        String water_due_date = recordsJson.getString("water_due_date");
                                                                                        Date date2;
                                                                                        try {
                                                                                            date2 = datePatternFormat.parse(water_due_date);
                                                                                        } catch (ParseException e) {
                                                                                            e.printStackTrace();
                                                                                            return;
                                                                                        }

                                                                                        String dateHistoryParsed2 = datePatternFormat.format(date2);


                                                                                        //adding in ArrayList to Adapter
                                                                                        Tenant tenant = new Tenant(dateHistoryParsed1, dateHistoryParsed2, water_amount, waterPaidString, elec_amount, elecPaidString, rentPaidString, water_consumption, elec_consumption, water_unit_cons, elec_unit_cons, water_unit_amount, elec_unit_amount);
                                                                                        tenants.add(tenant);

//                                                                                        textView5.setText(name_tenant.getText().toString());

                                                                                    }
                                                                                }

//                                                                                to not error when no records are found
                                                                                if (tenantAdapter == null) {

                                                                                } else {
                                                                                    tenantAdapter.notifyDataSetChanged();
                                                                                }

                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        }
                                                                    },
                                                                    new Response.ErrorListener() {
                                                                        @Override
                                                                        public void onErrorResponse(VolleyError error) {
                                                                            Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                            );

                                                            request1.authenticated(getActivity().getApplicationContext());
                                                            queue1.add(request1);

//
////                                                            Electricity Chart
//                                                            RequestQueue queue10 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                                            BackendJsonObjectRequest request10 = new BackendJsonObjectRequest(
//                                                                    Request.Method.GET,
//                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
//                                                                    null,
//                                                                    new Response.Listener<JSONObject>() {
//                                                                        @Override
//                                                                        public void onResponse(JSONObject response) {
//
//
//                                                                            try {
//
//                                                                                JSONArray tenantsJson = response.getJSONArray("records");
//                                                                                Log.d("selectedTenantId", String.valueOf(tenantsJson));
//
//                                                                                if (response.getJSONArray("records").length() == 0 || tenantsJson.equals(null) || tenantsJson.equals("") || tenantsJson.equals("[]")) {
//                                                                                    // Display a message to the user indicating that there are no records for this tenant
//                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for this tenant", Toast.LENGTH_SHORT).show();
//
//                                                                                }
//
//                                                                                ArrayList<BarEntry> barEntries = new ArrayList<>();
//                                                                                ArrayList <String> CONSUMP = new ArrayList<>();
//                                                                                for (int i = 0; i < tenantsJson.length(); i++) {
//                                                                                    JSONObject recordsJson = tenantsJson.getJSONObject(i);
//
//                                                                                    Integer elec_consumption = recordsJson.getInt("elec_consumption");
//                                                                                    String label = recordsJson.getString("date");
//
//                                                                                    BarEntry barEntry = new BarEntry(i, elec_consumption);
//                                                                                    barEntries.add(barEntry);
//                                                                                    CONSUMP.add(label);
//                                                                                    Log.d("historyData", String.valueOf(elec_consumption));
//
//                                                                                }
//
//                                                                                BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
//                                                                                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                                                barDataSet.setDrawValues(true);
//                                                                                elecConsumptionChart.setData(new BarData(barDataSet));
//                                                                                elecConsumptionChart.animateY(500);
//                                                                                elecConsumptionChart.getDescription().setText("");
//                                                                                elecConsumptionChart.getDescription().setTextColor(Color.BLACK);
//                                                                                XAxis xAxis = elecConsumptionChart.getXAxis();
//                                                                                xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
//                                                                                elecConsumptionChart.getAxisLeft().setAxisMinimum(0f);
//                                                                                elecConsumptionChart.getAxisRight().setAxisMinimum(0f);
//                                                                                elecConsumptionChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(CONSUMP));
//                                                                                elecConsumptionChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
//
//
////
//
//                                                                            } catch (JSONException e) {
//                                                                                e.printStackTrace();
//                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    },
//                                                                    new Response.ErrorListener() {
//                                                                        @Override
//                                                                        public void onErrorResponse(VolleyError error) {
//                                                                            Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//
//                                                                        }
//                                                                    }
//                                                            );
//
//                                                            request10.authenticated(getActivity().getApplicationContext());
//                                                            queue10.add(request10);
//
//
////                                                            Water Chart
//                                                            RequestQueue queue11 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                                            BackendJsonObjectRequest request11 = new BackendJsonObjectRequest(
//                                                                    Request.Method.GET,
//                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
//                                                                    null,
//                                                                    new Response.Listener<JSONObject>() {
//                                                                        @Override
//                                                                        public void onResponse(JSONObject response) {
//
//
//                                                                            try {
//
//                                                                                JSONArray tenantsJson = response.getJSONArray("records");
//                                                                                Log.d("selectedTenantId", String.valueOf(tenantsJson));
//
//                                                                                if (response.getJSONArray("records").length() == 0 || tenantsJson.equals(null) || tenantsJson.equals("") || tenantsJson.equals("[]")) {
//                                                                                    // Display a message to the user indicating that there are no records for this tenant
//                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for this tenant", Toast.LENGTH_SHORT).show();
//
//                                                                                }
//
//                                                                                ArrayList<BarEntry> barEntries = new ArrayList<>();
//                                                                                ArrayList <String> CONSUMP = new ArrayList<>();
//                                                                                for (int i = 0; i < tenantsJson.length(); i++) {
//                                                                                    JSONObject recordsJson = tenantsJson.getJSONObject(i);
//
//                                                                                    Integer elec_consumption = recordsJson.getInt("water_consumption");
//                                                                                    String label = recordsJson.getString("date");
//
//                                                                                    BarEntry barEntry = new BarEntry(i, elec_consumption);
//                                                                                    barEntries.add(barEntry);
//                                                                                    CONSUMP.add(label);
//                                                                                    Log.d("historyData", String.valueOf(elec_consumption));
//
//                                                                                }
//
//                                                                                BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
//                                                                                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                                                barDataSet.setDrawValues(true);
//                                                                                waterConsumptionChart.setData(new BarData(barDataSet));
//                                                                                waterConsumptionChart.animateY(500);
//                                                                                waterConsumptionChart.getDescription().setText("");
//                                                                                waterConsumptionChart.getDescription().setTextColor(Color.BLACK);
//                                                                                XAxis xAxis = waterConsumptionChart.getXAxis();
//                                                                                xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
//                                                                                waterConsumptionChart.getAxisLeft().setAxisMinimum(0f);
//                                                                                waterConsumptionChart.getAxisRight().setAxisMinimum(0f);
//                                                                                waterConsumptionChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(CONSUMP));
//                                                                                waterConsumptionChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
//
//
//
////
//
//                                                                            } catch (JSONException e) {
//                                                                                e.printStackTrace();
//                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    },
//                                                                    new Response.ErrorListener() {
//                                                                        @Override
//                                                                        public void onErrorResponse(VolleyError error) {
//                                                                            Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//
//                                                                        }
//                                                                    }
//                                                            );
//
//                                                            request11.authenticated(getActivity().getApplicationContext());
//                                                            queue11.add(request11);
//
////
////
//////                                                            RentChart
//                                                            RequestQueue queue12 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                                            BackendJsonObjectRequest request12 = new BackendJsonObjectRequest(
//                                                                    Request.Method.GET,
//                                                                    "/api/bill-history/get-record/" + selectedRoomNoId,
//                                                                    null,
//                                                                    new Response.Listener<JSONObject>() {
//                                                                        @Override
//                                                                        public void onResponse(JSONObject response) {
//
//
//                                                                            try {
//
//                                                                                JSONArray tenantsJson = response.getJSONArray("records");
//                                                                                Log.d("selectedTenantId", String.valueOf(tenantsJson));
//
//                                                                                if (response.getJSONArray("records").length() == 0 || tenantsJson.equals(null) || tenantsJson.equals("") || tenantsJson.equals("[]")) {
//                                                                                    // Display a message to the user indicating that there are no records for this tenant
//                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for this tenant", Toast.LENGTH_SHORT).show();
//
//                                                                                }
//
//                                                                                ArrayList<BarEntry> barEntries = new ArrayList<>();
//                                                                                ArrayList <String> FEE = new ArrayList<>();
//                                                                                for (int i = 0; i < tenantsJson.length(); i++) {
//                                                                                    JSONObject recordsJson = tenantsJson.getJSONObject(i);
//
//                                                                                    Integer rent_fee = recordsJson.getInt("rent_fee");
//                                                                                    String label = recordsJson.getString("rent_due_date");
//
//                                                                                    BarEntry barEntry = new BarEntry(i, rent_fee);
//                                                                                    barEntries.add(barEntry);
//                                                                                    FEE.add(label);
//                                                                                    Log.d("historyData", String.valueOf(rent_fee));
//
//                                                                                }
//
//                                                                                BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
//                                                                                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                                                barDataSet.setDrawValues(true);
//                                                                                rentChart.setData(new BarData(barDataSet));
//                                                                                rentChart.animateY(500);
//                                                                                rentChart.getDescription().setText("");
//                                                                                rentChart.getDescription().setTextColor(Color.BLACK);
//                                                                                XAxis xAxis = rentChart.getXAxis();
//                                                                                xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
//                                                                                rentChart.getAxisLeft().setAxisMinimum(0f);
//                                                                                rentChart.getAxisRight().setAxisMinimum(0f);
//                                                                                rentChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(FEE));
//                                                                                rentChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//
////
//
//                                                                            } catch (JSONException e) {
//                                                                                e.printStackTrace();
//                                                                                Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    },
//                                                                    new Response.ErrorListener() {
//                                                                        @Override
//                                                                        public void onErrorResponse(VolleyError error) {
//                                                                            Toast.makeText(getActivity().getApplicationContext(), "There are no records for this Tenant", Toast.LENGTH_SHORT).show();
//
//                                                                        }
//                                                                    }
//                                                            );
//
//                                                            request12.authenticated(getActivity().getApplicationContext());
//                                                            queue12.add(request12);



                                                            for (Tenant tenant : tenants) {
                                                                Log.d("TenantList", "elec_consumption: " + tenant.getElec_consump() + ", water_consumption: " + tenant.getWater_consump());
                                                            }


                                                            recordPdfDownload.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    String nameTenant = name_tenant.getText().toString();
                                                                    String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
                                                                    com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4.rotate());

// Specify the file path and name for the output PDF
                                                                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                                    dir.mkdirs(); // create the directory if it doesn't exist
                                                                    File file = new File(dir, safeNameTenant +"-"+ "Records.pdf");
                                                                    try {
                                                                        // Create a PdfWriter instance to write to the document
                                                                        PdfWriter.getInstance(document, new FileOutputStream(file));

                                                                        // Open the document
                                                                        document.open();

                                                                        Paragraph title = new Paragraph(" Records of "+ nameTenant, new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
                                                                        title.setAlignment(Element.ALIGN_CENTER);
                                                                        document.add(title);

// Add some space
                                                                        document.add(new Paragraph(" "));


                                                                        // Create a new table with two columns
                                                                        PdfPTable table = new PdfPTable(13);
                                                                        table.setTotalWidth(PageSize.A4.getWidth());
                                                                        float[] columnWidths = {5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f};
                                                                        table.setLockedWidth(true);
                                                                        table.setWidths(columnWidths);
                                                                        Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

                                                                        // Add the column headers to the table
                                                                        table.addCell(new Phrase("Electricity Date", font));
                                                                        table.addCell(new Phrase("Water Date", font));

                                                                        table.addCell(new Phrase("Electricity Consumption", font));
                                                                        table.addCell(new Phrase("Water Consumption", font));

                                                                        table.addCell(new Phrase("Electricity Total Bill Amount", font));
                                                                        table.addCell(new Phrase("Water Total Bill Amount", font));

                                                                        table.addCell(new Phrase("Electricity Unit Consumption", font));
                                                                        table.addCell(new Phrase("Water Unit Consumption", font));

                                                                        table.addCell(new Phrase("Electricity Unit Amount", font));
                                                                        table.addCell(new Phrase("Water Unit Amount", font));

                                                                        table.addCell(new Phrase("Electricity Status", font));
                                                                        table.addCell(new Phrase("Water Status", font));
                                                                        table.addCell(new Phrase("Rent Status", font));

                                                                        // Add each Tenant object to the table
                                                                        for (Tenant tenant : tenants) {
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_due_date()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_due_date()), font));

                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_consump()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_consump()), font));

                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_amount()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_amount()), font));

                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_unitConsump()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_unitConsump()), font));

                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_unitAmount()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_unitAmount()), font));

                                                                            table.addCell(new Phrase(String.valueOf(tenant.getElec_paid()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getWater_paid()), font));
                                                                            table.addCell(new Phrase(String.valueOf(tenant.getRent_paid()), font));
                                                                        }

                                                                        // Add the table to the document
                                                                        document.add(table);
                                                                        Toast.makeText(getActivity(), "Record exported to PDF", Toast.LENGTH_SHORT).show();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(getActivity(), "Did not exported to PDF", Toast.LENGTH_SHORT).show();
                                                                    } finally {
                                                                        // Close the document
                                                                        document.close();
                                                                    }
                                                                }
                                                            });


//                                                            elecPdfDownload.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View v) {
//                                                                    Bitmap chartBitmap = elecConsumptionChart.getChartBitmap();
//                                                                    String nameTenant = name_tenant.getText().toString();
//                                                                    String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                                    // Create a new document
//                                                                    PdfDocument document = new PdfDocument();
//
//                                                                    // Create a page and set its size
//                                                                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                                    PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                                    // Draw the bitmap on the page
//                                                                    Canvas canvas = page.getCanvas();
//                                                                    canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                                    // Finish the page and save the document
//                                                                    document.finishPage(page);
//
//                                                                    File file = new File(dir,safeNameTenant +"-"+ "ElectricityConsumptionChart.pdf");
//
//                                                                    try {
//                                                                        document.writeTo(new FileOutputStream(file));
//                                                                        Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                                    } catch (IOException e) {
//                                                                        e.printStackTrace();
//                                                                        Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                                    }
//
//                                                                    // Close the document
//                                                                    document.close();
//                                                                }
//                                                            });
//                                                            waterPdfDownload.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View v) {
//
//                                                                    Bitmap chartBitmap = waterConsumptionChart.getChartBitmap();
//                                                                    String nameTenant = name_tenant.getText().toString();
//                                                                    String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                                    // Create a new document
//                                                                    PdfDocument document = new PdfDocument();
//
//                                                                    // Create a page and set its size
//                                                                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                                    PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                                    // Draw the bitmap on the page
//                                                                    Canvas canvas = page.getCanvas();
//                                                                    canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                                    // Finish the page and save the document
//                                                                    document.finishPage(page);
//
//                                                                    File file = new File(dir,safeNameTenant +"-"+ "WaterConsumptionChart.pdf");
//
//                                                                    try {
//                                                                        document.writeTo(new FileOutputStream(file));
//                                                                        Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                                    } catch (IOException e) {
//                                                                        e.printStackTrace();
//                                                                        Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                                    }
//
//                                                                    // Close the document
//                                                                    document.close();
//
//                                                                }
//                                                            });
//
//                                                            rentPdfDownload.setOnClickListener(new View.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(View v) {
//
//                                                                    Bitmap chartBitmap = rentChart.getChartBitmap();
//                                                                    String nameTenant = name_tenant.getText().toString();
//                                                                    String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                                    // Create a new document
//                                                                    PdfDocument document = new PdfDocument();
//
//                                                                    // Create a page and set its size
//                                                                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                                    PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                                    // Draw the bitmap on the page
//                                                                    Canvas canvas = page.getCanvas();
//                                                                    canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                                    // Finish the page and save the document
//                                                                    document.finishPage(page);
//
//                                                                    File file = new File(dir,safeNameTenant +"-"+ "RentChart.pdf");
//
//                                                                    try {
//                                                                        document.writeTo(new FileOutputStream(file));
//                                                                        Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                                    } catch (IOException e) {
//                                                                        e.printStackTrace();
//                                                                        Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                                    }
//
//                                                                    // Close the document
//                                                                    document.close();
//
//                                                                }
//                                                            });


//                                                       inside of onItemSelected of spinner

                                                        }
                                                        //                                                       end of onItemSelect for units


                                                        @Override
                                                        public void onNothingSelected(AdapterView<?> parent) {

                                                        }
                                                    });


                                                } catch (JSONException e) {
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
                                request.authenticated(getActivity().getApplicationContext());
                                queue.add(request);

                                //end of onItemSelect
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

        return view;




    }

    private void exportElecBalance() {
        // Get the chart view and create a bitmap from it
        Bitmap chartBitmap = elecConsumptionChart.getChartBitmap();

        // Create a new document
        PdfDocument document = new PdfDocument();

        // Create a page and set its size
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Draw the bitmap on the page
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(chartBitmap, 0, 0, null);

        // Finish the page and save the document
        document.finishPage(page);
//        String fileName = "ElectricityBalance.pdf";
//        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
//        File file = new File(filePath);
        File file = new File(dir,name_tenant.getText().toString() +"-"+ "ElectricityConsumption.pdf");
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
        }

        // Close the document
        document.close();
    }


}
