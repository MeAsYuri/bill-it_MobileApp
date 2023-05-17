package com.example.billit_all;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.example.billit_all.Bill_history.Receipt;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;


public class HomeFragmentTenant extends Fragment implements View.OnClickListener{

    ImageView settings, notif, concerns;
    private WebView webViewOfficial;
    private ViewPager2 viewPager2;
    ArrayList<AnnouncementModel> announcementModelArrayList = new ArrayList<>();
    AnnouncementAdapter announcementAdapter;
    private Timer autoSwipeTimer;
    private AutoSwipeTask autoSwipeTask;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home_tenant, container, false);


        webViewOfficial=(WebView) view.findViewById(R.id.webViewOfficial);

        webViewOfficial.setWebViewClient(new WebViewClient());

        webViewOfficial.loadUrl("https://company.meralco.com.ph/news-and-advisories/latest-news-and-press-releases");
        WebSettings webSettings=webViewOfficial.getSettings();

        webSettings.setJavaScriptEnabled(true);

        viewPager2 = view.findViewById(R.id.viewPager);


        retrieveAnnouncement();

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

    public class mywebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view,url,favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            return true;
        }
    }

    //        String[] title = {"Rent Status", "Water Interruption", "Power Outage", "Due Date"};
//        String[] subject = {"I'll be adding an increase on rent",
//                "Will happen later, 5pm-8pm", "Brown out tomorrow", "Please pay your bills"};
////        String[] subject = {getString(R.string.subject)};
//        announcementModelArrayList = new ArrayList<>();
//
//        for (int i=0; i<title.length; i++){
//            AnnouncementModel announcementModel = new AnnouncementModel(title[i],subject[i]);
//            announcementModelArrayList.add(announcementModel);
//
//        }
//
//        AnnouncementAdapter announcementAdapter = new AnnouncementAdapter(announcementModelArrayList);
//        viewPager2.setAdapter(announcementAdapter);
//        viewPager2.setClipToPadding(false);
//        viewPager2.setClipChildren(false);
//        viewPager2.setOffscreenPageLimit(2);
//        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
}