package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.UserSharedPreferenceDataSource;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotifBillPayment extends AppCompatActivity {

    Button verifyBtn, receiptBtn, acceptPaymentBtn;
    CircularImageView profileImage;
    TextView nameTextView, contactTextView, idTextView, unitId, amountText, payTitle, providerId, gcashId, balanceId, balanceTitle;
    EditText paidText;
    ImageView settings;

    private ImageLoader imageLoader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif_bill_payment);

        acceptPaymentBtn = (Button) findViewById(R.id.acceptPayment);
        payTitle = (TextView) findViewById(R.id.payTitle);
        profileImage = (CircularImageView) findViewById(R.id.profile_image);

        balanceTitle = (TextView) findViewById(R.id.balanceTitle);
        balanceId = (TextView) findViewById(R.id.balance);
        providerId = (TextView) findViewById(R.id.providerId);
        gcashId = (TextView) findViewById(R.id.gcashId);
        amountText = (TextView) findViewById(R.id.amount);
        unitId = (TextView) findViewById(R.id.unitId);
        idTextView = (TextView) findViewById(R.id.id);
        nameTextView = (TextView) findViewById(R.id.name);
        contactTextView = (TextView) findViewById(R.id.contact);
        paidText = (EditText) findViewById(R.id.paid);

        settings = (ImageView) findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifBillPayment.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        receiptBtn = (Button) findViewById(R.id.receipt);
        receiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotifBillPayment.this, ReceiptViewing.class);
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


        acceptPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                String url = "https://bill-it.online/api/get/notification/page";
                StringRequest request = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String type = jsonObject.getString("type");
//                                String typeOfId = jsonObject.getString("type_of_id");
                                JSONObject data = jsonObject.getJSONObject("data").getJSONObject("data");
                                Log.d("USER_PAY", String.valueOf(data));
                                String prov = jsonObject.getJSONObject("data").getString("provider");
                                String paid = data.getString("gcash_amount");
                                String provider = data.getString("provider_unit_id");
                                String gcash = data.getString("gcash_id");
                                String amount = data.getString("amount");
                                String balance = data.getString("balance");

                                if (type.equals("payment") && prov.equals("Electricity") && !paid.equals(amount)) {
                                    // Handle tenant's verification data

                                    acceptPaymentElectricity();

                                    Toast.makeText(NotifBillPayment.this, "Electricity Payment Updated!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(NotifBillPayment.this, DashboardLandlord.class);
                                    startActivity(intent);

                                } else if (type.equals("payment") && prov.equals("Water") && !paid.equals(amount)) {
                                    // Handle tenant's verification data

                                    acceptPaymentWater();
                                    Toast.makeText(NotifBillPayment.this, "Water Payment Updated!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(NotifBillPayment.this, DashboardLandlord.class);
                                    startActivity(intent);

                                } else if (type.equals("payment") && prov.equals("Rent") && !paid.equals(amount)) {
                                    // Handle tenant's verification data

                                    acceptPaymentRent();
                                    Toast.makeText(NotifBillPayment.this, "Rent Payment Updated!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(NotifBillPayment.this, DashboardLandlord.class);
                                    startActivity(intent);

                                }

                                acceptPaymentBtn.setVisibility(View.GONE);
                                paidText.setEnabled(false);

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
        });


        retrieveData();

    }

    public void retrieveData() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        String url = "https://bill-it.online/api/get/notification/page";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String type = jsonObject.getString("type");
//                        String typeOfId = jsonObject.getString("type_of_id");
                        JSONObject data = jsonObject.getJSONObject("data").getJSONObject("data");
                        Log.d("USER_DATA", String.valueOf(data));
                        String prov = jsonObject.getJSONObject("data").getString("provider");
                        String paid = data.getString("gcash_amount");
                        String provider = data.getString("provider_unit_id");
                        String gcash = data.getString("gcash_id");
                        String amount = data.getString("amount");
                        String balance = data.getString("balance");

                        if (type.equals("payment") && prov.equals("Electricity") && !amount.equals(paid)) {
                            // Handle tenant's verification data

                            payTitle.setText("Electricity Payment");


                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            balanceId.setText(balance);
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            providerId.setText(provider);
                            gcashId.setText(gcash);


                            balanceId.setVisibility(View.VISIBLE);
                            balanceTitle.setVisibility(View.VISIBLE);
                            acceptPaymentBtn.setVisibility(View.VISIBLE);

                            if (balance.equals("0")) {
                                balanceId.setVisibility(View.GONE);
                                balanceTitle.setVisibility(View.GONE);
                                acceptPaymentBtn.setVisibility(View.GONE);
                            } else {
                                balanceId.setVisibility(View.VISIBLE);
                                balanceTitle.setVisibility(View.VISIBLE);
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            }
                            if (paid.equals("0")) {
                                paidText.setText("");
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            } else {
                                paidText.setEnabled(false);
                                paidText.setText(paid);
                                acceptPaymentBtn.setVisibility(View.GONE);
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

                        } else if (type.equals("payment") && prov.equals("Electricity")) {
                            // Handle tenant's verification data

                            payTitle.setText("Electricity Payment");

                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            paidText.setText(paid);
                            providerId.setText(provider);
                            gcashId.setText(gcash);
                            paidText.setEnabled(false);
                            acceptPaymentBtn.setVisibility(View.GONE);

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

                        } else if (type.equals("payment") && prov.equals("Water") && !amount.equals(paid)) {
                            // Handle tenant's verification data

                            payTitle.setText("Water Payment");

                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            balanceId.setText(balance);
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            providerId.setText(provider);
                            gcashId.setText(gcash);
                            paidText.setEnabled(true);

                            balanceId.setVisibility(View.VISIBLE);
                            balanceTitle.setVisibility(View.VISIBLE);
                            acceptPaymentBtn.setVisibility(View.VISIBLE);


                            if (balance.equals("0")) {
                                balanceId.setVisibility(View.GONE);
                                balanceTitle.setVisibility(View.GONE);
                                acceptPaymentBtn.setVisibility(View.GONE);
                            } else {
                                balanceId.setVisibility(View.VISIBLE);
                                balanceTitle.setVisibility(View.VISIBLE);
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            }
                            if (paid.equals("0")) {

                                paidText.setText("");
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            } else {
                                paidText.setEnabled(false);
                                paidText.setText(paid);
                                acceptPaymentBtn.setVisibility(View.GONE);
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

                        } else if (type.equals("payment") && prov.equals("Water")) {
                            // Handle tenant's verification data

                            payTitle.setText("Water Payment");

                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            paidText.setText(paid);
                            providerId.setText(provider);
                            gcashId.setText(gcash);
                            paidText.setEnabled(false);
                            acceptPaymentBtn.setVisibility(View.GONE);

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

                        } else if (type.equals("payment") && prov.equals("Rent") && !amount.equals(paid)) {
                            // Handle tenant's verification data

                            payTitle.setText("Rent Payment");

                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            balanceId.setText(balance);
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            providerId.setText(provider);
                            gcashId.setText(gcash);
                            paidText.setEnabled(true);

                            balanceId.setVisibility(View.VISIBLE);
                            balanceTitle.setVisibility(View.VISIBLE);
                            acceptPaymentBtn.setVisibility(View.GONE);



                            if (balance.equals("0")) {
                                balanceId.setVisibility(View.GONE);
                                balanceTitle.setVisibility(View.GONE);
                                acceptPaymentBtn.setVisibility(View.INVISIBLE);
                            } else {
                                balanceId.setVisibility(View.VISIBLE);
                                balanceTitle.setVisibility(View.VISIBLE);
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            }
                            if (paid.equals("0")) {
                                paidText.setText("");
                                acceptPaymentBtn.setVisibility(View.VISIBLE);
                            } else {
                                paidText.setEnabled(false);
                                paidText.setText(paid);
                                acceptPaymentBtn.setVisibility(View.GONE);
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

                        } else if (type.equals("payment") && prov.equals("Rent")) {
                            // Handle tenant's verification data

                            payTitle.setText("Rent Payment");

                            String name = data.getString("name");
                            String phone = data.getString("phone");
                            String unit = data.getString("unit_no");
                            nameTextView.setText(name);
                            contactTextView.setText(phone);
                            unitId.setText(unit);
                            amountText.setText(amount);
                            paidText.setText(paid);
                            providerId.setText(provider);
                            gcashId.setText(gcash);
                            paidText.setEnabled(false);
                            acceptPaymentBtn.setVisibility(View.GONE);

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

    public void acceptPaymentElectricity() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/paynow/electricity",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            String message = result.getString("message");
                            Toast.makeText(NotifBillPayment.this, "Water Payment Updated!", Toast.LENGTH_SHORT).show();
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
                params.put("provider_unit_id", providerId.getText().toString());
                params.put("gcash_id", gcashId.getText().toString());
                params.put("amount_paid", paidText.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void acceptPaymentWater() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/paynow/water",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            String message = result.getString("message");
                            Toast.makeText(NotifBillPayment.this, "Water Payment Updated!", Toast.LENGTH_SHORT).show();
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
                params.put("provider_unit_id", providerId.getText().toString());
                params.put("gcash_id", gcashId.getText().toString());
                params.put("amount_paid", paidText.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void acceptPaymentRent() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/paynow/rent",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            String message = result.getString("message");
                            Toast.makeText(NotifBillPayment.this, "Water Payment Updated!", Toast.LENGTH_SHORT).show();
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
                params.put("provider_unit_id", providerId.getText().toString());
                params.put("gcash_id", gcashId.getText().toString());
                params.put("amount_paid", paidText.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}