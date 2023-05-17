package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountApprovedLandlord extends AppCompatActivity {

    TextView message, messageTitle, idTextView;
    ImageView logo;
    Button confirmBtn;

    Animation Anim_top, Anim_bottom, Anim_logo;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_approved_landlord);

        idTextView = findViewById(R.id.id);
        message = findViewById(R.id.messageBody);
        messageTitle = findViewById(R.id.messageTitle);
        logo = findViewById(R.id.logo);
        confirmBtn = findViewById(R.id.confirmBtn);

        Anim_logo = AnimationUtils.loadAnimation(this,R.anim.logo_anim);
        Anim_top = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Anim_bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logo.setAnimation(Anim_logo);
        messageTitle.setAnimation(Anim_top);
        message.setAnimation(Anim_bottom);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountApprovedLandlord.this, DashboardLandlord.class);
                startActivity(intent);
                finish();
            }
        });

        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        int userId = userSharedPreferenceDataSource.getBackendId();
        idTextView.setText(String.valueOf(userId));

        retrieveProfile();

    }


    public void retrieveProfile() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//        String url = "http://172.34.5.245:8000/api/get/notification/page";
        String url = "http://192.168.1.5:8000/api/get/notification/page";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String type = jsonObject.getString("type");
                        String typeOfId = jsonObject.getString("type_of_id");
                        JSONObject data = jsonObject.getJSONObject("data");

                        if (type.equals("approval") && typeOfId.equals("landlord_id")) {
                            // Handle landlord's approval data
                            String dataId = data.getString("id");

                            boolean isSeen = data.getBoolean("isSeen");
                            String date = data.getString("date");
                            // ...
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", idTextView.getText().toString());
                return params;
            }
        };
        queue.add(request);
    }
}