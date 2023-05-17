package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VerifySuccessPage extends AppCompatActivity {

    TextView message, messageTitle;
    ImageView logo;
    Button confirmBtn;

    Animation Anim_top, Anim_bottom, Anim_logo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_success_page);

        message = findViewById(R.id.messageBody);
        messageTitle = findViewById(R.id.messageTitle);
        logo = findViewById(R.id.logo);
        confirmBtn = findViewById(R.id.confirmBtn);

        Anim_logo = AnimationUtils.loadAnimation(this,R.anim.logo_anim);
        Anim_top = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        Anim_bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logo.setAnimation(Anim_logo);
        messageTitle.setAnimation(Anim_top);
        message.setAnimation(Anim_bottom);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerifySuccessPage.this, DashboardLandlord.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        // do nothing
    }

}