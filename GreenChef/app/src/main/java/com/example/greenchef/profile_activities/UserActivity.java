package com.example.greenchef.profile_activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.greenchef.MapsActivity;
import com.example.greenchef.OptionsActivity;
import com.example.greenchef.R;
import com.example.greenchef.recipes_activities.FoodActivity;

import org.bson.Document;

import java.io.ByteArrayOutputStream;
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

public class UserActivity extends AppCompatActivity {
    private Bundle bundle;
    private String nombreUsuario;
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private EditText txtNombre, txtApellido, txtNick, txtEmail, txtTelefono;
    private TextView txtNombreUsuario;
    private Button btnGuardar;
    private String name, apellidos, email, telefono, password, fecharegistro, imagen;
    private int i;
    private Uri imageUri;
    private ImageButton btnMap, btnHome, btnRecetas, imgProfile;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private  byte[] imageBytes = new byte[0];
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){
        }

        // Dentro de tu clase
        imgProfile = this.findViewById(R.id.imgProfile);

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            imgProfile.setBackground(null);
                            // Mostrar la imagen en el ImageButton
                            // Cargar la imagen redondeada con Glide
                            Glide.with(this)
                                    .load(imageUri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(imgProfile);

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
                                        .append("id_usuario", id)
                                        .append("nick", txtNick.getText().toString())
                                        .append("email", txtEmail.getText().toString())
                                        .append("password", password)
                                        .append("fecharegistro", fecharegistro)
                                        .append("nombre", txtNombre.getText().toString())
                                        .append("apellidos", txtApellido.getText().toString())
                                        .append("telefono", txtTelefono.getText().toString())
                                        .append("img", imagen);

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
                                Document users = results.next();
                                id = users.getInteger("id_usuario");
                                name = users.getString("nombre");
                                apellidos = users.getString("apellidos");
                                email = users.getString("email");
                                telefono = users.getString("telefono");

                                password = users.getString("password");
                                fecharegistro = users.getString("fecharegistro");

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
                                    Glide.with(UserActivity.this)
                                            .load(bitmap)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(imgProfile);
                                }

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
    public void onInsertImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}