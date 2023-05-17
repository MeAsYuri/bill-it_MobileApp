package com.example.billit_all;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeRole extends AppCompatActivity {

    Button landlordbtn, tenantbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_role);

        landlordbtn =(Button) findViewById(R.id.landlordbtn);
        landlordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLandlordbtn();
            }
        });

        tenantbtn = (Button) findViewById(R.id.tenantbtn);
        tenantbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTenantbtn();
            }
        });
    }

    public void openLandlordbtn(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void openTenantbtn(){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}