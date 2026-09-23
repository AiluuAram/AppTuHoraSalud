package com.example.apptuhorasalud.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.apptuhorasalud.R;
import com.example.apptuhorasalud.UpdateMedicineActivity;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicineList;
    private IMedicineRepository repo;


    public MedicineAdapter(List<Medicine> medicineList, IMedicineRepository repo) {
        this.medicineList = medicineList;
        this.repo = repo;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvMedicineName.setText(medicine.getName());
        holder.tvMedicineQuantity.setText("Cantidad: " + medicine.getQuantity());

        holder.btnUpdateMedicine.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, UpdateMedicineActivity.class);
            intent.putExtra("medicineId", medicine.getId());
            intent.putExtra("medicineName", medicine.getName());
            intent.putExtra("medicineQuantity", medicine.getQuantity());
            intent.putExtra("idUsuario", medicine.getUserId());
            context.startActivity(intent);
        });

        holder.btnDeleteMedicine.setOnClickListener(v -> {
            // Delete action will be implemented later
            repo.softDeleteMedicine(medicine);
            medicineList.remove(medicine);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void updateList(List<Medicine> newList) {
        this.medicineList = newList;
        notifyDataSetChanged();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvMedicineQuantity;
        Button btnUpdateMedicine, btnDeleteMedicine;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvMedicineQuantity = itemView.findViewById(R.id.tvMedicineQuantity);
            btnUpdateMedicine = itemView.findViewById(R.id.btnUpdateMedicine);
            btnDeleteMedicine = itemView.findViewById(R.id.btnDeleteMedicine);
        }
    }
}
