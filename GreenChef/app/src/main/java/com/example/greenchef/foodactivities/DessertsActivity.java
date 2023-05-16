package com.example.greenchef.foodactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.greenchef.R;

public class DessertsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desserts);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }
    }
}