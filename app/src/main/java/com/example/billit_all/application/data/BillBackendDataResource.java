package com.example.billit_all.application.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class BillBackendDataResource {

    private static BillBackendDataResource instance;

    private Context appContext;
    private RequestQueue queue;

    public BillBackendDataResource(Context applicationContext) {
        appContext = applicationContext;
    }

    public BillBackendDataResource() {
    }

    public static BillBackendDataResource getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new BillBackendDataResource(applicationContext);
            instance.queue = AppVolleyRequestQueue.getInstance(applicationContext);
        }
        return instance;
    }

    public void retrieveUserFromBackend(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/auth/user/dashboard",
                requestParams,
                responseListener,
                errorListener
        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void updateProfile(
            String name,
            String email,
            String phone,
            int id,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject form = new JSONObject();
        form.put("name", name);
        form.put("email", email);
        form.put("phone", phone);
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.PUT,
                "/api/profile",
                form,
                responseListener,
                errorListener
        );
        queue.add(request);
    }

    public void fetchSlotTime(
            int slot_id,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/time-slot/" + slot_id,
                requestParams,
                responseListener,
                errorListener
        );
        queue.add(request);
    }

    public void getBikeSlotStatus(
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/slot/",
                requestParams,
                responseListener,
                errorListener
        );
        queue.add(request);
    }

    public void fetchUserFromBackend(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/auth/user/dashboard",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void storeTempUser(
            String backendToken,
            String username,
            String password,

            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject form = new JSONObject();
        form.put("name","");
        form.put("username", username);
        form.put("email", "");
        form.put("phone", "");
        form.put("password", password);
        form.put("phone", "");
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.POST,
                "/api/auth/user/temp-acct",
                form,
                responseListener,
                errorListener
        );
        request.authenticated(backendToken);
        queue.add(request);
    }
    public void fetchUserForLogin(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/auth/user/dashboard",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void updateProfile(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/auth/user/dashboard",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void updatePassword(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/auth/user/dashboard",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchTenantIdFromBackend(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/id/tenant/info",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }
    public void fetchTotalDatas(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/data",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchBalances(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/balances",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchPenalty(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/penaltybalances",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchElecStatus(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/electricity/users",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchWaterStatus(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/water/users",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

    public void fetchRentStatus(
            String backendToken,
            Response.Listener<JSONObject> responseListener,
            @Nullable Response.ErrorListener errorListener
    ) throws JSONException {
        JSONObject requestParams = new JSONObject();
        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                Request.Method.GET,
                "/api/dashboard/rent/users",
                requestParams,
                responseListener,
                errorListener

        );
        request.authenticated(backendToken);
        queue.add(request);
    }

}