package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.billit_all.Calculate.CalculateActivity3;
import com.example.billit_all.R;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContractAdding extends AppCompatActivity {
    private LinearLayout layoutEditText, layoutEditText2;
    private Button btnAddEditText;
    TextView textView,textView2,test;
    Button btn_save;
    Button btn_cancel;
    Button deleteButton, resetButton, procButton;
    String additionalTerms, selectedTenantId;
    TextView selectTenant;
    int countTerms = 13;
    ArrayList<List<EditText>> termsInputFields = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_adding);

        layoutEditText  = findViewById(R.id.layout_edit_text);
        btnAddEditText = findViewById(R.id.btn_add_edit_text);
        btn_cancel = findViewById(R.id.btn_cancel);
        resetButton = findViewById(R.id.btn_reset);
//        btn_save = findViewById(R.id.btn_save);
//        deleteButton = findViewById(R.id.btn_delete);

        procButton = findViewById(R.id.btn_proceed);
        test = findViewById(R.id.test);

//        btnAddEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EditText editText = new EditText(ContractAdding.this);
//                EditText editText2 = new EditText(ContractAdding.this);
//
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                layoutParams.weight = 10;
//                editText.setLayoutParams(layoutParams);
//                editText.setText((++countTerms) + ".");
//                editText.setHint("Subject Here:")
//                layoutEditText2.addView(editText2,layoutParams);
//
//                editText2.setLayoutParams(layoutParams);
//                editText2.setHint("Body Here:");
//                layoutEditText.addView(editText,layoutParams);
//
////                EditText editText2 = new EditText(ContractAdding.this);
////                layoutEditText.addView(editText2);
//                termsInputFields.add(Arrays.asList(editText,editText2));
//            }
//        });

        btnAddEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(ContractAdding.this);
                EditText editText2 = new EditText(ContractAdding.this);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 10;
                editText.setLayoutParams(layoutParams);
                editText.setText((++countTerms) + ".");
                editText.setHint("Subject Here:");

                editText2.setLayoutParams(layoutParams);
                editText2.setHint("Body Here:");

                layoutEditText.addView(editText,layoutParams);
                layoutEditText.addView(editText2,layoutParams);

                termsInputFields.add(Arrays.asList(editText,editText2));
            }
        });
//        btnAddEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                EditText editText = new EditText(ContractAdding.this);
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                layoutParams.weight = 10;
//                editText.setLayoutParams(layoutParams);
//                editText.setText((++countTerms) + "." + "\n");
//
//                layoutEditText.addView(editText,layoutParams);
//
////                EditText editText2 = new EditText(ContractAdding.this);
////                layoutEditText.addView(editText2);
//
//            }
//        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(ContractAdding.this, ContractForm.class);
                startActivity(c);
                finish();
            }
        });

//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int lastIndex = layoutEditText.getChildCount() - 1;
//                if (lastIndex >= 0) {
//                    layoutEditText.removeViewAt(lastIndex);
//                }
//            }
//        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadActivity();
            }
        });

        procButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveData();
                saveRentFees();

                Intent b = new Intent(ContractAdding.this, ContractSpecs.class);
                b.putExtra("additionalTerms", additionalTerms);
                startActivity(b);
                finish();

            }
        });
    }
    private void saveRentFees(){


        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getApplicationContext().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getApplicationContext().getApplicationContext());
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
                                Log.d("USER_ID",id);
                                selectedTenantId = id;

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());


                                JSONObject formdata = new JSONObject();
                                try {

                                    ArrayList<String>subjects = new ArrayList<String>();
                                    ArrayList<String>bodies = new ArrayList<String>();

                                    for (int i = 0; i < termsInputFields.size(); i++) {
                                        List<EditText> pair = termsInputFields.get(i);
                                        subjects.add(pair.get(0).getText().toString());
                                        bodies.add(pair.get(1).getText().toString());

                                    }

                                    formdata.put("subject", new JSONArray(subjects));
                                    formdata.put("body", new JSONArray(bodies));
//                                    formdata.put("rent_fee", rentFees);

                                    Log.d("null", String.valueOf(subjects));
                                    Log.d("null", String.valueOf(bodies));



                                    BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                            Request.Method.POST,
                                            "/api/contract-specification/mobile/" + selectedTenantId,
                                            formdata,
                                            new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    try {
                                                        String message = response.getString("message");
                                                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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


//
//    private void saveData() {
//        int count = layoutEditText.getChildCount();
//        ArrayList<String> arrayList = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            View view = layoutEditText.getChildAt(i);
//
//            if (view instanceof EditText ) {
//                EditText editText = (EditText) view;
//                String text = editText.getText().toString();
//
//
//                arrayList.add(text);
//
//                String separator = "\n";
//                StringBuilder stringBuilder = new StringBuilder();
//                for (String item : arrayList) {
//                    stringBuilder.append(item).append(separator);
//                }
//                // Remove the last separator
//                if (stringBuilder.length() > 0) {
//                    stringBuilder.setLength(stringBuilder.length() - separator.length());
//                }
//                String resultString = stringBuilder.toString();
//                //pass of result string to pass the data in the input fields
//                additionalTerms = resultString;
//
//            }
//        }
//    }
//    private void saveData2() {
//        int count = layoutEditText.getChildCount();
//        ArrayList <String> arrayList = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            View view = layoutEditText.getChildAt(i);
//
//            if (view instanceof EditText) {
//
//                EditText editText2 = (EditText) view;
//                String text2 = editText2.getText().toString();
//                arrayList.add(text2);
//
//                String separator = "\n";
//                StringBuilder stringBuilder = new StringBuilder();
//                for (String item : arrayList) {
//                    stringBuilder.append(item).append(separator);
//                }
//                // Remove the last separator
//                if (stringBuilder.length() > 0) {
//                    stringBuilder.setLength(stringBuilder.length() - separator.length());
//                }
//                String resultString = stringBuilder.toString();
//                //pass of result string to pass the data in the input fields
//                textView.setText(resultString);
//            }
//        }
//    }


    public void reloadActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}