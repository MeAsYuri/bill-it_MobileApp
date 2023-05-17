package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsTenant extends Fragment {

    EditText userId;
    RecyclerView notifRecycler;
    NotifAdapterTenant notifAdapterTenant;
    TextView verifyId;

    ArrayList<NotifTenantModel> tenants = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications_tenant, container, false);


        userId = view.findViewById(R.id.userId);
        verifyId = view.findViewById(R.id.verifyId);
        notifRecycler = view.findViewById(R.id.notifRecycler);
        notifRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        notifAdapterTenant = new NotifAdapterTenant(getContext(), tenants);
        notifRecycler.setAdapter(notifAdapterTenant);
        retrieveProfile();

        return view;
    }

    public void retrieveProfile(){
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
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
                                Log.d("USER_ID", id);
                                userId.setText(id);

                                getTenants();
                                updateIsShowed();
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

    public void getTenants() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());

        String url = "/api/get/notification";

        JSONObject body = new JSONObject();
        try {
            // Pass user ID in request body
            body.put("id", userId.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject original = response.getJSONObject("original");
                            JSONArray notifsjson = original.getJSONArray("notification");
                            Log.d("NOTIF_USER", String.valueOf(notifsjson));
                            tenants.clear();
                            for (int i = 0; i < notifsjson.length(); i++) {
                                JSONObject notifjson = notifsjson.getJSONObject(i);
                                Log.d("NOTIF_USER", String.valueOf(notifjson));
                                String id = notifjson.getString("id");
                                Log.d("NOTIF_USER", id);
                                String type = notifjson.getString("type");
                                String typeOfId = notifjson.getString("type_of_id");
                                String date = notifjson.getString("created_at");
                                String isSeen = notifjson.getString("isSeen");

                                NotifTenantModel notif = new NotifTenantModel(id, type, typeOfId, date, isSeen);

                                tenants.add(notif);
                            }

                            notifAdapterTenant.notifyDataSetChanged();


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
        request.authenticated(getActivity().getApplicationContext());
        queue.add(request);
    }


    public void updateIsShowed() {
        RequestQueue queue = AppVolleyRequestQueue.getInstance(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://bill-it.online/api/showed/notif/tenant",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            String message = result.getString("message");
                            Toast.makeText(getActivity(), "updated isShowed!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId.getText().toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onResume() {
        super.onResume();
        tenants.clear();
        retrieveProfile();
    }
}