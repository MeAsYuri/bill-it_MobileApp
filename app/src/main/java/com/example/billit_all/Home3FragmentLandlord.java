package com.example.billit_all;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.Response;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;

import org.json.JSONException;
import org.json.JSONObject;


public class Home3FragmentLandlord extends Fragment implements View.OnClickListener {

    ImageView addtenant, settings, notif;
    private WebView manilawaterOfficial;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home3_landlord, container, false);


        manilawaterOfficial=(WebView) view.findViewById(R.id.manilawaterOfficial);
//        webViewOfficial.setNestedScrollingEnabled(true);
//        webViewOfficial.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        manilawaterOfficial.setWebViewClient(new WebViewClient());

        manilawaterOfficial.loadUrl("https://www.manilawater.com/corporate/agos");
        WebSettings webSettings=manilawaterOfficial.getSettings();

        webSettings.setJavaScriptEnabled(true);

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
//                                        Fragment fragment = new NotificationsLandlord();
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
//
//        addtenant = view.findViewById(R.id.adduser);
//        addtenant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddTenant.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });

        return view;
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
}
