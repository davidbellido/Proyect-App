package com.example.greenchef;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.bson.Document;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class InsertUserProductActivity extends AppCompatActivity {
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private Bundle bundleProducto, bundleUsuario;
    private ImageView imgSupermarket;
    private String nombreSupermercado, nombreUsuario, imagen;
    private int idSupermercado, idUsuario;
    private EditText txtNombreProducto, txtPrecioProducto;
    private ImageButton btnImgProducto;
    private Button btnInsertarProducto;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_user_product);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        bundleProducto = getIntent().getExtras();
        nombreSupermercado = bundleProducto.getString("nombreSupermercado");
        idSupermercado = bundleProducto.getInt("idSupermercado");

        bundleUsuario = getIntent().getExtras();
        nombreUsuario = bundleUsuario.getString("nombreUsuario");

        buscarIdUsuario(nombreUsuario);

        imgSupermarket = this.findViewById(R.id.imgSupermarket);

        btnImgProducto = this.findViewById(R.id.btnInsertImage);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            // Mostrar la imagen en el ImageButton
                            btnImgProducto.setImageURI(imageUri);

                            try {
                                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                byte[] buffer = new byte[8192];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                                }
                                inputStream.close();

                                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                                if (imageBytes != null && imageBytes.length > 0) {
                                    String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
                                    imagen = encodedImage.replaceAll("\\n", "");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }




                        }
                    }
                });

        switch (nombreSupermercado){
            case "Mercadona":
                imgSupermarket.setBackground(ContextCompat.getDrawable(this, R.drawable.mercadona));
                break;
            case "Carrefour":
                imgSupermarket.setBackground(ContextCompat.getDrawable(this, R.drawable.carrefour));
                break;
            case "Lidl":
                imgSupermarket.setBackground(ContextCompat.getDrawable(this, R.drawable.lidl));
                break;
            case "Supermercado Dia":
                imgSupermarket.setBackground(ContextCompat.getDrawable(this, R.drawable.dia));
                break;
            default:
                imgSupermarket.setBackground(ContextCompat.getDrawable(this, R.drawable.mercado));
        }

        txtNombreProducto = this.findViewById(R.id.txtNombreProducto);
        txtPrecioProducto = this.findViewById(R.id.txtPrecio);

        btnInsertarProducto = this.findViewById(R.id.btnInsertarProducto);
        btnInsertarProducto.setOnClickListener(v -> {
            String nombreProducto = txtNombreProducto.getText().toString();
            double precioProducto = Double.parseDouble(txtPrecioProducto.getText().toString());

            insertProduct(nombreProducto, precioProducto, idSupermercado, idUsuario, imagen);
        });
    }

    private void insertProduct(String nombreProducto, double precioProducto, int idSupermercado, int idUsuario, String imagen) {
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
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("SupermarketProducts");

                    Document product = new Document();
                    product.append("id", 32)
                            .append("nombre", nombreProducto)
                            .append("id_user", idUsuario)
                            .append("id_supermarket", idSupermercado)
                            .append("precio", precioProducto)
                            .append("img", imagen);

                        mongoCollection.insertOne(product).getAsync(result1 -> {
                            if (result1.isSuccess()) {
                                new SweetAlertDialog(InsertUserProductActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("¡Producto añadido!")
                                        .setConfirmText("Volver")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                txtPrecioProducto.setText("");
                                                txtNombreProducto.setText("");
                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            } else {
                                Toast.makeText(InsertUserProductActivity.this, "No insertado", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(InsertUserProductActivity.this, "No conectado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buscarIdUsuario(String nombreUsuario){
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

                                idUsuario = users.getInteger("id_usuario");
                            }

                        } else {
                            Toast.makeText(InsertUserProductActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new SweetAlertDialog(InsertUserProductActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error al conectar con la base de datos")
                            .show();
                }
            }
        });
    }
    public void onInsertImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}