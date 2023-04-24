package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenchef.Procesos.Procesos;

import java.util.Calendar;

public class OptionsActivity extends AppCompatActivity {
    private ImageButton btnRecetas;
    private ImageButton btnMapa;
    private ImageButton btnPerfil;
    private TextView txtSaludo, txtSaludoUsuario;
    private Bundle bundle;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        txtSaludo = findViewById(R.id.saludo);
        txtSaludoUsuario = findViewById(R.id.saludoUsuario);

        saludarSegunHoraDelDia();

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");
        txtSaludoUsuario.setText("Hola " + nombreUsuario + "!");

        btnRecetas = this.findViewById(R.id.btnRecetas);
        btnRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, FoodActivity.class);
                OptionsActivity.this.startActivity(i);
            }
        });

        btnMapa = this.findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, MapsActivity.class);
                OptionsActivity.this.startActivity(i);
            }
        });

        btnPerfil = this.findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, ProfileActivity.class);
                OptionsActivity.this.startActivity(i);
            }
        });

    }

    public void saludarSegunHoraDelDia() {
        Calendar cal = Calendar.getInstance();
        int hora = cal.get(Calendar.HOUR_OF_DAY);

        if (hora >= 6 && hora < 12) {
            txtSaludo.setText("Buenos días!");
        } else if (hora >= 12 && hora < 20) {
            txtSaludo.setText("Buenas tardes!");
        } else {
            txtSaludo.setText("Buenas noches!");
        }
    }
}