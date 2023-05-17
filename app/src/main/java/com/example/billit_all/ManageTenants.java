package com.example.billit_all;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.example.billit_all.Bill_history.Tenant;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ManageTenants extends AppCompatActivity {

    LinearLayout tenantsContainer;
    Button pdfDownload,pdfDownload2, pdfDownload3, verifiedTenant, unverifiedTenant, tempAccTenant;;
    private ImageLoader imageLoader;
    AlertDialog.Builder builder;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tenants);

        tenantsContainer = findViewById(R.id.tenantsContainer);
        pdfDownload = findViewById(R.id.pdfDownload);
        pdfDownload2 = findViewById(R.id.pdfDownload2);
        pdfDownload3 = findViewById(R.id.pdfDownload3);
        verifiedTenant = findViewById(R.id.verifiedTenant);
        unverifiedTenant = findViewById(R.id.unverifiedTenant);
        tempAccTenant = findViewById(R.id.tempAccTenant);

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

        verifiedTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfDownload.setVisibility(View.VISIBLE);
                pdfDownload2.setVisibility(View.GONE);
                pdfDownload3.setVisibility(View.GONE);
                retrieveVerifiedTenants();
            }
        });
        verifiedTenant.performClick();

        unverifiedTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfDownload.setVisibility(View.GONE);
                pdfDownload2.setVisibility(View.VISIBLE);
                pdfDownload3.setVisibility(View.GONE);
                retrieveUnverifiedTenants();
            }
        });

        tempAccTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfDownload.setVisibility(View.GONE);
                pdfDownload2.setVisibility(View.GONE);
                pdfDownload3.setVisibility(View.VISIBLE);
                retrieveTempAccTenants();
            }
        });
//        retrieveTenants();
    }




    public void retrieveVerifiedTenants(){
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
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                String landlord_name = user.getString("name");
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/manage/tenants/verified/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray manageTenants = response.getJSONArray("units");

                                                    tenantsContainer.removeAllViews();
                                                    ArrayList<String> tenants = new ArrayList<>();
                                                    for (int i = 0; i < manageTenants.length(); i++) {
                                                        JSONObject manage = manageTenants.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.manage_tenants_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        CircularImageView profileTenant = view.findViewById(R.id.profileTenant);
                                                        TextView nameTenant = view.findViewById(R.id.nameTenant);
                                                        TextView rentTenant = view.findViewById(R.id.rentTenant);
                                                        TextView dateTenant = view.findViewById(R.id.dateTenant);
                                                        ImageButton deleteTenant = view.findViewById(R.id.deleteTenant);
                                                        profileTenant.setVisibility(View.VISIBLE);

                                                        String tenantId = manage.getString("unit_no");
                                                        tenantNum.setText(tenantId);
                                                        String tenantName = manage.getString("name");
                                                        nameTenant.setText(tenantName);
                                                        String tenantRent = manage.getString("rent_fee");
                                                        rentTenant.setText(tenantRent);
                                                        String tenantDate = manage.getString("date");
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                        Date date = dateFormat.parse(tenantDate);
                                                        dateFormat.applyPattern("MMMM dd yyyy");
                                                        String formattedDate = dateFormat.format(date);
                                                        dateTenant.setText(formattedDate);
                                                        String profileUrl = manage.getString("profile");


                                                        imageLoader.get(profileUrl, new ImageLoader.ImageListener() {
                                                            @Override
                                                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                                                Bitmap bitmap = response.getBitmap();
                                                                if (bitmap != null) {
                                                                    profileTenant.setImageBitmap(bitmap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // Handle errors
                                                            }
                                                        });

                                                        String tenant_userId = manage.getString("id");
                                                        tenants.add(formattedDate);
                                                        tenants.add(tenantId);
                                                        tenants.add(tenantName);
                                                        tenants.add(tenantRent);

                                                        pdfDownload.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String safeNameTenant = landlord_name.replaceAll("[^a-zA-Z0-9]", "_");
                                                                com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4.rotate());

// Specify the file path and name for the output PDF
                                                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                                dir.mkdirs(); // create the directory if it doesn't exist
                                                                File file = new File(dir, safeNameTenant +"-"+ "VerifiedTenants.pdf");
                                                                try {
                                                                    // Create a PdfWriter instance to write to the document
                                                                    PdfWriter.getInstance(document, new FileOutputStream(file));

                                                                    // Open the document
                                                                    document.open();

                                                                    Paragraph title = new Paragraph(" Tenants of "+ landlord_name, new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
                                                                    title.setAlignment(Element.ALIGN_CENTER);
                                                                    document.add(title);

// Add some space
                                                                    document.add(new Paragraph(" "));


                                                                    // Create a new table with two columns
                                                                    PdfPTable table = new PdfPTable(4);
                                                                    table.setTotalWidth(PageSize.A4.getWidth());
                                                                    float[] columnWidths = {5f, 5f, 5f, 5f};
                                                                    table.setLockedWidth(true);
                                                                    table.setWidths(columnWidths);
                                                                    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

                                                                    // Add the column headers to the table
                                                                    table.addCell(new Phrase("Date", font));
                                                                    table.addCell(new Phrase("Unit No", font));
                                                                    table.addCell(new Phrase("Name", font));
                                                                    table.addCell(new Phrase("Rent Fee", font));


                                                                    for (int j = 0; j < tenants.size(); j += 4) {
                                                                        String tenantDate = tenants.get(j);
                                                                        String tenantUnitNo = tenants.get(j + 1);
                                                                        String tenantName = tenants.get(j + 2);
                                                                        String tenantRentFee = tenants.get(j + 3);
                                                                        table.addCell(new PdfPCell(new Phrase(tenantDate, font))); // Add cell under "Date" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantUnitNo, font))); // Add cell under "Unit No" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantName, font))); // Add cell under "Name" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantRentFee, font))); // Add cell under "Rent Fee" column
                                                                    }


                                                                    // Add the table to the document
                                                                    document.add(table);
                                                                    Toast.makeText(getApplicationContext(), "PDF Exported", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(getApplicationContext(), "Did not exported to PDF", Toast.LENGTH_SHORT).show();
                                                                } finally {
                                                                    // Close the document
                                                                    document.close();
                                                                }
                                                            }
                                                        });

                                                        //click delete button ORIGINALLL
                                                        builder = new AlertDialog.Builder(ManageTenants.this);
                                                        deleteTenant.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                builder.setTitle("Delete Tenant")
                                                                        .setMessage("Do you want to remove this tenant?")
                                                                        .setCancelable(true)
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                RequestQueue queueDel = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
//                                                                loginPreferenceDataSource.getBackendToken();
                                                                                StringRequest requestDel = new StringRequest(Request.Method.DELETE, "https://bill-it.online/api/manage-tenants/" + tenant_userId,
                                                                                        new Response.Listener<String>()
                                                                                        {
                                                                                            @Override
                                                                                            public void onResponse(String response) {
                                                                                                // response
                                                                                                Toast.makeText(ManageTenants.this, response, Toast.LENGTH_LONG).show();
                                                                                                tenantsContainer.removeView(view);
                                                                                                loginPreferenceDataSource.getBackendToken();
                                                                                                Log.d("response", response);
                                                                                                recreate();
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
                                                                                        map.put("user_id", tenant_userId);
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


                                                            }
                                                        }); //end of button



                                                        tenantsContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                request.authenticated(getApplicationContext());
                                queue.add(request);

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

    public void retrieveUnverifiedTenants(){
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
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                String landlord_name = user.getString("name");
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/manage/tenants/unverified/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray manageTenants = response.getJSONArray("units");

                                                    tenantsContainer.removeAllViews();
                                                    ArrayList<String> tenants = new ArrayList<>();
                                                    for (int i = 0; i < manageTenants.length(); i++) {
                                                        JSONObject manage = manageTenants.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.manage_tenants_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        CircularImageView profileTenant = view.findViewById(R.id.profileTenant);
                                                        TextView nameTenant = view.findViewById(R.id.nameTenant);
                                                        TextView rentTenant = view.findViewById(R.id.rentTenant);
                                                        TextView dateTenant = view.findViewById(R.id.dateTenant);
                                                        ImageButton approveTenant = view.findViewById(R.id.approveTenant);
                                                        ImageButton deleteTenant = view.findViewById(R.id.deleteTenant);
                                                        profileTenant.setVisibility(View.VISIBLE);
                                                        approveTenant.setVisibility(View.VISIBLE);

                                                        String tenantId = manage.getString("unit_no");
                                                        tenantNum.setText(tenantId);
                                                        String tenantName = manage.getString("name");
                                                        nameTenant.setText(tenantName);
                                                        String tenantRent = manage.getString("rent_fee");
                                                        rentTenant.setText(tenantRent);
                                                        String tenantDate = manage.getString("date");
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                        Date date = dateFormat.parse(tenantDate);
                                                        dateFormat.applyPattern("MMMM dd yyyy");
                                                        String formattedDate = dateFormat.format(date);
                                                        dateTenant.setText(formattedDate);
                                                        String profileUrl = manage.getString("profile");
                                                        imageLoader.get(profileUrl, new ImageLoader.ImageListener() {
                                                            @Override
                                                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                                                Bitmap bitmap = response.getBitmap();
                                                                if (bitmap != null) {
                                                                    profileTenant.setImageBitmap(bitmap);
                                                                }
                                                            }

                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                // Handle errors
                                                            }
                                                        });

                                                        String tenant_userId = manage.getString("id");
                                                        String unverified_userId = manage.getString("user_id");

                                                        tenants.add(formattedDate);
                                                        tenants.add(tenantId);
                                                        tenants.add(tenantName);
                                                        tenants.add(tenantRent);

                                                        pdfDownload2.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String safeNameTenant = landlord_name.replaceAll("[^a-zA-Z0-9]", "_");
                                                                com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4.rotate());

// Specify the file path and name for the output PDF
                                                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                                dir.mkdirs(); // create the directory if it doesn't exist
                                                                File file = new File(dir, safeNameTenant +"-"+ "UnverifiedAccounts.pdf");
                                                                try {
                                                                    // Create a PdfWriter instance to write to the document
                                                                    PdfWriter.getInstance(document, new FileOutputStream(file));

                                                                    // Open the document
                                                                    document.open();

                                                                    Paragraph title = new Paragraph(" Tenants of "+ landlord_name, new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
                                                                    title.setAlignment(Element.ALIGN_CENTER);
                                                                    document.add(title);

// Add some space
                                                                    document.add(new Paragraph(" "));


                                                                    // Create a new table with two columns
                                                                    PdfPTable table = new PdfPTable(4);
                                                                    table.setTotalWidth(PageSize.A4.getWidth());
                                                                    float[] columnWidths = {5f, 5f, 5f, 5f};
                                                                    table.setLockedWidth(true);
                                                                    table.setWidths(columnWidths);
                                                                    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

                                                                    // Add the column headers to the table
                                                                    table.addCell(new Phrase("Date", font));
                                                                    table.addCell(new Phrase("Unit No", font));
                                                                    table.addCell(new Phrase("Email", font));
                                                                    table.addCell(new Phrase("Rent Fee", font));


                                                                    for (int j = 0; j < tenants.size(); j += 4) {
                                                                        String tenantDate = tenants.get(j);
                                                                        String tenantUnitNo = tenants.get(j + 1);
                                                                        String tenantName = tenants.get(j + 2);
                                                                        String tenantRentFee = tenants.get(j + 3);
                                                                        table.addCell(new PdfPCell(new Phrase(tenantDate, font))); // Add cell under "Date" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantUnitNo, font))); // Add cell under "Unit No" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantName, font))); // Add cell under "Name" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantRentFee, font))); // Add cell under "Rent Fee" column
                                                                    }


                                                                    // Add the table to the document
                                                                    document.add(table);
                                                                    Toast.makeText(getApplicationContext(), "PDF Exported", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(getApplicationContext(), "Did not exported to PDF", Toast.LENGTH_SHORT).show();
                                                                } finally {
                                                                    // Close the document
                                                                    document.close();
                                                                }
                                                            }
                                                        });

                                                        approveTenant.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/notif/user/verify",
                                                                        new Response.Listener<String>() {
                                                                            @Override
                                                                            public void onResponse(String response) {
                                                                                try {
                                                                                    JSONObject result = new JSONObject(response);
                                                                                    String message = result.getString("message");
                                                                                    tenantsContainer.removeView(view);
                                                                                    loginPreferenceDataSource.getBackendToken();
                                                                                    Toast.makeText(ManageTenants.this, "Account Verified!", Toast.LENGTH_SHORT).show();
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
                                                                        params.put("id", unverified_userId);
                                                                        return params;
                                                                    }
                                                                };
                                                                queue.add(stringRequest);
                                                                Intent intent = new Intent(ManageTenants.this, VerifySuccessPage.class);
                                                                startActivity(intent);
                                                            }
                                                        }); //end of approve tenant button


                                                        //click delete button ORIGINALLL
                                                        builder = new AlertDialog.Builder(ManageTenants.this);
                                                        deleteTenant.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                builder.setTitle("Delete Tenant")
                                                                        .setMessage("Do you want to remove this tenant?")
                                                                        .setCancelable(true)
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                RequestQueue queueDel = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
//                                                                loginPreferenceDataSource.getBackendToken();
                                                                                StringRequest requestDel = new StringRequest(Request.Method.DELETE, "https://bill-it.online/api/manage-tenants/" + tenant_userId,
                                                                                        new Response.Listener<String>()
                                                                                        {
                                                                                            @Override
                                                                                            public void onResponse(String response) {
                                                                                                // response
                                                                                                Toast.makeText(ManageTenants.this, response, Toast.LENGTH_LONG).show();
                                                                                                tenantsContainer.removeView(view);
                                                                                                loginPreferenceDataSource.getBackendToken();
                                                                                                Log.d("response", response);
                                                                                                recreate();
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
                                                                                        map.put("user_id", tenant_userId);
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
                                                            }
                                                        }); //end of button

                                                        tenantsContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                request.authenticated(getApplicationContext());
                                queue.add(request);

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

    public void retrieveTempAccTenants(){
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
                                Log.d("USER_ID", String.valueOf(user));
                                String id = user.getString("user_id");
                                String landlord_name = user.getString("name");
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/manage/tenants/temp-acct/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray manageTenants = response.getJSONArray("units");

                                                    tenantsContainer.removeAllViews();
                                                    ArrayList<String> tenants = new ArrayList<>();
                                                    for (int i = 0; i < manageTenants.length(); i++) {
                                                        JSONObject manage = manageTenants.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.manage_tenants_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
//                                                        CircularImageView profileTenant = view.findViewById(R.id.profileTenant);
                                                        TextView nameTenant = view.findViewById(R.id.nameTenant);
                                                        TextView rentTenant = view.findViewById(R.id.rentTenant);
                                                        TextView dateTenant = view.findViewById(R.id.dateTenant);
                                                        ImageButton deleteTenant = view.findViewById(R.id.deleteTenant);

                                                        String tenantId = manage.getString("unit_no");
                                                        tenantNum.setText(tenantId);
                                                        String tenantName = manage.getString("email");
                                                        nameTenant.setText(tenantName);
                                                        String tenantRent = manage.getString("rent_fee");
                                                        rentTenant.setText(tenantRent);
                                                        String tenantDate = manage.getString("date");
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                                        Date date = dateFormat.parse(tenantDate);
                                                        dateFormat.applyPattern("MMMM dd yyyy");
                                                        String formattedDate = dateFormat.format(date);
                                                        dateTenant.setText(formattedDate);
//                                                        String profileUrl = manage.getString("profile");
//                                                        Glide.with(ManageTenants.this)
//                                                                .load(profileUrl)
//                                                                .into(profileTenant);
                                                        String tenant_userId = manage.getString("id");
                                                        tenants.add(formattedDate);
                                                        tenants.add(tenantId);
                                                        tenants.add(tenantName);
                                                        tenants.add(tenantRent);

                                                        pdfDownload3.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String safeNameTenant = landlord_name.replaceAll("[^a-zA-Z0-9]", "_");
                                                                com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4.rotate());

// Specify the file path and name for the output PDF
                                                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                                                dir.mkdirs(); // create the directory if it doesn't exist
                                                                File file = new File(dir, safeNameTenant +"-"+ "TemporaryAccounts.pdf");
                                                                try {
                                                                    // Create a PdfWriter instance to write to the document
                                                                    PdfWriter.getInstance(document, new FileOutputStream(file));

                                                                    // Open the document
                                                                    document.open();

                                                                    Paragraph title = new Paragraph(" Tenants of "+ landlord_name, new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD));
                                                                    title.setAlignment(Element.ALIGN_CENTER);
                                                                    document.add(title);

// Add some space
                                                                    document.add(new Paragraph(" "));


                                                                    // Create a new table with two columns
                                                                    PdfPTable table = new PdfPTable(4);
                                                                    table.setTotalWidth(PageSize.A4.getWidth());
                                                                    float[] columnWidths = {5f, 5f, 5f, 5f};
                                                                    table.setLockedWidth(true);
                                                                    table.setWidths(columnWidths);
                                                                    Font font = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);

                                                                    // Add the column headers to the table
                                                                    table.addCell(new Phrase("Date", font));
                                                                    table.addCell(new Phrase("Unit No", font));
                                                                    table.addCell(new Phrase("Email", font));
                                                                    table.addCell(new Phrase("Rent Fee", font));


                                                                    for (int j = 0; j < tenants.size(); j += 4) {
                                                                        String tenantDate = tenants.get(j);
                                                                        String tenantUnitNo = tenants.get(j + 1);
                                                                        String tenantName = tenants.get(j + 2);
                                                                        String tenantRentFee = tenants.get(j + 3);
                                                                        table.addCell(new PdfPCell(new Phrase(tenantDate, font))); // Add cell under "Date" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantUnitNo, font))); // Add cell under "Unit No" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantName, font))); // Add cell under "Name" column
                                                                        table.addCell(new PdfPCell(new Phrase(tenantRentFee, font))); // Add cell under "Rent Fee" column
                                                                    }


                                                                    // Add the table to the document
                                                                    document.add(table);
                                                                    Toast.makeText(getApplicationContext(), "PDF Exported", Toast.LENGTH_SHORT).show();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(getApplicationContext(), "Did not exported to PDF", Toast.LENGTH_SHORT).show();
                                                                } finally {
                                                                    // Close the document
                                                                    document.close();
                                                                }
                                                            }
                                                        });

                                                        //click delete button ORIGINALLL
                                                        builder = new AlertDialog.Builder(ManageTenants.this);
                                                        deleteTenant.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                builder.setTitle("Delete Tenant")
                                                                        .setMessage("Do you want to remove this tenant?")
                                                                        .setCancelable(true)
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                RequestQueue queueDel = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
//                                                                loginPreferenceDataSource.getBackendToken();
                                                                                StringRequest requestDel = new StringRequest(Request.Method.DELETE, "https://bill-it.online/api/manage-tenants/" + tenant_userId,
                                                                                        new Response.Listener<String>()
                                                                                        {
                                                                                            @Override
                                                                                            public void onResponse(String response) {
                                                                                                // response
                                                                                                Toast.makeText(ManageTenants.this, response, Toast.LENGTH_LONG).show();
                                                                                                tenantsContainer.removeView(view);
                                                                                                loginPreferenceDataSource.getBackendToken();
                                                                                                Log.d("response", response);
                                                                                                recreate();
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
                                                                                        map.put("user_id", tenant_userId);
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
                                                            }
                                                        }); //end of button

                                                        tenantsContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                request.authenticated(getApplicationContext());
                                queue.add(request);

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
}