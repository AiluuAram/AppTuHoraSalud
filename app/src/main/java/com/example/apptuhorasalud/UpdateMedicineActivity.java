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

public class UpdateMedicineActivity extends AppCompatActivity {

    private EditText inputMedicineName, inputQuantity;
    private Button btnUpdateMedicine;

    private String medicineName, quantity;
    private int userId;
    private int medicineId;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_medicina);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollUpdateMedicine), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputMedicineName = findViewById(R.id.inputMedicineName);
        inputQuantity = findViewById(R.id.inputQuantity);
        btnUpdateMedicine = findViewById(R.id.btnUpdateMedicine);

        // Get data from intent
        userId = getIntent().getIntExtra("idUsuario", -1);
        medicineId = getIntent().getIntExtra("medicineId", -1);
        String currentName = getIntent().getStringExtra("medicineName");
        int currentQuantity = getIntent().getIntExtra("medicineQuantity", 0);

        // Pre-populate fields with current data
        if (currentName != null) {
            inputMedicineName.setText(currentName);
        }
        inputQuantity.setText(String.valueOf(currentQuantity));

        btnUpdateMedicine.setOnClickListener(v -> onUpdateMedicineClick());
    }

    // methods
    private void onUpdateMedicineClick() {
        getValues();
        if (!validateInput()) return;
        updateMedicine();
    }

    private void getValues() {
        medicineName = inputMedicineName.getText().toString().trim();
        quantity = inputQuantity.getText().toString().trim();
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

        if (userId == -1 || medicineId == -1) {
            Toast.makeText(this, "Error: Datos incompletos", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateMedicine() {
        Medicine medicine = new Medicine(medicineId, medicineName, Integer.parseInt(quantity), userId, false);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IMedicineRepository repo = new MedicineRepositoryImpl(db.medicineDao());

            repo.updateMedicine(medicine).join();
            Log.d("DB", "Medicamento actualizado correctamente: " + medicine.getName() + " - Cantidad: " + medicine.getQuantity());

            runOnUiThread(() -> {
                FormUtils.showSuccess(this, "Medicamento actualizado correctamente");
                finish();
            });
        }).start();
    }
}
