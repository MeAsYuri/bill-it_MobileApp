package com.example.billit_all.application.data;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreferenceDataSource {
    private static UserSharedPreferenceDataSource instance = null;

    public static final String SHARED_PREFERENCE_NAME = "backend_user";
    public static final String USER_ID = "user_id";
    public static final String NAME = "user_name";
    private SharedPreferences sharedPreferences = null;

    public UserSharedPreferenceDataSource(SharedPreferences prefs) {
        this.sharedPreferences = prefs;
    }

    public static UserSharedPreferenceDataSource getInstance(Context applicationContext) {
        if(instance == null) {
            SharedPreferences prefs = applicationContext.getSharedPreferences( SHARED_PREFERENCE_NAME, applicationContext.MODE_PRIVATE);
            instance = new UserSharedPreferenceDataSource(prefs);
        }
        return instance;
    }

    public void storeBackendId(int user_id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_ID, user_id);
        editor.commit();
    }

    public void storeBackendName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.commit();
    }

    public int getBackendId() {
        int user_id = sharedPreferences.getInt(USER_ID, 1);
        return user_id;
    }

    public String getBackendName() {
        String name = sharedPreferences.getString(NAME, null);
        return name;
    }
}
