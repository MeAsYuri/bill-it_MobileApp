package com.example.billit_all.application.data;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppVolleyRequestQueue {

    private static RequestQueue instance;

    public static RequestQueue getInstance(Context appContext) {
        if(instance == null) {
            instance = Volley.newRequestQueue(appContext);
        }
        return instance;
    }
}
