package com.example.billit_all.Payment;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
//import com.example.billit_all.ConcernsTenants;
import com.example.billit_all.AddGcash;
import com.example.billit_all.EditGcash;
import com.example.billit_all.R;
import com.example.billit_all.Settings;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.example.billit_all.application.data.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaymentElectricity extends AppCompatActivity {

//    private ListView listView;
//    String url = "http://10.0.2.2:8000/api/paynow/electricity";

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
//    Double amount;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_electricity);

//        toolbar = findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fileName = findViewById(R.id.fileNameElec);
        chooseFile = findViewById(R.id.chooseFileElec);
        uploadPayment = findViewById(R.id.uploadPaymentElec);
        cancelPayment = findViewById(R.id.cancelPaymentElec);
        tenantId = findViewById(R.id.tenantIdElec);
        tenantAmount = findViewById(R.id.amountElec);
        qrPic = findViewById(R.id.qrPic);
        downloadContract = (Button) findViewById(R.id.downloadContract);

//        tenantIdTry.setText(getIntent().getExtras().getString("id"));

        id = getIntent().getExtras().getInt("id");
        Log.d("ID_PAY", String.valueOf(id));
        int idGet = getIntent().getExtras().getInt("id");
        tenantId.setText(String.valueOf(idGet));

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

        findViewById(R.id.chooseFileElec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(PaymentElectricity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(PaymentElectricity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(PaymentElectricity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
//                    Log.e("Else", "Else");
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

                Toast.makeText(PaymentElectricity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(PaymentElectricity.this, "No Image", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(PaymentElectricity.this, "No Image", Toast.LENGTH_SHORT).show();
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
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
////                RequestQueue requestQueue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.fetchUserFromBackend(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                String user_id = user.getString("user_id");
////                                String role = user.getString("role");
////                                if (role.equals("Tenant") && role == "Tenant") {
////                                    addGcash.setVisibility(View.INVISIBLE);
////                                } else {
//                                    RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//                                    BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                                            Request.Method.GET,
//                                            "/api/gcash-qr/qr/" + user_id, null,
//                                            new Response.Listener<JSONObject>() {
//                                                @Override
//                                                public void onResponse(JSONObject response) {
//                                                    try {
////                                                                JSONObject qrCode = response.getJSONObject("hasQr");
//                                                        Object hasQr = response.get("hasQr");
//                                                        if (hasQr instanceof Boolean && !(Boolean) hasQr) {
//                                                            // hasQr is false
////                                                            Intent intent = new Intent(getApplicationContext(), AddGcash.class);
////                                                            intent.putExtra("id", id);
////                                                            startActivity(intent);
//                                                        } else if (hasQr instanceof JSONObject) {
//                                                            // hasQr is true
//                                                            JSONObject qrObject = (JSONObject) hasQr;
//                                                            String qr = qrObject.getString("qr");
//                                                            imageLoader.get(qr, new ImageLoader.ImageListener() {
//                                                                @Override
//                                                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                                                                    Bitmap bitmap = response.getBitmap();
//                                                                    if (bitmap != null) {
//                                                                        qrPic.setImageBitmap(bitmap);
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onErrorResponse(VolleyError error) {
//                                                                    // Handle errors
//                                                                }
//                                                            });
//                                                            // Extract other values as needed
////                                                            Intent intent = new Intent(getApplicationContext(), EditGcash.class);
////                                                            intent.putExtra("id", id);
////                                                            intent.putExtra("qr", qr);
////                                                            startActivity(intent);
//                                                        } else {
//                                                            // Unexpected response format
//                                                            Log.e(TAG, "Unexpected response format: " + response.toString());
//                                                        }
//
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                }
//                                            },
//                                            new Response.ErrorListener() {
//                                                @Override
//                                                public void onErrorResponse(VolleyError error) {
//                                                }
//                                            }
//                                    );
//                                    queue.add(request);
////                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    null
//            );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
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
//                    profileImage.setImageBitmap(bitmap);
                    fileName.setText(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        PaymentElectricity.this,"no image selected",
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

    public void paymentUpload(final Bitmap bitmap) {
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

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://bill-it.online/api/paynow/electricity/" + id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
//                        Toast.makeText(PaymentElectricity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.INVISIBLE);
//                        startActivity(new Intent(PaymentElectricity.this, HistoryFragment.class));
                        Dialog dialog = new Dialog(PaymentElectricity.this);
                        dialog.setContentView(R.layout.payment_success_dialog);

                        Button btnOkay = dialog.findViewById(R.id.okayBtn);

                        btnOkay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(PaymentElectricity.this, "Payment Success", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        });

                        dialog.show();
//                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(PaymentElectricity.this, "Error! Please check file", Toast.LENGTH_SHORT).show();
                Toast.makeText(PaymentElectricity.this, "Payment Success", Toast.LENGTH_SHORT).show();
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
                Log.d("ID_PAY", String.valueOf(id));
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
//                startActivity(new Intent(PaymentElectricity.this, ConcernsTenants.class));
//                break;
//            case R.id.settings:
//                startActivity(new Intent(PaymentElectricity.this, Settings.class));
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}






//        listView = findViewById(R.id.listView);


//        ArrayList<Long> list = new ArrayList<>();
//        ArrayAdapter adapter = new ArrayAdapter<Long>(this, R.layout.list_payment, list);
//        listView.setAdapter(adapter);
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("MerRecord");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                list.clear();
//                for (DataSnapshot snapshot1 : snapshot.getChildren()){
//                    PaymentInformation infopayment = snapshot1.getValue(PaymentInformation.class);
//                    //getting the columns
//                    Long txt = infopayment.getCR() + infopayment.getPR() +  infopayment.getPRate() + infopayment.getTotal();
//                    list.add(txt);
//                    list.add((Long) snapshot1.getValue());
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });