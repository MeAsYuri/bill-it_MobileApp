package com.example.billit_all;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Concerns extends AppCompatActivity {

    String[] item = {"Bill Error", "Wrong Computation", "Unchanged Payment Status", "Other"};

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapterItems;

    EditText concernDesc, announceTitle, announceSubj, subjOther;
    Button submitConcern, submitAnnouncement, allConcerns, respondedConcerns, pendingConcerns, mineConcerns;
    LinearLayout concernContainer, announcementLinear, concernLinear;
    TextView subtitleTxt, subjectTxt, descriptionTxt, announcementTxt, subjOtherTxt, listConcerns;
    TextInputLayout textInputTxt;

    String url = "http://192.168.1.5:8000/api/tokens/revoke";
    Toolbar toolbar_landlord, toolbar_tenant;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concerns);

        toolbar_landlord = findViewById(R.id.toolbar_landlord);

        setSupportActionBar(toolbar_landlord);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_tenant = findViewById(R.id.toolbar_tenant);

        setSupportActionBar(toolbar_tenant);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        autoCompleteTextView = findViewById(R.id.autoCompleteTxt);

        subjOtherTxt = findViewById(R.id.subjOtherTxt);
        subjOther = findViewById(R.id.subjOther);
        concernDesc = findViewById(R.id.concernDesc);
        submitConcern = findViewById(R.id.submitConcern);
        concernContainer = findViewById(R.id.concernContainer);
        allConcerns = findViewById(R.id.allConcerns);
        respondedConcerns = findViewById(R.id.respondedConcerns);
        pendingConcerns = findViewById(R.id.pendingConcerns);
        mineConcerns = findViewById(R.id.mineConcerns);
        concernLinear = findViewById(R.id.concernLinear);

        subtitleTxt = findViewById(R.id.subtitleTxt);
        subjectTxt = findViewById(R.id.subjectTxt);
        textInputTxt = findViewById(R.id.textInputTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        listConcerns = findViewById(R.id.listConcerns);

        announcementTxt = findViewById(R.id.announcementTxt);
        announcementLinear = findViewById(R.id.announcementLinear);
        announceTitle = findViewById(R.id.announceTitle);
        announceSubj = findViewById(R.id.announceSubj);
        submitAnnouncement = findViewById(R.id.submitAnnouncement);

        Drawable icon_announcement = getResources().getDrawable(R.drawable.announcement_enter);
        Drawable icon_concern = getResources().getDrawable(R.drawable.concern_enter);
        Drawable icon_list_concern = getResources().getDrawable(R.drawable.list_concern);

        // Set the bounds of the icon to the desired size
        int iconWidth = getResources().getDimensionPixelSize(R.dimen.icon_width);
        int iconHeight = getResources().getDimensionPixelSize(R.dimen.icon_height);
        icon_announcement.setBounds(0, 0, iconWidth, iconHeight);
        icon_concern.setBounds(0, 0, iconWidth, iconHeight);
        icon_list_concern.setBounds(0, 0, iconWidth, iconHeight);

        // Set the icon on the left of the TextView
        announcementTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(icon_announcement, null, null, null);
        subtitleTxt.setCompoundDrawablesRelativeWithIntrinsicBounds(icon_concern, null, null, null);
        listConcerns.setCompoundDrawablesRelativeWithIntrinsicBounds(icon_list_concern, null, null, null);

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
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                if (role.equals("Tenant")) {
                                    concernLinear.setVisibility(View.VISIBLE);
                                    subtitleTxt.setVisibility(View.VISIBLE);
                                    subjectTxt.setVisibility(View.VISIBLE);
                                    textInputTxt.setVisibility(View.VISIBLE);
                                    autoCompleteTextView.setVisibility(View.VISIBLE);
                                    descriptionTxt.setVisibility(View.VISIBLE);
                                    concernDesc.setVisibility(View.VISIBLE);
                                    submitConcern.setVisibility(View.VISIBLE);
                                    mineConcerns.setVisibility(View.VISIBLE);
                                } else {
                                    announcementTxt.setVisibility(View.VISIBLE);
                                    announcementLinear.setVisibility(View.VISIBLE);
                                }
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
        adapterItems = new ArrayAdapter<>(this, R.layout.concerns_list, item);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(Concerns.this, "Item: " + item, Toast.LENGTH_SHORT).show();

                if (item.equals("Other")) {
                    // Show the additional EditText
                    subjOtherTxt.setVisibility(View.VISIBLE);
                    subjOther.setVisibility(View.VISIBLE);
                } else {
                    // Hide the additional EditText
                    subjectTxt.setVisibility(View.GONE);
                    subjOther.setVisibility(View.GONE);
                }
            }
        });

//        concernsGrid = findViewById(R.id.concernsGrid);

        //submit ng announcement
        submitAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterAnnouncement();
            }
        });

        //submit ng concern
        submitConcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterConcern();
            }
        });

        allConcerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveAllConcerns();
            }
        });
        allConcerns.performClick();
        allConcerns.refreshDrawableState();

        respondedConcerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveRespondedConcerns();
            }
        });

        pendingConcerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrievePendingConcerns();
            }
        });

        mineConcerns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveMineConcerns();
            }
        });

//        retrieveAllConcerns();
    }

    public void retrieveAllConcerns(){
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
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/concerns/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray concerns = response.getJSONArray("concerns");

                                                    concernContainer.removeAllViews();

                                                    for (int i = 0; i < concerns.length(); i++) {
                                                        JSONObject concern = concerns.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.concerns_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        TextView concernSubj = view.findViewById(R.id.subjTitle);
                                                        TextView concernDes = view.findViewById(R.id.descConcern);
                                                        TextView concernResponse = view.findViewById(R.id.response);
                                                        EditText responseEnter = view.findViewById(R.id.responseEnter);
                                                        Button replyResponse = view.findViewById(R.id.replyResponse);
                                                        Button submitResponse = view.findViewById(R.id.submitResponse);
                                                        Button cancelResponse = view.findViewById(R.id.cancelResponse);

                                                        responseEnter.setVisibility(View.GONE);
                                                        submitResponse.setVisibility(View.GONE);
                                                        cancelResponse.setVisibility(View.GONE);

                                                        String concernId = concern.getString("id");
                                                        String tenantId = concern.getString("unit_id");
                                                        tenantNum.setText(tenantId);
                                                        String concernTitle = concern.getString("subject");
                                                        concernSubj.setText(concernTitle);
                                                        String concernDescrip = concern.getString("description");
                                                        concernDes.setText(concernDescrip);
                                                        String responses = concern.getString("response");
                                                        if (responses != "null" && !responses.isEmpty()) {
                                                            replyResponse.setVisibility(View.GONE);
                                                            String responseText = "<font color='#000000'><b>Response: </b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText + responses));
                                                        } else {
                                                            String responseText = "<font color='#000000'><b>No response yet.</b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText));
                                                            replyResponse.setVisibility(View.VISIBLE);
                                                        }

                                                        //set tag for concern_id
//                                                        view.setTag(concern);

                                                        if (role.equals("Tenant")) {
                                                            replyResponse.setVisibility(View.GONE);
                                                        }

                                                        //click reply button
                                                        replyResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
//                                                                Toast.makeText(ConcernsTenants.this, "Read details button", Toast.LENGTH_SHORT).show();
                                                                replyResponse.setVisibility(View.GONE);
                                                                responseEnter.setVisibility(View.VISIBLE);
                                                                submitResponse.setVisibility(View.VISIBLE);
                                                                cancelResponse.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                        //submit response of landlord
                                                        submitResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                final String landlordResponse = responseEnter.getText().toString();
//                                                                final String concernId = (String) view.getTag().toString();
//                                                                final Integer concernId = Integer.valueOf(view.getTag().toString());

                                                                final JSONObject form = new JSONObject();
                                                                try {
                                                                    form.put("concern_id", concernId);
                                                                    form.put("response", landlordResponse);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                RequestQueue queueResponse = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                                                BackendJsonObjectRequest requestResponse = new BackendJsonObjectRequest(
                                                                        Request.Method.POST,
                                                                        "/api/concern/response/mobile", form,
                                                                        new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                try {
                                                                                    String message = response.getString("message");
                                                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                                                    recreate();

                                                                                } catch (JSONException e) {
                                                                                    Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                );
                                                                queueResponse.add(requestResponse);
                                                                // Hide the buttons when submitConcern is clicked
                                                                responseEnter.setVisibility(View.GONE);
                                                                submitResponse.setVisibility(View.GONE);
                                                                cancelResponse.setVisibility(View.GONE);
                                                            }
                                                        });
                                                        cancelResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // Hide the buttons when cancelConcern is clicked
                                                                responseEnter.setVisibility(View.GONE);
                                                                submitResponse.setVisibility(View.GONE);
                                                                cancelResponse.setVisibility(View.GONE);
                                                                replyResponse.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                        concernContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
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

    public void retrieveRespondedConcerns(){
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
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/concerns/responded/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray concerns = response.getJSONArray("concerns");

                                                    concernContainer.removeAllViews();

                                                    for (int i = 0; i < concerns.length(); i++) {
                                                        JSONObject concern = concerns.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.concerns_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        TextView concernSubj = view.findViewById(R.id.subjTitle);
                                                        TextView concernDes = view.findViewById(R.id.descConcern);
                                                        TextView concernResponse = view.findViewById(R.id.response);
                                                        EditText responseEnter = view.findViewById(R.id.responseEnter);
                                                        Button replyResponse = view.findViewById(R.id.replyResponse);
                                                        Button submitResponse = view.findViewById(R.id.submitResponse);
                                                        Button cancelResponse = view.findViewById(R.id.cancelResponse);

                                                        responseEnter.setVisibility(View.GONE);
                                                        submitResponse.setVisibility(View.GONE);
                                                        cancelResponse.setVisibility(View.GONE);

                                                        String concernId = concern.getString("id");
                                                        String tenantId = concern.getString("unit_id");
                                                        tenantNum.setText(tenantId);
                                                        String concernTitle = concern.getString("subject");
                                                        concernSubj.setText(concernTitle);
                                                        String concernDescrip = concern.getString("description");
                                                        concernDes.setText(concernDescrip);
                                                        String responses = concern.getString("response");
                                                        if (responses != "null" && !responses.isEmpty()) {
                                                            replyResponse.setVisibility(View.GONE);
                                                            String responseText = "<font color='#000000'><b>Response: </b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText + responses));
                                                        } else {
                                                            String responseText = "<font color='#000000'><b>No response yet.</b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText));
                                                            replyResponse.setVisibility(View.VISIBLE);
                                                        }

                                                        //set tag for concern_id
//                                                        view.setTag(concern);

                                                        if (role.equals("Tenant")) {
                                                            replyResponse.setVisibility(View.GONE);
                                                        }

//                                                        //click reply button
//                                                        replyResponse.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
////                                                                Toast.makeText(ConcernsTenants.this, "Read details button", Toast.LENGTH_SHORT).show();
//                                                                replyResponse.setVisibility(View.GONE);
//                                                                responseEnter.setVisibility(View.VISIBLE);
//                                                                submitResponse.setVisibility(View.VISIBLE);
//                                                                cancelResponse.setVisibility(View.VISIBLE);
//                                                            }
//                                                        });
//
//                                                        //submit response of landlord
//                                                        submitResponse.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
//                                                                final String landlordResponse = responseEnter.getText().toString();
////                                                                final String concernId = (String) view.getTag().toString();
////                                                                final Integer concernId = Integer.valueOf(view.getTag().toString());
//
//                                                                final JSONObject form = new JSONObject();
//                                                                try {
//                                                                    form.put("concern_id", concernId);
//                                                                    form.put("response", landlordResponse);
//                                                                } catch (JSONException e) {
//                                                                    e.printStackTrace();
//                                                                }
//
//                                                                RequestQueue queueResponse = AppVolleyRequestQueue.getInstance(getApplicationContext());
//                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
//                                                                BackendJsonObjectRequest requestResponse = new BackendJsonObjectRequest(
//                                                                        Request.Method.POST,
//                                                                        "/api/concern/response/mobile", form,
//                                                                        new Response.Listener<JSONObject>() {
//                                                                            @Override
//                                                                            public void onResponse(JSONObject response) {
//                                                                                try {
//                                                                                    String message = response.getString("message");
//                                                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
//                                                                                    recreate();
//
//                                                                                } catch (JSONException e) {
//                                                                                    Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                                                                                    e.printStackTrace();
//                                                                                }
//
//                                                                            }
//                                                                        },
//                                                                        new Response.ErrorListener() {
//                                                                            @Override
//                                                                            public void onErrorResponse(VolleyError error) {
//                                                                                Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                                                                            }
//                                                                        }
//                                                                );
//                                                                queueResponse.add(requestResponse);
//                                                                // Hide the buttons when submitConcern is clicked
//                                                                responseEnter.setVisibility(View.GONE);
//                                                                submitResponse.setVisibility(View.GONE);
//                                                                cancelResponse.setVisibility(View.GONE);
//                                                            }
//                                                        });
//                                                        cancelResponse.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
//                                                                // Hide the buttons when cancelConcern is clicked
//                                                                responseEnter.setVisibility(View.GONE);
//                                                                submitResponse.setVisibility(View.GONE);
//                                                                cancelResponse.setVisibility(View.GONE);
//                                                            }
//                                                        });

                                                        concernContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
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

    public void retrievePendingConcerns(){
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
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/concerns/pending/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray concerns = response.getJSONArray("concerns");

                                                    concernContainer.removeAllViews();

                                                    for (int i = 0; i < concerns.length(); i++) {
                                                        JSONObject concern = concerns.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.concerns_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        TextView concernSubj = view.findViewById(R.id.subjTitle);
                                                        TextView concernDes = view.findViewById(R.id.descConcern);
                                                        TextView concernResponse = view.findViewById(R.id.response);
                                                        EditText responseEnter = view.findViewById(R.id.responseEnter);
                                                        Button replyResponse = view.findViewById(R.id.replyResponse);
                                                        Button submitResponse = view.findViewById(R.id.submitResponse);
                                                        Button cancelResponse = view.findViewById(R.id.cancelResponse);

                                                        responseEnter.setVisibility(View.GONE);
                                                        submitResponse.setVisibility(View.GONE);
                                                        cancelResponse.setVisibility(View.GONE);

                                                        String concernId = concern.getString("id");
                                                        String tenantId = concern.getString("unit_id");
                                                        tenantNum.setText(tenantId);
                                                        String concernTitle = concern.getString("subject");
                                                        concernSubj.setText(concernTitle);
                                                        String concernDescrip = concern.getString("description");
                                                        concernDes.setText(concernDescrip);
                                                        String responses = concern.getString("response");
                                                        if (responses != "null" && !responses.isEmpty()) {
                                                            replyResponse.setVisibility(View.GONE);
                                                            String responseText = "<font color='#000000'><b>Response: </b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText + responses));
                                                        } else {
                                                            String responseText = "<font color='#000000'><b>No response yet.</b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText));
                                                            replyResponse.setVisibility(View.VISIBLE);
                                                        }

                                                        //set tag for concern_id
//                                                        view.setTag(concern);

                                                        if (role.equals("Tenant")) {
                                                            replyResponse.setVisibility(View.GONE);
                                                        }

                                                        //click reply button
                                                        replyResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
//                                                                Toast.makeText(ConcernsTenants.this, "Read details button", Toast.LENGTH_SHORT).show();
                                                                replyResponse.setVisibility(View.GONE);
                                                                responseEnter.setVisibility(View.VISIBLE);
                                                                submitResponse.setVisibility(View.VISIBLE);
                                                                cancelResponse.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                        //submit response of landlord
                                                        submitResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                final String landlordResponse = responseEnter.getText().toString();
//                                                                final String concernId = (String) view.getTag().toString();
//                                                                final Integer concernId = Integer.valueOf(view.getTag().toString());

                                                                final JSONObject form = new JSONObject();
                                                                try {
                                                                    form.put("concern_id", concernId);
                                                                    form.put("response", landlordResponse);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                RequestQueue queueResponse = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                                                BackendJsonObjectRequest requestResponse = new BackendJsonObjectRequest(
                                                                        Request.Method.POST,
                                                                        "/api/concern/response/mobile", form,
                                                                        new Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                                try {
                                                                                    String message = response.getString("message");
                                                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                                                    recreate();

                                                                                } catch (JSONException e) {
                                                                                    Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }
                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                );
                                                                queueResponse.add(requestResponse);
                                                                // Hide the buttons when submitConcern is clicked
                                                                responseEnter.setVisibility(View.GONE);
                                                                submitResponse.setVisibility(View.GONE);
                                                                cancelResponse.setVisibility(View.GONE);
                                                            }
                                                        });
                                                        cancelResponse.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                // Hide the buttons when cancelConcern is clicked
                                                                responseEnter.setVisibility(View.GONE);
                                                                submitResponse.setVisibility(View.GONE);
                                                                cancelResponse.setVisibility(View.GONE);
                                                                replyResponse.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                        concernContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
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

    public void retrieveMineConcerns(){
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
                                Log.d("USER_ID", id);
                                String role = user.getString("role");

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/concerns/unit/mobile/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray concerns = response.getJSONArray("concerns");

                                                    concernContainer.removeAllViews();

                                                    for (int i = 0; i < concerns.length(); i++) {
                                                        JSONObject concern = concerns.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.concerns_card, null);

                                                        TextView tenantNum = view.findViewById(R.id.tenantNumber);
                                                        TextView concernSubj = view.findViewById(R.id.subjTitle);
                                                        TextView concernDes = view.findViewById(R.id.descConcern);
                                                        TextView concernResponse = view.findViewById(R.id.response);
                                                        EditText responseEnter = view.findViewById(R.id.responseEnter);
                                                        Button replyResponse = view.findViewById(R.id.replyResponse);
                                                        Button submitResponse = view.findViewById(R.id.submitResponse);
                                                        Button cancelResponse = view.findViewById(R.id.cancelResponse);

                                                        responseEnter.setVisibility(View.GONE);
                                                        submitResponse.setVisibility(View.GONE);
                                                        cancelResponse.setVisibility(View.GONE);

                                                        String concernId = concern.getString("id");
                                                        String tenantId = concern.getString("unit_id");
                                                        tenantNum.setText(tenantId);
                                                        String concernTitle = concern.getString("subject");
                                                        concernSubj.setText(concernTitle);
                                                        String concernDescrip = concern.getString("description");
                                                        concernDes.setText(concernDescrip);
                                                        String responses = concern.getString("response");
                                                        if (responses != "null" && !responses.isEmpty()) {
                                                            replyResponse.setVisibility(View.GONE);
                                                            String responseText = "<font color='#000000'><b>Response: </b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText + responses));
                                                        } else {
                                                            String responseText = "<font color='#000000'><b>No response yet.</b></font>";
                                                            concernResponse.setText(Html.fromHtml(responseText));
                                                            replyResponse.setVisibility(View.VISIBLE);
                                                        }

                                                        //set tag for concern_id
//                                                        view.setTag(concern);

                                                        if (role.equals("Tenant")) {
                                                            replyResponse.setVisibility(View.GONE);
                                                        }

                                                        concernContainer.addView(view);
                                                    }

                                                } catch (JSONException e) {
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

    public void enterAnnouncement(){

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
                                Log.d("USER_ID", id);

                                final String title = announceTitle.getText().toString();
                                final String description = announceSubj.getText().toString();
                                final JSONObject form = new JSONObject();
                                try {
                                    form.put("subject", title);
                                    form.put("description", description);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.POST,
                                        "/api/announcements/post/" + id, form,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String message = response.getString("message");
                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                    recreate();
                                                    announceTitle.setText("");
                                                    announceSubj.setText("");
//                                                   loginPreferenceDataSource.storeBackendToken(token);

                                                } catch (JSONException e) {
                                                    Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                );
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

    public void enterConcern(){

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
                                Log.d("USER_ID", id);

                                final String title = autoCompleteTextView.getText().toString();
                                final String description = concernDesc.getText().toString();
                                String subject;

                                if (title.equals("Other")) {
//                                    subjectTxt.setVisibility(View.VISIBLE);
//                                    subjOther.setVisibility(View.VISIBLE);
                                    subject = "Other: " + subjOther.getText().toString();
                                } else {
                                    subject = title;
                                }

                                final JSONObject form = new JSONObject();
                                try {
                                    form.put("subject", subject);
                                    form.put("description", description);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.POST,
                                        "/api/concern/mobile/" + id, form,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String message = response.getString("message");
                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                    recreate();
                                                    autoCompleteTextView.setText("");
                                                    subjOther.setText("");
                                                    concernDesc.setText("");
//                                                   loginPreferenceDataSource.storeBackendToken(token);

                                                } catch (JSONException e) {
                                                    Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(Concerns.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                );
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                                String id = user.getString("user_id");
                                String role = user.getString("role");
                                if (role.equals("Tenant")) {
                                    toolbar_tenant.setVisibility(View.VISIBLE);
                                    getMenuInflater().inflate(R.menu.top_nav_tenant, menu);
                                } else {
                                    toolbar_landlord.setVisibility(View.VISIBLE);
                                    getMenuInflater().inflate(R.menu.top_nav_landlord, menu);
                                }
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
//        getMenuInflater().inflate(R.menu.top_nav_landlord, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.notification:
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
                                        String role = user.getString("role");

                                        // Get the FragmentManager
                                        FragmentManager fragmentManager = getSupportFragmentManager();

                                        // Begin a FragmentTransaction
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                        // Create a new instance of your fragment
                                        Fragment fragment = new NotificationsLandlord();

                                        // Replace the current layout with your fragment
                                        fragmentTransaction.replace(R.id.container, fragment);

                                        // Add the transaction to the back stack
                                        fragmentTransaction.addToBackStack(null);

                                        // Commit the transaction
                                        fragmentTransaction.commit();

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
                break;
            case R.id.addTenant:
//                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Concerns.this, AddTenant.class));
                break;
            case R.id.concern:
//                Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Concerns.this, Concerns.class));
                break;
            case R.id.addCash:
//                Toast.makeText(this, "Cash Payment", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Concerns.this, CashPayment.class));
                break;
            case R.id.addQrGcash:
                BillBackendDataResource userDataSource1 = BillBackendDataResource.getInstance(getApplicationContext());
                LoginPreferenceDataSource loginPreferenceDataSource1 = LoginPreferenceDataSource.getInstance(getApplicationContext());
                String backendToken1 = loginPreferenceDataSource1.getBackendToken();
                try {
                    userDataSource1.fetchUserFromBackend(
                            backendToken1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject user = response.getJSONObject("user");
                                        String id = user.getString("user_id");
                                        String role = user.getString("role");
//                                        if (role.equals("Tenant") && role == "Tenant") {
//                                            addGcash.setVisibility(View.INVISIBLE);
//                                        } else {
                                        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
                                        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                                Request.Method.GET,
                                                "/api/gcash-qr/qr/" + id, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        try {
//                                                                JSONObject qrCode = response.getJSONObject("hasQr");
                                                            Object hasQr = response.get("hasQr");
                                                            if (hasQr instanceof Boolean && !(Boolean) hasQr) {
                                                                // hasQr is false
                                                                Intent intent = new Intent(getApplicationContext(), AddGcash.class);
                                                                intent.putExtra("id", id);
                                                                startActivity(intent);
                                                            } else if (hasQr instanceof JSONObject) {
                                                                // hasQr is true
                                                                JSONObject qrObject = (JSONObject) hasQr;
                                                                String qr = qrObject.getString("qr");
                                                                // Extract other values as needed
                                                                Intent intent = new Intent(getApplicationContext(), EditGcash.class);
                                                                intent.putExtra("id", id);
                                                                intent.putExtra("qr", qr);
                                                                startActivity(intent);
                                                            } else {
                                                                // Unexpected response format
                                                                Log.e(TAG, "Unexpected response format: " + response.toString());
                                                            }

                                                        } catch (JSONException e) {
//                                                                Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
//                                                                progressBar.setVisibility(View.INVISIBLE);
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
                                        queue.add(request);
//                                        }
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
                break;
            case R.id.settings:
                startActivity(new Intent(Concerns.this, Settings.class));
                break;
            case R.id.logout:
                RequestQueue queue = Volley.newRequestQueue(this);

                StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                    // Handle successful response
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");
                        if (success) {
                            // Handle successful logout
                            Toast.makeText(this, "User Logged Out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle unsuccessful logout
                        }
                    } catch (JSONException e) {
                        // Handle JSON exception
                    }
                }, error -> {
                    // Handle error
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // Add token to headers if necessary
                        Map<String, String> headers = new HashMap<>();
                        String token = LoginPreferenceDataSource.getInstance(getApplicationContext()).getBackendToken();
                        if (token != null) {
                            headers.put("Authorization", "Bearer " + token);
                        }
                        return headers;
                    }
                };
                queue.add(request);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}