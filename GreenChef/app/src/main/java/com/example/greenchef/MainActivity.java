package com.example.greenchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView txtSignUp;
    private TextView txtSignUp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        txtSignUp = findViewById(R.id.txtRegistro);
        txtSignUp2 = findViewById(R.id.txtRegistro2);

        txtSignUp.setOnClickListener(onClickSignUp());
        txtSignUp2.setOnClickListener(onClickSignUp());
    }

    private View.OnClickListener onClickSignUp(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                MainActivity.this.startActivity(i);
            }
        };
    }
}
