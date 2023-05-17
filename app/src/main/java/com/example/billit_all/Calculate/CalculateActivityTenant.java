package com.example.billit_all.Calculate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.billit_all.R;

public class CalculateActivityTenant extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tenant);
    }
    public void calculate_2_tenant(View v) {

        Intent b = new Intent(this, CalculateActivityTenant2.class);
        startActivity(b);
        finish();
    }
}