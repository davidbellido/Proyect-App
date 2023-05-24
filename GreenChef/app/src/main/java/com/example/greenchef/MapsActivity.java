package com.example.greenchef;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.greenchef.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.bson.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private LatLng latLng;
    private Bundle bundle;
    private Bundle bundleProducto;
    private String nombreUsuario;
    private String AppId = "pruebaproyecto-urnlx";
    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bundle = getIntent().getExtras();
        nombreUsuario = bundle.getString("nombreUsuario");

        // Solicita los permisos necesarios
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Inicializa el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Verifica si se otorgó el permiso de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtiene la ubicación actual del usuario
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Verifica si la ubicación es válida
                            if (location != null) {
                                // Muestra la ubicación en el mapa
                                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                // Habilitar la capa de ubicación
                                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    mMap.setMyLocationEnabled(true);
                                }

// Personalizar el estilo del punto de ubicación
                                UiSettings uiSettings = mMap.getUiSettings();
                                uiSettings.setMyLocationButtonEnabled(true);
                                markSupermarket();
                            }
                        }
                    });
        }

        if (nombreUsuario.equals("admin")) {
            mMap.setOnMapClickListener(this);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
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
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Admin");

                    Document query = new Document("log", nombreUsuario);
                    mongoCollection.findOne(query).getAsync(result1 -> {
                        if (result1.isSuccess()) {

                            // SweetAlertDialog donde puedes introducir nombre, dirección, teléfono y horario
                            SweetAlertDialog alert = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Introducir Supermercado");

// Crear los campos de texto programáticamente
                            final EditText etNombre = new EditText(MapsActivity.this);
                            etNombre.setHint("Nombre");

                            final EditText etTelefono = new EditText(MapsActivity.this);
                            etTelefono.setHint("Teléfono");

                            final EditText etHoraApertura = new EditText(MapsActivity.this);
                            etHoraApertura.setHint("Hora de apertura");

                            final EditText etHoraCierre = new EditText(MapsActivity.this);
                            etHoraCierre.setHint("Hora de cierre");

// Agregar los campos de texto al SweetAlertDialog
                            LinearLayout layout = new LinearLayout(MapsActivity.this);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            layout.addView(etNombre);
                            layout.addView(etTelefono);
                            layout.addView(etHoraApertura);
                            layout.addView(etHoraCierre);

                            alert.setCustomView(layout);

// Configurar los botones del SweetAlertDialog
                            alert.setConfirmText("Guardar")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            // Obtener los valores ingresados por el usuario
                                            String nombre = etNombre.getText().toString();
                                            int telefono = Integer.parseInt(etTelefono.getText().toString());
                                            String horaApertura = etHoraApertura.getText().toString();
                                            String horaCierre = etHoraCierre.getText().toString();
                                            Double latitud = latLng.latitude;
                                            Double longitud = latLng.longitude;
                                            String via = "";

                                            Geocoder geocoder = new Geocoder(MapsActivity.this);

                                            try {
                                                List<Address> direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                                                Address direccion = direcciones.get(0);
                                                via = direccion.getAddressLine(0);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            mongoClient = user.getMongoClient("mongodb-atlas");
                                            mongoDatabase = mongoClient.getDatabase("GreenChef");
                                            MongoCollection<Document> mongoCollection2 = mongoDatabase.getCollection("SuperMarket");

                                            Document supermarket = new Document();
                                            supermarket.append("id", 8)
                                                    .append("nombre", nombre)
                                                    .append("direccion", via)
                                                    .append("latitud", latitud)
                                                    .append("longitud", longitud)
                                                    .append("telefono", telefono)
                                                    .append("hapertura", horaApertura)
                                                    .append("hcierre", horaCierre);

                                            mongoCollection2.insertOne(supermarket).getAsync(result1 -> {
                                                if (result1.isSuccess()) {
                                                    Toast.makeText(MapsActivity.this, "Insertado", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MapsActivity.this, "No insertado", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            mMap.addMarker(new MarkerOptions().position(latLng).title(nombre));

                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .setCancelText("Cancelar")
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    });

// Mostrar el SweetAlertDialog
                            alert.create();
                            alert.show();

                        } else {
                            Toast.makeText(MapsActivity.this, "No tienes permiso para marcar un supermercado", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void markSupermarket() {
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
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("SuperMarket");

                    RealmResultTask<MongoCursor<Document>> queryTask = mongoCollection.find().iterator();

                    queryTask.getAsync(task -> {
                        if (task.isSuccess()) {
                            MongoCursor<Document> results = task.get();
                            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                                        // Definir el radio de distancia deseado en metros
                                        double radio = 800; // Por ejemplo, 1000 metros (1 km)

                                        while (results.hasNext()) {
                                            Document supermarket = results.next();
                                            double lat = supermarket.getDouble("latitud");
                                            double lon = supermarket.getDouble("longitud");

                                            LatLng supermarketLatLng = new LatLng(lat, lon);
                                            float[] distance = new float[1];
                                            Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, supermarketLatLng.latitude, supermarketLatLng.longitude, distance);

                                            if (distance[0] <= radio) {
                                                mMap.addMarker(new MarkerOptions().position(supermarketLatLng));
                                            }

                                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                @Override
                                                public boolean onMarkerClick(Marker marker) {
                                                    LatLng position = marker.getPosition();
                                                    Document query = new Document("latitud", position.latitude);
                                                    mongoCollection.findOne(query).getAsync(result1 -> {

                                                        Document supermercado = result1.get();
                                                        int id = supermercado.getInteger("id");
                                                        String nombre = supermercado.getString("nombre");
                                                        String direccion = supermercado.getString("direccion");
                                                        int telefono = supermercado.getInteger("telefono");
                                                        String hapertura = supermercado.getString("hapertura");
                                                        String hcierre = supermercado.getString("hcierre");

                                                        bundleProducto = new Bundle();
                                                        bundleProducto.putString("nombreSupermercado", nombre);
                                                        bundleProducto.putInt("idSupermercado", id);

                                                        if (supermercado != null) {
                                                            SweetAlertDialog builder = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.NORMAL_TYPE);
                                                            builder.setTitleText(nombre);
                                                            builder.setContentText("Dirección: " + direccion + "\n" +
                                                                    "Teléfono: " + telefono + "\n" +
                                                                    "Horario de apertura: " + hapertura + "\n" +
                                                                    "Horario de cierre: " + hcierre);
                                                            builder.setNeutralButton("Productos", new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            Intent i = new Intent(MapsActivity.this,SupermarketProductsActivity.class);
                                                                            i.putExtras(bundleProducto);
                                                                            MapsActivity.this.startActivity(i);
                                                                        }
                                                                    });

                                                            builder.setConfirmText("Añadir")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                            sweetAlertDialog.dismiss();
                                                                        }
                                                                    });

                                                            builder.create();
                                                            builder.show();
                                                        }
                                                    });
                                                    return false;
                                                }
                                            });
                                        }
                                    }
                                }
                            });

                        }else {
                           Toast.makeText(MapsActivity.this, "Error al buscar supermercado", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MapsActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}