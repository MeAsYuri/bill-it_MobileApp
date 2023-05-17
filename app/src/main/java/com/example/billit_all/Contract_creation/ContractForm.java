package com.example.billit_all.Contract_creation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.billit_all.Calculate.CalculateActivity2;
import com.example.billit_all.R;
import com.example.billit_all.TemporaryLogin;

import java.util.ArrayList;

public class ContractForm extends AppCompatActivity {


    Button cancelBtn, proceedBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_form);


        cancelBtn = findViewById(R.id.cancelBtn);
        proceedBtn = findViewById(R.id.proceedBtn);



        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent b = new Intent(ContractForm.this, TemporaryLogin.class);
                startActivity(b);
            }
        });

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent c = new Intent(ContractForm.this, ContractSpecs.class);
                startActivity(c);
            }
        });

    }


}