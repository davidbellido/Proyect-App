package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

    }
}