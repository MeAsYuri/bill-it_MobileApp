package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import com.example.billit_all.application.data.VolleyMultipartRequest;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileLandlord extends AppCompatActivity implements View.OnClickListener{

    //    String url = "http://10.0.2.2:8000/api/update/landlord/profile";
//    String url = "http://172.34.5.245:8000/api/update/profile";
    String url = "https://bill-it.online/api/update/profile";

    EditText tenantName, tenantEmail, tenantUsername, tenantPhone, tenantId;
    //    TextView idTenant;
    ImageView logo, settings;
    Button updateTenantBtn, uploadImageBtn;
    ProgressBar progressBar;
    private CircularImageView profileImage;

    private ImageLoader imageLoader;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_landlord);

        profileImage = findViewById(R.id.profile_image);
        uploadImageBtn = findViewById(R.id.uploadImageBtn);

        logo = (ImageView) findViewById(R.id.logo);
        tenantId = (EditText) findViewById(R.id.tenantId);
        tenantName = (EditText) findViewById(R.id.tenantName);
        tenantEmail = (EditText) findViewById(R.id.tenantEmail);
        tenantUsername = (EditText) findViewById(R.id.tenantUsername);
        tenantPhone = (EditText) findViewById(R.id.tenantPhone);
        updateTenantBtn = (Button) findViewById(R.id.updateTenantBtn);
        updateTenantBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

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


        findViewById(R.id.uploadImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(EditProfileLandlord.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileLandlord.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(EditProfileLandlord.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileLandlord.this, Settings.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });



        getTenant();
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {

//                    textView.setText("File Selected");
                    Log.d("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
//                    registerUser(bitmap);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        EditProfileLandlord.this,"no image selected",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.updateTenantBtn:
                editTenant(bitmap);

                break;
        }

    }

    public void getTenant() {

        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
        String backendToken = loginPreferenceDataSource.getBackendToken();
        try {
            userDataSource.updateProfile(
                    backendToken,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject user = response.getJSONObject("user");
                                String id = user.getString("user_id");
                                tenantId.setText(id);
                                String name = user.getString("name");
                                tenantName.setText(name);
                                String email = user.getString("email");
                                tenantEmail.setText(email);
                                String username = user.getString("username");
                                tenantUsername.setText(username);
                                String phone = user.getString("phone");
                                tenantPhone.setText(phone);
                                String role = user.getString("role");

//                                editTenant();

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

    public void editTenant(final Bitmap bitmap) {
        String idTenant = tenantId.getText().toString();
        String nameTenant = tenantName.getText().toString();
        String emailTenant = tenantEmail.getText().toString();
        String usernameTenant = tenantUsername.getText().toString();
        String phoneTenant = tenantPhone.getText().toString();

        if (nameTenant.isEmpty() || emailTenant.isEmpty() || usernameTenant.isEmpty() || phoneTenant.isEmpty()) {
            Toast.makeText(EditProfileLandlord.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(EditProfileLandlord.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(EditProfileLandlord.this, DashboardLandlord.class));
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(EditProfileLandlord.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
                Toast.makeText(EditProfileLandlord.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
                startActivity(new Intent(EditProfileLandlord.this, DashboardLandlord.class));
                finish();
            }

        })

        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("id", tenantId.getText().toString());
                stringMap.put("name", tenantName.getText().toString());
                stringMap.put("email", tenantEmail.getText().toString());
                stringMap.put("username", tenantUsername.getText().toString());
                stringMap.put("phone", tenantPhone.getText().toString());

                return stringMap;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("profile", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);


//        String idTenant = tenantId.getText().toString();
//        String nameTenant = tenantName.getText().toString();
//        String emailTenant = tenantEmail.getText().toString();
//        String usernameTenant = tenantUsername.getText().toString();
//        String phoneTenant = tenantPhone.getText().toString();
//
//        if (nameTenant.isEmpty() || emailTenant.isEmpty() || usernameTenant.isEmpty() || phoneTenant.isEmpty()) {
//            Toast.makeText(EditProfileLandlord.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//
//                Toast.makeText(EditProfileLandlord.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//                startActivity(new Intent(EditProfileLandlord.this, DashboardLandlord.class));
//                finish();
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(EditProfileLandlord.this, "Error! Please check credentials", Toast.LENGTH_SHORT).show();
//                progressBar.setVisibility(View.INVISIBLE);
//                error.printStackTrace();
//            }
//
//        })
//
//        {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                Map<String, String> stringMap = new HashMap<>();
//
//                stringMap.put("id", tenantId.getText().toString());
//                stringMap.put("name", tenantName.getText().toString());
//                stringMap.put("email", tenantEmail.getText().toString());
//                stringMap.put("username", tenantUsername.getText().toString());;
//                stringMap.put("phone", tenantPhone.getText().toString());
//
//                return stringMap;
//            }
//
//        };
//
//        queue.add(request);
    }

}