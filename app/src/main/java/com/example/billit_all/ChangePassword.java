package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

//    String url = "http://172.34.5.245:8000/api/auth/profile/password";
    String url = "https://bill-it.online/api/auth/profile/password";

    EditText currentPassword, newPassword, userId;
    Button updatePassBtn;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userId = (EditText) findViewById(R.id.userId);
        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);

        updatePassBtn = (Button) findViewById(R.id.updatePassButton);
        updatePassBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        getPassword();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updatePassButton:
                updatePassword();
                break;
        }

    }


    public void getPassword() {

        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.updatePassword(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                String id = user.getString("user_id");
                                userId.setText(id);
                                Log.d("USERPASS", id);


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

    public void updatePassword() {

        String passwordNew = newPassword.getText().toString();
        String passwordCurrent = currentPassword.getText().toString();


        if (passwordNew.isEmpty()) {
            newPassword.setError("Please fill in your new Password");
            newPassword.requestFocus();
            return;
        }

        if (passwordNew.equals(passwordCurrent)) {
            newPassword.setError("Invalid! Current and New Password cannot be the same!");
            newPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(ChangePassword.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(ChangePassword.this, Login.class));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangePassword.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }

        })

        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("id", userId.getText().toString());
                stringMap.put("current_password", currentPassword.getText().toString());
                stringMap.put("new_password", newPassword.getText().toString());

                return stringMap;
            }

        };

        queue.add(request);
    }
}