package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContractViewingNotif extends AppCompatActivity {

    ImageView contractImg, settings;
    TextView contractImgName, idTextView;
    Button downloadContract;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_viewing_notif);

        contractImg = (ImageView) findViewById(R.id.contractImg);
        contractImgName = (TextView) findViewById(R.id.contractImgName);
        downloadContract = (Button) findViewById(R.id.downloadContract);
        settings = (ImageView) findViewById(R.id.settings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContractViewingNotif.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        downloadContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                bitmapDrawable = (BitmapDrawable) contractImg.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream = null;

                File sdCard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdCard.getAbsolutePath() + "/Download");
                Directory.mkdir();

                String filename = String.format("ContractSignature.jpg", System.currentTimeMillis());
                File outfile = new File(Directory, filename);

                Toast.makeText(ContractViewingNotif.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

                try {
                    fileOutputStream = new FileOutputStream(outfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outfile));
                    sendBroadcast(intent);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ContractViewingNotif.this, "No Image", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ContractViewingNotif.this, "No Image", Toast.LENGTH_SHORT).show();
                }

            }

        });
        idTextView = (TextView) findViewById(R.id.id);

        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
        int userId = userSharedPreferenceDataSource.getBackendId();
        idTextView.setText(String.valueOf(userId));

//        UserSharedPreferenceDataSource userSharedPreferenceDataSource = UserSharedPreferenceDataSource.getInstance(getApplicationContext());
//        int contractImg = userSharedPreferenceDataSource.getBackendId();
//        contractImg.setText(String.valueOf(contractImg));

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

    public void retrieveProfile(){
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//        String url = "http://192.168.1.5:8000/api/get/notification/page";
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


                            String contract = data.getString("contract_img");
                            Log.d("IMAGE_URL", contract);
                            imageLoader.get(contract, new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                    Bitmap bitmap = response.getBitmap();
                                    if (bitmap != null) {
                                        contractImg.setImageBitmap(bitmap);
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