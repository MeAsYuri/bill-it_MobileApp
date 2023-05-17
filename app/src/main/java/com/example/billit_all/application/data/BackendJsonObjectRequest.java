package com.example.billit_all.application.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
//import com.example.billit_all.application.data.LoginSharedPreferenceDataSource;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BackendJsonObjectRequest extends JsonObjectRequest{
//    protected static final String BASE_URL = "http://10.0.2.2:8000";
//  protected static final String BASE_URL = "http://192.168.100.65:8000";
  protected static final String BASE_URL = "https://bill-it.online";
//    school ipaddress
//    protected static final String BASE_URL = "http://172.34.5.245:8000";

    @Nullable
    protected String authenticationToken;

    /* constructors */


    /**
     * {@inheritDoc}
     * @param endpoint the path after BASE_URL. start with slash.
     * (e.g. "/api/login")
     *
     * @see JsonObjectRequest#JsonObjectRequest(String, Response.Listener, Response.ErrorListener)  JsonObjectRequest
     */
    public BackendJsonObjectRequest(
            String endpoint,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener
    ) {
        super(getFullPath(endpoint) , listener, errorListener);
    }

    /**
     * @param endpoint the path after BASE_URL. start with slash.
     * (e.g. "/api/login")
     *
     * @see JsonObjectRequest#JsonObjectRequest(int, String, JSONObject, Response.Listener, Response.ErrorListener)  JsonObjectRequest
     */
    public BackendJsonObjectRequest(
            int method,
            String endpoint,
            @Nullable JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener
    ) {
        super(method, getFullPath(endpoint), jsonRequest, listener, errorListener);
    }

    /* public methods */

    /**
     * call for authenticated requests.
     *
     * @param token token given by backend on Login.
     */
    public void authenticated(String token) {
        this.authenticationToken = token;
    }

    public void authenticated(Context appContext) {
//        LoginSharedPreferenceDataSource loginPrefs = LoginSharedPreferenceDataSource.getInstance(appContext);
//        String backendToken = loginPrefs.getBackendAuthToken();
//        SharedPreferences preferences = appContext.getSharedPreferences();
        SharedPreferences preferences = appContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        authenticated(token);

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        if (this.isAuthenticated()) {
            headers.put("Authorization", "Bearer " + this.authenticationToken);
        }
        return headers;
    }

    /* internal methods */

    protected boolean isAuthenticated() {
        return this.authenticationToken != null;
    }

    protected static String getFullPath(String endpoint) {
        return BackendJsonObjectRequest.BASE_URL + endpoint;
    }
}