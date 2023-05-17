package com.example.billit_all.Bill_history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.AlgorithmConstraints;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoryFragmentTenant extends Fragment {

    RecyclerView tenantRecycler;
    RecyclerView tenantRecycler2;
    RecyclerView tenantRecycler3;
    RecyclerView tenantRecycler4;
    TenantAdapter tenantAdapter;
    ReceiptAdapterTenant receiptAdapter;
    ReceiptAdapterTenant2 receiptAdapter2;
    ReceiptAdapterTenant3 receiptAdapter3;
    ArrayList<Tenant> tenants = new ArrayList<>();
    ArrayList<Receipt> receipts = new ArrayList<>();
    ArrayList<Receipt2> receipts2 = new ArrayList<>();
    String selectedTenantId, selectedTenantId2, selectedUnitID, selectedUnitElecID, selectedUnitWaterID, selectedUnitRentID;
    Spinner selectElectricityReceipt, selectWaterReceipt, selectRentReceipt;

    TextView name_tenant, phone_tenant, rentFee_tenant;

    SimpleDateFormat datePatternFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_tenant, container, false);

        tenantRecycler = view.findViewById(R.id.tenantRecycler);
        tenantRecycler2 = view.findViewById(R.id.tenantRecyclerRecord);
        tenantRecycler3 = view.findViewById(R.id.tenantRecyclerWater);
        tenantRecycler4 = view.findViewById(R.id.tenantRecyclerRent);


        selectElectricityReceipt = view.findViewById(R.id.select_tenant_electricity);
        selectWaterReceipt = view.findViewById(R.id.select_tenant_water);
        selectRentReceipt = view.findViewById(R.id.select_tenant_rent);

        name_tenant = view.findViewById(R.id.name_tenant);
        phone_tenant = view.findViewById(R.id.phone_tenant);
        rentFee_tenant = view.findViewById(R.id.rentFee_tenant);


        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.fetchTenantIdFromBackend(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                Log.d("USER_ID_Tenant", String.valueOf(user));
                                String id = user.getString("unit_id");
                                Log.d("USER_ID_Tenant", id);



//                                                            profile Tenant
                                RequestQueue queue0 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request0 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/bill-history/get-record/" + id,
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

                                                    Log.d("data", name);
                                                    Log.d("data", phone);
                                                    Log.d("data", rent_fee);
                                                    Log.d("data", profile);

                                                    name_tenant.setText("Name: " + name);
                                                    phone_tenant.setText("Contact: " + phone);
                                                    rentFee_tenant.setText("Rent Fee: " + rent_fee);


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

                                request0.authenticated(getActivity().getApplicationContext());
                                queue0.add(request0);


                                RequestQueue queue1 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request1 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/bill-history/get-record/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {


                                                try {
                                                    if (response.getJSONArray("records").length() == 0) {
                                                        // Display a message to the user indicating that there are no records for this tenant
                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for this tenant", Toast.LENGTH_SHORT).show();
                                                        tenantRecycler.setVisibility(View.GONE);
                                                        return;
                                                    }
                                                    JSONArray tenantsJson = response.getJSONArray("records");
                                                    Log.d("selectedTenantId", String.valueOf(tenantsJson));
                                                    tenants.clear();

                                                    for (int i = 0; i < tenantsJson.length(); i++) {
                                                        JSONObject recordsJson = tenantsJson.getJSONObject(i);
                                                        tenantRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                                        tenantRecycler.setAdapter(tenantAdapter);
                                                        tenantAdapter = new TenantAdapter(tenants);


//                                                        Integer elec_reading = recordsJson.getInt("elec_reading");
//                                                        Integer water_reading = recordsJson.getInt("water_reading");
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
                                                        Tenant tenant = new Tenant(dateHistoryParsed1,dateHistoryParsed2 , water_amount, waterPaidString, elec_amount, elecPaidString, rentPaidString, water_consumption, elec_consumption, water_unit_cons, elec_unit_cons, water_unit_amount, elec_unit_amount);
                                                        tenants.add(tenant);

                                                    }

//                                                    to not error when no records are found
                                                    if (tenantAdapter == null) {

                                                    } else {
                                                        tenantAdapter.notifyDataSetChanged();
                                                    }

                                                    tenantAdapter.notifyDataSetChanged();


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Theres no record for this Tenant", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                );

                                request1.authenticated(getActivity().getApplicationContext());
                                queue1.add(request1);


//                                                            eletricity spinner
                                RequestQueue queue5 = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request5 = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/bill-history/get-record/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {

                                                    if (response.getJSONArray("elec_receipts").length() == 0) {
                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
                                                        tenantRecycler2.setVisibility(View.GONE);
                                                    }

                                                    JSONArray tenantsJson1 = response.getJSONArray("elec_receipts");

                                                    ArrayList<String> ELEC_DATEs = new ArrayList<>();
                                                    HashMap<String, String> elecMap = new HashMap<>();
//                                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                    for (int j = 0; j < tenantsJson1.length(); j++) {
                                                        JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                        String elec_id = tenant.getString("id");
                                                        String elec_date = tenant.getString("date");

//                                                        // Parse the input da te string to a Date object
//                                                        Date date = null;
//                                                        try {
//                                                            date = inputFormat.parse(elec_date);
//                                                        } catch (ParseException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                        // Format the date to the desired output format
//                                                        String formattedDate = outputFormat.format(date);

                                                        ELEC_DATEs.add(elec_date);
                                                        elecMap.put(elec_date, elec_id);
                                                        Log.d("selectedUnitID", "this is hashmap" + elec_date);
                                                        Log.d("selectedUnitID", "this is hashmap" + elecMap);
                                                    }
                                                    ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ELEC_DATEs);
                                                    selectElectricityReceipt.setAdapter(spinnerAdapter1);

                                                    selectElectricityReceipt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                                                        selectedUnitID = selectElectricityReceipt.getSelectedItem().toString();
//                                                                                        Log.d("selectedUnitID", selectedUnitID);

                                                            String selectedDate = selectElectricityReceipt.getSelectedItem().toString();
                                                            Log.d("selectedUnitID", "this is hashmap" + selectedDate);
                                                            // Use the date to get the corresponding id from the map
                                                            String selectedId = elecMap.get(selectedDate);
                                                            Log.d("selectedUnitID", "this is hashmap" + selectedId);
                                                            // Set the selectedUnitID to the selected id
                                                            selectedUnitElecID = selectedId;
                                                            Log.d("selectedUnitElecID", "this is hashmap" + selectedUnitElecID);


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
                                                                                if (tenantsJson == null) {
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                Log.d("Electricitydata", String.valueOf(tenantsJson));
                                                                                tenantRecycler2.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                tenantRecycler2.setAdapter(receiptAdapter);
                                                                                receiptAdapter = new ReceiptAdapterTenant(receipts);
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
                                                                                Receipt receipt = new Receipt(PaidString, dateHistoryParsed, total_consumption, total_bill, price_rate, unit_no, unit_consumption, unit_amount, elec_reading, water_reading, unit_id, qrCode,due_date, tenant_id);
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
                                        "/api/bill-history/get-record/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    if (response.getJSONArray("water_receipts").length() == 0) {
                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
                                                        tenantRecycler3.setVisibility(View.GONE);
                                                    }


                                                    JSONArray tenantsJson1 = response.getJSONArray("water_receipts");

                                                    ArrayList<String> WATER_DATEs = new ArrayList<>();
                                                    HashMap<String, String> waterMap = new HashMap<>();
//                                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                    for (int j = 0; j < tenantsJson1.length(); j++) {
                                                        JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                        String water_id = tenant.getString("id");
                                                        String water_date = tenant.getString("date");

//                                                        // Parse the input da te string to a Date object
//                                                        Date date = null;
//                                                        try {
//                                                            date = inputFormat.parse(water_date);
//                                                        } catch (ParseException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                        // Format the date to the desired output format
//                                                        String formattedDate = outputFormat.format(date);

                                                        WATER_DATEs.add(water_date);
                                                        waterMap.put(water_date, water_id);
                                                        Log.d("selectedUnitID", "this is hashmap" + water_date);
                                                        Log.d("selectedUnitID", "this is hashmap" + waterMap);
                                                    }
                                                    ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, WATER_DATEs);
                                                    selectWaterReceipt.setAdapter(spinnerAdapter1);
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
                                                                                if (tenantsJson == null) {
                                                                                    Toast.makeText(getActivity().getApplicationContext(), "There are no records for Water Receipt", Toast.LENGTH_SHORT).show();
                                                                                    tenantRecycler3.setVisibility(View.GONE);
                                                                                }
                                                                                Log.d("Waterdata", String.valueOf(tenantsJson));
                                                                                tenantRecycler3.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                tenantRecycler3.setAdapter(receiptAdapter2);
                                                                                receiptAdapter2 = new ReceiptAdapterTenant2(receipts);
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
                                        "/api/bill-history/get-record/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {

                                                    if (response.getJSONArray("rent_receipts").length() == 0) {
                                                        Toast.makeText(getActivity().getApplicationContext(), "There are no records for Electricity Receipt", Toast.LENGTH_SHORT).show();
                                                        tenantRecycler4.setVisibility(View.GONE);
                                                    }
                                                    JSONArray tenantsJson1 = response.getJSONArray("rent_receipts");

                                                    ArrayList<String> RENT_DATEs = new ArrayList<>();
                                                    HashMap<String, String> rentMap = new HashMap<>();
//                                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                                                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                                                    for (int j = 0; j < tenantsJson1.length(); j++) {
                                                        JSONObject tenant = tenantsJson1.getJSONObject(j);
                                                        String rent_id = tenant.getString("id");
                                                        String rent_date = tenant.getString("date");

//                                                        // Parse the input da te string to a Date object
//                                                        Date date = null;
//                                                        try {
//                                                            date = inputFormat.parse(rent_date);
//                                                        } catch (ParseException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                        // Format the date to the desired output format
//                                                        String formattedDate = outputFormat.format(date);

                                                        RENT_DATEs.add(rent_date);
                                                        rentMap.put(rent_date, rent_id);
                                                        Log.d("selectedUnitID", "this is hashmap" + rent_date);
                                                        Log.d("selectedUnitID", "this is hashmap" + rentMap);
                                                    }
                                                    ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, RENT_DATEs);
                                                    selectRentReceipt.setAdapter(spinnerAdapter1);
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
                                                                                    tenantRecycler4.setVisibility(View.GONE);
                                                                                }
                                                                                Log.d("Rentdata", String.valueOf(tenantsJson));
                                                                                tenantRecycler4.setLayoutManager(new LinearLayoutManager(getContext()));
                                                                                tenantRecycler4.setAdapter(receiptAdapter3);
                                                                                receiptAdapter3 = new ReceiptAdapterTenant3(receipts2);
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

                                            }
                                        }
                                );

                                request7.authenticated(getActivity().getApplicationContext());
                                queue7.add(request7);


//

                                //start of useR_tenant
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
        //end of unseR_tenant

        return view;
    }


}