package com.example.apptuhorasalud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptuhorasalud.application.useCase.User.ValidateUser;
import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.domain.interfaces.IUserRepository;
import com.example.apptuhorasalud.infrastructure.data.AppDatabase;
import com.example.apptuhorasalud.infrastructure.repository.UsuarioRepositoryImpl;
import com.example.apptuhorasalud.utils.DatabaseHelper;
import com.example.apptuhorasalud.utils.FormUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView tvRegister, tvRecover;
    private String email, password;
    private static final String DB_NAME = "usuarios-db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.btnRegister);
        tvRecover = findViewById(R.id.btnRecover);


        // login
        btnLogin.setOnClickListener(v -> onLoginClick());

        // navegation to registerActivity
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // navegation to recover
        tvRecover.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecoverActivity.class);
            startActivity(intent);
        });
    }

    // Methods
    private void onLoginClick() {
        getValues();
        if (!validateInput(email, password)) return;
        loginUser(email, password);
    }
    private Boolean validateInput(String email, String password){
        if (email.isEmpty()) {
            FormUtils.showError(this, "Ingrese su correo", inputEmail);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            FormUtils.showError(this, "Correo inválido", inputEmail);
            return false;
        }
        if (password.isEmpty()) {
            FormUtils.showError(this, "Ingrese su contraseña", inputEmail);
            return false;
        }
        return true;
    }
    private void getValues(){
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
    }
    private void loginUser(String email, String password) {
        AppDatabase db = DatabaseHelper.getInstance(getApplicationContext());
        IUserRepository repo = new UsuarioRepositoryImpl(db.usuarioDao());
        ValidateUser useCase = new ValidateUser(repo);

        useCase.execute(email, password)
                .thenAccept(usuario -> {
                    runOnUiThread(() -> handleLoginResult(usuario));
                })
                .exceptionally(e -> {
                    Log.e("LOGIN_ERROR", "Error en login: " + e.getMessage(), e);
                    runOnUiThread(() -> Toast.makeText(this, "Error interno: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    return null;
                });
    }
    private void handleLoginResult(User user) {
        if (user != null && user.getName() != null) {
            Log.d("LOGIN", "Acceso exitoso: " + user.getEmail());
            FormUtils.showSuccess(this, "Bienvenido" + user.getName());
            navigateToHome(user.getName(), user.getId());
        } else {
            Log.d("LOGIN", "Credenciales incorrectas para: " + email);
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }
    private void navigateToHome(String nameUser, int userId) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("nombreUsuario", nameUser);
        intent.putExtra("idUsuario", userId);
        startActivity(intent);
        finish();
    }

}
