package com.example.aquihaydetailing;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;


import java.util.List;


public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.ViewHolder> {


    private Context context;
    private List<Cita> citas;


    public CitasAdapter(Context context, List<Cita> citas) {
        this.context = context;
        this.citas = citas;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cita, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cita cita = citas.get(position);


        // Texto formateado
        holder.textServicio.setText("Servicio: " + cita.getServicio());
        holder.textFecha.setText("Fecha: " + cita.getFecha());
        holder.textHora.setText("Horario: " + cita.getHora());
        holder.textPrecio.setText("Precio: " + cita.getPrecio());
        holder.textTamaño.setText("Tamaño: " + cita.getTamaño());
        holder.textEstado.setText(cita.getEstado());


        // Badge de color según estado
        String estado = cita.getEstado().toLowerCase();


        if (estado.contains("pendiente")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_pendiente);
        } else if (estado.contains("completada")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_completada);
        } else if (estado.contains("cancelada")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_cancelada);
        } else {
            // Por si algún día añades más estados
            holder.textEstado.setBackgroundResource(R.drawable.estado_pendiente);
        }
    }


    @Override
    public int getItemCount() {
        return citas.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textServicio, textFecha, textHora, textPrecio, textTamaño, textEstado;


        public ViewHolder(View itemView) {
            super(itemView);
            textServicio = itemView.findViewById(R.id.textServicio);
            textFecha = itemView.findViewById(R.id.textFecha);
            textHora = itemView.findViewById(R.id.textHora);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textTamaño = itemView.findViewById(R.id.textTamaño);
            textEstado = itemView.findViewById(R.id.textEstado);
        }
    }
}