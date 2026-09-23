package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.apptuhorasalud.domain.interfaces.IIngresoStockRepository;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.domain.models.IngresoStock;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.IngresoStockRepositoryImpl;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;
import com.example.apptuhorasalud.utils.FormUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InsertIngresoStockActivity extends AppCompatActivity {

    private Spinner spinnerMedicine;
    private EditText inputCantidad;
    private Button btnSaveIngreso;

    private int userId;
    private final List<Medicine> medicines = new ArrayList<>();

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_ingreso_stock);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollInsertIngreso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerMedicine = findViewById(R.id.spinnerMedicine);
        inputCantidad = findViewById(R.id.inputCantidad);
        btnSaveIngreso = findViewById(R.id.btnSaveIngreso);

        userId = getIntent().getIntExtra("idUsuario", -1);

        loadMedicines();

        btnSaveIngreso.setOnClickListener(v -> onSaveClick());
    }

    private void loadMedicines() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());
            List<Medicine> result = repo.getMedicinesByUserId(userId).join();

            runOnUiThread(() -> {
                medicines.clear();
                medicines.addAll(result);
                List<String> names = new ArrayList<>();
                for (Medicine m : medicines) {
                    names.add(m.getName() + " (stock: " + m.getQuantity() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, names);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedicine.setAdapter(adapter);
            });
        }).start();
    }

    private void onSaveClick() {
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }
        if (medicines.isEmpty()) {
            Toast.makeText(this, "Primero tenés que cargar un medicamento", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantidadStr = inputCantidad.getText().toString().trim();
        if (TextUtils.isEmpty(cantidadStr)) {
            FormUtils.showError(this, "Ingrese la cantidad", inputCantidad);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            FormUtils.showError(this, "La cantidad debe ser un número válido", inputCantidad);
            return;
        }
        if (cantidad <= 0) {
            FormUtils.showError(this, "La cantidad debe ser mayor a 0", inputCantidad);
            return;
        }

        Medicine medicine = medicines.get(spinnerMedicine.getSelectedItemPosition());
        saveIngreso(medicine, cantidad);
    }

    private void saveIngreso(Medicine medicine, int cantidad) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IIngresoStockRepository ingresoRepo = new IngresoStockRepositoryImpl(db.ingresoStockDao());
            IMedicineRepository medicineRepo = new MedicineRepositoryImpl(db.medicineDao());

            IngresoStock ingreso = new IngresoStock(0, medicine.getId(), medicine.getName(), cantidad, fecha, userId, false);
            ingresoRepo.addIngreso(ingreso).join();

            medicine.setQuantity(medicine.getQuantity() + cantidad);
            medicineRepo.updateMedicine(medicine).join();

            Log.d("DB", "Ingreso guardado: " + medicine.getName() + " +" + cantidad + " -> stock " + medicine.getQuantity());

            runOnUiThread(() -> {
                FormUtils.showSuccess(this, "Ingreso registrado. Nuevo stock: " + medicine.getQuantity());
                finish();
            });
        }).start();
    }
}