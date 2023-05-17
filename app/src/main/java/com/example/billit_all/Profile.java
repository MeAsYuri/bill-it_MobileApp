package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class Profile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private Button logoutBtn, proceedBtn, editProf;
    private CircularImageView profileImage;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        editProf = (Button) findViewById(R.id.editProf);
        profileImage = findViewById(R.id.profile_image);
//        proceedBtn = (Button) findViewById(R.id.proceedBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(Profile.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        editProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, EditProfileLandlord.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

//        user = FirebaseAuth.getInstance().getCurrentUser();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        userID = user.getUid();
//
////        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);
//        final TextView topNameTextView = (TextView) findViewById(R.id.topName);
//        final TextView nameTextView = (TextView) findViewById(R.id.name);
//        final TextView emailTextView = (TextView) findViewById(R.id.email);
//        final TextView contactTextView = (TextView) findViewById(R.id.contact);
//
//        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User userProfile = snapshot.getValue(User.class);
//
//                if(userProfile != null){
//                    String name = userProfile.name;
//                    String email = userProfile.email;
//                    String contact = userProfile.phone;
////                    String image = userProfile.image;
//
////                    greetingTextView.setText("Welcome, " + name + "!");
//                    topNameTextView.setText(name);
//                    nameTextView.setText(name);
//                    emailTextView.setText(email);
//                    contactTextView.setText(contact);
//
//                    if(snapshot.hasChild("image"))
//                    {
//                        String image = snapshot.child("image").getValue().toString();
//                        Picasso.get().load(image).into(profileImage);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//
//            }
//        });

//        JSONObject form = new JSONObject();
//        try {
//            form.put("email", email);
//            form.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestQueue queue = AppVolleyRequestQueue.getInstance(getApplicationContext());
//        BackendJsonObjectRequest request = new BackendJsonObjectRequest(
//                Request.Method.POST,
//                "/api/tokens/create", form,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String token = response.getString("token");
//                            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("token", token);
//                            editor.commit();
////                            startActivity(new Intent(Profile.this, DashboardLandlord.class));
//                        } catch (JSONException e) {
//
////                            progressBar.setVisibility(View.INVISIBLE);
//                            e.printStackTrace();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
////                        progressBar.setVisibility(View.INVISIBLE);
//
//                    }
//                }
//        );
//        //add this line below if needed for authentication
//        request.authenticated(getApplicationContext());
//        queue.add(request);
    }

}
