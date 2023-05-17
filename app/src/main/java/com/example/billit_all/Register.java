package com.example.billit_all;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.VolleyMultipartRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class Register extends AppCompatActivity implements View.OnClickListener {

//    String url = "http://10.0.2.2:8000/api/register";

    //    String url = "http://192.168.254.104:8000/api/register";
    String url = "https://bill-it.online/api/register/landlord";


    private EditText nameTxt, usernameTxt, emailTxt, phoneTxt, passwordTxt, conPasswordTxt, houseNumTxt, streetTxt, subdTxt, brgyTxt, cityTxt, zipTxt;
    private Button registerBtn, uploadImageBtn;
    private TextView loginNowBtn, tncReadMe;
    private ProgressBar progressBar;
    private CheckBox tnc_agree;
    String encodeImageString;
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

//    private CircularImageView profileImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        nameTxt = (EditText) findViewById(R.id.name);
        usernameTxt = (EditText) findViewById(R.id.username);
        emailTxt = (EditText) findViewById(R.id.email);
        phoneTxt = (EditText) findViewById(R.id.phone);
        passwordTxt = (EditText) findViewById(R.id.password);
        conPasswordTxt = (EditText) findViewById(R.id.conPassword);
        houseNumTxt = (EditText) findViewById(R.id.houseNum);
        streetTxt = (EditText) findViewById(R.id.street);
        subdTxt = (EditText) findViewById(R.id.subdivision);
        brgyTxt = (EditText) findViewById(R.id.barangay);
        cityTxt = (EditText) findViewById(R.id.city);
        zipTxt = (EditText) findViewById(R.id.zipcode);

        tnc_agree = (CheckBox) findViewById(R.id.tnc_agree);

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);

        loginNowBtn = (TextView) findViewById(R.id.loginNow);
        loginNowBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        
        tncReadMe = (TextView) findViewById(R.id.tnc_readme);
        tncReadMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, TermsConditions.class));
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.loginNow:
                startActivity(new Intent(this, Login.class));
                break;
            case R.id.registerBtn:
                registerUser();
                break;
        }
    }

    private void registerUser() {

        String name = nameTxt.getText().toString();
        String username = usernameTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String phone = phoneTxt.getText().toString();
        String password = passwordTxt.getText().toString();
        String conPassword = conPasswordTxt.getText().toString();
        String houseNo = houseNumTxt.getText().toString();
        String street = streetTxt.getText().toString();
        String subd = subdTxt.getText().toString();
        String brgy = brgyTxt.getText().toString();
        String city = cityTxt.getText().toString();
        String zipcode = zipTxt.getText().toString();


        if(name.isEmpty()){
            nameTxt.setError("Please enter your full name");
            nameTxt.requestFocus();
            return;
        }
        if(email.isEmpty()){
            emailTxt.setError("Please enter your email address");
            emailTxt.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            phoneTxt.setError("Please enter your contact number");
            phoneTxt.requestFocus();
            return;
        }
        if(phone.length() < 11 ){
            phoneTxt.setError("Invalid phone number, please input an 11 digit number");
            phoneTxt.requestFocus();
            return;
        }
        if(password.isEmpty()){
            nameTxt.setError("Please enter a 6 character password");
            nameTxt.requestFocus();
            return;
        }
        if(conPassword.isEmpty()){
            conPasswordTxt.setError("Please re-enter your password");
            conPasswordTxt.requestFocus();
            return;
        }
        if(!password.equals(conPassword)){
            conPasswordTxt.setError("Password does not match, Try again.");
            conPasswordTxt.requestFocus();
            return;
        }
        if(name.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(Register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(houseNo.isEmpty() || street.isEmpty() || brgy.isEmpty() || city.isEmpty() || zipcode.isEmpty()) {
            Toast.makeText(Register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTxt.setError("Please provide a valid email!");
            emailTxt.requestFocus();
            return;
        }
        if(!tnc_agree.isChecked()){
            Toast.makeText(Register.this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
            tnc_agree.requestFocus();
            return;
        }

        if(password.length() < 6 ) {
            passwordTxt.setError("Please enter 6 or more character password");
            passwordTxt.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
//                new Response.Listener<NetworkResponse>() {
//                    @Override
//                    public void onResponse(NetworkResponse response) {
//                        if(!tnc_agree.isChecked()){
//                            Toast.makeText(Register.this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
//                            tnc_agree.requestFocus();
//                            progressBar.setVisibility(View.INVISIBLE);
//                        }else {
//
//                            Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.INVISIBLE);
//                            userLogin();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Register.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//                error.printStackTrace();
//            }
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(!tnc_agree.isChecked()){
                    Toast.makeText(Register.this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                    tnc_agree.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);
                }else {

                    Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    userLogin();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Register.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("name", name);
                stringMap.put("username", username);
                stringMap.put("email", email);
                stringMap.put("phone", phone);
                stringMap.put("password", password);
                stringMap.put("house_no", houseNo);
                stringMap.put("street", street);
                stringMap.put("subd", subd);
                stringMap.put("brgy", brgy);
                stringMap.put("city", city);
                stringMap.put("zipcode", zipcode);
                stringMap.put("tnc_agree", "true");

                return stringMap;
            }

        };

        queue.add(request);
    }

    private void userLogin(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();


        progressBar.setVisibility(View.VISIBLE);

        JSONObject form = new JSONObject();
        try {
            form.put("email", email);
            form.put("password", password);
//            form.put("getRole", true);
        } catch (JSONException e) {
            progressBar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.POST,
                "/api/tokens/create", form,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            String name = response.getString("name");
                            String token = response.getString("token");

//                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("token", token);
//                            editor.commit();

                            loginPreferenceDataSource.storeBackendToken(token);

//                            startActivity(new Intent(Login.this, DashboardLandlord.class));
                            retrieveProfileForLogin(token);

                        } catch (JSONException e) {
                            Toast.makeText(Register.this, "Please check your credentials", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
        //add this line below if needed for authentication
//        request.authenticated(getApplicationContext());
        queue.add(request);

    }

    public void retrieveProfileForLogin(String backendToken){
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        try {
            userDataSource.fetchUserForLogin(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                String name = user.getString("name");
                                String isVerified = user.getString("isVerified");
                                String role = user.getString("role");
//                                String contractImg = user.getString("contract_img");
//
                                startActivity(new Intent(Register.this, RegUploadImg.class));

                            } catch (JSONException e) {
                                Toast.makeText(Register.this, "Please check your credentials", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
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