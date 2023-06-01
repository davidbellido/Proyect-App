package com.example.greenchef.login_register_activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.greenchef.R;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class RegisterActivity extends AppCompatActivity {
    private TextView txtLogin;
    private EditText username;
    private EditText correo;
    private EditText password;
    private Button btnInicioSesion;
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private boolean existeUsuario ;
    private int id;

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
            setContentView(R.layout.activity_register_tablet);
        } else {
            // El dispositivo es un móvil
            // Cargamos el diseño para móviles
            setContentView(R.layout.activity_register);
        }

        //setContentView(R.layout.activity_register);

        username = this.findViewById(R.id.Username);
        password = this.findViewById(R.id.password);
        correo = this.findViewById(R.id.email);

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

                if (usuario.isEmpty() || contrasenia.isEmpty() || email.isEmpty()){
                    new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Rellena todos los campos")
                            .show();
                    return;
                }else
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

                    //Llamamos al metodo para recuperar el ultimo id de la coleccion de Users
                    recuperarUltimoId();

                    User user = app.currentUser();
                    mongoClient = user.getMongoClient("mongodb-atlas");
                    mongoDatabase = mongoClient.getDatabase("GreenChef");
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Users");

                    Document usuario = new Document();
                    usuario.append("id_usuario", id+1)
                            .append("nick", nick)
                            .append("email", email)
                            .append("password", contrasenia)
                            .append("fecharegistro", fechaRegistro)
                            .append("nombre", "")
                            .append("apellidos", "")
                            .append("telefono", "");

                    if (comprobarUsuario(nick)) {
                        if (validarEmail(email)) {
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
                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("El email no es válido")
                                    .show();

                            correo.setText("");
                        }
                        } else {
                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Este nombre de usuario ya existe")
                                    .show();

                            username.setText("");

                        }
                } else {
                    Toast.makeText(RegisterActivity.this, "No conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        }

    private boolean comprobarUsuario(String nick) {
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

                    Document query = new Document("nick", nick);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {

                                existeUsuario = true;

                        } else {
                            existeUsuario = false;
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "No conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return existeUsuario;
    }

    public void recuperarUltimoId(){
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

                    // Consulta el ultimo usuario por orden natural (último documento insertado)
                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find().sort(new Document("$natural", -1)).limit(1).iterator();

                    queryTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()) {
                                Document users = results.next();

                                id = users.getInteger("id_usuario");

                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al buscar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //Metodo para validar el email
    //Este metodo es de la clase Patterns y nos permite validar el email con una expresion regular que ya viene definida en la clase Patterns
    // y retorna un booleano si es correcto o no el email
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
