package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Bill_history.BillsTenant;
import com.example.billit_all.Bill_history.HistoryFragmentTenant;
import com.example.billit_all.Calculate.ElectricityCalculate;
import com.example.billit_all.Calculate.ElectricityCalculateTenant;
import com.example.billit_all.Calculate.WaterCalculate;
import com.example.billit_all.Calculate.WaterCalculateTenant;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardTenant extends AppCompatActivity {

    BottomNavigationView bottomNavigationView, nav;

    Toolbar toolbar;
    String url = "http://192.168.1.5:8000/api/tokens/revoke";
    //initialization of fragments
    HistoryFragmentTenant historyFragment = new HistoryFragmentTenant();
    UserFragment userFragment = new UserFragment();
    HomeFragmentTenant homeFragmentTenant = new HomeFragmentTenant();
    Home2FragmentTenant home2FragmentTenant = new Home2FragmentTenant();
    Home3FragmentTenant home3FragmentTenant = new Home3FragmentTenant();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_tenant);

        nav = findViewById(R.id.navbar);
        nav.setVisibility(View.VISIBLE);

        //        initialization of bottomNav and floatingAct
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //priority fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentTenant).commit();

        //execution of itemListener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentTenant).commit();
                        NavigationBarView nav = findViewById(R.id.navbar);
                        nav.setVisibility(View.VISIBLE);
                        return true;
                    case  R.id.history:
                        NavigationBarView nav2 = findViewById(R.id.navbar);
                        nav2.setVisibility(View.GONE);
                        nav2.setAlpha(0);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,historyFragment).commit();

                        return true;
                    case  R.id.user:
                        NavigationBarView nav4 = findViewById(R.id.navbar);
                        nav4.setVisibility(View.GONE);
                        nav4.setAlpha(0);

                        getSupportFragmentManager().beginTransaction().replace(R.id.container,userFragment).commit();

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
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragmentTenant).commit();
                        return true;
                    case  R.id.maynilad:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home2FragmentTenant).commit();
                        return true;
                    case R.id.manilaWater:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home3FragmentTenant).commit();
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
        Intent i = new Intent(this, WaterCalculateTenant.class);
        //start the another activity
        startActivity(i);

    }

    public void electric(View view){
        //instantiate
        Intent v = new Intent(this, ElectricityCalculateTenant.class);
        //start the another activity
        startActivity(v);

    }
    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.top_nav_tenant, menu);

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
                                        Fragment fragment = new NotificationsTenant();

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
            case R.id.concern:
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardTenant.this, Concerns.class));
                break;
            case R.id.settings:
                startActivity(new Intent(DashboardTenant.this, Settings.class));
                break;
            case R.id.bills:
                startActivity(new Intent(DashboardTenant.this, BillsTenant.class));
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