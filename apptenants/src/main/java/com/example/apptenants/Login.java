package com.example.apptenants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText emailTxt, passwordTxt;
    private Button loginBtn;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        emailTxt = (EditText) findViewById(R.id.email);
        passwordTxt = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                userLogin();
                break;

        }
    }

    private void userLogin(){

        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if(email.isEmpty()){
            emailTxt.setError("Email address is required");
            emailTxt.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTxt.setError("Email is not valid!");
            emailTxt.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordTxt.setError("Password is required!");
            passwordTxt.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordTxt.setError("Invalid! Password is too short");
            passwordTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    //EMAIL VERIFICATION IF USER IS VERIFIED
                    if(user.isEmailVerified()){
                        startActivity(new Intent(Login.this, DashboardTenant.class));

                    }
                    else{
                        user.sendEmailVerification();
                        Toast.makeText(Login.this, "PLEASE CHECK YOUR EMAIL TO VERIFY ACCOUNT!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }
                else {
                    Toast.makeText(Login.this, "Login Failed! Please check your credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}