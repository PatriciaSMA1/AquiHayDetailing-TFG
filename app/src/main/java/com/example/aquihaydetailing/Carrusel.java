package com.example.aquihaydetailing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class Carrusel extends RecyclerView.Adapter<Carrusel.CarruselViewHolder> {


    private List<Integer> imagenes;
    private List<String> titulos;
    private List<String> descripciones;


    public Carrusel(List<Integer> imagenes, List<String> titulos, List<String> descripciones) {
        this.imagenes = imagenes;
        this.titulos = titulos;
        this.descripciones = descripciones;
    }


    @NonNull
    @Override
    public CarruselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.imagenes_carrusel, parent, false);
        return new CarruselViewHolder(vista);
    }


    @Override
    public void onBindViewHolder(@NonNull CarruselViewHolder holder, int position) {
        holder.imageView.setImageResource(imagenes.get(position));
        holder.titulo.setText(titulos.get(position));
        holder.descripcion.setText(descripciones.get(position));
    }


    @Override
    public int getItemCount() {
        return imagenes.size();
    }


    static class CarruselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titulo, descripcion;


        public CarruselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageCarrusel);
            titulo = itemView.findViewById(R.id.tituloServicio);
            descripcion = itemView.findViewById(R.id.descripcionServicio);
        }
    }
}

