package com.example.greenchef.recipes_activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greenchef.R;

import java.util.ArrayList;

public class RecipesCompoundActivity extends AppCompatActivity {
    private String nombreReceta;
    private String descripcion;
    private ArrayList<String> ingredientes;
    private String procedimiento;
    private String tiempo;
    private int porciones;
    private byte[] imagen;
    private Bundle bundle;

    private TextView txtNombreReceta, txtDescripcion, txtIngredientes, txtProcedimiento, txtTiempo, txtPorciones;
    private ImageView imgReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_compound);

        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
        }

        bundle = getIntent().getExtras();
        nombreReceta = bundle.getString("nombreReceta");
        descripcion = bundle.getString("descripcion");

        ingredientes = bundle.getStringArrayList("ingredientes");
        StringBuilder ingredientesText = new StringBuilder();
        for (String ingrediente : ingredientes) {
            ingredientesText.append("- ").append(ingrediente).append("\n");
        }

        procedimiento = bundle.getString("procedimiento");
        String[] pasos = procedimiento.split("\\.\\s");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pasos.length; i++) {
            sb.append(pasos[i] + "\n");
        }

        tiempo = bundle.getString("tiempo");
        porciones = bundle.getInt("porciones");

        imagen = bundle.getByteArray("imagen");

        txtNombreReceta = this.findViewById(R.id.txtNombreReceta);
        txtDescripcion = this.findViewById(R.id.txtDescripcionR);
        txtIngredientes = this.findViewById(R.id.txtContenidoIngredientes);
        txtProcedimiento = this.findViewById(R.id.txtContenidoPreparacion);
        txtTiempo = this.findViewById(R.id.txtTiempoReal);
        txtPorciones = this.findViewById(R.id.txtNporciones);
        imgReceta = this.findViewById(R.id.imgReceta);

        txtNombreReceta.setText(nombreReceta);
        txtDescripcion.setText(descripcion);
        txtIngredientes.setText(ingredientesText.toString());
        txtProcedimiento.setText(sb.toString());
        txtTiempo.setText(tiempo);
        txtPorciones.setText(String.valueOf(porciones));

        // Convertir los bytes en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagen, 0, imagen.length);
        imgReceta.setImageBitmap(bitmap);

        // Aplicar el ajuste de escala al ImageView
        imgReceta.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}