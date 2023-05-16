package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.greenchef.foodactivities.CerealActivity;
import com.example.greenchef.foodactivities.DairyActivity;
import com.example.greenchef.foodactivities.DessertsActivity;
import com.example.greenchef.foodactivities.DriedFruitsActivity;
import com.example.greenchef.foodactivities.FruitsActivity;
import com.example.greenchef.foodactivities.GrainsActivity;
import com.example.greenchef.foodactivities.LegumesActivity;
import com.example.greenchef.foodactivities.ProteinActivity;
import com.example.greenchef.foodactivities.VegetablesActivity;

public class FoodActivity extends AppCompatActivity {
    private ImageButton btnProtein, btnVegetables,btnCereal, btnFruits, btnGrains, btnDairy, btnDriedFruits, btndesserts, btnLegumes;
    private ImageButton btnMap, btnHome, btnProfile;
    private Bundle bundle;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){
        }

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");
        bundle.putString("nombreUsuario", nombreUsuario);

        btnProtein = this.findViewById(R.id.btnProteina);
        btnVegetables = this.findViewById(R.id.btnVerduras);
        btnCereal = this.findViewById(R.id.btnCereales);
        btnFruits = this.findViewById(R.id.btnFrutas);
        btnGrains = this.findViewById(R.id.btnGranos);
        btnDairy = this.findViewById(R.id.btnLacteos);
        btnDriedFruits = this.findViewById(R.id.btnFsecos);
        btndesserts = this.findViewById(R.id.btnPostres);
        btnLegumes = this.findViewById(R.id.btnLegumbres);

        btnMap = this.findViewById(R.id.btnMap);
        btnHome = this.findViewById(R.id.btnHome);
        btnProfile = this.findViewById(R.id.btnUsuario);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, MapsActivity.class);
                i.putExtras(bundle);
                FoodActivity.this.startActivity(i);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, OptionsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, ProfileActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnProtein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, ProteinActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, VegetablesActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnCereal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, CerealActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, FruitsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnGrains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, GrainsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnDairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DairyActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnDriedFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DriedFruitsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btndesserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DessertsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnLegumes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, LegumesActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });
    }

}