package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.greenchef.profile_activities.UserActivity;
import com.example.greenchef.recipes_activities.FoodActivity;

import org.bson.Document;

import java.util.Base64;
import java.util.Calendar;

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


public class OptionsActivity extends AppCompatActivity {
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private ImageButton btnRecetas;
    private ImageButton btnMapa;
    private ImageButton btnPerfil;
    private TextView txtSaludo, txtSaludoUsuario;
    private ImageView imgProfile, imgUltReceta;
    private Bundle bundle, bundleMapa;
    private String nombreUsuario, imagen;
    private  byte[] imageBytes = new byte[0];
    private TextView txtNombreReceta, txtTipoReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        txtSaludo = this.findViewById(R.id.saludo);
        txtSaludoUsuario = this.findViewById(R.id.saludoUsuario);
        txtNombreReceta = this.findViewById(R.id.txtNombreUltReceta);
        txtTipoReceta = this.findViewById(R.id.txtTipoUltReceta);
        imgProfile = this.findViewById(R.id.imgPerfilHome);
        imgUltReceta = this.findViewById(R.id.imgUltReceta);

        saludarSegunHoraDelDia();

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");
        txtSaludoUsuario.setText("Hola " + nombreUsuario + "!");

        bundleMapa = new Bundle();
        bundleMapa.putString("nombreUsuario", nombreUsuario);

        btnRecetas = this.findViewById(R.id.btnRecetas);
        btnRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, FoodActivity.class);
                i.putExtras(bundleMapa);
                OptionsActivity.this.startActivity(i);
            }
        });

        btnMapa = this.findViewById(R.id.btnMapa);
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, MapsActivity.class);
                i.putExtras(bundleMapa);
                OptionsActivity.this.startActivity(i);
            }
        });

        btnPerfil = this.findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OptionsActivity.this, UserActivity.class);
                i.putExtras(bundleMapa);
                OptionsActivity.this.startActivity(i);
            }
        });

        recuperarImagenUsuario();
        recuperarUltimaReceta();

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

    public void recuperarImagenUsuario(){
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
                                Document users = results.next();

                                // Obtener la imagen codificada en base64 desde el campo 'image'
                                imagen = users.getString("img");

                                // Decodificar la imagen de base64 a bytes
                                if (imagen != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        imageBytes = Base64.getDecoder().decode(imagen);
                                    }
                                }

                                // Convierte los bytes en un objeto Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                if (imagen != null){
                                    imgProfile.setBackground(null);
                                    // Cargar la imagen redondeada con Glide
                                    Glide.with(OptionsActivity.this)
                                            .load(bitmap)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(imgProfile);
                                }
                            }
                        } else {
                            Toast.makeText(OptionsActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void recuperarUltimaReceta(){
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
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Recipes");

                    // Consulta la última receta por orden natural (último documento insertado)
                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find().sort(new Document("$natural", -1)).limit(1).iterator();

                    queryTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()) {
                                Document recetas = results.next();

                                String nombreReceta = recetas.getString("nombre");
                                String tipoReceta = recetas.getString("tipo");
                                // Obtener la imagen codificada en base64 desde el campo 'image'
                                String encodedImage = recetas.getString("img");

                                // Decodificar la imagen de base64 a bytes
                                byte[] imageBytes = new byte[0];
                                if (encodedImage != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        imageBytes = Base64.getDecoder().decode(encodedImage);
                                    }
                                } else{
                                    Toast.makeText(OptionsActivity.this, "Imagen vacia", Toast.LENGTH_SHORT).show();
                                }

                                // Convierte los bytes en un objeto Bitmap
                                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                // Establece los valores de los elementos de la vista
                                imgUltReceta.setImageBitmap(bitmap);
                                // Aplica el ajuste de escala al ImageView
                                imgUltReceta.setScaleType(ImageView.ScaleType.FIT_XY);

                                txtNombreReceta.setText(nombreReceta);
                                txtTipoReceta.setText(tipoReceta);
                            }
                        } else {
                            Toast.makeText(OptionsActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}