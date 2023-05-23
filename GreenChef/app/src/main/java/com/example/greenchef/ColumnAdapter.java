package com.example.greenchef;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNombre;
        private TextView txtPrecio;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
        }
    }
}