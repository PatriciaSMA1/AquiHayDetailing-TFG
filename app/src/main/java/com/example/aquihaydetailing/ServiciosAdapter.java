package com.example.aquihaydetailing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ServiciosAdapter extends RecyclerView.Adapter<ServiciosAdapter.ServicioViewHolder> {

    private Context context;
    private List<Servicio> listaServicios;
    private OnReservarClickListener listener;

    public interface OnReservarClickListener {
        void onReservarClick(Servicio servicio);
    }

    public ServiciosAdapter(Context context, List<Servicio> listaServicios, OnReservarClickListener listener) {
        this.context = context;
        this.listaServicios = listaServicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicio = listaServicios.get(position);

        holder.textNombreServicio.setText(servicio.getNombre());

        // Convertir lista de tareas en texto con bullets
        StringBuilder tareasTexto = new StringBuilder();
        for (String tarea : servicio.getTareasIncluidas()) {
            tareasTexto.append("• ").append(tarea).append("\n");
        }
        holder.textTareasIncluidas.setText(tareasTexto.toString().trim());

        holder.textTiempoEstimado.setText("Tiempo estimado: " + servicio.getTiempoEstimado());

        holder.btnReservarServicio.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReservarClick(servicio);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {

        TextView textNombreServicio, textDescripcionServicio, textTareasIncluidas, textTiempoEstimado;
        Button btnReservarServicio;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);

            textNombreServicio = itemView.findViewById(R.id.textNombreServicio);
            textTareasIncluidas = itemView.findViewById(R.id.textTareasIncluidas);
            textTiempoEstimado = itemView.findViewById(R.id.textTiempoEstimado);
            btnReservarServicio = itemView.findViewById(R.id.btnReservarServicio);
        }
    }
}