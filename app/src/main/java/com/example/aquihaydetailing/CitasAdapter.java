package com.example.aquihaydetailing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

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

        // Lógica de colores y visibilidad del botón de cancelar
        if (estado.contains("pendiente")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_pendiente);
            holder.btnCancelar.setVisibility(View.VISIBLE); // Solo se puede cancelar si está pendiente
        } else if (estado.contains("completada")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_completada);
            holder.btnCancelar.setVisibility(View.GONE); // Si ya se completó, no se cancela
        } else if (estado.contains("cancelada")) {
            holder.textEstado.setBackgroundResource(R.drawable.estado_cancelada);
            holder.btnCancelar.setVisibility(View.GONE); // Si ya está cancelada, no sale el botón
        } else {
            holder.textEstado.setBackgroundResource(R.drawable.estado_pendiente);
            holder.btnCancelar.setVisibility(View.GONE);
        }

        // Lógica del Botón Cancelar
        holder.btnCancelar.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancelar Reserva")
                    .setMessage("¿Estás seguro de que quieres cancelar esta cita?")
                    .setPositiveButton("Sí, cancelar", (dialog, which) -> {

                        // Actualización en Firebase Firestore
                        FirebaseFirestore.getInstance().collection("reservas")
                                .document(cita.getId()) // Importante: el objeto Cita debe guardar su ID de Firebase
                                .update("estado", "cancelada")
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show();

                                    // Actualizamos el objeto localmente para que el color cambie sin recargar la pantalla
                                    cita.setEstado("cancelada");
                                    notifyItemChanged(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textServicio, textFecha, textHora, textPrecio, textTamaño, textEstado;
        Button btnCancelar; // Referencia al nuevo botón

        public ViewHolder(View itemView) {
            super(itemView);
            textServicio = itemView.findViewById(R.id.textServicio);
            textFecha = itemView.findViewById(R.id.textFecha);
            textHora = itemView.findViewById(R.id.textHora);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textTamaño = itemView.findViewById(R.id.textTamaño);
            textEstado = itemView.findViewById(R.id.textEstado);
            btnCancelar = itemView.findViewById(R.id.btnCancelar); // Enlazamos el botón del XML
        }
    }
}