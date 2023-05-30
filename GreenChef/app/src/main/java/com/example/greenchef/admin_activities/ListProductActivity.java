package com.example.greenchef.admin_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenchef.R;
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

public class ListProductActivity extends AppCompatActivity {
    String AppId = "pruebaproyecto-urnlx";
    MongoDatabase mongoDatabase;
    MongoClient mongoClient;
    List<Producto> listaProductos = new ArrayList<>();
    ArrayAdapter<Producto> adaptador;
    int id_producto;
    int i = 0;

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

                        // Convierte los bytes en un objeto Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(producto.getImagen(), 0, producto.getImagen().length);

                        // Establece los valores de los elementos de la vista
                        imageView.setImageBitmap(bitmap);

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
        SweetAlertDialog builder = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        builder.setTitleText("Editar producto");

        // Inflar el layout personalizado para el diálogo de edición
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        builder.setCustomView(dialogView);

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
        builder.setConfirmText("Guardar");
                builder.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
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
                final SweetAlertDialog dialogo = new SweetAlertDialog(ListProductActivity.this, SweetAlertDialog.PROGRESS_TYPE)
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
                                .setContentText("El producto se ha actualizado correctamente")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                }.start();
            }
        });
        builder.setCancelText("Borrar");
        builder.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                // Eliminar el producto de MongoDB
                eliminarProducto(producto.getId());

                // Eliminar el producto de la lista
                listaProductos.remove(producto);

                // Notificar al adaptador que los datos han cambiado
                adaptador.notifyDataSetChanged();

                // Mostrar un mensaje de éxito
                final SweetAlertDialog dialogo = new SweetAlertDialog(ListProductActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText("Borrando")
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
                        dialogo.setTitleText("¡Borrado!")
                                .setContentText("El producto se ha borrado correctamente")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                }.start();
                builder.dismiss();
            }
        });

        // Mostrar el diálogo
        builder.create();
        builder.show();
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
                            .append("id", productId)
                            .append("nombre", nuevoNombre)
                            .append("id_user", nuevoUsuario)
                            .append("id_supermarket", nuevoSupermercado)
                            .append("precio", nuevoPrecio);

                    // Actualiza el documento en MongoDB
                    mongoCollection.updateOne(filtro, actualizaciones).getAsync( result -> {
                        if (result.isSuccess()) {
                            Log.d("ListProductActivity", "Producto actualizado en MongoDB");
                        } else {
                            Log.d("ListProductActivity", "Producto actualizado en MongoDB");
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
                                Document products = results.next();
                                int id = products.getInteger("id");
                                String name = products.getString("nombre");
                                int id_user = products.getInteger("id_user");
                                int id_supermarket = products.getInteger("id_supermarket");
                                double price = products.getDouble("precio");
                                id_producto = products.getInteger("id");

                                // Obtener la imagen codificada en base64 desde el campo 'image'
                                String encodedImage = products.getString("img");

                                // Decodificar la imagen de base64 a bytes
                                byte[] imageBytes = new byte[0];
                                if (encodedImage != null) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        imageBytes = Base64.getDecoder().decode(encodedImage);
                                    }
                                }else
                                    Toast.makeText(ListProductActivity.this, "Imagen vacia", Toast.LENGTH_SHORT).show();

                                // Crea una instancia de Producto con los datos obtenidos y añádelo a la lista
                                Producto producto = new Producto(id,name, id_user, id_supermarket, price);
                                producto.setImagen(imageBytes);
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

    //Metodo para eliminar un producto de la colleccion SupermarketProducts
    private void eliminarProducto(int productId) {
        // Obtén la referencia a la colección "SupermarketProducts" en MongoDB
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("SupermarketProducts");

        // Crea el filtro para buscar el producto por su ID
        Document filtro = new Document().append("id", productId);
        RealmResultTask<MongoCursor<Document>> findTask = mongoCollection.find(filtro).iterator();

        findTask.getAsync(task -> {
            if (task.isSuccess()) {
                MongoCursor<Document> results = task.get();
                if (results.hasNext()) {
                    // Elimina el documento de MongoDB
                    mongoCollection.deleteOne(filtro).getAsync( result -> {
                        if (result.isSuccess()) {
                            Log.d("ListProductActivity", "Producto eliminado de MongoDB");
                        } else {
                            Log.d("ListProductActivity", "Producto no eliminado de MongoDB");
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
}
