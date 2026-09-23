package com.example.apptuhorasalud;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;
import com.example.apptuhorasalud.application.useCase.User.AddUser;
import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.domain.interfaces.IUserRepository;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.UsuarioRepositoryImpl;
import com.example.apptuhorasalud.utils.FormUtils;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputDocument, inputName, inputLastname, inputEmail, inputPassword, inputConfirmPassword, inputBirthDate;
    private Button btnSaveUser;

    private String document, name, lastname, email, birthDate, password, confirmPassword;

    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollRegistro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inputDocument = findViewById(R.id.inputDocument);
        inputName = findViewById(R.id.inputName);
        inputLastname = findViewById(R.id.inputLastname);
        inputBirthDate = findViewById(R.id.inputBirthDate);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword= findViewById(R.id.inputConfirmPassword);
        btnSaveUser = findViewById(R.id.btnSaveUser);

        setupBirthDateFormatter();
        btnSaveUser.setOnClickListener(v -> onRegisterClick());
    }

    // methods
    private void setupBirthDateFormatter() {
        inputBirthDate.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                isUpdating = true;

                String digits = s.toString().replaceAll("[^\\d]", "");
                if (digits.length() > 8) digits = digits.substring(0, 8);

                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 2 || i == 4) formatted.append('/');
                    formatted.append(digits.charAt(i));
                }

                s.replace(0, s.length(), formatted.toString());
                isUpdating = false;
            }
        });
    }

    private void onRegisterClick() {
        getValues();
        if (!validateInput()) return;
        saveUser();
    }
    private void getValues(){
        document = inputDocument.getText().toString().trim();
        name = inputName.getText().toString().trim();
        lastname = inputLastname.getText().toString().trim();
        birthDate = inputBirthDate.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        confirmPassword = inputConfirmPassword.getText().toString().trim();
    }
    private boolean validateInput(){
        if (TextUtils.isEmpty(document)) {
            FormUtils.showError(this, "Ingrese el documento", inputDocument);
            return false;
        }

        if (document.length() < 8) {
            FormUtils.showError(this, "El documento debe tener al menos 8 dígitos", inputDocument);
            return false;
        }

        try {
            Long.parseLong(document);
        } catch (NumberFormatException e) {
            FormUtils.showError(this, "El documento debe ser numérico", inputDocument);
            return false;
        }

        if (name.isEmpty()) {
            FormUtils.showError(this, "Ingrese un nombre", inputName);
            return false;
        }
        if (lastname.isEmpty()) {
            FormUtils.showError(this, "Ingrese un apellido", inputLastname);
            return false;
        }
        if (email.isEmpty()) {
            FormUtils.showError(this, "El correo es obligatorio", inputEmail);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FormUtils.showError(this, "Ingrese un correo válido", inputEmail);
            return false;
        }
        if (password.isEmpty()) {
            FormUtils.showError(this, "Ingrese una contraseña", inputPassword);
            return false;
        }
        if (birthDate.isEmpty()) {
            FormUtils.showError(this, "Ingrese una fecha de nacimiento", inputBirthDate);
            return false;
        }
        if (!birthDate.matches("\\d{2}/\\d{2}/\\d{4}")) {
            FormUtils.showError(this, "La fecha debe tener el formato DD/MM/AAAA", inputBirthDate);
            return false;
        }
        int day   = Integer.parseInt(birthDate.substring(0, 2));
        int month = Integer.parseInt(birthDate.substring(3, 5));
        int year  = Integer.parseInt(birthDate.substring(6));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (day < 1 || day > 31) {
            FormUtils.showError(this, "El día debe estar entre 01 y 31", inputBirthDate);
            return false;
        }
        if (month < 1 || month > 12) {
            FormUtils.showError(this, "El mes debe estar entre 01 y 12", inputBirthDate);
            return false;
        }
        if (year < 1900 || year > currentYear) {
            FormUtils.showError(this, "Ingrese un año válido", inputBirthDate);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            FormUtils.showError(this, "Las contraseñas no coinciden", inputConfirmPassword);
            return false;
        }
        return true;
    }


    private void saveUser(){
        User user = new User(null, Integer.parseInt(document), name, lastname, birthDate, email, password);

        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).build();
            IUserRepository repo = new UsuarioRepositoryImpl(db.usuarioDao());
            User existingUser = repo.getByEmailUser(user.getEmail()).join();
            if (existingUser != null) {
                runOnUiThread(() -> FormUtils.showError(this, "El usuario ya existe", inputEmail));
                return;
            }

            AddUser useCase = new AddUser(repo);

            useCase.execute(user);
            Log.d("DB", "Usuario guardado correctamente: " + user.getName() + " - " + user.getEmail());

            runOnUiThread(() -> {FormUtils.showSuccess(this, "Usuario guardado correctamente");
                finish();
            });
        }).start();
    }

}
