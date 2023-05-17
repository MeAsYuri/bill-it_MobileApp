package com.example.billit_all;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Help extends AppCompatActivity {

    TextView faqPassword, faqSubmeter, faqHistory, faqCalculate, faqPaymentOpt, faqGcash, faqPayStat;
    LinearLayout passLinear, submeterLinear, historyLinear, calculateLinear, payoptLinear, gcashLinear, paystatLinear;
    Button backSetButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        faqPassword = findViewById(R.id.faqPassword);
        passLinear = findViewById(R.id.passLinear);
        faqSubmeter = findViewById(R.id.faqSubmeter);
        submeterLinear = findViewById(R.id.submeterLinear);
        faqHistory = findViewById(R.id.faqHistory);
        historyLinear = findViewById(R.id.historyLinear);
        faqCalculate = findViewById(R.id.faqCalculate);
        calculateLinear = findViewById(R.id.calculateLinear);
        faqPaymentOpt = findViewById(R.id.faqPaymentOpt);
        payoptLinear = findViewById(R.id.payoptLinear);
        faqGcash = findViewById(R.id.faqGcash);
        gcashLinear = findViewById(R.id.gcashLinear);
        faqPayStat = findViewById(R.id.faqPayStat);
        paystatLinear = findViewById(R.id.paystatLinear);
        backSetButton = findViewById(R.id.backSetButton);

        passLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        submeterLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        historyLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        calculateLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        payoptLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        gcashLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        paystatLinear.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        backSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public void expandPassword(View view) {
        int password = (faqPassword.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
        TransitionManager.beginDelayedTransition(passLinear, new AutoTransition());
        faqPassword.setVisibility(password);
    }

    public void expandSubmeter(View view) {
        int submeter = (faqSubmeter.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(submeterLinear, new AutoTransition());
        faqSubmeter.setVisibility(submeter);
    }

    public void expandHistory(View view) {
        int history = (faqHistory.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(historyLinear, new AutoTransition());
        faqHistory.setVisibility(history);
    }

    public void expandCalculate(View view) {
        int calculate = (faqCalculate.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(calculateLinear, new AutoTransition());
        faqCalculate.setVisibility(calculate);
    }

    public void expandPayOpt(View view) {
        int payopt = (faqPaymentOpt.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(payoptLinear, new AutoTransition());
        faqPaymentOpt.setVisibility(payopt);
    }

    public void expandGcash(View view) {
        int gcash = (faqGcash.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(gcashLinear, new AutoTransition());
        faqGcash.setVisibility(gcash);
    }

    public void expandPayStat(View view) {
        int paystat = (faqPayStat.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(paystatLinear, new AutoTransition());
        faqPayStat.setVisibility(paystat);
    }
}