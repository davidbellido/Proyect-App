package com.example.greenchef.admin_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.greenchef.login_register_activities.MainActivity;
import com.example.greenchef.MapsActivity;
import com.example.greenchef.R;

public class AdminActivity extends AppCompatActivity {
    private ImageButton btnlogOut;
    private Button btnMapAdmin, btnUpdateAdmin;
    private Bundle bundle, bundleMapa;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");

        bundleMapa = new Bundle();
        bundleMapa.putString("nombreUsuario", nombreUsuario);

        btnMapAdmin = this.findViewById(R.id.btnMapaAdmin);
        btnMapAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminActivity.this, MapsActivity.class);
                i.putExtras(bundleMapa);
                AdminActivity.this.startActivity(i);
            }
        });

        btnUpdateAdmin = this.findViewById(R.id.btnModificarAdmin);
        btnUpdateAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminActivity.this, ListProductActivity.class);
                AdminActivity.this.startActivity(i);
            }
        });

        btnlogOut = this.findViewById(R.id.btnLogOut);
        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se realiza la lógica de cierre de sesión y redirección
                logoutAdmin();
            }
        });

    }

    private void logoutAdmin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}