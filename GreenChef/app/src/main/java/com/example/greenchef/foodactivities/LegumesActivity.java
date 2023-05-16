package com.example.greenchef.foodactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.greenchef.R;

public class LegumesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legumes);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }
    }
}