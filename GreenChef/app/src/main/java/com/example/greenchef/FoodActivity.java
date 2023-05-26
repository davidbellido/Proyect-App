package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.greenchef.foodactivities.CerealActivity;
import com.example.greenchef.foodactivities.DairyActivity;
import com.example.greenchef.foodactivities.DessertsActivity;
import com.example.greenchef.foodactivities.DriedFruitsActivity;
import com.example.greenchef.foodactivities.FruitsActivity;
import com.example.greenchef.foodactivities.GrainsActivity;
import com.example.greenchef.foodactivities.LegumesActivity;
import com.example.greenchef.foodactivities.ProteinActivity;
import com.example.greenchef.foodactivities.VegetablesActivity;
import com.example.greenchef.model.Recetas;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Base64;

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

public class FoodActivity extends AppCompatActivity {
    private ImageButton btnProtein, btnVegetables,btnCereal, btnFruits, btnGrains, btnDairy, btnDriedFruits, btnDesserts, btnLegumes;
    private ImageButton btnMap, btnHome, btnProfile;
    private ImageView imgRecipe;
    private Bundle bundle;
    private String nombreUsuario;
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){
        }

        generarImagenesAleatorias();

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("ELIJA EL TIPO DE ALIMENTO QUE DESEE")
                .setContentText("Dependiendo de que tipo de alimento escoja, la aplicación le proporcionará recetas los más ajustables posibles a sus preferencias")
                .setConfirmButtonBackgroundColor(getResources().getColor(R.color.verde03))
                .setCancelButtonBackgroundColor(getResources().getColor(cn.pedant.SweetAlert.R.color.red_btn_bg_color))
                .setConfirmText("Continuar")
                .setCancelText("Cancelar")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent i = new Intent(FoodActivity.this, OptionsActivity.class);
                        startActivity(i);
                        finish();
                    }
                })
                .show();

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");
        bundle.putString("nombreUsuario", nombreUsuario);

        imgRecipe = this.findViewById(R.id.imgRecipe);

        btnProtein = this.findViewById(R.id.btnProteina);
        btnVegetables = this.findViewById(R.id.btnVerduras);
        btnCereal = this.findViewById(R.id.btnCereales);
        btnFruits = this.findViewById(R.id.btnFrutas);
        btnGrains = this.findViewById(R.id.btnGranos);
        btnDairy = this.findViewById(R.id.btnLacteos);
        btnDriedFruits = this.findViewById(R.id.btnFsecos);
        btnDesserts = this.findViewById(R.id.btnPostres);
        btnLegumes = this.findViewById(R.id.btnLegumbres);

        btnMap = this.findViewById(R.id.btnMap);
        btnHome = this.findViewById(R.id.btnHome);
        btnProfile = this.findViewById(R.id.btnUsuario);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, MapsActivity.class);
                i.putExtras(bundle);
                FoodActivity.this.startActivity(i);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, OptionsActivity.class);
                i.putExtras(bundle);
                FoodActivity.this.startActivity(i);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, UserActivity.class);
                i.putExtras(bundle);
                FoodActivity.this.startActivity(i);
            }
        });

        btnProtein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, ProteinActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnVegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, VegetablesActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnCereal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, CerealActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, FruitsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnGrains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, GrainsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnDairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DairyActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnDriedFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DriedFruitsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnDesserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, DessertsActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });

        btnLegumes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodActivity.this, LegumesActivity.class);
                FoodActivity.this.startActivity(i);
            }
        });
    }

    public void generarImagenesAleatorias(){
        //Generar numeros aleatorios de 1 a 36
        int numeroAleatorio = (int) (Math.random() * 36) + 1;

        recuperarImagenes(numeroAleatorio);

    }

    private void recuperarImagenes(int numeroAleatorio) {
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

                    Document queryFilter = new Document().append("id", numeroAleatorio);
                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find(queryFilter).iterator();

                    queryTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()) {
                                Document recipes = results.next();

                                // Obtener la imagen codificada en base64 desde el campo 'image'
                                String encodedImage = recipes.getString("img");

                                // Decodificar la imagen de base64 a bytes
                                byte[] imageBytes = new byte[0];
                                if (encodedImage != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        imageBytes = Base64.getDecoder().decode(encodedImage);
                                    }

                                    // Convierte los bytes en un objeto Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                                    // Establece los valores de los elementos de la vista
                                    imgRecipe.setImageBitmap(bitmap);

                                    // Aplica el ajuste de escala al ImageView
                                    imgRecipe.setScaleType(ImageView.ScaleType.FIT_XY);
                                }else
                                    Toast.makeText(FoodActivity.this, "Imagen vacia", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(FoodActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(FoodActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}