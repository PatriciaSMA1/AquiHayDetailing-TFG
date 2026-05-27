package com.example.aquihaydetailing;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.HorarioViewHolder> {

    public interface OnHorarioSelectListener {
        void onSelect(String horario);
    }

    private final List<String> horarios;
    private List<String> ocupados = new ArrayList<>(); // ⭐ Lista de horas ya reservadas
    private String seleccionado = null;
    private final OnHorarioSelectListener onSelect;

    public HorarioAdapter(List<String> horarios, OnHorarioSelectListener onSelect) {
        this.horarios = new ArrayList<>(horarios);
        this.onSelect = onSelect;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horario_card, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        String horario = horarios.get(position);
        holder.text.setText(horario);

        // --- LÓGICA DE BLOQUEO SI ESTÁ OCUPADO ---
        if (ocupados.contains(horario)) {
            holder.card.setCardBackgroundColor(Color.parseColor("#E0E0E0")); // Gris claro
            holder.text.setTextColor(Color.parseColor("#9E9E9E")); // Texto apagado
            holder.card.setStrokeColor(Color.TRANSPARENT);
            holder.itemView.setEnabled(false); // ❌ No se puede clicar
            holder.itemView.setAlpha(0.6f);
        } else {
            holder.itemView.setEnabled(true);
            holder.itemView.setAlpha(1.0f);

            boolean isSelected = horario.equals(seleccionado);
            holder.card.setCardBackgroundColor(isSelected ? Color.parseColor("#FFD700") : Color.WHITE);
            holder.text.setTextColor(isSelected ? Color.BLACK : Color.parseColor("#333333"));
            holder.card.setStrokeColor(isSelected ? Color.BLACK : Color.parseColor("#DDDDDD"));

            holder.itemView.setOnClickListener(v -> {
                seleccionado = horario;
                notifyDataSetChanged();
                onSelect.onSelect(horario);
            });
        }
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    // ⭐ Método para que la Activity nos diga qué horas bloquear
    public void setHorariosOcupados(List<String> listaOcupados) {
        this.ocupados = listaOcupados;
        this.seleccionado = null; // Deseleccionamos si había algo marcado
        notifyDataSetChanged();
    }

    public void updateList(List<String> nuevaLista) {
        horarios.clear();
        horarios.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    static class HorarioViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final MaterialCardView card;

        HorarioViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textHorario);
            card = itemView.findViewById(R.id.cardHorario);
        }
    }
}