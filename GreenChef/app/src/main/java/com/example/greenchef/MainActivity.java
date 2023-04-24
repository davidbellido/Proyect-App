package com.example.greenchef;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenchef.Model.Usuarios;
import com.example.greenchef.Procesos.Procesos;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    private TextView txtSignUp;
    private TextView txtSignUp2;
    private EditText username;
    private EditText password;
    private Button btnInicioSesion;
    private Realm realm;
    private boolean contraseniaCorrecta, existeUsuario;
    private Bundle bundle;
    Procesos proceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.password);
        txtSignUp = findViewById(R.id.txtRegistro);
        txtSignUp2 = findViewById(R.id.txtRegistro2);

        txtSignUp.setOnClickListener(onClickSignUp());
        txtSignUp2.setOnClickListener(onClickSignUp());

        initRealm();

        btnInicioSesion = this.findViewById(R.id.btnInicioSesion);
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = username.getText().toString();
                bundle = new Bundle();
                bundle.putString("nombreUsuario", usuario);

                String contrasenia = password.getText().toString();
                proceso = new Procesos();

                existeUsuario = proceso.existeUsuario(usuario);
                if (existeUsuario){
                    contraseniaCorrecta = proceso.comprobarPassword(usuario, contrasenia);
                    if (contraseniaCorrecta){
                        Intent i = new Intent(MainActivity.this,OptionsActivity.class);
                        i.putExtras(bundle);
                        MainActivity.this.startActivity(i);
                    }else {
                        Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getApplicationContext(), "Usuario existe", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getApplicationContext(), "Usuario no existe", Toast.LENGTH_SHORT).show();

            }
        });

        //realm.close();

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }
    }

    /*private class LoginTask extends AsyncTask<String, Usuarios, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            proceso = new Procesos();

            existeUsuario = proceso.existeUsuario(strings[0]);
            if (existeUsuario){
                Toast.makeText(getApplicationContext(), "Usuario existe", Toast.LENGTH_SHORT).show();
            }else
                Toast.makeText(getApplicationContext(), "Usuario no existe", Toast.LENGTH_SHORT).show();

            return null;
        }
    }*/

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name("GreenChef")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
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

