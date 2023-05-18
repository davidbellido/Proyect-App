package com.example.greenchef.foodactivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.greenchef.ListProductActivity;
import com.example.greenchef.R;
import com.example.greenchef.model.Recetas;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

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

public class ProteinActivity extends AppCompatActivity {
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private List<Recetas> listaRecetas = new ArrayList<>();
    private ArrayAdapter<Recetas> adaptador;
    int id_producto;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protein);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
        }

            ListView listView = findViewById(R.id.lvProtein);
            obtenerListaRecetas(new RecetasCallback() {
                @Override
                public void onRecetasObtenidos(List<Recetas> listaRecetas) {
                    adaptador = new ArrayAdapter<Recetas>(ProteinActivity.this, R.layout.element_list_recipes, listaRecetas) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            if (convertView == null) {
                                convertView = getLayoutInflater().inflate(R.layout.element_list_recipes, parent, false);
                            }
                            ImageView imageView = convertView.findViewById(R.id.imgRecetas);
                            TextView nombreTextView = convertView.findViewById(R.id.txtNombre);
                            TextView tiempoTextView = convertView.findViewById(R.id.txtTiempo);

                            // Obtén el objeto Producto correspondiente a la posición en la lista
                            Recetas receta = getItem(position);

                            // Establece los valores de los elementos de la vista
                            imageView.setImageResource(receta.getImagen());
                            nombreTextView.setText(receta.getNombre());
                            tiempoTextView.setText(receta.getTiempo());

                            return convertView;
                        }
                    };

                    listView.setAdapter(adaptador);
                    listView.setDivider(new ColorDrawable(getResources().getColor(R.color.verde04)));
                    listView.setDividerHeight(20);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Obtener el producto seleccionado
                    Recetas recetaSeleccionada = listaRecetas.get(position);

                    // Crear el Intent
                    Intent intent = new Intent(ProteinActivity.this, RecipesCompoundActivity.class);
                    bundle = new Bundle();

                    // Pasar los datos de la receta seleccionada como extras del Intent
                    bundle.putString("nombreReceta", recetaSeleccionada.getNombre());
                    bundle.putString("descripcion", recetaSeleccionada.getDescripcion());
                    bundle.putStringArrayList("ingredientes", recetaSeleccionada.getIngredientes());
                    bundle.putString("procedimiento", recetaSeleccionada.getProcedimiento());
                    bundle.putString("tiempo", recetaSeleccionada.getTiempo());
                    bundle.putInt("porciones", recetaSeleccionada.getPorciones());

                    // Iniciar la actividad RecipesCompoundActivity
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
    }


        private void obtenerListaRecetas (RecetasCallback callback){
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

                        Document queryFilter = new Document().append("tipo", "Proteina");
                        RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find(queryFilter).iterator();

                        queryTask.getAsync(task -> {
                            if (task.isSuccess()) {
                                MongoCursor<Document> results = task.get();
                                while (results.hasNext()) {
                                    Document recipes = results.next();
                                    String name = recipes.getString("nombre");
                                    String descripcion = recipes.getString("descripcion");
                                    ArrayList<String> ingredientes = (ArrayList<String>) recipes.get("ingredientes");
                                    String procedimiento = recipes.getString("procedimiento");
                                    String tiempo = recipes.getString("tiempo_preparacion");
                                    int porciones = recipes.getInteger("porciones");

                                    // Crea una instancia de Producto con los datos obtenidos y añádelo a la lista
                                    Recetas recetas = new Recetas(name, descripcion, ingredientes, procedimiento, tiempo, porciones);
                                    listaRecetas.add(recetas);
                                }

                                // Asegúrate de llamar a la devolución de llamada con la lista de productos
                                callback.onRecetasObtenidos(listaRecetas);
                            } else {
                                Toast.makeText(ProteinActivity.this, "Error al buscar recetas", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ProteinActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Interfaz de devolución de llamada para obtener la lista de recetas
        interface RecetasCallback {
            void onRecetasObtenidos(List<Recetas> listaProductos);
        }
}