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

    // Método llamado cuando se necesita crear una nueva vista para un elemento de la lista
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Infla el diseño de la vista del elemento de la columna desde el archivo XML 'item_column'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_column, parent, false);
        return new ViewHolder(view);
    }

    // Método llamado cuando se necesita asociar datos a una vista en una posición específica
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Obtiene el producto en la posición especificada
        Producto producto = items.get(position);

        // Establece el nombre y el precio del producto en los TextView correspondientes
        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText(String.valueOf(producto.getPrecio()) + "€");

        // Convierte los bytes de la imagen en un objeto Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(producto.getImagen(), 0, producto.getImagen().length);

        // Establece el Bitmap en el ImageView de la vista
        holder.imgProducto.setImageBitmap(bitmap);
    }

    // Método que devuelve la cantidad de elementos en la lista
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Clase interna que representa la vista de un elemento en el RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNombre;
        private TextView txtPrecio;
        private ImageView imgProducto;

        public ViewHolder(View itemView) {
            super(itemView);
            // Obtiene las referencias a los TextView y al ImageView en la vista del elemento
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}
