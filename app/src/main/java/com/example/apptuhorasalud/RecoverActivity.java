package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptuhorasalud.application.useCase.User.UpdateUserPassword;
import com.example.apptuhorasalud.application.useCase.User.ValidateIdentity;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.UsuarioRepositoryImpl;
import com.example.apptuhorasalud.utils.DatabaseHelper;
import com.example.apptuhorasalud.utils.FormUtils;

public class RecoverActivity extends AppCompatActivity {

    private EditText inputEmail, inputDocument, inputNewPassword, inputConfirmPassword;
    private Button btnValidate, btnRecover;
    private String email, documentStr, newPassword, confirmPassword;
    private ValidateIdentity validateIdentity;
    private UpdateUserPassword updateUserPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recover);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recover), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputDocument = findViewById(R.id.inputDocument);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnValidate = findViewById(R.id.btnValidate);
        btnRecover = findViewById(R.id.btnRecover);

        // Inyección de casos de uso
        AppDatabase db = DatabaseHelper.getInstance(getApplicationContext());
        UsuarioRepositoryImpl repo = new UsuarioRepositoryImpl(db.usuarioDao());
        validateIdentity = new ValidateIdentity(repo);
        updateUserPassword = new UpdateUserPassword(repo);

        btnValidate.setOnClickListener(v -> {
            getValues();
            if (!validateInputSync()) return;

            int document = Integer.parseInt(documentStr);

            validateIdentity.execute(email, document)
                    .thenAccept(result -> runOnUiThread(() -> {
                        if (result) {
                            FormUtils.showSuccess(this, "Identidad validada correctamente");

                            LinearLayout passwordSection = findViewById(R.id.passwordSection);
                            passwordSection.setVisibility(View.VISIBLE);

                        } else {
                            FormUtils.showError(this, "Correo y documento no coinciden", inputDocument);
                        }
                    }));
        });

        btnRecover.setOnClickListener(v -> {
            getNewPasswordValue();
            if (!validateInputPasswordSync()) return;

            updateUserPassword.execute(email, newPassword)
                    .thenRun(() -> runOnUiThread(() -> {
                        FormUtils.showSuccess(this, "Contraseña actualizada correctamente");
                        LinearLayout passwordSection = findViewById(R.id.passwordSection);
                        passwordSection.setVisibility(View.GONE);
                        finish();
                    }))
                    .exceptionally(e -> {
                        Log.e("RECOVERY", "Error al actualizar contraseña", e);
                        runOnUiThread(() -> Toast.makeText(this, "Error interno: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        return null;
                    });
        });
    }

    private void getValues() {
        email = inputEmail.getText().toString().trim();
        documentStr = inputDocument.getText().toString().trim();
    }

    private void getNewPasswordValue() {
        newPassword = inputNewPassword.getText().toString().trim();
        confirmPassword = inputConfirmPassword.getText().toString().trim();
    }

    private boolean validateInputSync() {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FormUtils.showError(this, "Ingrese un correo válido", inputEmail);
            return false;
        }

        if (TextUtils.isEmpty(documentStr) || documentStr.length() < 8) {
            FormUtils.showError(this, "Ingrese un documento válido", inputDocument);
            return false;
        }

        try {
            Integer.parseInt(documentStr);
        } catch (NumberFormatException e) {
            FormUtils.showError(this, "El documento debe ser numérico", inputDocument);
            return false;
        }

        return true;
    }

    private boolean validateInputPasswordSync() {
        if (newPassword.isEmpty()) {
            FormUtils.showError(this, "Ingrese su contraseña", inputNewPassword);
            return false;
        }
        if (confirmPassword.isEmpty()) {
            FormUtils.showError(this, "Confirme su contraseña", inputConfirmPassword);
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            FormUtils.showError(this, "Las contraseñas no coinciden", inputConfirmPassword);
            return false;
        }

        return true;
    }
}

