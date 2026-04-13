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
    private String seleccionado = null;
    private final OnHorarioSelectListener onSelect;

    public HorarioAdapter(List<String> horarios, OnHorarioSelectListener onSelect) {
        // Convertimos la lista en mutable para poder actualizarla
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

        boolean isSelected = horario.equals(seleccionado);
        holder.card.setCardBackgroundColor(isSelected ? Color.parseColor("#FFD700") : Color.WHITE);
        holder.text.setTextColor(isSelected ? Color.BLACK : Color.parseColor("#333333"));
        holder.card.setStrokeWidth(isSelected ? 2 : 1);
        holder.card.setStrokeColor(isSelected ? Color.parseColor("#000000") : Color.parseColor("#DDDDDD"));
        holder.card.setElevation(isSelected ? 6f : 2f);

        holder.itemView.setOnClickListener(v -> {
            seleccionado = horario;
            notifyDataSetChanged();
            onSelect.onSelect(horario);
        });
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    // ⭐⭐⭐ MÉTODO NUEVO PARA ACTUALIZAR LA LISTA ⭐⭐⭐
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




