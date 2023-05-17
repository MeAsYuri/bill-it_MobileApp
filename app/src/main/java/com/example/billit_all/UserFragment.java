package com.example.billit_all;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.billit_all.Bill_history.BillsTenant;
import com.example.billit_all.Payment.PaymentElectricity;
import com.example.billit_all.application.data.AppVolleyRequestQueue;
import com.example.billit_all.application.data.BackendJsonObjectRequest;
import com.example.billit_all.application.data.BillBackendDataResource;
import com.example.billit_all.application.data.LoginPreferenceDataSource;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class UserFragment extends Fragment {

    ImageView settings, notif;
    Button logoutBtn, proceedBtn, editProf, addGcash, Bills;
    CircularImageView profileImage;
    TextView topNameTextView, nameTextView, emailTextView,usernameTextView,contactTextView;

    private ImageLoader imageLoader;

    @SuppressLint("MissingInflatedId")


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        logoutBtn = view.findViewById(R.id.logoutBtn);
        editProf = view.findViewById(R.id.editProf);
        profileImage = view.findViewById(R.id.profile_image);
        topNameTextView = view.findViewById(R.id.topName);
        nameTextView = view.findViewById(R.id.name);
        emailTextView = view.findViewById(R.id.email);
        usernameTextView = view.findViewById(R.id.username);
        contactTextView = view.findViewById(R.id.contact);
        addGcash = view.findViewById(R.id.addGcash);


        addGcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
                LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//                RequestQueue requestQueue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
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
                                        String role = user.getString("role");
                                        if (role.equals("Tenant") && role == "Tenant") {
                                            addGcash.setVisibility(View.INVISIBLE);
                                        } else {
                                            RequestQueue queue = AppVolleyRequestQueue.getInstance(getActivity().getApplicationContext());
                                            BackendJsonObjectRequest request = new BackendJsonObjectRequest(
                                                    Request.Method.GET,
                                                    "/api/gcash-qr/qr/" + id, null,
                                                    new Response.Listener<JSONObject>() {
                                                        @Override
                                                        public void onResponse(JSONObject response) {
                                                            try {
//                                                                JSONObject qrCode = response.getJSONObject("hasQr");
                                                                Object hasQr = response.get("hasQr");
                                                                if (hasQr instanceof Boolean && !(Boolean) hasQr) {
                                                                    // hasQr is false
                                                                    Intent intent = new Intent(getActivity().getApplicationContext(), AddGcash.class);
                                                                    intent.putExtra("id", id);
                                                                    startActivity(intent);
                                                                } else if (hasQr instanceof JSONObject) {
                                                                    // hasQr is true
                                                                    JSONObject qrObject = (JSONObject) hasQr;
                                                                    String qr = qrObject.getString("qr");
                                                                    // Extract other values as needed
                                                                    Intent intent = new Intent(getActivity().getApplicationContext(), EditGcash.class);
                                                                    intent.putExtra("id", id);
                                                                    intent.putExtra("qr", qr);
                                                                    startActivity(intent);
                                                                } else {
                                                                    // Unexpected response format
                                                                    Log.e(TAG, "Unexpected response format: " + response.toString());
                                                                }
//                                                                String hasQr = response.getString("hasQr");
//                                                                if (hasQr.equals("true")) {
//                                                                    Intent intent = new Intent(getActivity().getApplicationContext(), EditGcash.class);
//                                                                    intent.putExtra("id", id);
//                                                                    startActivity(intent);
//                                                                } else {
//                                                                    Intent intent = new Intent(getActivity().getApplicationContext(), AddGcash.class);
//                                                                    intent.putExtra("id", id);
//                                                                    startActivity(intent);
//                                                                }

                                                            } catch (JSONException e) {
//                                                                Toast.makeText(Login.this, "Please check your credentials", Toast.LENGTH_LONG).show();
//                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
//                                                            Toast.makeText(Login.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                            );
                                            queue.add(request);
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
        });


        imageLoader = new ImageLoader(Volley.newRequestQueue(getActivity().getApplicationContext()),
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

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
//                                        if (role.equals("Tenant")) {
//                                            Fragment fragment = new NotificationsTenant();
//                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                            transaction.replace(R.id.container, fragment);
//                                            transaction.addToBackStack(null);
//                                            transaction.commit();
//                                        } else {
//                                            Fragment fragment = new NotificationsLandlord();
//                                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                                            transaction.replace(R.id.container, fragment);
//                                            transaction.addToBackStack(null);
//                                            transaction.commit();
//                                        }
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

        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        String role = user.getString("role");
                                        Log.d("USERS_EDIT", String.valueOf(user));
                                        if (role.equals("Landlord")) {
                                            Intent intent = new Intent(getActivity(), EditProfileLandlord.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(getActivity(), EditProfileTenant.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
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
        });
        retrieveProfile();
//        BillBackendDataResource userDataSource = BillBackendDataResource.getInstance(getActivity().getApplicationContext());
//        LoginPreferenceDataSource loginPreferenceDataSource = LoginPreferenceDataSource.getInstance(getActivity().getApplicationContext());
//        String backendToken = loginPreferenceDataSource.getBackendToken();
//        try {
//            userDataSource.fetchUserFromBackend(
//                    backendToken,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject user = response.getJSONObject("user");
//                                String role = user.getString("role");
//                                Log.d("USERS_EDIT", String.valueOf(user));
//                                if (role.equals("Tenant")) {
//                                    Bills.setVisibility(View.VISIBLE);
//                                } else {
//                                    Bills.setVisibility(View.GONE);
//
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    },
//                    null
//            );
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        Bills.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentBills = new Intent(getActivity(), BillsTenant.class);
//                intentBills.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intentBills);
//            }
//        });
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
                                String name = user.getString("name");
                                topNameTextView.setText(name);
                                nameTextView.setText(name);
                                String email = user.getString("email");
                                emailTextView.setText(email);
                                String username = user.getString("username");
                                usernameTextView.setText(username);
                                String phone = user.getString("phone");
                                contactTextView.setText(phone);
                                String role = user.getString("role");
                                if (role.equals("Tenant")) {
                                    addGcash.setVisibility(View.GONE);
                                } else {
                                    addGcash.setVisibility(View.VISIBLE);
                                }

                                String imageUrl = user.getString("profile");
                                Log.d("IMAGE_URL", imageUrl);
                                imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                                    @Override
                                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                        Bitmap bitmap = response.getBitmap();
                                        if (bitmap != null) {
                                            profileImage.setImageBitmap(bitmap);
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Handle errors
                                    }
                                });

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

}