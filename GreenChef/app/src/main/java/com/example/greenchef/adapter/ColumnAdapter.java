package com.example.greenchef.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.greenchef.R;
import com.example.greenchef.model.Producto;

import java.util.List;

public class ColumnAdapter extends RecyclerView.Adapter<ColumnAdapter.ViewHolder> {
    private List<Producto> items;

    public ColumnAdapter(List<Producto> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_column, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Producto producto = items.get(position);
        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText(String.valueOf(producto.getPrecio()) + "€");

        // Convierte los bytes en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(producto.getImagen(), 0, producto.getImagen().length);

        // Establece los valores de los elementos de la vista
        holder.imgProducto.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNombre;
        private TextView txtPrecio;
        private ImageView imgProducto;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}