    package com.example.billit_all;

    import androidx.appcompat.app.AppCompatActivity;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.os.Bundle;
    import android.util.Log;
    import android.util.LruCache;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.ImageLoader;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.billit_all.application.data.AppVolleyRequestQueue;
    import com.example.billit_all.application.data.UserSharedPreferenceDataSource;

    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.HashMap;
    import java.util.Map;

    public class ReceiptViewing extends AppCompatActivity {

        ImageView receiptImg, settings;
        TextView receiptImgName, idTextView;

        private ImageLoader imageLoader;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_receipt_viewing);

            settings = (ImageView) findViewById(R.id.settings);

            receiptImg = (ImageView) findViewById(R.id.receiptImg);
            receiptImgName = (TextView) findViewById(R.id.receiptImgName);

            idTextView = (TextView) findViewById(R.id.id);

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

            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ReceiptViewing.this, Settings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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
                            String typeOfId = jsonObject.getString("type_of_id");
                            JSONObject data = jsonObject.getJSONObject("data").getJSONObject("data");
                            String prov = jsonObject.getJSONObject("data").getString("provider");
                            Log.d("USER_RECEIPT", String.valueOf(data));

                            if (type.equals("payment update") && typeOfId.equals("gcash_id")) {
                                // Handle tenant's verification data

                                String imageUrl = data.getString("receipt");
//                                receiptImgName.setText(imageUrl);
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            receiptImg.setImageBitmap(bitmap);
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

                                String imageUrl = data.getString("receipt");
//                                receiptImgName.setText(imageUrl);
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            receiptImg.setImageBitmap(bitmap);
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

                                String imageUrl = data.getString("receipt");
//                                receiptImgName.setText(imageUrl);
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            receiptImg.setImageBitmap(bitmap);
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

                                String imageUrl = data.getString("receipt");
//                                receiptImgName.setText(imageUrl);
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            receiptImg.setImageBitmap(bitmap);
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
    }