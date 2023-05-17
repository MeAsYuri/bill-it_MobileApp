package com.example.apptenants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private FirebaseUser tenant;
    private DatabaseReference reference;

    private String tenantID;

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
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        tenant = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Tenants");
        tenantID = tenant.getUid();

        final TextView houseTextView = (TextView) findViewById(R.id.houseNum);
        final TextView nameTextView = (TextView) findViewById(R.id.name);
        final TextView emailTextView = (TextView) findViewById(R.id.email);
        final TextView contactTextView = (TextView) findViewById(R.id.contact);

        reference.child(tenantID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Tenant tenantProfile = snapshot.getValue(Tenant.class);

                if(tenantProfile != null){
                    String housenum = tenantProfile.houseTenant;
                    String name = tenantProfile.nameTenant;
                    String email = tenantProfile.emailTenant;
                    String contact = tenantProfile.contactTenant;
//                    String image = userProfile.image;

//                    greetingTextView.setText("Welcome, " + name + "!");
                    houseTextView.setText(housenum);
                    nameTextView.setText(name);
                    emailTextView.setText(email);
                    contactTextView.setText(contact);

                    if(snapshot.hasChild("image"))
                    {
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }
}