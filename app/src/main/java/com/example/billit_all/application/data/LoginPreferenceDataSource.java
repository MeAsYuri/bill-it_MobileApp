package com.example.billit_all.application.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.billit_all.Login;

public class LoginPreferenceDataSource {
    private static LoginPreferenceDataSource instance = null;

    public static final String SHARED_PREFERENCE_NAME = "backend_login";
    public static final String TOKEN_KEY_NAME = "token";
    private SharedPreferences sharedPreferences = null;

    public LoginPreferenceDataSource(SharedPreferences prefs) {
        this.sharedPreferences = prefs;
    }


    public static LoginPreferenceDataSource getInstance(Context applicationContext) {
        if(instance == null) {
            SharedPreferences prefs = applicationContext.getSharedPreferences( SHARED_PREFERENCE_NAME, applicationContext.MODE_PRIVATE);
            instance = new LoginPreferenceDataSource(prefs);
        }
        return instance;
    }

    public void storeBackendToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_KEY_NAME, token);
        editor.commit();
    }

    public String getBackendToken() {
        String token = sharedPreferences.getString(TOKEN_KEY_NAME, null);
        return token;
    }
}
