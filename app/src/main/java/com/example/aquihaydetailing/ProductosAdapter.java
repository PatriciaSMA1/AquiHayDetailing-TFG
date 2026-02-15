package com.example.aquihaydetailing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;


import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private Context context;
    private List<Producto> listaProductos;

    public ProductosAdapter(Context context, List<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.textNombre.setText(producto.getNombre());
        holder.textDescripcion.setText(producto.getDescripcion());
        holder.textServicios.setText("Usado en: " + producto.getServiciosUsados());

        // 🔥 Cargar imagen redondeada con Glide
        Glide.with(context)
                .load(producto.getImagenUrl())
                .placeholder(R.drawable.circle_background) // imagen de carga
                .circleCrop()                              // redondea la imagen
                .into(holder.imgProducto);

        // Abrir enlace si existe
        holder.itemView.setOnClickListener(v -> {
            if (producto.getEnlaceFullCarX() != null && !producto.getEnlaceFullCarX().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(producto.getEnlaceFullCarX()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView textNombre, textDescripcion, textServicios;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            textNombre = itemView.findViewById(R.id.textNombreProducto);
            textDescripcion = itemView.findViewById(R.id.textDescripcionProducto);
            textServicios = itemView.findViewById(R.id.textServiciosUsados);
        }
    }
}