package com.example.apptenants;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CalculateActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate1);


    }

    public void calculate_3(View v) {

        Intent c = new Intent(this, CalculateActivity3.class);
        startActivity(c);
        finish();
    }

    public void calculate_4(View v) {

        Intent d = new Intent(this, CalculateActivity4.class);
        startActivity(d);
        finish();
    }
}