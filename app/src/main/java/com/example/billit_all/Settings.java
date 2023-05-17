package com.example.billit_all;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Contract_creation.Contract;
import com.example.billit_all.Contract_creation.ContractView;
import com.example.billit_all.Contract_creation.ContractViewLandlord;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Settings extends AppCompatActivity {

    //    String url = "http://10.0.2.2:8000/api/tokens/revoke";
    String url = "https://bill-it.online/api/tokens/revoke";
    Toolbar toolbar_landlord, toolbar_tenant;
    Button termsButton,contractButton,privacyButton,helpButton,changePassButton,logoutBtn;
    private String token;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar_landlord = findViewById(R.id.toolbar_landlord);

        setSupportActionBar(toolbar_landlord);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_tenant = findViewById(R.id.toolbar_tenant);

        setSupportActionBar(toolbar_tenant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        termsButton = (Button) findViewById(R.id.termsButton);
        termsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTermsConditions();
            }
        });

        contractButton = (Button) findViewById(R.id.contractButton);

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
                                Log.d("USERS_EDIT", String.valueOf(user));
                                if (role.equals("Tenant")) {
                                    contractButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.this, ContractView.class);
                                            startActivity(intent);

                                        }
                                    });
                                } else {
                                    contractButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.this, ContractViewLandlord.class);
                                            startActivity(intent);

                                        }
                                    });

                                }
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


        privacyButton = (Button) findViewById(R.id.privacyButton);
        privacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openPrivacyPolicy();
            }
        });

        helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openHelp();
            }
        });

        changePassButton = (Button) findViewById(R.id.changePassButton);
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openChangePassword();
            }
        });

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }

    private void logout() {
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
    }


    public void openTermsConditions() {
        Intent intent = new Intent(this, TermsConditions.class);
        startActivity(intent);
    }

//    public void openContract() {
//        Intent intent = new Intent(this, Contract.class);
//        startActivity(intent);
//    }

    public void openPrivacyPolicy() {
        Intent intent = new Intent(this, PrivacyPolicy.class);
        startActivity(intent);

    }

    public void openHelp() {
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    public void openChangePassword() {
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                                String id = user.getString("user_id");
                                String role = user.getString("role");
                                if (role.equals("Tenant")) {
                                    toolbar_tenant.setVisibility(View.VISIBLE);
                                    getMenuInflater().inflate(R.menu.top_nav_tenant, menu);
                                } else {
                                    toolbar_landlord.setVisibility(View.VISIBLE);
                                    getMenuInflater().inflate(R.menu.top_nav_landlord, menu);
                                }
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
//        getMenuInflater().inflate(R.menu.top_nav_landlord, menu);

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
                break;
            case R.id.addTenant:
//                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.this, AddTenant.class));
                break;
            case R.id.concern:
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.this, Concerns.class));
                break;
            case R.id.addCash:
                Toast.makeText(this, "Cash Payment", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.this, CashPayment.class));
                break;
            case R.id.addQrGcash:
                BillBackendDataResource userDataSource1 = BillBackendDataResource.getInstance(getApplicationContext());
                LoginPreferenceDataSource loginPreferenceDataSource1 = LoginPreferenceDataSource.getInstance(getApplicationContext());
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
                break;
            case R.id.settings:
                startActivity(new Intent(Settings.this, Settings.class));
                break;
//            case R.id.logout:
//                RequestQueue queue = Volley.newRequestQueue(this);
//
//                StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
//                    // Handle successful response
//                    try {
//                        JSONObject jsonResponse = new JSONObject(response);
//                        boolean success = jsonResponse.getBoolean("success");
//                        String message = jsonResponse.getString("message");
//                        if (success) {
//                            // Handle successful logout
//                            Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(this, Login.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            // Handle unsuccessful logout
//                        }
//                    }
//                    catch (JSONException e) {
//                        // Handle JSON exception
//                    }
//                }, error -> {
//                    // Handle error
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        // Add token to headers if necessary
//                        Map<String, String> headers = new HashMap<>();
//                        String token = LoginPreferenceDataSource.getInstance(getApplicationContext()).getBackendToken();
//                        if (token != null) {
//                            headers.put("Authorization", "Bearer " + token);
//                        }
//                        return headers;
//                    }
//                };
//                queue.add(request);
//                break;
        }

        return super.onOptionsItemSelected(item);
    }
}