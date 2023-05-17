package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportIssue extends AppCompatActivity {
    EditText issueSubj, issueDesc;
    Button submitIssue;
    LinearLayout issueContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        issueSubj = findViewById(R.id.issueSubj);
        issueDesc = findViewById(R.id.issueDesc);
        submitIssue = findViewById(R.id.submitIssue);
        issueContainer = findViewById(R.id.issueContainer);

        submitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterIssue();
            }
        });

        retrieveIssues();
    }

    public void enterIssue(){

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

                                final String subject = issueSubj.getText().toString();
                                final String description = issueDesc.getText().toString();
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
                                        "/api/report-issue/post/" + id, form,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    String message = response.getString("message");
                                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                    recreate();
                                                    issueSubj.setText("");
                                                    issueDesc.setText("");
//                                                   loginPreferenceDataSource.storeBackendToken(token);

                                                } catch (JSONException e) {
                                                    Toast.makeText(ReportIssue.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ReportIssue.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    public void retrieveIssues(){
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
                                        "/api/report-issues/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray issues = response.getJSONArray("data");

                                                    issueContainer.removeAllViews();

                                                    for (int i = 0; i < issues.length(); i++) {
                                                        JSONObject issue = issues.getJSONObject(i);

                                                        View view = getLayoutInflater().inflate(R.layout.issues_card, null);

                                                        TextView subjReport = view.findViewById(R.id.reportListSubj);
                                                        TextView descReport = view.findViewById(R.id.reportListDesc);
                                                        TextView responseReport = view.findViewById(R.id.reportResponse);

                                                        String reportSub = issue.getString("subject");
                                                        subjReport.setText(reportSub);
                                                        String reportDesc = issue.getString("description");
                                                        descReport.setText(reportDesc);
                                                        String responses = issue.getString("response");
                                                        if (responses != "null" && !responses.isEmpty()) {
                                                            String responseText = "<font color='#000000'><b>Response: </b></font>";
                                                            responseReport.setText(Html.fromHtml(responseText + responses));
                                                        } else {
                                                            String responseText = "<font color='#000000'><b>No response yet.</b></font>";
                                                            responseReport.setText(Html.fromHtml(responseText));
                                                        }

                                                        issueContainer.addView(view);
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
}
