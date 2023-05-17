package com.example.billit_all.Calculate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.billit_all.R;

public class CalculateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
    }

    public void calculate_2(View v) {

        Intent b = new Intent(this, CalculateActivity2.class);
        startActivity(b);
        finish();
    }
}