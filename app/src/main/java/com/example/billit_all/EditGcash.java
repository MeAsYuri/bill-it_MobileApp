package com.example.billit_all;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditGcash extends AppCompatActivity {

    CheckBox gcashCheckbox;
    TextView qrTxt, qrFile;
    Button chooseQr, cancelQr, submitQr, deleteQr;
    ImageView qrImg;
    ProgressBar progressBar;
    Button downloadQr;
    BitmapDrawable bitmapDrawable;

    private ImageLoader imageLoader;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private static final int REQUEST_PERMISSIONS = 100;
    Bitmap bitmap;
    private String filePath;

    String id;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gcash);

        gcashCheckbox = findViewById(R.id.gcashCheckbox);
        qrTxt = findViewById(R.id.qrTxt);
        qrFile = findViewById(R.id.qrFile);
        chooseQr = findViewById(R.id.chooseQr);
        cancelQr = findViewById(R.id.cancelQr);
        submitQr = findViewById(R.id.submitQr);
        progressBar = findViewById(R.id.progressBar);
        qrImg = findViewById(R.id.qrImg);
        deleteQr = findViewById(R.id.deleteQr);
        downloadQr = findViewById(R.id.downloadQr);

        gcashCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    qrTxt.setVisibility(View.VISIBLE);
                    qrFile.setVisibility(View.VISIBLE);
                    chooseQr.setVisibility(View.VISIBLE);
//                cancelQr.setVisibility(View.VISIBLE);
                    submitQr.setVisibility(View.VISIBLE);
                    deleteQr.setVisibility(View.VISIBLE);
                } else {
                    qrTxt.setVisibility(View.GONE);
                    qrFile.setVisibility(View.GONE);
                    chooseQr.setVisibility(View.GONE);
//                cancelQr.setVisibility(View.VISIBLE);
                    submitQr.setVisibility(View.GONE);
                }
            }
        });

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

        chooseQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(EditGcash.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(EditGcash.this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(EditGcash.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
//                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });

        downloadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapDrawable=(BitmapDrawable) qrImg.getDrawable();
                bitmap=bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream=null;

                File sdCard = Environment.getExternalStorageDirectory();
                File Directory=new File(sdCard.getAbsolutePath()+ "/Download");
                Directory.mkdir();

                String filename=String.format("QrLandlord.jpg",System.currentTimeMillis());
                File outfile=new File(Directory,filename);

                Toast.makeText(EditGcash.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();

                try {
                    fileOutputStream=new FileOutputStream(outfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outfile));
                    sendBroadcast(intent);

                }catch (FileNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(EditGcash.this, "No Image", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(EditGcash.this, "No Image", Toast.LENGTH_SHORT).show();
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
        builder = new AlertDialog.Builder(EditGcash.this);
        deleteQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Delete Gcash QR")
                        .setMessage("Do you want to remove your QR Code?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                RequestQueue queueDel = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                id = getIntent().getExtras().getString("id");
                                StringRequest requestDel = new StringRequest(Request.Method.DELETE, "https://bill-it.online/api/gcash-qr/qr-remove/" + id,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Toast.makeText(EditGcash.this, response, Toast.LENGTH_LONG).show();
                                                loginPreferenceDataSource.getBackendToken();
                                                Log.d("response", response);
                                                finish();
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error.
                                                Log.d("response", "" + error);
                                            }
                                        }) {
                                    protected HashMap<String,String> getParams() throws AuthFailureError{
                                        HashMap<String,String> map = new HashMap<>();
                                        map.put("user_id", id);
//                                                                        map.
                                        return map;
                                    }
                                };
                                queueDel.add(requestDel);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            } //end of delete button
        });

        retrieveQrCode();
    }

    public void retrieveQrCode(){

        String imageUrl = getIntent().getExtras().getString("qr");
//        tenantId.setText(String.valueOf(idGet));
        Log.d("IMAGE_URL", imageUrl);
        imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (bitmap != null) {
                    qrImg.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
            }
        });
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.fetchUserFromBackend(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                String name = user.getString("name");
//                                topNameTextView.setText(name);
//                                nameTextView.setText(name);
//                                String email = user.getString("email");
//                                emailTextView.setText(email);
//                                String phone = user.getString("phone");
//                                contactTextView.setText(phone);
//                                String role = user.getString("role");
//                                if (role.equals("Tenant")) {
//                                    addGcash.setVisibility(View.GONE);
//                                } else {
//                                    addGcash.setVisibility(View.VISIBLE);
//                                }
//
//                                String imageUrl = user.getString("profile");
//                                Log.d("IMAGE_URL", imageUrl);
//                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
//                                    @Override
//                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                                        Bitmap bitmap = response.getBitmap();
//                                        if (bitmap != null) {
//                                            profileImage.setImageBitmap(bitmap);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        // Handle errors
//                                    }
//                                });
//
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
                    qrFile.setText(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        EditGcash.this,"no image selected",
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

    public void qrUpload(final Bitmap bitmap) {

        id = getIntent().getExtras().getString("id");

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://bill-it.online/api/gcash-qr/" + id,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Toast.makeText(EditGcash.this, "QR Code Uploaded", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(EditGcash.this, DashboardLandlord.class));
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
                Toast.makeText(EditGcash.this, "QR Code Uploaded", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditGcash.this, DashboardLandlord.class));
                progressBar.setVisibility(View.INVISIBLE);
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