package com.example.greenchef;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenchef.model.Producto;

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

public class ListProductActivity extends AppCompatActivity {
    String AppId = "pruebaproyecto-urnlx";
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;
    List<Producto> listaProductos = new ArrayList<>();
    ArrayAdapter<Producto> adaptador;
    int id_producto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        try {
            this.getSupportActionBar().hide();
        }catch (Exception e){

        }

        ListView listView = findViewById(R.id.lvLista);
        obtenerListaProductos(new ProductosCallback() {
            @Override
            public void onProductosObtenidos(List<Producto> listaProductos) {
                adaptador = new ArrayAdapter<Producto>(ListProductActivity.this, R.layout.element_list, listaProductos) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = getLayoutInflater().inflate(R.layout.element_list, parent, false);
                        }

                        // Obtén referencias a los elementos de la vista
                        ImageView imageView = convertView.findViewById(R.id.ivImagen);
                        TextView tituloTextView = convertView.findViewById(R.id.tvTitulo);
                        TextView directorTextView = convertView.findViewById(R.id.tvDirector);
                        TextView duracionTextView = convertView.findViewById(R.id.tvDuracion);
                        TextView precioTextView = convertView.findViewById(R.id.tvPrecio);

                        // Obtén el objeto Producto correspondiente a la posición en la lista
                        Producto producto = getItem(position);

                        // Establece los valores de los elementos de la vista
                        imageView.setImageResource(producto.getImagen());
                        tituloTextView.setText(producto.getNombre());
                        String usuario = String.valueOf(producto.getId_usuario());
                        directorTextView.setText("Usuario: " + usuario);
                        String supermarket = String.valueOf(producto.getId_supermercado());
                        duracionTextView.setText("Supermercado: " + supermarket);
                        precioTextView.setText(String.valueOf(producto.getPrecio())+"€");

                        return convertView;
                    }
                };

                listView.setAdapter(adaptador);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el producto seleccionado
                Producto productoSeleccionado = listaProductos.get(position);

                // Mostrar el diálogo de edición
                mostrarDialogoEdicion(productoSeleccionado);
            }
        });
    }

    private void mostrarDialogoEdicion(final Producto producto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar producto");

        // Inflar el layout personalizado para el diálogo de edición
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setView(dialogView);

        // Obtener las referencias a los elementos del diálogo
        EditText nombreEditText = dialogView.findViewById(R.id.etNombre);
        EditText usuarioEditText = dialogView.findViewById(R.id.etUsuario);
        EditText supermercadoEditText = dialogView.findViewById(R.id.etSupermercado);
        EditText precioEditText = dialogView.findViewById(R.id.etPrecio);

        // Establecer los valores iniciales en los campos del diálogo
        nombreEditText.setText(producto.getNombre());
        usuarioEditText.setText(String.valueOf(producto.getId_usuario()));
        supermercadoEditText.setText(String.valueOf(producto.getId_supermercado()));
        precioEditText.setText(String.valueOf(producto.getPrecio()));

        // Configurar los botones del diálogo
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener los nuevos valores de los campos del diálogo
                String nuevoNombre = nombreEditText.getText().toString();
                int nuevoUsuario = Integer.parseInt(usuarioEditText.getText().toString());
                int nuevoSupermercado = Integer.parseInt(supermercadoEditText.getText().toString());
                double nuevoPrecio = Double.parseDouble(precioEditText.getText().toString());

                // Actualizar el producto en MongoDB
                modificarProducto(producto.getId(), nuevoNombre, nuevoUsuario, nuevoSupermercado, nuevoPrecio);

                // Actualizar los datos del producto en la lista
                producto.setNombre(nuevoNombre);
                producto.setId_usuario(nuevoUsuario);
                producto.setId_supermercado(nuevoSupermercado);
                producto.setPrecio(nuevoPrecio);

                // Notificar al adaptador que los datos han cambiado
                adaptador.notifyDataSetChanged();

                // Mostrar un mensaje de éxito
                Toast.makeText(ListProductActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modificarProducto(int productId, String nuevoNombre, int nuevoUsuario, int nuevoSupermercado, double nuevoPrecio) {
        // Obtén la referencia a la colección "SupermarketProducts" en MongoDB
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("SupermarketProducts");

        // Crea el filtro para buscar el producto por su ID
        Document filtro = new Document().append("id", productId);
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(filtro).iterator();

        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                if (results.hasNext()) {
                    // Crea el objeto con los campos que se van a actualizar
                    Document actualizaciones =  new Document()
                            .append("nombre", nuevoNombre)
                            .append("id_user", nuevoUsuario)
                            .append("id_supermarket", nuevoSupermercado)
                            .append("precio", nuevoPrecio);

                    // Actualiza el documento en MongoDB
                    mongoCollection.updateOne(filtro, actualizaciones).getAsync( result -> {
                        if (result.isSuccess()) {
                            Toast.makeText(ListProductActivity.this, "Producto actualizado en MongoDB", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ListProductActivity.this, "Producto actualizado en MongoDB", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ListProductActivity.this, "No se ha encontrado el producto", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListProductActivity.this, "Error al buscar el producto", Toast.LENGTH_SHORT).show();
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

                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find().iterator();

                    queryTask.getAsync(task->{
                        if (task.isSuccess()){
                            MongoCursor<Document> results = task.get();
                            while (results.hasNext()){
                                Document supermarket = results.next();
                                int id = supermarket.getInteger("id");
                                String name = supermarket.getString("nombre");
                                int id_user = supermarket.getInteger("id_user");
                                int id_supermarket = supermarket.getInteger("id_supermarket");
                                double price = supermarket.getDouble("precio");
                                id_producto = supermarket.getInteger("id");

                                // Crea una instancia de Producto con los datos obtenidos y añádelo a la lista
                                Producto producto = new Producto(id,name, id_user, id_supermarket, price);
                                listaProductos.add(producto);
                            }

                            // Asegúrate de llamar a la devolución de llamada con la lista de productos
                            callback.onProductosObtenidos(listaProductos);
                        }else {
                            Toast.makeText(ListProductActivity.this, "Error al buscar productos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ListProductActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Interfaz de devolución de llamada para obtener la lista de productos
    interface ProductosCallback {
        void onProductosObtenidos(List<Producto> listaProductos);
    }
}
