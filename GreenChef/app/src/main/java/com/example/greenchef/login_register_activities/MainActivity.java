package com.example.greenchef.login_register_activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.greenchef.OptionsActivity;
import com.example.greenchef.R;
import com.example.greenchef.admin_activities.AdminActivity;

import org.bson.Document;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;


public class MainActivity extends AppCompatActivity {
    private TextView txtSignUp;
    private EditText username;
    private EditText password;
    private Button btnInicioSesion;
    private Realm realm;
    private Bundle bundle;
    String AppId = "pruebaproyecto-urnlx";
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.Username);
        password = findViewById(R.id.password);
        txtSignUp = findViewById(R.id.txtRegistro);

        txtSignUp.setOnClickListener(onClickSignUp());


        btnInicioSesion = this.findViewById(R.id.btnInicioSesion);
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = username.getText().toString();
                bundle = new Bundle();
                bundle.putString("nombreUsuario", usuario);

                String contrasenia = password.getText().toString();

                if (usuario.equals("admin")){
                    loginAdmins(usuario,contrasenia);
                }else
                    loginUsers(usuario,contrasenia);


            }
        });


        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }
    }

    private void loginUsers(String usuario,String contrasenia) {
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

                    Document query = new Document("nick", usuario).append("password", contrasenia);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {
                            Document usuario = result1.get();
                            if (usuario != null) {
                                Intent i = new Intent(MainActivity.this, OptionsActivity.class);
                                i.putExtras(bundle);
                                MainActivity.this.startActivity(i);
                            } else {

                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Usuario o contraseña incorrectos")
                                        .show();
                            }
                        } else {
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Error al buscar usuario")
                                    .show();
                        }
                    });
                } else {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error al conectar con la base de datos")
                            .show();
                }
            }
        });
    }

    private void loginAdmins(String usuario,String contrasenia) {
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
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Admin");

                    Document query = new Document("log", usuario).append("password", contrasenia);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {
                            Document usuario = result1.get();
                            if (usuario != null) {
                                Intent i = new Intent(MainActivity.this, AdminActivity.class);
                                i.putExtras(bundle);
                                MainActivity.this.startActivity(i);
                            } else {
                                // Crear un AlertDialog.Builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                // Establecer el título y el mensaje del diálogo
                                builder.setTitle("Error en el Login").setMessage("Lo siento, no tienes permiso de admin o la contraseña es incorrecta.");

                                // Añadir un botón "Aceptar" al diálogo
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // Cerrar el diálogo
                                        dialog.dismiss();
                                    }
                                });

                                // Crear el AlertDialog y mostrarlo
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error al buscar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private View.OnClickListener onClickSignUp(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(i);
            }
        };
    }
}

