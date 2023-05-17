package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContractViewing extends AppCompatActivity {

    ImageView contractImg, settings;
    TextView contractImgName, idTextView;

    private ImageLoader imageLoader;
    Button downloadContract;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_viewing);

        contractImg = (ImageView) findViewById(R.id.contractImg);
        contractImgName = (TextView) findViewById(R.id.contractImgName);

        settings = (ImageView) findViewById(R.id.settings);
        downloadContract = (Button) findViewById(R.id.downloadContract);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContractViewing.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        idTextView = (TextView) findViewById(R.id.id);

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

                    Toast.makeText(ContractViewing.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(ContractViewing.this, "No Image", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ContractViewing.this, "No Image", Toast.LENGTH_SHORT).show();
                    }

            }

        });

        retrieveProfile();
//        if (contractImg == null || contractImg.getDrawable() == null) {
//
//        } else {
//
//        }
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

                                String imageUrl = user.getString("contract_img");
                                Log.d("IMAGE_URL", imageUrl);
//                                contractImgName.setText(imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            contractImg.setImageBitmap(bitmap);
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Handle errors
                                        downloadContract.setVisibility(View.GONE);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                downloadContract.setVisibility(View.GONE);
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