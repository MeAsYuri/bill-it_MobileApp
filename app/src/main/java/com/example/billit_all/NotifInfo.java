package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotifInfo extends AppCompatActivity {

    Button logoutBtn, verifyBtn, contractBtn;
    CircularImageView profileImage;
    TextView topNameTextView, nameTextView, emailTextView,contactTextView, idTextView, isVerifiedText, userId, unitId;
    ImageView settings;

    private ImageLoader imageLoader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_info);

        verifyBtn = (Button) findViewById(R.id.verify);
        profileImage = (CircularImageView) findViewById(R.id.profile_image);

        unitId = (TextView) findViewById(R.id.unitId);
        userId = (TextView) findViewById(R.id.userId);
        idTextView = (TextView) findViewById(R.id.id);
        topNameTextView = (TextView) findViewById(R.id.topName);
        nameTextView = (TextView) findViewById(R.id.name);
        emailTextView = (TextView) findViewById(R.id.email);
        contactTextView = (TextView) findViewById(R.id.contact);
        isVerifiedText = (TextView) findViewById(R.id.isVerified);

        settings = (ImageView) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifInfo.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        contractBtn = (Button) findViewById(R.id.contract);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifInfo.this, ContractViewingNotif.class);
                startActivity(intent);
            }
        });

        verifyBtn.setVisibility(View.VISIBLE);
//        verifyBtn.setBackgroundColor(getResources().getColor(R.color.bluebg));

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
                Intent intent = new Intent(NotifInfo.this, VerifySuccessPage.class);
                startActivity(intent);
            }
        });

        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        int userId = userSharedPreferenceDataSource.getBackendId();
        idTextView.setText(String.valueOf(userId));


        imageLoader = new ImageLoader(Volley.newRequestQueue(getApplicationContext()),
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });




        retrieveProfile();

    }

    public void retrieveProfile() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        String url = "https://bill-it.online/api/get/notification/page";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String type = jsonObject.getString("type");
//                        String typeOfId = jsonObject.getString("type_of_id");
                        JSONObject data = jsonObject.getJSONObject("data");
                        Log.d("USER_DATA", String.valueOf(data));

                        if (type.equals("verification")) {
                            // Handle tenant's verification data

                            String username = data.getString("username");
                            String email = data.getString("email");
                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("house_no");
                            topNameTextView.setText(name);
                            nameTextView.setText(name);
                            emailTextView.setText(email);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            String usersId = data.getString("user_id");
                            userId.setText(usersId);
                            String isVerified = data.getString("isVerified");
                            isVerifiedText.setText(isVerified);

                            if(isVerified.equals("0")){
                                verifyBtn.setClickable(true);
//                                verifyBtn.setBackgroundColor(getResources().getColor(R.color.bluebg));
                            }
                            if(isVerified.equals("1")) {
                                verifyBtn.setClickable(false);
                                verifyBtn.setBackgroundColor(getResources().getColor(R.color.inactive));
                            }

                            String imageUrl = data.getString("profile");
                            Log.d("IMAGE_URL", imageUrl);
                            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                    Bitmap bitmap = response.getBitmap();
                                    if (bitmap != null) {
                                        profileImage.setImageBitmap(bitmap);
                                        Log.d("IMAGE_LOADED", "Image loaded successfully");
                                    }else {
//                                        Log.d("IMAGE_LOAD", "Bitmap is null");
                                    }
                                }



                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle errors
                                    Log.d("IMAGE_LOAD", "Error loading image: " + error.getMessage());
                                }
                            });

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


    public void verifyUser() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/notif/user/verify",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            String message = result.getString("message");
                            Toast.makeText(NotifInfo.this, "Account Verified!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

}