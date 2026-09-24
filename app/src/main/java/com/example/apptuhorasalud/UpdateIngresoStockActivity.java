package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.List;

public class UpdateIngresoStockActivity extends AppCompatActivity {

    private TextView tvUpdateMedicamento;
    private EditText inputCantidadEdit;
    private Button btnGuardarEdit;

    private int ingresoId;
    private int medicamentoId;
    private String medicamentoNombre;
    private int cantidadVieja;
    private String fecha;
    private int userId;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_ingreso_stock);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollUpdateIngreso), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvUpdateMedicamento = findViewById(R.id.tvUpdateMedicamento);
        inputCantidadEdit = findViewById(R.id.inputCantidadEdit);
        btnGuardarEdit = findViewById(R.id.btnGuardarEdit);

        ingresoId = getIntent().getIntExtra("ingresoId", -1);
        medicamentoId = getIntent().getIntExtra("medicamentoId", -1);
        medicamentoNombre = getIntent().getStringExtra("medicamentoNombre");
        cantidadVieja = getIntent().getIntExtra("cantidad", 0);
        fecha = getIntent().getStringExtra("fecha");
        userId = getIntent().getIntExtra("idUsuario", -1);

        if (ingresoId == -1 || userId == -1) {
            Toast.makeText(this, "Error: ingreso no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvUpdateMedicamento.setText(medicamentoNombre);
        inputCantidadEdit.setText(String.valueOf(cantidadVieja));

        btnGuardarEdit.setOnClickListener(v -> onGuardarClick());
    }

    private void onGuardarClick() {
        String cantidadStr = inputCantidadEdit.getText().toString().trim();
        if (TextUtils.isEmpty(cantidadStr)) {
            FormUtils.showError(this, "Ingrese la cantidad", inputCantidadEdit);
            return;
        }

        int cantidadNueva;
        try {
            cantidadNueva = Integer.parseInt(cantidadStr);
        } catch (NumberFormatException e) {
            FormUtils.showError(this, "La cantidad debe ser un número válido", inputCantidadEdit);
            return;
        }
        if (cantidadNueva <= 0) {
            FormUtils.showError(this, "La cantidad debe ser mayor a 0", inputCantidadEdit);
            return;
        }

        guardarCambios(cantidadNueva);
    }

    private void guardarCambios(int cantidadNueva) {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IIngresoStockRepository ingresoRepo = new IngresoStockRepositoryImpl(db.ingresoStockDao());
            IMedicineRepository medicineRepo = new MedicineRepositoryImpl(db.medicineDao());

            IngresoStock ingresoActualizado = new IngresoStock(
                    ingresoId, medicamentoId, medicamentoNombre, cantidadNueva, fecha, userId, false);
            ingresoRepo.updateIngreso(ingresoActualizado).join();

            List<Medicine> medicines = medicineRepo.getMedicinesByUserId(userId).join();
            for (Medicine m : medicines) {
                if (m.getId() == medicamentoId) {
                    int nuevoStock = m.getQuantity() - cantidadVieja + cantidadNueva;
                    if (nuevoStock < 0) nuevoStock = 0;
                    m.setQuantity(nuevoStock);
                    medicineRepo.updateMedicine(m).join();
                    Log.d("DB", "Ingreso editado. Nuevo stock de " + m.getName() + ": " + nuevoStock);
                    break;
                }
            }

            runOnUiThread(() -> {
                FormUtils.showSuccess(this, "Ingreso actualizado");
                finish();
            });
        }).start();
    }
}