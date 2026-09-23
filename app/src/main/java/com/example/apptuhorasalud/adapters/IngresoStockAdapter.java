package com.example.apptuhorasalud.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptuhorasalud.R;
import com.example.apptuhorasalud.domain.models.IngresoStock;

import java.util.List;

public class IngresoStockAdapter extends RecyclerView.Adapter<IngresoStockAdapter.IngresoViewHolder> {

    private List<IngresoStock> ingresoList;

    public IngresoStockAdapter(List<IngresoStock> ingresoList) {
        this.ingresoList = ingresoList;
    }

    @NonNull
    @Override
    public IngresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingreso_stock, parent, false);
        return new IngresoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresoViewHolder holder, int position) {
        IngresoStock ingreso = ingresoList.get(position);
        holder.tvIngresoMedicine.setText(ingreso.getMedicamentoNombre());
        holder.tvIngresoFecha.setText(ingreso.getFecha());
        holder.tvIngresoCantidad.setText("+" + ingreso.getCantidad());
    }

    @Override
    public int getItemCount() {
        return ingresoList.size();
    }

    public void updateList(List<IngresoStock> newList) {
        this.ingresoList = newList;
        notifyDataSetChanged();
    }

    static class IngresoViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngresoMedicine, tvIngresoFecha, tvIngresoCantidad;

        public IngresoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngresoMedicine = itemView.findViewById(R.id.tvIngresoMedicine);
            tvIngresoFecha = itemView.findViewById(R.id.tvIngresoFecha);
            tvIngresoCantidad = itemView.findViewById(R.id.tvIngresoCantidad);
        }
    }
}