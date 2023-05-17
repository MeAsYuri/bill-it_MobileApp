package com.example.billit_all;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.billit_all.Calculate.ElectricityCalculate;
import com.example.billit_all.Calculate.WaterCalculate;

public class CalculateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculate, container, false);
    }

    public void electric(View view){
        //instantiate
        Intent v = new Intent(getContext(), ElectricityCalculate.class);
        //start the another activity
        startActivity(v);

    }
    public void water(View view){
        //instantiate
        Intent i = new Intent(getContext(), WaterCalculate.class);
        //start the another activity
        startActivity(i);

    }
}