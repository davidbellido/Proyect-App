package com.example.greenchef.admin_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
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
        // Obtener el objeto Configuration
        Configuration configuration = getResources().getConfiguration();

        // Verificar si el dispositivo es una tablet según el tamaño de pantalla
        boolean isTablet = (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        if (isTablet) {
            // El dispositivo es una tablet
            // Cargamos el diseño para tablets
            setContentView(R.layout.activity_admin_tablet);
        } else {
            // El dispositivo es un móvil
            // Cargamos el diseño para móviles
            setContentView(R.layout.activity_admin);
        }

        //setContentView(R.layout.activity_admin);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            // Manejar cualquier excepción que ocurra al ocultar la barra de acción
        }

        // Obtener el nombre de usuario de la actividad anterior a través del objeto Bundle
        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");

        // Crear un nuevo objeto Bundle para pasar datos a la actividad de mapas
        bundleMapa = new Bundle();
        bundleMapa.putString("nombreUsuario", nombreUsuario);

        // Configurar el botón de mapa de administrador
        btnMapAdmin = this.findViewById(R.id.btnMapaAdmin);
        btnMapAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de mapas y pasar el objeto Bundle
                Intent i = new Intent(AdminActivity.this, MapsActivity.class);
                i.putExtras(bundleMapa);
                AdminActivity.this.startActivity(i);
            }
        });

        // Configurar el botón de actualización de administrador
        btnUpdateAdmin = this.findViewById(R.id.btnModificarAdmin);
        btnUpdateAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de lista de productos
                Intent i = new Intent(AdminActivity.this, ListProductActivity.class);
                AdminActivity.this.startActivity(i);
            }
        });

        // Configurar el botón de cierre de sesión de administrador
        btnlogOut = this.findViewById(R.id.btnLogOut);
        btnlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Realizar la lógica de cierre de sesión y redirección
                logoutAdmin();
            }
        });
    }

    // Método para realizar el cierre de sesión del administrador
    private void logoutAdmin() {
        // Crear un intento para iniciar la actividad de inicio de sesión
        Intent intent = new Intent(this, MainActivity.class);
        // Establecer las banderas para borrar la pila de actividades y crear una nueva tarea
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Iniciar la actividad de inicio de sesión y finalizar la actividad actual
        startActivity(intent);
        finish();
    }
}
