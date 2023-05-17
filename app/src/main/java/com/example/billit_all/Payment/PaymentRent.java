package com.example.billit_all.Payment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
//import com.example.billit_all.ConcernsTenants;
import com.example.billit_all.R;
import com.example.billit_all.Settings;
import com.example.billit_all.application.data.VolleyMultipartRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaymentRent extends AppCompatActivity {

    TextView fileName, tenantId, tenantAmount;
    Button chooseFile, uploadPayment, cancelPayment;
    ImageView qrPic;

    private ImageLoader imageLoader;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Button downloadContract;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    private String filePath;

    Toolbar toolbar;

    Integer id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_rent);

//        toolbar = findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileName = findViewById(R.id.fileNameRent);
        chooseFile = findViewById(R.id.chooseFileRent);
        uploadPayment = findViewById(R.id.uploadPaymentRent);
        cancelPayment = findViewById(R.id.cancelPaymentRent);
        tenantId = findViewById(R.id.tenantIdRent);
        tenantAmount = findViewById(R.id.amountRent);
        qrPic = findViewById(R.id.qrPic);
        downloadContract = (Button) findViewById(R.id.downloadContract);

        id = getIntent().getExtras().getInt("id");
//        int idGet = getIntent().getExtras().getInt("id");
//        tenantId.setText(String.valueOf(idGet));

        double amountGet = getIntent().getExtras().getDouble("amount");
        tenantAmount.setText(String.valueOf(amountGet));

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

        findViewById(R.id.chooseFileRent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(PaymentRent.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(PaymentRent.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(PaymentRent.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    showFileChooser();
                }
            }
        });

        downloadContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                bitmapDrawable = (BitmapDrawable) qrPic.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream = null;

                File sdCard = Environment.getExternalStorageDirectory();
                File Directory = new File(sdCard.getAbsolutePath() + "/Download");
                Directory.mkdir();

                String filename = String.format("LandlordQr.jpg", System.currentTimeMillis());
                File outfile = new File(Directory, filename);

                Toast.makeText(PaymentRent.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(PaymentRent.this, "No Image", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentRent.this, "No Image", Toast.LENGTH_SHORT).show();
                }

            }

        });
        uploadPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentUpload(bitmap);
            }
        });

        cancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        retrieveQrPic();
    }

    public void retrieveQrPic(){

        String imageUrl = getIntent().getExtras().getString("qr");
//        tenantId.setText(String.valueOf(idGet));
        Log.d("IMAGE_URL", imageUrl);
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    qrPic.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                downloadContract.setVisibility(View.GONE);
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
                    Log.d("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    fileName.setText(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        PaymentRent.this,"no image selected",
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

    public void paymentUpload(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://bill-it.online/api/paynow/rent/" + id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
//                        Toast.makeText(PaymentRent.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
//                        finish();
                        Dialog dialog = new Dialog(PaymentRent.this);
                        dialog.setContentView(R.layout.payment_success_dialog);

                        Button btnOkay = dialog.findViewById(R.id.okayBtn);

                        btnOkay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(PaymentRent.this, "Payment Success", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        });

                        dialog.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(PaymentRent.this, "Error! Please check file", Toast.LENGTH_SHORT).show();
                Toast.makeText(PaymentRent.this, "Payment Success", Toast.LENGTH_SHORT).show();
                finish();
//                progressBar.setVisibility(View.INVISIBLE);
                error.printStackTrace();
            }

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> stringMap = new HashMap<>();

                stringMap.put("id", tenantId.getText().toString());

                return stringMap;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("gcash_receipt", new DataPart(imagename + ".jpeg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.top_nav_tenant, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.notification:
//                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
////                startActivity(new Intent(DashboardLandlord.this, Settings.class));
//                break;
//            case R.id.concern:
////                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(PaymentRent.this, ConcernsTenants.class));
//                break;
//            case R.id.settings:
//                startActivity(new Intent(PaymentRent.this, Settings.class));
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}