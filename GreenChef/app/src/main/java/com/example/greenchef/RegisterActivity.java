package com.example.greenchef;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtLogin;
    private EditText username;
    private EditText correo;
    private EditText password;
    private Button btnInicioSesion;
    String AppId = "pruebaproyecto-urnlx";
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.password);
        correo = findViewById(R.id.email);

        // Creamos un objeto Date con la fecha y hora actuales
        Date fechaActual = new Date();

        // Creamos un objeto SimpleDateFormat para dar formato a la fecha
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

        // Obtenemos la fecha formateada como un string
        String fechaRegistro = formatoFecha.format(fechaActual);

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
                String fecha = fechaRegistro.toString();

                registerUsers(usuario,email,contrasenia, fecha);
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

    private void registerUsers(String nick, String email, String contrasenia, String fechaRegistro) {
        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(AppId).build());

        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {

                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("GreenChef");
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Users");

                    Document usuario = new Document();
                    usuario.append("id_usuario", 4)
                            .append("nick", nick)
                            .append("email", email)
                            .append("password", contrasenia)
                            .append("fecharegistro", fechaRegistro)
                            .append("nombre", "")
                            .append("apellidos", "")
                            .append("telefono", "");

                    mongoCollection.insertOne(usuario).getAsync(result1 -> {
                        if (result1.isSuccess()) {
                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("¡Registro exitoso!")
                                    .setContentText("¡Bienvenido a GreenChef!")
                                    .setConfirmText("Volver")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            RegisterActivity.this.finish();
                                        }
                                    })
                                    .show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "No insertado", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "No conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        }
    }
