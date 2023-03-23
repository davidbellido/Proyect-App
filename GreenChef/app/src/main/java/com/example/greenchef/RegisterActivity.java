package com.example.greenchef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenchef.Procesos.Procesos;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtLogin;
    private EditText username;
    private EditText correo;
    private EditText password;
    private Button btnInicioSesion;
    Procesos proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.password);
        correo = findViewById(R.id.email);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        btnInicioSesion = this.findViewById(R.id.btnInicioSesion);
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = username.getText().toString();
                String contrasenia = password.getText().toString();
                String email = correo.getText().toString();

                proceso = new Procesos();

                proceso.guardarUsuario(usuario,email,contrasenia);
            }
        });

        txtLogin = findViewById(R.id.txtLogin);

        txtLogin.setOnClickListener(onClickSignUp());
    }

    private View.OnClickListener onClickSignUp(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterActivity.this.finish();
            }
        };
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("GreenChef")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
