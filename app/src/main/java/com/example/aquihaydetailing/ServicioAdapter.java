package com.example.aquihaydetailing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    public interface OnServicioSelectListener {
        void onSelect(String servicio);
    }

    private final List<String> servicios;
    private final OnServicioSelectListener onSelect;

    public ServicioAdapter(List<String> servicios, OnServicioSelectListener onSelect) {
        this.servicios = servicios;
        this.onSelect = onSelect;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_servicio_card, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        String servicio = servicios.get(position);
        holder.text.setText(servicio);
        holder.itemView.setOnClickListener(v -> onSelect.onSelect(servicio));
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    static class ServicioViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        ServicioViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textServicio);
        }
    }
}








