package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTenant extends AppCompatActivity implements View.OnClickListener {

    //    String url = "http://10.0.2.2:8000/api/tenant/temp-acct";
//    String url = "http://172.34.5.245:8000/api/tenant/temp-acct";
    String url = "https://bill-it.online/api/tenant/temp-acct";

    ImageView settings;
    private TextView rentFee,unitId;
    private EditText tenantUsername, tenantEmail, tenantPassword, userIdTxt;
    private Button addTenant;
    private ProgressBar progressBar;
    private Spinner spinner;

    ArrayAdapter<String> unitsAdapter;
    private List<String> unitsList = new ArrayList<>();
    private List<String> unitIdsList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);

        unitId = findViewById(R.id.unitId);
        rentFee = findViewById(R.id.rentFee);
        spinner = findViewById(R.id.spinner);
        tenantEmail = findViewById(R.id.tenantEmail);
        tenantPassword = findViewById(R.id.tenantPassword);
        userIdTxt = findViewById(R.id.useridTxt);

        addTenant = (Button) findViewById(R.id.addTenantBtn);
        addTenant.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTenant.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        retrieveProfile();


    }

    @Override
    public void onClick (View view) {
        switch (view.getId()){
            case R.id.addTenantBtn:
                registerTenant();
                break;
        }
    }

    public void retrieveProfile(){
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
                                userIdTxt.setText(id);

                                getTenants();
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

    public void getTenants() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());

        String url = "/api/units/tenant";

        JSONObject body = new JSONObject();
        try {
            // Pass user ID in request body
            body.put("id", userIdTxt.getText().toString());
            Log.d("iduser", String.valueOf(userIdTxt));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray notifsjson = response.getJSONArray("units");
                            Log.d("UNITS", String.valueOf(notifsjson));
                            for (int i = 0; i < notifsjson.length(); i++) {
                                JSONObject notifjson = notifsjson.getJSONObject(i);
                                Log.d("UNITS", String.valueOf(notifjson));
                                String id = notifjson.getString("id");
                                Log.d("UNITS", id);
                                String unit = notifjson.getString("unit_no");
                                Log.d("UNITS", unit);
                                String rent = notifjson.getString("rent_fee");
                                Log.d("UNITS", rent);
//
//                                unitsList.add(unit);

                                // Add the unit and rent fee to the unitsList
//                                unitsList.add(unit + " (Rent: " + rent + ")");
                                // Add the unit, rent fee and id to the unitsList
//                                unitsList.add(unit + " (Rent: " + rent + ")|" + id);
                                unitsList.add(unit + " (Rent: " + rent + ")");
                                unitIdsList.add(id);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTenant.this, android.R.layout.simple_spinner_item, unitsList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                            // Add an onItemSelectedListener to the spinner
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    // Get the selected item from the spinner
                                    String selectedItem = parent.getItemAtPosition(position).toString();

//                                    // Extract the ID and rent fee from the selected item
//                                    int pipeIndex = selectedItem.indexOf("|");
//                                    String selectedUnit = selectedItem.substring(0, pipeIndex);
//                                    String selectedId = selectedItem.substring(pipeIndex + 1);
//
//                                    // Set the rent fee text on the EditText view
//                                    rentFee.setText("₱" + selectedUnit.substring(selectedUnit.indexOf(":") + 2, selectedUnit.indexOf(")")) + " for this unit ");
//
//                                    // Set the ID text on the TextView view
//                                    unitId.setText(selectedId);

                                    // Extract the ID and rent fee from the selected item
                                    String selectedId = unitIdsList.get(position);

                                    // Set the rent fee text on the EditText view
                                    rentFee.setText("₱" + selectedItem.substring(selectedItem.indexOf(":") + 2, selectedItem.indexOf(")")) + " for this unit ");

                                    // Set the ID text on the TextView view
                                    unitId.setText(selectedId);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // Do nothing
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
        request.authenticated(getApplicationContext());
        queue.add(request);
    }


//    private void populateSpinner() {
////        String userId = userIdTxt.getText().toString(); // get the String value of userIdTxt
//        String id = userIdTxt.getText().toString(); // get the String value of userIdTxt
//        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//        HashMap<String, String> params = new HashMap<>();
//        params.put("id", String.valueOf(userIdTxt));
//        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                Request.Method.POST,
//                "/api/units/tenant",
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray units = response.getJSONArray("units");
//                            Log.d("USER_UNIT", String.valueOf(units));
//                            ArrayList<String> items = new ArrayList<>();
//                            for (int i = 0; i < units.length(); i++){
//                                JSONObject tenant = units.getJSONObject(i);
//                                String unit = tenant.getString("unit_no");
//                                Log.d("UNIT",unit);
//                                items.add(unit);
//                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddTenant.this, android.R.layout.simple_spinner_item, items);
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                            spinner.setAdapter(adapter);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        queue.add(request);
//    }

    private void registerTenant(){

        String emailTenant = tenantEmail.getText().toString();
        String passwordTenant = tenantPassword.getText().toString();
        String userId = userIdTxt.getText().toString();
        String Id = unitId.getText().toString();


        if(emailTenant.isEmpty()){
            tenantEmail.setError("Please enter the tenant's temporary username");
            tenantEmail.requestFocus();
            return;
        }

        if(passwordTenant.isEmpty()){
            tenantPassword.setError("Please enter a 6 character password");
            tenantPassword.requestFocus();
            return;
        }

        if (emailTenant.isEmpty() || passwordTenant.isEmpty()){
            Toast.makeText(AddTenant.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Id.isEmpty()){
            Toast.makeText(AddTenant.this, "No Unit Available", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (passwordTenant.length() < 6){
//            tenantPassword.setError("Please enter 6 or more characters");
//            tenantPassword.requestFocus();
//            return;
//        }

        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                startActivity(new Intent(AddTenant.this, DashboardLandlord.class));
                Toast.makeText(AddTenant.this, "Temporary Account Created", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddTenant.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }

        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("temp_email", tenantEmail.getText().toString());
                stringMap.put("temp_password", tenantPassword.getText().toString());
                stringMap.put("landlord_id", userId);
                stringMap.put("unit_no", Id);

                return stringMap;
            }
        };
        queue.add(request);
    }
}