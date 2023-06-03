package com.example.greenchef.login_register_activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
        // Obtener el objeto Configuration
        Configuration configuration = getResources().getConfiguration();

        // Verificar si el dispositivo es una tablet según el tamaño de pantalla
        boolean isTablet = (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

        if (isTablet) {
            // El dispositivo es una tablet
            // Cargamos el diseño para tablets
            setContentView(R.layout.activity_main_tablet);
        } else {
            // El dispositivo es un móvil
            // Cargar el diseño para móviles
            setContentView(R.layout.activity_main);
        }

        // Inicialización de los elementos de la interfaz de usuario
        username = findViewById(R.id.Username);
        password = findViewById(R.id.password);
        txtSignUp = findViewById(R.id.txtRegistro);
        btnInicioSesion = this.findViewById(R.id.btnInicioSesion);

        // Configuración de los eventos de clic en los botones
        txtSignUp.setOnClickListener(onClickSignUp());
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = username.getText().toString();
                bundle = new Bundle();
                bundle.putString("nombreUsuario", usuario);

                String contrasenia = password.getText().toString();

                if (usuario.equals("admin")) {
                    loginAdmins(usuario, contrasenia);
                } else
                    loginUsers(usuario, contrasenia);
            }
        });

        // Ocultar la barra de acción
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
        }
    }

    // Método para el inicio de sesión de usuarios normales
    private void loginUsers(String usuario, String contrasenia) {
        // Inicialización de Realm y la aplicación de MongoDB
        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(AppId).build());

        // Inicio de sesión anónimo en MongoDB
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    // Obtención del cliente y la base de datos de MongoDB
                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("GreenChef");
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Users");

                    // Consulta para buscar al usuario en la base de datos
                    Document query = new Document("nick", usuario).append("password", contrasenia);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {
                            Document usuario = result1.get();
                            if (usuario != null) {
                                // Si el usuario existe, se inicia la actividad OptionsActivity
                                Intent i = new Intent(MainActivity.this, OptionsActivity.class);
                                i.putExtras(bundle);
                                MainActivity.this.startActivity(i);
                            } else {
                                // Si el usuario no existe, se muestra un mensaje de error
                                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Usuario o contraseña incorrectos")
                                        .show();
                            }
                        } else {
                            // Si hay un error en la búsqueda del usuario, se muestra un mensaje de error
                            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Error al buscar usuario")
                                    .show();
                        }
                    });
                } else {
                    // Si hay un error en la conexión con la base de datos, se muestra un mensaje de error
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error al conectar con la base de datos")
                            .show();
                }
            }
        });
    }

    // Método para el inicio de sesión de administradores
    private void loginAdmins(String usuario, String contrasenia) {
        // Inicialización de Realm y la aplicación de MongoDB
        Realm.init(this);
        App app = new App(new AppConfiguration.Builder(AppId).build());

        // Inicio de sesión anónimo en MongoDB
        Credentials credentials = Credentials.anonymous();
        app.loginAsync(credentials, new App.Callback<User>() {
            @Override
            public void onResult(App.Result<User> result) {
                if (result.isSuccess()) {
                    // Obtención del cliente y la base de datos de MongoDB
                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("GreenChef");
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Admin");

                    // Consulta para buscar al administrador en la base de datos
                    Document query = new Document("log", usuario).append("password", contrasenia);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {
                            Document usuario = result1.get();
                            if (usuario != null) {
                                // Si el administrador existe, se inicia la actividad AdminActivity
                                Intent i = new Intent(MainActivity.this, AdminActivity.class);
                                i.putExtras(bundle);
                                MainActivity.this.startActivity(i);
                            } else {
                                // Si el administrador no existe, se muestra un mensaje de error
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Error en el Login").setMessage("Lo siento, no tienes permiso de admin o la contraseña es incorrecta.");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
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

    // Método para manejar el evento de clic en el enlace de registro
    private View.OnClickListener onClickSignUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad RegisterActivity al hacer clic en el enlace de registro
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                MainActivity.this.startActivity(i);
            }
        };
    }
}
