package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import com.example.apptuhorasalud.domain.models.Medicine;
import com.example.apptuhorasalud.domain.interfaces.IMedicineRepository;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.MedicineRepositoryImpl;
import com.example.apptuhorasalud.utils.FormUtils;

public class InsertMedicineActivity extends AppCompatActivity {

    private EditText inputMedicineName, inputQuantity, inputStockMinimo;
    private Button btnSaveMedicine;

    private String medicineName, quantity, stockMinimoStr;
    private int userId;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_medicine);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollInsertMedicine), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputMedicineName = findViewById(R.id.inputMedicineName);
        inputQuantity = findViewById(R.id.inputQuantity);
        inputStockMinimo = findViewById(R.id.inputStockMinimo);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        userId = getIntent().getIntExtra("idUsuario", -1);

        btnSaveMedicine.setOnClickListener(v -> onSaveMedicineClick());
    }

    private void onSaveMedicineClick() {
        getValues();
        if (!validateInput()) return;
        saveMedicine();
    }

    private void getValues() {
        medicineName = inputMedicineName.getText().toString().trim();
        quantity = inputQuantity.getText().toString().trim();
        stockMinimoStr = inputStockMinimo.getText().toString().trim();
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(medicineName)) {
            FormUtils.showError(this, "Ingrese el nombre del medicamento", inputMedicineName);
            return false;
        }

        if (medicineName.length() < 2) {
            FormUtils.showError(this, "El nombre debe tener al menos 2 caracteres", inputMedicineName);
            return false;
        }

        if (TextUtils.isEmpty(quantity)) {
            FormUtils.showError(this, "Ingrese la cantidad", inputQuantity);
            return false;
        }

        try {
            int qty = Integer.parseInt(quantity);
            if (qty <= 0) {
                FormUtils.showError(this, "La cantidad debe ser mayor a 0", inputQuantity);
                return false;
            }
        } catch (NumberFormatException e) {
            FormUtils.showError(this, "La cantidad debe ser un número válido", inputQuantity);
            return false;
        }

        if (!TextUtils.isEmpty(stockMinimoStr)) {
            try {
                int min = Integer.parseInt(stockMinimoStr);
                if (min < 0) {
                    FormUtils.showError(this, "El stock mínimo no puede ser negativo", inputStockMinimo);
                    return false;
                }
            } catch (NumberFormatException e) {
                FormUtils.showError(this, "El stock mínimo debe ser un número válido", inputStockMinimo);
                return false;
            }
        }

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveMedicine() {
        int stockMinimo = TextUtils.isEmpty(stockMinimoStr) ? 5 : Integer.parseInt(stockMinimoStr);

        Medicine medicine = new Medicine(0, medicineName, Integer.parseInt(quantity), userId, false);
        medicine.setStockMinimo(stockMinimo);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());

            repo.addMedicine(medicine).join();
            Log.d("DB", "Medicamento guardado: " + medicine.getName() + " - Cantidad: " + medicine.getQuantity() + " - Mínimo: " + medicine.getStockMinimo());

            runOnUiThread(() -> {
                FormUtils.showSuccess(this, "Medicamento guardado correctamente");
                finish();
            });
        }).start();
    }
}