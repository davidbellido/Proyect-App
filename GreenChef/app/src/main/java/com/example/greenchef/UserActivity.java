package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenchef.foodactivities.ProteinActivity;
import com.example.greenchef.model.Recetas;

import org.bson.Document;

import java.util.ArrayList;

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

public class UserActivity extends AppCompatActivity {
    private Bundle bundle;
    private String nombreUsuario;
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private EditText txtNombre, txtApellido, txtNick, txtEmail, txtTelefono;
    private TextView txtNombreUsuario;
    private Button btnGuardar;
    private String name, apellidos, email, telefono, password, fecharegistro;
    private int i;
    private ImageButton btnMap, btnHome, btnRecetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){
        }

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");

        txtNombre = this.findViewById(R.id.txtNombre);
        txtApellido = this.findViewById(R.id.txtApellido);
        txtNick = this.findViewById(R.id.txtNick);
        txtEmail = this.findViewById(R.id.txtEmail);
        txtTelefono = this.findViewById(R.id.txtTelefono);
        txtNombreUsuario = this.findViewById(R.id.txtNombreUsuario);

        buscarUsuario(nombreUsuario);

        btnGuardar = this.findViewById(R.id.btnGuardarUsuario);
        btnGuardar.setOnClickListener(v -> {
            actualizarUsuario(nombreUsuario);
        });

        btnMap = this.findViewById(R.id.btnMap);
        btnHome = this.findViewById(R.id.btnHome);
        btnRecetas = this.findViewById(R.id.btnReceta);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, MapsActivity.class);
                i.putExtras(bundle);
                UserActivity.this.startActivity(i);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, OptionsActivity.class);
                i.putExtras(bundle);
                UserActivity.this.startActivity(i);
            }
        });

        btnRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserActivity.this, FoodActivity.class);
                i.putExtras(bundle);
                UserActivity.this.startActivity(i);
            }
        });
    }

    private void actualizarUsuario(String nick) {
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

                    // Crea el filtro para buscar el producto por su ID
                    Document filtro = new Document().append("nick", nick);
                    RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(filtro).iterator();

                    findTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            if (results.hasNext()) {
                                // Crea el objeto con los campos que se van a actualizar
                                Document actualizaciones = new Document()
                                        .append("id_usuario", 4)
                                        .append("nick", txtNick.getText().toString())
                                        .append("email", txtEmail.getText().toString())
                                        .append("password", password)
                                        .append("fecharegistro", fecharegistro)
                                        .append("nombre", txtNombre.getText().toString())
                                        .append("apellidos", txtApellido.getText().toString())
                                        .append("telefono", txtTelefono.getText().toString());

                                // Actualiza el documento en MongoDB
                                mongoCollection.updateOne(filtro, actualizaciones).getAsync(resul -> {
                                    if (resul.isSuccess()) {
                                        // Mostrar un mensaje de éxito
                                        final SweetAlertDialog dialogo = new SweetAlertDialog(UserActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                                                .setTitleText("Actualizando")
                                                .setContentText("Espere por favor...");
                                        dialogo.show();
                                        dialogo.setCancelable(false);
                                        new CountDownTimer(800 * 7, 800) {
                                            public void onTick(long millisUntilFinished) {
                                                i++;
                                                switch (i) {
                                                    case 0:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.blue_btn_bg_color));
                                                        break;
                                                    case 1:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.material_deep_teal_50));
                                                        break;
                                                    case 2:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.success_stroke_color));
                                                        break;
                                                    case 3:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.material_deep_teal_20));
                                                        break;
                                                    case 4:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.material_blue_grey_80));
                                                        break;
                                                    case 5:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.warning_stroke_color));
                                                        break;
                                                    case 6:
                                                        dialogo.getProgressHelper().setBarColor(getResources().getColor(cn.pedant.SweetAlert.R.color.success_stroke_color));
                                                        break;
                                                }
                                            }

                                            public void onFinish() {
                                                i = -1;
                                                dialogo.setTitleText("¡Actualizado!")
                                                        .setContentText("Usuario actualizado correctamente")
                                                        .setConfirmText("OK")
                                                        .setConfirmClickListener(null)
                                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            }
                                        }.start();
                                    } else {
                                        Log.d("ListProductActivity", "Usuario actualizado en MongoDB");
                                    }
                                });
                            } else {
                                Toast.makeText(UserActivity.this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserActivity.this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void buscarUsuario(String nombreUsuario){
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

                    Document queryFilter = new Document().append("nick", nombreUsuario);
                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find(queryFilter).iterator();

                    queryTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()) {
                                Document recipes = results.next();
                                name = recipes.getString("nombre");
                                apellidos = recipes.getString("apellidos");
                                email = recipes.getString("email");
                                telefono = recipes.getString("telefono");

                                password = recipes.getString("password");
                                fecharegistro = recipes.getString("fecharegistro");

                                txtNombreUsuario.setText(name + " " + apellidos);

                                txtNombre.setText(name);
                                txtApellido.setText(apellidos);
                                txtNick.setText(nombreUsuario);
                                txtEmail.setText(email);
                                txtTelefono.setText(telefono);

                            }
                        } else {
                            Toast.makeText(UserActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }
        });
    }
}