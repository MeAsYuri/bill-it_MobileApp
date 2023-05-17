package com.example.billit_all;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

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

import java.util.ArrayList;
import java.util.Timer;


public class Home3FragmentTenant extends Fragment implements View.OnClickListener {

    ImageView settings, notif;
    private WebView manilawaterOfficial;
    private ViewPager2 viewPager2;
    ArrayList<AnnouncementModel> announcementModelArrayList;
    private Timer autoSwipeTimer;
    private AutoSwipeTask autoSwipeTask;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home3_tenant, container, false);


        manilawaterOfficial = (WebView) view.findViewById(R.id.manilawaterOfficial);
//        webViewOfficial.setNestedScrollingEnabled(true);
//        webViewOfficial.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        manilawaterOfficial.setWebViewClient(new WebViewClient());

        manilawaterOfficial.loadUrl("https://www.manilawater.com/corporate/agos");
        WebSettings webSettings = manilawaterOfficial.getSettings();

        webSettings.setJavaScriptEnabled(true);

        viewPager2 = view.findViewById(R.id.viewPager);


        retrieveAnnouncement();


//        notif = view.findViewById(R.id.notif);
//        notif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//                String backendToken = loginPreferenceDataSource.getBackendToken();
//                try {
//                    userDataSource.fetchUserFromBackend(
//                            backendToken,
//                            new Response.Listener<JSONObject>() {
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    try {
//                                        JSONObject user = response.getJSONObject("user");
//                                        String role = user.getString("role");
//
//                                        Fragment fragment = new NotificationsTenant();
//                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                        transaction.replace(R.id.container, fragment);
//                                        transaction.addToBackStack(null);
//                                        transaction.commit();
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            },
//                            null
//                    );
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//        settings = view.findViewById(R.id.settings);
//        settings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), Settings.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });


        return view;
    }

    public void retrieveAnnouncement() {
        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
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

                                RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                        Request.Method.GET,
                                        "/api/announcements/all/" + id,
                                        null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    JSONArray announcementsArray = response.getJSONArray("data");

                                                    if (announcementsArray == null || announcementsArray.length() == 0) {
                                                        viewPager2.setVisibility(View.GONE);
                                                        if (autoSwipeTimer != null) {
                                                            autoSwipeTimer.cancel();
                                                        }
                                                        if (autoSwipeTask != null) {
                                                            autoSwipeTask.cancel();
                                                        }
                                                    } else {
                                                        viewPager2.setVisibility(View.VISIBLE);
                                                        autoSwipeTimer = new Timer();
                                                        autoSwipeTask = new AutoSwipeTask(viewPager2);
                                                        autoSwipeTimer.schedule(autoSwipeTask, 3000, 3000); // Delay of 3000ms (3 seconds) and repeat every 3000ms
                                                    }

                                                    String[] title = new String[announcementsArray.length()];
                                                    String[] subject = new String[announcementsArray.length()];

                                                    for (int i = 0; i < announcementsArray.length(); i++) {
                                                        JSONObject announcementObject = announcementsArray.getJSONObject(i);
                                                        title[i] = announcementObject.getString("subject");
                                                        subject[i] = announcementObject.getString("description");
                                                    }
                                                    announcementModelArrayList = new ArrayList<>();

                                                    for (int i = 0; i < title.length; i++) {
                                                        AnnouncementModel announcementModel = new AnnouncementModel(title[i], subject[i]);
                                                        announcementModelArrayList.add(announcementModel);
                                                    }

                                                    AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(announcementModelArrayList);
                                                    viewPager2.setAdapter(announcementAdapter);
                                                    viewPager2.setClipToPadding(false);
                                                    viewPager2.setClipChildren(false);
                                                    viewPager2.setOffscreenPageLimit(2);
                                                    viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
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
    public void onClick(View v) {

    }

    public class mywebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}