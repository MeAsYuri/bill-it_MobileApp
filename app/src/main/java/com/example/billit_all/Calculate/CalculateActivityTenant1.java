package com.example.billit_all.Calculate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.billit_all.R;

public class CalculateActivityTenant1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_tenant1);
    }
    public void calculate_3_tenant(View v) {

        Intent c = new Intent(this, CalculateActivityTenant3.class);
        startActivity(c);
        finish();
    }

    public void calculate_4_tenant(View v) {

        Intent d = new Intent(this, CalculateActivityTenant4.class);
        startActivity(d);
        finish();
    }
}