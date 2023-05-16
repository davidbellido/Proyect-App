package com.example.greenchef.foodactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.greenchef.R;

public class DairyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dairy);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }
    }
}