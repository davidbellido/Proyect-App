package com.example.greenchef.foodactivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.greenchef.R;
import com.example.greenchef.model.Recetas;

import java.util.ArrayList;

public class RecipesCompoundActivity extends AppCompatActivity {
    private String nombreReceta;
    private String descripcion;
    private ArrayList<String> ingredientes;
    private String procedimiento;
    private String tiempo;
    private int porciones;
    private Bundle bundle;

    private TextView txtNombreReceta, txtDescripcion, txtIngredientes, txtProcedimiento, txtTiempo, txtPorciones;

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

        txtNombreReceta = this.findViewById(R.id.txtNombreReceta);
        txtDescripcion = this.findViewById(R.id.txtDescripcionR);
        txtIngredientes = this.findViewById(R.id.txtContenidoIngredientes);
        txtProcedimiento = this.findViewById(R.id.txtContenidoPreparacion);
        txtTiempo = this.findViewById(R.id.txtTiempoReal);
        txtPorciones = this.findViewById(R.id.txtNporciones);

        txtNombreReceta.setText(nombreReceta);
        txtDescripcion.setText(descripcion);
        txtIngredientes.setText(ingredientesText.toString());
        txtProcedimiento.setText(sb.toString());
        txtTiempo.setText(tiempo);
        txtPorciones.setText(String.valueOf(porciones));
    }
}