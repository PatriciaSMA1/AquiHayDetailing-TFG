package com.example.aquihaydetailing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {

    private EditText editCorreo;
    private Button btnEnviar, btnVolverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_contrasena);


        editCorreo = findViewById(R.id.editCorreoRecuperar);
        btnEnviar = findViewById(R.id.btnEnviarRecuperacion);
        btnVolverLogin = findViewById(R.id.btnVolverLogin); // ✅ nuevo botón


        btnEnviar.setOnClickListener(v -> {
            String correo = editCorreo.getText().toString().trim();


            if (correo.isEmpty()) {
                Toast.makeText(this, "Introduce tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(correo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Correo enviado. Revisa tu bandeja de entrada", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // ✅ acción del botón para volver al login
        btnVolverLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RecuperarContrasena.this, PantallaLogin.class);
            startActivity(intent);
            finish();
        });
    }
}







