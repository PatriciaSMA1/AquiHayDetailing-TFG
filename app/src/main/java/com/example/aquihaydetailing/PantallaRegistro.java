package com.example.aquihaydetailing;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaRegistro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText txtCorreo, txtContraseña, txtNombre, txtTelefono;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtTelefono = findViewById(R.id.txtTelefono); // NUEVO
        txtContraseña = findViewById(R.id.txtContraseña);
        ImageView iconTogglePassword = findViewById(R.id.iconTogglePassword);

        // 👁️ Lógica del ojo con fuente fija
        iconTogglePassword.setOnClickListener(v -> {
            boolean visible = (txtContraseña.getInputType() &
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                    == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

            if (visible) {
                txtContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                iconTogglePassword.setImageResource(R.drawable.ojo_cerrado);
            } else {
                txtContraseña.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                iconTogglePassword.setImageResource(R.drawable.ojo_abierto);
            }

            txtContraseña.setSelection(txtContraseña.getText().length());
            txtContraseña.setTypeface(ResourcesCompat.getFont(this, R.font.michroma));
            txtContraseña.setTextSize(16);
        });

        // ⭐ BOTÓN REGISTRAR
        findViewById(R.id.botonRegistrar).setOnClickListener(v -> {

            String nombre = txtNombre.getText().toString().trim();
            String correo = txtCorreo.getText().toString().trim();
            String telefono = txtTelefono.getText().toString().trim(); // NUEVO
            String contraseña = txtContraseña.getText().toString().trim();

            // Validación de campos vacíos
            if (nombre.isEmpty() || correo.isEmpty() || telefono.isEmpty() || contraseña.isEmpty()) {
                mostrarDialogoErrores("Completa todos los campos antes de continuar.");
                return;
            }

            // Validación de teléfono
            if (telefono.length() < 9) {
                mostrarDialogoErrores("Introduce un número de teléfono válido.");
                return;
            }

            // Validación de contraseña
            String mensajeError = validarContrasenaConDetalle(contraseña);
            if (mensajeError != null) {
                mostrarDialogoErrores(mensajeError);
                return;
            }

            // Crear usuario en Firebase
            mAuth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser usuario = mAuth.getCurrentUser();

                    if (usuario != null) {

                        // Guardar nombre en el perfil de Firebase Auth
                        UserProfileChangeRequest perfil = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nombre)
                                .build();
                        usuario.updateProfile(perfil);

                        // Guardar datos en Firestore
                        Map<String, Object> datosUsuario = new HashMap<>();
                        datosUsuario.put("nombre", nombre);
                        datosUsuario.put("correo", correo);
                        datosUsuario.put("telefono", telefono); // NUEVO

                        db.collection("usuarios").document(usuario.getUid()).set(datosUsuario)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, PantallaPrincipal.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    mostrarDialogoErrores("Error al guardar datos: " + e.getMessage());
                                });
                    }

                } else {
                    mostrarDialogoErrores("Error al crear usuario: " + task.getException().getMessage());
                }
            });
        });

        // ⭐ BOTÓN VOLVER
        findViewById(R.id.btnVolverLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, PantallaLogin.class));
            finish();
        });
    }

    // Validación detallada de contraseña
    private String validarContrasenaConDetalle(String password) {
        List<String> errores = new ArrayList<>();

        if (password.length() < 8) errores.add("- Mínimo 8 caracteres");

        boolean mayus = false, minus = false, numero = false, especial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) mayus = true;
            else if (Character.isLowerCase(c)) minus = true;
            else if (Character.isDigit(c)) numero = true;
            else especial = true;
        }

        if (!mayus) errores.add("- Una mayúscula");
        if (!minus) errores.add("- Una minúscula");
        if (!numero) errores.add("- Un número");
        if (!especial) errores.add("- Un símbolo (@#$%&*!?)");

        if (errores.isEmpty()) return null;
        return "La contraseña debe incluir:\n" + String.join("\n", errores);
    }

    // Diálogo de errores
    private void mostrarDialogoErrores(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error de registro")
                .setMessage(mensaje)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Entendido", null)
                .show();
    }
}