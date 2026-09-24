package com.example.apptuhorasalud.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptuhorasalud.R;
import com.example.apptuhorasalud.domain.models.IngresoStock;

import java.util.List;

public class IngresoStockAdapter extends RecyclerView.Adapter<IngresoStockAdapter.IngresoViewHolder> {

    public interface OnIngresoActionListener {
        void onAnular(IngresoStock ingreso);
        void onEditar(IngresoStock ingreso);
    }

    private List<IngresoStock> ingresoList;
    private final OnIngresoActionListener listener;

    public IngresoStockAdapter(List<IngresoStock> ingresoList, OnIngresoActionListener listener) {
        this.ingresoList = ingresoList;
        this.listener = listener;
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

        holder.btnAnular.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnular(ingreso);
            }
        });

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(ingreso);
            }
        });
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
        Button btnAnular, btnEditar;

        public IngresoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngresoMedicine = itemView.findViewById(R.id.tvIngresoMedicine);
            tvIngresoFecha = itemView.findViewById(R.id.tvIngresoFecha);
            tvIngresoCantidad = itemView.findViewById(R.id.tvIngresoCantidad);
            btnAnular = itemView.findViewById(R.id.btnAnular);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}