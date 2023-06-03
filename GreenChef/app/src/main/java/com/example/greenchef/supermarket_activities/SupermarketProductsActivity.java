package com.example.greenchef.supermarket_activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.greenchef.CustomDividerItemDecoration;
import com.example.greenchef.R;
import com.example.greenchef.adapter.ColumnAdapter;
import com.example.greenchef.model.Producto;


import org.bson.Document;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

public class SupermarketProductsActivity extends AppCompatActivity {
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private Bundle bundle;
    private ImageView imgSupermarket;
    private String nombreSupermercado;
    private int idSupermercado;
    private List<Producto> listaProductos = new ArrayList<>();
    private int i = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_products);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        // Mostrar un mensaje de carga
        final SweetAlertDialog dialogo = new SweetAlertDialog(SupermarketProductsActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("Cargando Productos")
                .setContentText("Espere por favor...");
        dialogo.show();
        dialogo.setCancelable(false);
        new CountDownTimer(800 * 2, 800) {
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

            @Override
            public void onFinish() {
                i = -1;
                dialogo.dismiss();
            }
        }.start();

        bundle = getIntent().getExtras();
        nombreSupermercado = bundle.getString("nombreSupermercado");
        idSupermercado = bundle.getInt("idSupermercado");

        imgSupermarket = this.findViewById(R.id.imgSupermarket);

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

        RecyclerView recyclerView = this.findViewById(R.id.lvProducts);
        obtenerListaProductos(new ProductosCallback() {
            @Override
            public void onProductosObtenidos(List<Producto> listaProductos) {
                ColumnAdapter adapter = new ColumnAdapter(listaProductos);
                recyclerView.setAdapter(adapter);

                // Configurar el GridLayoutManager para el RecyclerView con 2 columnas
                GridLayoutManager layoutManager = new GridLayoutManager(SupermarketProductsActivity.this, 2);
                recyclerView.setLayoutManager(layoutManager);

                // Establecer el divisor personalizado para el RecyclerView
                int dividerHeight = 30; // Altura personalizada en píxeles
                CustomDividerItemDecoration dividerItemDecoration = new CustomDividerItemDecoration(SupermarketProductsActivity.this, layoutManager.getOrientation(), dividerHeight);
                dividerItemDecoration.setDrawable(new ColorDrawable(ContextCompat.getColor(SupermarketProductsActivity.this, R.color.verde01)));
                recyclerView.addItemDecoration(dividerItemDecoration);

            }
        });
    }

    private void obtenerListaProductos(ProductosCallback callback) {
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

                    Document queryFilter = new Document().append("id_supermarket", idSupermercado);
                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find(queryFilter).iterator();

                    queryTask.getAsync(task->{
                        if (task.isSuccess()){
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()){
                                Document products = results.next();
                                int id = products.getInteger("id");
                                String name = products.getString("nombre");
                                int id_user = products.getInteger("id_user");
                                int id_supermarket = products.getInteger("id_supermarket");
                                double price = products.getDouble("precio");

                                // Obtener la imagen codificada en base64 desde el campo 'image'
                                String encodedImage = products.getString("img");

                                // Decodificar la imagen de base64 a bytes
                                byte[] imageBytes = new byte[0];
                                if (encodedImage != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        imageBytes = Base64.getDecoder().decode(encodedImage);
                                    }
                                }else
                                    Toast.makeText(SupermarketProductsActivity.this, "Imagen vacia", Toast.LENGTH_SHORT).show();

                                // Crear una instancia de Producto con los datos obtenidos y añadirlo a la lista
                                Producto producto = new Producto(id,name, id_user, id_supermarket, price);
                                producto.setImagen(imageBytes);
                                listaProductos.add(producto);
                            }

                            callback.onProductosObtenidos(listaProductos);
                        }else {
                            Toast.makeText(SupermarketProductsActivity.this, "Error al buscar productos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SupermarketProductsActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Interfaz de devolución de llamada para obtener la lista de productos
    interface ProductosCallback {
        void onProductosObtenidos(List<Producto> listaProductos);
    }
}