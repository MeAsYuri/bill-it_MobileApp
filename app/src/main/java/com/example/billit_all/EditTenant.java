package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Contract_creation.ContractSigning;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditTenant extends AppCompatActivity implements View.OnClickListener{

//    //    String url = "http://10.0.2.2:8000/api/temp-acct/update/tenant";
//    String url = "http://172.34.5.245:8000/api/temp-acct/update/tenant";
    String url = "https://bill-it.online/api/temp-acct/update/tenant";


    EditText tenantName, tenantEmail, tenantUsername, tenantPhone, tenantId, houseNumber, street, subd, brgy, city, zip;
    //    TextView idTenant;
    ImageView logo, contractImage, settings;
    Button updateTenantBtn, uploadImageBtn;
    ProgressBar progressBar;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tenant);

        houseNumber = (EditText) findViewById(R.id.houseNum);
        street = (EditText) findViewById(R.id.street);
        subd = (EditText) findViewById(R.id.subdivision);
        brgy = (EditText) findViewById(R.id.barangay);
        city = (EditText) findViewById(R.id.city);
        zip = (EditText) findViewById(R.id.zipcode);
        logo = (ImageView) findViewById(R.id.logo);
        tenantId = (EditText) findViewById(R.id.tenantId);
        tenantName = (EditText) findViewById(R.id.tenantName);
        tenantEmail = (EditText) findViewById(R.id.tenantEmail);
        tenantUsername = (EditText) findViewById(R.id.tenantUsername);
        tenantPhone = (EditText) findViewById(R.id.tenantPhone);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);
        updateTenantBtn = (Button) findViewById(R.id.updateTenantBtn);
        updateTenantBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditTenant.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getTenant();

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updateTenantBtn:
                editTenant();
                break;
        }

    }

    public void getTenant() {

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
                                String id = user.getString("user_id");
                                tenantId.setText(id);
                                String name = user.getString("name");
//                                tenantName.setText(name);
                                String email = user.getString("email");
                                tenantEmail.setText(email);
//                                String username = user.getString("username");
//                                tenantUsername.setText(username);
                                String phone = user.getString("phone");
//                                tenantPhone.setText(phone);

//                                editTenant();

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

    public void editTenant() {

        String idTenant = tenantId.getText().toString();
        String nameTenant = tenantName.getText().toString();
        String emailTenant = tenantEmail.getText().toString();
        String usernameTenant = tenantUsername.getText().toString();
        String phoneTenant = tenantPhone.getText().toString();

        String tenantHouseNum = houseNumber.getText().toString();
        String tenantStreet = street.getText().toString();
        String tenantSubd = subd.getText().toString();
        String tenantBrgy = brgy.getText().toString();
        String tenantCity = city.getText().toString();
        String tenantZip = zip.getText().toString();

        if (nameTenant.isEmpty() || emailTenant.isEmpty() || usernameTenant.isEmpty() || phoneTenant.isEmpty()) {
            Toast.makeText(EditTenant.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tenantHouseNum.isEmpty() || tenantStreet.isEmpty() || tenantSubd.isEmpty() || tenantCity.isEmpty() || tenantBrgy.isEmpty() || tenantZip.isEmpty()) {
            Toast.makeText(EditTenant.this, "Please fill out all the fields for the address!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                Toast.makeText(EditTenant.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                // create intent to start ContractSigning activity
//                startActivity(new Intent(EditTenant.this, ContractSigning.class));


                Intent intent = new Intent(EditTenant.this, ContractSigning.class);

                String tenant_name = tenantName.getText().toString();
                String tenant_houseNo = houseNumber.getText().toString();
                String tenant_street = street.getText().toString();
                String tenant_subd = subd.getText().toString();
                String tenant_brgy = brgy.getText().toString();
                String tenant_city = city.getText().toString();
                String tenant_zip = zip.getText().toString();


                intent.putExtra("tenant_name", tenant_name);
                intent.putExtra("tenant_houseNo", tenant_houseNo);
                intent.putExtra("tenant_street", tenant_street);
                intent.putExtra("tenant_subd", tenant_subd);
                intent.putExtra("tenant_brgy", tenant_brgy);
                intent.putExtra("tenant_city", tenant_city);
                intent.putExtra("tenant_zip", tenant_zip);

                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditTenant.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("id", tenantId.getText().toString());
                stringMap.put("name", tenantName.getText().toString());
                stringMap.put("email", tenantEmail.getText().toString());
                stringMap.put("username", tenantUsername.getText().toString());
                stringMap.put("phone", tenantPhone.getText().toString());
                stringMap.put("house_no", houseNumber.getText().toString());
                stringMap.put("street", street.getText().toString());
                stringMap.put("subd", subd.getText().toString());
                stringMap.put("brgy", brgy.getText().toString());
                stringMap.put("city", city.getText().toString());
                stringMap.put("zipcode", zip.getText().toString());

                return stringMap;
            }


        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

}