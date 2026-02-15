package com.example.aquihaydetailing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PantallaLogin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText txtLogin, txtContraseña;
    private TextView errorLogin, errorContraseña;
    private CheckBox checkRecordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        txtLogin = findViewById(R.id.txtLogin);
        txtContraseña = findViewById(R.id.txtContraseña);
        errorLogin = findViewById(R.id.errorLogin);
        errorContraseña = findViewById(R.id.errorContraseña);
        checkRecordar = findViewById(R.id.checkRecordar);
        ImageView iconTogglePassword = findViewById(R.id.iconTogglePassword);
        TextView linkOlvidado = findViewById(R.id.textContraseñaOlvidada);

        // 👁️ Lógica del ojo con fuente fija
        iconTogglePassword.setOnClickListener(v -> {
            boolean visible = (txtContraseña.getInputType() & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
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

        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String correoGuardado = prefs.getString("correo", "");
        if (!correoGuardado.isEmpty()) {
            txtLogin.setText(correoGuardado);
            checkRecordar.setChecked(true);
        }

        txtLogin.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    errorLogin.setVisibility(View.GONE);
                    txtLogin.setBackgroundResource(R.drawable.edittext_box);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        txtContraseña.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    errorContraseña.setVisibility(View.GONE);
                    txtContraseña.setBackgroundResource(R.drawable.edittext_box);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        Button botonAcceder = findViewById(R.id.botonAcceder);
        botonAcceder.setOnClickListener(v -> {
            String correo = txtLogin.getText().toString().trim();
            String contraseña = txtContraseña.getText().toString().trim();

            boolean valido = true;
            if (correo.isEmpty()) {
                errorLogin.setVisibility(View.VISIBLE);
                txtLogin.setBackgroundResource(R.drawable.edittext_box_error);
                valido = false;
            } else {
                errorLogin.setVisibility(View.GONE);
                txtLogin.setBackgroundResource(R.drawable.edittext_box);
            }

            if (contraseña.isEmpty()) {
                errorContraseña.setVisibility(View.VISIBLE);
                txtContraseña.setBackgroundResource(R.drawable.edittext_box_error);
                valido = false;
            } else {
                errorContraseña.setVisibility(View.GONE);
                txtContraseña.setBackgroundResource(R.drawable.edittext_box);
            }

            if (!valido) {
                Toast.makeText(PantallaLogin.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = prefs.edit();
            if (checkRecordar.isChecked()) {
                editor.putString("correo", correo);
            } else {
                editor.remove("correo");
            }
            editor.apply();

            mAuth.signInWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            db.collection("usuarios").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        if (!document.exists()) {
                                            Map<String, Object> datos = new HashMap<>();
                                            datos.put("nombre", user.getDisplayName() != null ? user.getDisplayName() : "No disponible");
                                            datos.put("correo", user.getEmail());
                                            db.collection("usuarios").document(user.getUid()).set(datos);
                                        }
                                    });

                            startActivity(new Intent(this, PantallaPrincipal.class));
                            finish();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(this, "No existe ese correo, regístrate primero", Toast.LENGTH_LONG).show();
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Error al iniciar sesión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        findViewById(R.id.textRegistrarse).setOnClickListener(v ->
                startActivity(new Intent(this, PantallaRegistro.class)));

        findViewById(R.id.textTerminosPolitica).setOnClickListener(v -> {
            Intent intent = new Intent(this, LegalActivity.class);
            intent.putExtra("seccion", "terminos");
            intent.putExtra("origin", "login");
            startActivity(intent);
        });

        linkOlvidado.setOnClickListener(v ->
                startActivity(new Intent(this, RecuperarContrasena.class)));
    }
}