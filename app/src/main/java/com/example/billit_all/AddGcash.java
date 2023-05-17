package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Payment.PaymentElectricity;
import com.example.billit_all.application.data.VolleyMultipartRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddGcash extends AppCompatActivity {

    CheckBox gcashCheckbox;
    TextView qrTxt, qrFile;
    Button chooseQr, cancelQr, submitQr;
    ProgressBar progressBar;
    ImageView qr;

    //    private ImageLoader imageLoader;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

    String id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gcash);

//        id = getIntent().getExtras().getString("id");

        gcashCheckbox = findViewById(R.id.gcashCheckbox);
        qrTxt = findViewById(R.id.qrTxt);
        qrFile = findViewById(R.id.qrFile);
        chooseQr = findViewById(R.id.chooseQr);
        cancelQr = findViewById(R.id.cancelQr);
        submitQr = findViewById(R.id.submitQr);
        progressBar = findViewById(R.id.progressBar);
        qr = findViewById(R.id.qr);


        gcashCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    qrTxt.setVisibility(View.VISIBLE);
                    qrFile.setVisibility(View.VISIBLE);
                    chooseQr.setVisibility(View.VISIBLE);
//                cancelQr.setVisibility(View.VISIBLE);
                    submitQr.setVisibility(View.VISIBLE);
                } else {
                    qrTxt.setVisibility(View.GONE);
                    qrFile.setVisibility(View.GONE);
                    chooseQr.setVisibility(View.GONE);
//                cancelQr.setVisibility(View.VISIBLE);
                    submitQr.setVisibility(View.GONE);
                }
            }
        });

//        imageLoader = new ImageLoader(Volley.newRequestQueue(getApplicationContext()),
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });

        chooseQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(AddGcash.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(AddGcash.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(AddGcash.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
//                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });

        submitQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrUpload(bitmap);
            }
        });

        cancelQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Receipt"), PICK_IMAGE_REQUEST);
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
                    qrFile.setText(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        AddGcash.this,"no image selected",
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.uploadPayment:
//                paymentUpload(bitmap);
//
//                break;
//        }
//
//    }

    public void qrUpload(final Bitmap bitmap) {

        id = getIntent().getExtras().getString("id");
        Log.d("ID_QR", id);
//        String idTenant = tenantId.getText().toString();
//        String nameTenant = tenantName.getText().toString();
//        String emailTenant = tenantEmail.getText().toString();
//        String usernameTenant = tenantUsername.getText().toString();
//        String phoneTenant = tenantPhone.getText().toString();
//
//        if (nameTenant.isEmpty() || emailTenant.isEmpty() || usernameTenant.isEmpty() || phoneTenant.isEmpty()) {
//            Toast.makeText(EditProfile.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://bill-it.online/api/gcash-qr/" + id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(AddGcash.this, "QR Code Uploaded", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(AddGcash.this, DashboardLandlord.class));
//                        Dialog dialog = new Dialog(AddGcash.this);
//                        dialog.setContentView(R.layout.payment_success_dialog);
//
//                        Button btnOkay = dialog.findViewById(R.id.okayBtn);
//
//                        btnOkay.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(AddGcash.this, "QR Code Uploaded", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                                finish();
//                            }
//                        });
//
//                        dialog.show();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddGcash.this, "QR Code Uploaded", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(AddGcash.this, DashboardLandlord.class));
                error.printStackTrace();
            }

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("id", id);
//                stringMap.put("name", tenantName.getText().toString());
//                stringMap.put("email", tenantEmail.getText().toString());
//                stringMap.put("username", tenantUsername.getText().toString());
//                stringMap.put("phone", tenantPhone.getText().toString());

                return stringMap;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("qr_code", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
}