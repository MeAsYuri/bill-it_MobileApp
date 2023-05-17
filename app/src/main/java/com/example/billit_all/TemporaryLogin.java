package com.example.billit_all;import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Contract_creation.ContractForm;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TemporaryLogin extends AppCompatActivity {

    Button contSpecsBtn;
    CircularImageView profileImage;
    TextView topNameTextView, nameTextView, emailTextView,contactTextView, logoutBtn, contractIdTextView, userIdTxtView;

    private ImageLoader imageLoader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporary_login);

        userIdTxtView = findViewById(R.id.user_id);
        contractIdTextView = findViewById(R.id.contract_id);
        logoutBtn = findViewById(R.id.logout);
        contSpecsBtn = findViewById(R.id.contSpecs);
        profileImage = findViewById(R.id.profile_image);
        topNameTextView = findViewById(R.id.topName);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        contactTextView = findViewById(R.id.contact);


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


        contSpecsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TemporaryLogin.this, ContractForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });




//        getProfile();
        retrieveProfile();


    }

    private void logout() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, "https://bill-it.online/api/tokens/revoke", response -> {
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
//                                Log.d("USER_USER", String.valueOf(user));
                                String name = user.getString("name");
                                topNameTextView.setText(name);
                                nameTextView.setText(name);
                                String email = user.getString("email");
                                emailTextView.setText(email);
                                String phone = user.getString("phone");
                                contactTextView.setText(phone);
                                String id = user.getString("user_id");
                                userIdTxtView.setText(id);

                                retrieveContract();

                                String imageUrl = user.getString("profile");
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            profileImage.setImageBitmap(bitmap);
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Handle errors
                                    }
                                });

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


    public void retrieveContract() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        String url = "https://bill-it.online/api/conspecs/info";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject data = jsonArray.getJSONObject(0); // assuming there is only one element in the array
                        Log.d("DATA_SPEC", String.valueOf(data));
                        String landlord = data.optString("landlord_id");
                        contractIdTextView.setText(landlord);
                        if (contractIdTextView != null) {
                            contSpecsBtn.setClickable(false);
                            contSpecsBtn.setBackgroundColor(getResources().getColor(R.color.inactive));
                        } else {
                            contSpecsBtn.setClickable(true);
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
                params.put("id", userIdTxtView.getText().toString());
                return params;
            }
        };
        queue.add(request);
    }



}
