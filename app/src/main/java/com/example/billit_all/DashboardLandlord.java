package com.example.billit_all;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Bill_history.HistoryFragment;
import com.example.billit_all.Calculate.ElectricityCalculate;
import com.example.billit_all.Calculate.WaterCalculate;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardLandlord extends AppCompatActivity {

    BottomNavigationView bottomNavigationView, nav;

    Toolbar toolbar;
    String url = "http://192.168.1.5:8000/api/tokens/revoke";
    //initialization of fragments

    ChartFragment chartFragment = new ChartFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    UserFragment userFragment = new UserFragment();
    HomeFragmentLandlord homeFragmentLandlord = new HomeFragmentLandlord();

    Home2FragmentLandlord home2FragmentLandlord = new Home2FragmentLandlord();
    Home3FragmentLandlord home3FragmentLandlord = new Home3FragmentLandlord();
    CalculateFragment calculateFragment = new CalculateFragment();

    String[] permissions = {
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int REQUEST_CODE_PERMISSIONS = 123;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_landlord);


        checkPermissions();

        nav = findViewById(R.id.navbar);
        nav.setVisibility(View.VISIBLE);

//        initialization of bottomNav and floatingAct
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //priority fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentLandlord).commit();

        //execution of itemListener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.home:
                        NavigationBarView nav = findViewById(R.id.navbar);
                        nav.setVisibility(View.VISIBLE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentLandlord).commit();

                        return true;
                    case  R.id.history:
                        NavigationBarView nav2 = findViewById(R.id.navbar);
                        nav2.setAlpha(0);
                        nav2.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,historyFragment).commit();

                        return true;
                    case  R.id.chart:
                        NavigationBarView nav4 = findViewById(R.id.navbar);
                        nav4.setAlpha(0);
                        nav4.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,chartFragment).commit();

                        return true;
                    case  R.id.user:
                        //hide news navbar
                        NavigationBarView nav5 = findViewById(R.id.navbar);
                        nav5.setAlpha(0);
                        nav5.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,userFragment).commit();

                        return true;
                    case  R.id.calculate:
                        NavigationBarView nav3 = findViewById(R.id.navbar);
                        nav3.setAlpha(0);
                        nav3.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,calculateFragment).commit();

                        return true;
                    default:

                }

                return false;
            }



        });


        //execution of itemListener
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.meralco:
                        item.setChecked(true);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentLandlord).commit();
                        return true;
                    case  R.id.maynilad:
                        item.setChecked(true);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home2FragmentLandlord).commit();
                        return true;
                    case R.id.manilaWater:
                        item.setChecked(true);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home3FragmentLandlord).commit();
                        return true;

                    default:
                        return false;
                }

            }

        });



    }


    public void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void openProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void openAddTenant() {
        Intent intent = new Intent(this, AddTenant.class);
        startActivity(intent);
    }

    //lunching a new activity
    public void water(View view){
        //instantiate
        Intent i = new Intent(this, WaterCalculate.class);
        //start the another activity
        startActivity(i);

    }

    public void electric(View view){
        //instantiate
        Intent v = new Intent(this, ElectricityCalculate.class);
        //start the another activity
        startActivity(v);

    }

    public void report_issue(View view){
        //instantiate
        Intent v = new Intent(this, ReportIssue.class);
        //start the another activity
        startActivity(v);

    }
    @Override
    public void onBackPressed() {
        // do nothing
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(permission);
                }
            }
            if (permissionsNeeded.size() > 0) {
                requestPermissions(permissionsNeeded.toArray(new String[0]), REQUEST_CODE_PERMISSIONS);
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            // Handle the permissions that were not granted
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.top_nav_landlord, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification:
                BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                String backendToken = loginPreferenceDataSource.getBackendToken();
                try {
                    userDataSource.fetchUserFromBackend(
                            backendToken,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject user = response.getJSONObject("user");
                                        String role = user.getString("role");

                                        // Get the FragmentManager
                                        FragmentManager fragmentManager = getSupportFragmentManager();

                                        // Begin a FragmentTransaction
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        // Create a new instance of your fragment
                                        Fragment fragment = new NotificationsLandlord();

                                        // Replace the current layout with your fragment
                                        fragmentTransaction.replace(R.id.container, fragment);

                                        // Add the transaction to the back stack
                                        fragmentTransaction.addToBackStack(null);

                                        // Commit the transaction
                                        fragmentTransaction.commit();

//                                        Fragment fragment = new NotificationsLandlord();
//                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                        transaction.replace(R.id.container, fragment);
//                                        transaction.addToBackStack(null);
//                                        transaction.commit();
//                                        startActivity(new Intent(DashboardLandlord.this, NotificationsLandlord.class));

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
//                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(DashboardLandlord.this, Settings.class));
                break;
            case R.id.addTenant:
//                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardLandlord.this, AddTenant.class));
                break;
            case R.id.concern:
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardLandlord.this, Concerns.class));
                break;
            case R.id.addCash:
                Toast.makeText(this, "Cash Payment", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardLandlord.this, CashPayment.class));
                break;
            case R.id.addQrGcash:
                BillBackendDataResource userDataSource1 = BillBackendDataResource.getInstance(getApplicationContext());
                LoginPreferenceDataSource loginPreferenceDataSource1 = LoginPreferenceDataSource.getInstance(getApplicationContext());
//                RequestQueue requestQueue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                String backendToken1 = loginPreferenceDataSource1.getBackendToken();
                try {
                    userDataSource1.fetchUserFromBackend(
                            backendToken1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject user = response.getJSONObject("user");
                                        String id = user.getString("user_id");
                                        String role = user.getString("role");
//                                        if (role.equals("Tenant") && role == "Tenant") {
//                                            addGcash.setVisibility(View.INVISIBLE);
//                                        } else {
                                        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                                Request.Method.GET,
                                                "/api/gcash-qr/qr/" + id, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
//                                                                JSONObject qrCode = response.getJSONObject("hasQr");
                                                            Object hasQr = response.get("hasQr");
                                                            if (hasQr instanceof Boolean && !(Boolean) hasQr) {
                                                                // hasQr is false
                                                                Intent intent = new Intent(getApplicationContext(), AddGcash.class);
                                                                intent.putExtra("id", id);
                                                                startActivity(intent);
                                                            } else if (hasQr instanceof JSONObject) {
                                                                // hasQr is true
                                                                JSONObject qrObject = (JSONObject) hasQr;
                                                                String qr = qrObject.getString("qr");
                                                                // Extract other values as needed
                                                                Intent intent = new Intent(getApplicationContext(), EditGcash.class);
                                                                intent.putExtra("id", id);
                                                                intent.putExtra("qr", qr);
                                                                startActivity(intent);
                                                            } else {
                                                                // Unexpected response format
                                                                Log.e(TAG, "Unexpected response format: " + response.toString());
                                                            }

                                                        } catch (JSONException e) {
//                                                                Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
//                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
//                                                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                                                            progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                        );
                                        queue.add(request);
//                                        }
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
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(DashboardLandlord.this, Concerns.class));
                break;
            case R.id.settings:
                startActivity(new Intent(DashboardLandlord.this, Settings.class));
                break;
            case R.id.logout:
                RequestQueue queue = Volley.newRequestQueue(this);

                StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                    // Handle successful response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            // Handle successful logout
                            Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle unsuccessful logout
                        }
                    } catch (JSONException e) {
                        // Handle JSON exception
                    }
                }, error -> {
                    // Handle error
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // Add token to headers if necessary
                        Map<String, String> headers = new HashMap<>();
                        String token = LoginPreferenceDataSource.getInstance(getApplicationContext()).getBackendToken();
                        if (token != null) {
                            headers.put("Authorization", "Bearer " + token);
                        }
                        return headers;
                    }
                };
                queue.add(request);
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(DashboardLandlord.this, Concerns.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}