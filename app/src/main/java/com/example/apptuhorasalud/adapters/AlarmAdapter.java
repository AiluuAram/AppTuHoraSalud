package com.example.apptuhorasalud.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptuhorasalud.R;
import com.example.apptuhorasalud.UpdateAlarmActivity;
import com.example.apptuhorasalud.domain.models.Alarm;

import java.util.List;
import java.util.Locale;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    public interface OnAlarmToggleListener {
        void onToggle(Alarm alarm);
    }

    public interface OnAlarmDeleteListener {
        void onDelete(Alarm alarm);
    }

    private List<Alarm> alarmList;
    private final OnAlarmToggleListener toggleListener;
    private final OnAlarmDeleteListener deleteListener;

    public AlarmAdapter(List<Alarm> alarmList, OnAlarmToggleListener toggleListener, OnAlarmDeleteListener deleteListener) {
        this.alarmList = alarmList;
        this.toggleListener = toggleListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.tvAlarmMedicine.setText(alarm.getMedicineName());
        holder.tvAlarmDose.setText("Dosis: " + alarm.getDose());
        holder.tvAlarmTime.setText(String.format(Locale.getDefault(), "%02d:%02d", alarm.getHour(), alarm.getMinute()));

        if (alarm.isActive()) {
            holder.tvAlarmStatus.setText("Activa");
            holder.tvAlarmStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.btnToggleAlarm.setText("Desactivar");
            holder.btnToggleAlarm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935")));
        } else {
            holder.tvAlarmStatus.setText("Inactiva");
            holder.tvAlarmStatus.setTextColor(Color.parseColor("#B71C1C"));
            holder.btnToggleAlarm.setText("Activar");
            holder.btnToggleAlarm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        holder.btnToggleAlarm.setOnClickListener(v -> {
            if (toggleListener != null) {
                toggleListener.onToggle(alarm);
            }
        });

        holder.btnUpdateAlarm.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, UpdateAlarmActivity.class);
            intent.putExtra("alarmId", alarm.getId());
            intent.putExtra("medicineId", alarm.getMedicineId());
            intent.putExtra("medicineName", alarm.getMedicineName());
            intent.putExtra("dose", alarm.getDose());
            intent.putExtra("hour", alarm.getHour());
            intent.putExtra("minute", alarm.getMinute());
            intent.putExtra("idUsuario", alarm.getUserId());
            intent.putExtra("isActive", alarm.isActive());
            context.startActivity(intent);
        });

        holder.btnDeleteAlarm.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void updateList(List<Alarm> newList) {
        this.alarmList = newList;
        notifyDataSetChanged();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlarmMedicine, tvAlarmDose, tvAlarmTime, tvAlarmStatus;
        Button btnUpdateAlarm, btnToggleAlarm, btnDeleteAlarm;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlarmMedicine = itemView.findViewById(R.id.tvAlarmMedicine);
            tvAlarmDose = itemView.findViewById(R.id.tvAlarmDose);
            tvAlarmTime = itemView.findViewById(R.id.tvAlarmTime);
            tvAlarmStatus = itemView.findViewById(R.id.tvAlarmStatus);
            btnUpdateAlarm = itemView.findViewById(R.id.btnUpdateAlarm);
            btnToggleAlarm = itemView.findViewById(R.id.btnToggleAlarm);
            btnDeleteAlarm = itemView.findViewById(R.id.btnDeleteAlarm);
        }
    }
}
