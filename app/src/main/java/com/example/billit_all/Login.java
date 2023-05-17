package com.example.billit_all;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.Contract_creation.Contract;
import com.example.billit_all.Contract_creation.ContractForm;
import com.example.billit_all.Contract_creation.ContractPreview;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.AlgorithmConstraints;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView registerNowBtn, resetPassBtn;
    private EditText emailTxt, passwordTxt;
    private Button loginBtn;

    //    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        mAuth = FirebaseAuth.getInstance();

        registerNowBtn = (TextView) findViewById(R.id.registerNowBtn);
        registerNowBtn.setOnClickListener(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        emailTxt = (EditText) findViewById(R.id.email);
        passwordTxt = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        resetPassBtn = (TextView) findViewById(R.id.resetPassBtn);
        resetPassBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registerNowBtn:
                startActivity(new Intent(this,Register.class));
                break;

            case R.id.resetPassBtn:
                startActivity(new Intent(this,ForgotPassword.class));
                break;

            case R.id.loginBtn:
                userLogin();
                break;
        }
    }

    private void userLogin(){
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if(email.isEmpty()){
            emailTxt.setError("Username is required");
            emailTxt.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordTxt.setError("Password is required!");
            passwordTxt.requestFocus();
            return;
        }
//        if(password.length() < 6){
//            passwordTxt.setError("Invalid! Password is too short");
//            passwordTxt.requestFocus();
//            return;
//        }
        progressBar.setVisibility(View.VISIBLE);
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if(task.isSuccessful()){
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                    if(user.isEmailVerified()){
//                        startActivity(new Intent(Login.this, DashboardLandlord.class));
//
//                    }
////                    else{
////                        user.sendEmailVerification();
////                        Toast.makeText(Login.this, "PLEASE CHECK YOUR EMAIL TO VERIFY ACCOUNT!", Toast.LENGTH_LONG).show();
////                        progressBar.setVisibility(View.GONE);
////                    }
//
//                }
//                else {
//                    Toast.makeText(Login.this, "Login Failed! Please check your credentials", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

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
                        Log.d("Response", response.toString());

                        try {
//                            String name = response.getString("name");
                            String token = response.getString("token");
                            Log.d("token", response.toString());
//                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("token", token);
//                            editor.commit();
                            loginPreferenceDataSource.storeBackendToken(token);
//                            startActivity(new Intent(Login.this, DashboardLandlord.class));
                            retrieveProfileForLogin(token);

                        } catch (JSONException e) {
                            Log.e("Error", e.toString());
                            Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                                Log.d("login", name);
                                Log.d("login", isVerified);
                                Log.d("login", role);
                                if(name.equals("null")){
                                    startActivity(new Intent(Login.this, ContractPreview.class));
                                    Toast.makeText(Login.this, "Contract", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else if(isVerified.equals("0") && role.equals("Tenant")){
                                    Toast.makeText(Login.this, "Please wait for your account to be verified first!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else if(isVerified.equals("0") && role.equals("Landlord")){
                                    startActivity(new Intent(Login.this, TemporaryLogin.class));
                                    Toast.makeText(Login.this, "Contract Specs", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else if(isVerified.equals("1") && role.equals("Tenant")){
                                    startActivity(new Intent(Login.this, DashboardTenant.class));
                                    Toast.makeText(Login.this, "Sucessfully Logged In", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                } else if(!name.equals("") || !name.equals("null") && isVerified.equals("1") && role.equals("Landlord")) {
                                    startActivity(new Intent(Login.this, DashboardLandlord.class));
                                    Toast.makeText(Login.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else {

                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}