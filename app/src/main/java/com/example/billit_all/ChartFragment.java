package com.example.billit_all;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.billit_all.Payment.PaymentElectricity;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;


public class ChartFragment extends Fragment {

    BarChart elecBalance, waterBalance, rentBalance, elecpenaltyBalance, waterpenaltyBalance, rentpenaltyBalance;
    PieChart elecStatus, waterStatus, rentStatus;
    TextView totalTenant, penaltyTenant, paidElec, paidWater, unpaidElec, unpaidWater, paidRent, unpaidRent;
    Button viewTenants, downloadElecBalance;

    Button elecBalanceChart, waterBalanceChart, rentBalanceChart, elecpenaltyBalanceChart, waterpenaltyBalanceChart, rentpenaltyBalanceChart, elecStatusChart, waterStatusChart, rentStatusChart;
    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private ViewPager2 viewPager2;
    ArrayList<AnnouncementModel> announcementModelArrayList;
    private Timer autoSwipeTimer;
    private AutoSwipeTask autoSwipeTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chart, container, false);

        totalTenant = view.findViewById(R.id.totalTenant);
        viewTenants = view.findViewById(R.id.viewTenants);
//        penaltyTenant = view.findViewById(R.id.penaltyTenant);
//        paidElec = view.findViewById(R.id.paidElec);
//        paidWater = view.findViewById(R.id.paidWater);
//        unpaidElec = view.findViewById(R.id.unpaidElec);
//        unpaidWater = view.findViewById(R.id.unpaidWater);
//        paidRent = view.findViewById(R.id.paidRent);
//        unpaidRent = view.findViewById(R.id.unpaidRent);
        elecBalance = view.findViewById(R.id.elecBalance);
        waterBalance = view.findViewById(R.id.waterBalance);
        rentBalance = view.findViewById(R.id.rentBalance);
        elecpenaltyBalance = view.findViewById(R.id.elecpenaltyBalance);
        waterpenaltyBalance = view.findViewById(R.id.waterpenaltyBalance);
        rentpenaltyBalance = view.findViewById(R.id.rentpenaltyBalance);
//        elecStatus = view.findViewById(R.id.elecStatus);
//        waterStatus = view.findViewById(R.id.waterStatus);
//        rentStatus = view.findViewById(R.id.rentStatus);

        elecBalanceChart = view.findViewById(R.id.elecBalanceChart);
        waterBalanceChart = view.findViewById(R.id.waterBalanceChart);
        rentBalanceChart = view.findViewById(R.id.rentBalanceChart);

        elecpenaltyBalanceChart = view.findViewById(R.id.elecpenaltyBalanceChart);
        waterpenaltyBalanceChart = view.findViewById(R.id.waterpenaltyBalanceChart);
        rentpenaltyBalanceChart = view.findViewById(R.id.rentpenaltyBalanceChart);

//        elecStatusChart = view.findViewById(R.id.elecStatusChart);
//        waterStatusChart = view.findViewById(R.id.waterStatusChart);
//        rentStatusChart = view.findViewById(R.id.rentStatusChart);
//        downloadElecBalance = view.findViewById(R.id.downloadElecBalance);

        viewPager2 = view.findViewById(R.id.viewPager);


        retrieveTotalDatas();
        retrieveElecBalance();
        retrieveWaterBalance();
        retrieveRentBalance();
        retrieveElecPenaltyBalance();
        retrieveWaterPenaltyBalance();
        retrieveRentPenaltyBalance();
//        retrieveElecStatus();
//        retrieveWaterStatus();
//        retrieveRentStatus();
        retrieveAnnouncement();
        viewTenants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewTenants.getContext(), ManageTenants.class);
//                intent.putExtra("id", tenantID_position);
//                intent.putExtra("amount", tenantAmount_position);
                viewTenants.getContext().startActivity(intent);
            }
        });




        return view;
    }

    public void retrieveAnnouncement() {
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/announcements/all/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray announcementsArray = response.getJSONArray("data");

                                                    if (announcementsArray == null || announcementsArray.length() == 0) {
                                                        viewPager2.setVisibility(View.GONE);
                                                        if (autoSwipeTimer != null) {
                                                            autoSwipeTimer.cancel();
                                                        }
                                                        if (autoSwipeTask != null) {
                                                            autoSwipeTask.cancel();
                                                        }
                                                    } else {
                                                        viewPager2.setVisibility(View.VISIBLE);
                                                        autoSwipeTimer = new Timer();
                                                        autoSwipeTask = new AutoSwipeTask(viewPager2);
                                                        autoSwipeTimer.schedule(autoSwipeTask, 3000, 3000); // Delay of 3000ms (3 seconds) and repeat every 3000ms
                                                    }

                                                    String[] title = new String[announcementsArray.length()];
                                                    String[] subject = new String[announcementsArray.length()];

                                                    for (int i = 0; i < announcementsArray.length(); i++) {
                                                        JSONObject announcementObject = announcementsArray.getJSONObject(i);
                                                        title[i] = announcementObject.getString("subject");
                                                        subject[i] = announcementObject.getString("description");
                                                    }
                                                    announcementModelArrayList = new ArrayList<>();

                                                    for (int i = 0; i < title.length; i++) {
                                                        AnnouncementModel announcementModel = new AnnouncementModel(title[i], subject[i]);
                                                        announcementModelArrayList.add(announcementModel);
                                                    }

                                                    AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(announcementModelArrayList);
                                                    viewPager2.setAdapter(announcementAdapter);
                                                    viewPager2.setClipToPadding(false);
                                                    viewPager2.setClipChildren(false);
                                                    viewPager2.setOffscreenPageLimit(2);
                                                    viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
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

    public void retrieveTotalDatas(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/data/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    int tenantTotal = response.getInt("totalUser");
                                                    totalTenant.setText(String.valueOf(tenantTotal));

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

    public void retrieveElecBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/balances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray elecBal = response.getJSONArray("electricitybalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<elecBal.length(); i++){
                                                        JSONObject electricity = elecBal.getJSONObject(i);
                                                        String label = electricity.getString("name");
                                                        float value = (float) electricity.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }
                                                    BarDataSet barDataSet = new BarDataSet(barEntries,"Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    elecBalance.setData(new BarData(barDataSet));
                                                    elecBalance.animateY(500);
                                                    elecBalance.getDescription().setText("");
                                                    elecBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = elecBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
                                                    elecBalance.getDescription().setTextAlign(Paint.Align.CENTER);
                                                    elecBalance.getAxisLeft().setAxisMinimum(0f);
                                                    elecBalance.getAxisRight().setAxisMinimum(0f);
                                                    elecBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    elecBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


                                                    elecBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Bitmap chartBitmap = elecBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-ElectricityBalanceChart.pdf");

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
                                                    });
//                                                    end of pdf

                                                } catch (JSONException e)  {
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

    public void retrieveWaterBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/balances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray waterBal = response.getJSONArray("waterbalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<waterBal.length(); i++){
                                                        JSONObject water = waterBal.getJSONObject(i);
                                                        String label = water.getString("name");
                                                        float value = (float) water.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }


                                                    BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    waterBalance.setData(new BarData(barDataSet));
                                                    waterBalance.animateY(500);
                                                    waterBalance.getDescription().setText("");
                                                    waterBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = waterBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
                                                    waterBalance.getAxisLeft().setAxisMinimum(0f);
                                                    waterBalance.getAxisRight().setAxisMinimum(0f);
                                                    waterBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    waterBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


                                                    waterBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Bitmap chartBitmap = waterBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-WaterBalanceChart.pdf");

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

    public void retrieveRentBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/balances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray rentBal = response.getJSONArray("rentbalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<rentBal.length(); i++){
                                                        JSONObject rent = rentBal.getJSONObject(i);
                                                        String label = rent.getString("name");
                                                        float value = (float) rent.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }


                                                    BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    rentBalance.setData(new BarData(barDataSet));
                                                    rentBalance.animateY(500);
                                                    rentBalance.getDescription().setText("Tenants Rent Balance");
                                                    rentBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = rentBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1
                                                    rentBalance.getLegend().setTextColor(Color.BLACK);
                                                    rentBalance.getAxisLeft().setAxisMinimum(0f);
                                                    rentBalance.getAxisRight().setAxisMinimum(0f);
                                                    rentBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    rentBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
//                                                    rentBalance.getXAxis().setLabelRotationAngle(45f);


                                                    rentBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            Bitmap chartBitmap = rentBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-RentBalanceChart.pdf");

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

    public void retrieveElecPenaltyBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/penaltybalances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray penaltyBal = response.getJSONArray("elecpenaltybalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<penaltyBal.length(); i++){
                                                        JSONObject penalty = penaltyBal.getJSONObject(i);
                                                        String label = penalty.getString("name");
                                                        float value = (float) penalty.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }
                                                    BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    elecpenaltyBalance.setData(new BarData(barDataSet));
                                                    elecpenaltyBalance.animateY(500);
                                                    elecpenaltyBalance.getDescription().setText("");
                                                    elecpenaltyBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = elecpenaltyBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1

                                                    elecpenaltyBalance.getAxisLeft().setAxisMinimum(0f);
                                                    elecpenaltyBalance.getAxisRight().setAxisMinimum(0f);
                                                    elecpenaltyBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    elecpenaltyBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


                                                    elecpenaltyBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Bitmap chartBitmap = elecpenaltyBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-ElectricityPenaltyBalanceChart.pdf");

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

    public void retrieveWaterPenaltyBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/penaltybalances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray penaltyBal = response.getJSONArray("waterpenaltybalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<penaltyBal.length(); i++){
                                                        JSONObject penalty = penaltyBal.getJSONObject(i);
                                                        String label = penalty.getString("name");
                                                        float value = (float) penalty.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }
                                                    BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    waterpenaltyBalance.setData(new BarData(barDataSet));
                                                    waterpenaltyBalance.animateY(500);
                                                    waterpenaltyBalance.getDescription().setText("");
                                                    waterpenaltyBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = waterpenaltyBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1

                                                    waterpenaltyBalance.getAxisLeft().setAxisMinimum(0f);
                                                    waterpenaltyBalance.getAxisRight().setAxisMinimum(0f);
                                                    waterpenaltyBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    waterpenaltyBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


                                                    waterpenaltyBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Bitmap chartBitmap = waterpenaltyBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-WaterPenaltyBalanceChart.pdf");

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

    public void retrieveRentPenaltyBalance(){
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
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/dashboard/penaltybalances/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray penaltyBal = response.getJSONArray("rentpenaltybalance");
                                                    ArrayList<BarEntry> barEntries = new ArrayList<>();
                                                    ArrayList <String> NAME = new ArrayList<>();
                                                    for (int i=0; i<penaltyBal.length(); i++){
                                                        JSONObject penalty = penaltyBal.getJSONObject(i);
                                                        String label = penalty.getString("name");
                                                        float value = (float) penalty.getInt("balance");
                                                        BarEntry barEntry = new BarEntry(i, value);
                                                        barEntries.add(barEntry);
                                                        NAME.add(label);
                                                    }
                                                    BarDataSet barDataSet = new BarDataSet(barEntries, "Balances");
                                                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                    barDataSet.setDrawValues(true);
                                                    rentpenaltyBalance.setData(new BarData(barDataSet));
                                                    rentpenaltyBalance.animateY(500);
                                                    rentpenaltyBalance.getDescription().setText("");
                                                    rentpenaltyBalance.getDescription().setTextColor(Color.BLACK);
                                                    XAxis xAxis = rentpenaltyBalance.getXAxis();
                                                    xAxis.setGranularity(1f); // set the minimum interval between two consecutive labels to 1

                                                    rentpenaltyBalance.getAxisLeft().setAxisMinimum(0f);
                                                    rentpenaltyBalance.getAxisRight().setAxisMinimum(0f);
                                                    rentpenaltyBalance.getXAxis().setValueFormatter(new IndexAxisValueFormatter(NAME));
                                                    rentpenaltyBalance.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


                                                    rentpenaltyBalanceChart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Bitmap chartBitmap = rentpenaltyBalance.getChartBitmap();
//                                                            String nameTenant = name_tenant.getText().toString();
//                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
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

                                                            File file = new File(dir,"Tenants-RentPenaltyBalanceChart.pdf");

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

//    public void retrieveElecStatus(){
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.updateProfile(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                Log.d("USER_ID", String.valueOf(user));
//                                String id = user.getString("user_id");
//                                Log.d("USER_ID", id);
//                                String role = user.getString("role");
//
//                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                                        Request.Method.GET,
//                                        "/api/dashboard/electricity/users/" + id,
//                                        null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                try {
////                                                    JSONArray elecStat = response.getJSONArray("data");
////                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
////                                                    ArrayList <String> NAME = new ArrayList<>();
////
////                                                    for (int i=0; i<elecStat.length(); i++){
////                                                        JSONObject electricityStat = elecStat.getJSONObject(i);
////                                                        String label = electricityStat.getString("name");
////                                                        float value = (float) electricityStat.getInt("y");
////                                                        PieEntry pieEntry = new PieEntry(value, label);
////                                                        pieEntries.add(pieEntry);
////                                                        NAME.add(label);
////                                                    }
//                                                    JSONObject elecStat = response.getJSONObject("data");
//                                                    JSONArray paidUsers = elecStat.getJSONArray("paidUsers");
//                                                    JSONArray unpaidUsers = elecStat.getJSONArray("unpaidUsers");
//
//                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
//                                                    ArrayList<String> NAME = new ArrayList<>();
//
//                                                    for (int i = 0; i < paidUsers.length(); i++) {
//                                                        JSONObject user = paidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    for (int i = 0; i < unpaidUsers.length(); i++) {
//                                                        JSONObject user = unpaidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
//
//                                                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                    pieDataSet.setSliceSpace(2f); // add some space between slices
//
//
//                                                    PieData pieData = new PieData(pieDataSet);
//                                                    pieData.setValueTextSize(14f);
//                                                    pieData.setValueTextColor(Color.BLACK);
//
//                                                    elecStatus.setData(pieData);
//                                                    elecStatus.animateXY(500, 500);
//                                                    elecStatus.getDescription().setEnabled(false);
//                                                    elecStatus.setHoleColor(Color.parseColor("#FFDAAF"));
//                                                    Legend legend = elecStatus.getLegend();
//                                                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//                                                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//                                                    legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//                                                    legend.setDrawInside(false);
//                                                    legend.setXEntrySpace(7f);
//                                                    legend.setYEntrySpace(7f);
//                                                    legend.setYOffset(0f);
//                                                    legend.setWordWrapEnabled(true);
//                                                    legend.setFormSize(12f);
//                                                    legend.setFormToTextSpace(5f);
//                                                    float legendSizePercent = Math.min(120f, 100f * (NAME.size() / 10f));
//                                                    legend.setMaxSizePercent(200f);
//                                                    rentStatus.invalidate();
//
//
//                                                    elecStatusChart.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            Bitmap chartBitmap = elecStatus.getChartBitmap();
////                                                            String nameTenant = name_tenant.getText().toString();
////                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                            // Create a new document
//                                                            PdfDocument document = new PdfDocument();
//
//                                                            // Create a page and set its size
//                                                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                            PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                            // Draw the bitmap on the page
//                                                            Canvas canvas = page.getCanvas();
//                                                            canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                            // Finish the page and save the document
//                                                            document.finishPage(page);
//
//                                                            File file = new File(dir,"Tenants-ElectricityStatusChart.pdf");
//
//                                                            try {
//                                                                document.writeTo(new FileOutputStream(file));
//                                                                Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                                Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                            }
//
//                                                            // Close the document
//                                                            document.close();
//                                                        }
//                                                    });
//
//
//
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//
//
//                                            }
//                                        },
//                                        new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//
//                                            }
//                                        }
//                                );
//
//                                request.authenticated(getActivity().getApplicationContext());
//                                queue.add(request);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    null
//            );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public void retrieveWaterStatus(){
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.updateProfile(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                Log.d("USER_ID", String.valueOf(user));
//                                String id = user.getString("user_id");
//                                Log.d("USER_ID", id);
//                                String role = user.getString("role");
//
//                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                                        Request.Method.GET,
//                                        "/api/dashboard/water/users/" + id,
//                                        null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                try {
////                                                    JSONArray waterStat = response.getJSONArray("data");
////                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
////                                                    ArrayList <String> NAME = new ArrayList<>();
////
////                                                    for (int i=0; i<waterStat.length(); i++){
////                                                        JSONObject waterStats = waterStat.getJSONObject(i);
////                                                        String label = waterStats.getString("name");
////                                                        float value = (float) waterStats.getInt("y");
////                                                        PieEntry pieEntry = new PieEntry(value, label);
////                                                        pieEntries.add(pieEntry);
////                                                        NAME.add(label);
////                                                    }
//                                                    JSONObject data = response.getJSONObject("data");
//                                                    JSONArray paidUsers = data.getJSONArray("paidUsers");
//                                                    JSONArray unpaidUsers = data.getJSONArray("unpaidUsers");
//
//                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
//                                                    ArrayList<String> NAME = new ArrayList<>();
//
//                                                    for (int i = 0; i < paidUsers.length(); i++) {
//                                                        JSONObject user = paidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    for (int i = 0; i < unpaidUsers.length(); i++) {
//                                                        JSONObject user = unpaidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
//                                                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                    pieDataSet.setSliceSpace(2f); // add some space between slices
//
//                                                    PieData pieData = new PieData(pieDataSet);
//                                                    pieData.setValueTextSize(14f);
//                                                    pieData.setValueTextColor(Color.BLACK);
//
//                                                    waterStatus.setData(pieData);
//                                                    waterStatus.animateXY(500, 500);
//                                                    waterStatus.getDescription().setEnabled(false);
//                                                    waterStatus.setHoleColor(Color.parseColor("#FFDAAF"));
//                                                    Legend legend = waterStatus.getLegend();
//                                                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//                                                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//                                                    legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//                                                    legend.setDrawInside(false);
//                                                    legend.setXEntrySpace(7f);
//                                                    legend.setYEntrySpace(7f);
//                                                    legend.setYOffset(0f);
//                                                    legend.setWordWrapEnabled(true);
//                                                    legend.setFormSize(12f);
//                                                    legend.setFormToTextSpace(5f);
//                                                    float legendSizePercent = Math.min(120f, 100f * (NAME.size() / 10f));
//                                                    legend.setMaxSizePercent(200f);
//                                                    rentStatus.invalidate();
//
//
//                                                    waterStatusChart.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            Bitmap chartBitmap = waterStatus.getChartBitmap();
////                                                            String nameTenant = name_tenant.getText().toString();
////                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                            // Create a new document
//                                                            PdfDocument document = new PdfDocument();
//
//                                                            // Create a page and set its size
//                                                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                            PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                            // Draw the bitmap on the page
//                                                            Canvas canvas = page.getCanvas();
//                                                            canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                            // Finish the page and save the document
//                                                            document.finishPage(page);
//
//                                                            File file = new File(dir,"Tenants-WaterStatusChart.pdf");
//
//                                                            try {
//                                                                document.writeTo(new FileOutputStream(file));
//                                                                Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                                Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                            }
//
//                                                            // Close the document
//                                                            document.close();
//                                                        }
//                                                    });
//
//
//
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//
//
//                                            }
//                                        },
//                                        new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//
//                                            }
//                                        }
//                                );
//
//                                request.authenticated(getActivity().getApplicationContext());
//                                queue.add(request);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    null
//            );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void retrieveRentStatus(){
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.updateProfile(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                Log.d("USER_ID", String.valueOf(user));
//                                String id = user.getString("user_id");
//                                Log.d("USER_ID", id);
//                                String role = user.getString("role");
//
//                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                                        Request.Method.GET,
//                                        "/api/dashboard/rent/users/" + id,
//                                        null,
//                                        new Response.Listener<JSONObject>() {
//                                            @Override
//                                            public void onResponse(JSONObject response) {
//                                                try {
////                                                    JSONArray rentStat = response.getJSONArray("data");
////                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
////                                                    ArrayList <String> NAME = new ArrayList<>();
////                                                    for (int i=0; i<rentStat.length(); i++){
////                                                        JSONObject rentStats = rentStat.getJSONObject(i);
////                                                        String label = rentStats.getString("name");
////                                                        float value = (float) rentStats.getInt("y");
////                                                        PieEntry pieEntry = new PieEntry(value, label);
////                                                        pieEntries.add(pieEntry);
////                                                        NAME.add(label);
////                                                    }
//                                                    JSONObject rentStat = response.getJSONObject("data");
//                                                    JSONArray paidUsers = rentStat.getJSONArray("paidUsers");
//                                                    JSONArray unpaidUsers = rentStat.getJSONArray("unpaidUsers");
//
//                                                    ArrayList<PieEntry> pieEntries = new ArrayList<>();
//                                                    ArrayList<String> NAME = new ArrayList<>();
//
//                                                    for (int i = 0; i < paidUsers.length(); i++) {
//                                                        JSONObject user = paidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    for (int i = 0; i < unpaidUsers.length(); i++) {
//                                                        JSONObject user = unpaidUsers.getJSONObject(i);
//                                                        String label = user.getString("name");
//                                                        float value = (float) user.getInt("y");
//                                                        PieEntry pieEntry = new PieEntry(value, label);
//                                                        pieEntries.add(pieEntry);
//                                                        NAME.add(label);
//                                                    }
//
//                                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
//                                                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                                                    pieDataSet.setSliceSpace(2f); // add some space between slices
//
//                                                    PieData pieData = new PieData(pieDataSet);
//                                                    pieData.setValueTextSize(14f);
//                                                    pieData.setValueTextColor(Color.BLACK);
//
//                                                    rentStatus.setData(pieData);
//                                                    rentStatus.animateXY(500, 500);
//                                                    rentStatus.getDescription().setEnabled(false);
//                                                    rentStatus.setHoleColor(Color.parseColor("#FFDAAF"));
//                                                    Legend legend = rentStatus.getLegend();
//
//                                                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//                                                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//                                                    legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
//                                                    legend.setDrawInside(false);
//                                                    legend.setXEntrySpace(7f);
//                                                    legend.setYEntrySpace(7f);
//                                                    legend.setYOffset(0f);
//                                                    legend.setWordWrapEnabled(true);
//                                                    legend.setFormSize(12f);
//                                                    legend.setFormToTextSpace(5f);
//                                                    float legendSizePercent = Math.min(120f, 100f * (NAME.size() / 10f));
//                                                    legend.setMaxSizePercent(200f);
//                                                    rentStatus.invalidate();
//
//                                                    rentStatusChart.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View v) {
//                                                            Bitmap chartBitmap = rentStatus.getChartBitmap();
////                                                            String nameTenant = name_tenant.getText().toString();
////                                                            String safeNameTenant = nameTenant.replaceAll("[^a-zA-Z0-9]", "_");
//                                                            // Create a new document
//                                                            PdfDocument document = new PdfDocument();
//
//                                                            // Create a page and set its size
//                                                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(chartBitmap.getWidth(), chartBitmap.getHeight(), 1).create();
//                                                            PdfDocument.Page page = document.startPage(pageInfo);
//
//                                                            // Draw the bitmap on the page
//                                                            Canvas canvas = page.getCanvas();
//                                                            canvas.drawBitmap(chartBitmap, 0, 0, null);
//
//                                                            // Finish the page and save the document
//                                                            document.finishPage(page);
//
//                                                            File file = new File(dir,"Tenants-RentStatusChart.pdf");
//
//                                                            try {
//                                                                document.writeTo(new FileOutputStream(file));
//                                                                Toast.makeText(getActivity(), "Chart exported to PDF", Toast.LENGTH_SHORT).show();
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                                Toast.makeText(getActivity(), "Export failed", Toast.LENGTH_SHORT).show();
//                                                            }
//
//                                                            // Close the document
//                                                            document.close();
//                                                        }
//                                                    });
//
//
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//
//
//                                            }
//                                        },
//                                        new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//
//                                            }
//                                        }
//                                );
//
//                                request.authenticated(getActivity().getApplicationContext());
//                                queue.add(request);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    null
//            );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

    private void exportElecBalance() {
        // Get the chart view and create a bitmap from it
        Bitmap chartBitmap = elecBalance.getChartBitmap();

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
        File file = new File(dir,"ElectricityBalance.pdf");
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