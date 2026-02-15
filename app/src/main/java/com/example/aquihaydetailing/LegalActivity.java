package com.example.aquihaydetailing;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


public class LegalActivity extends AppCompatActivity {


    private String origin; // para saber desde dónde se abrió


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);


        TextView textTerminos = findViewById(R.id.textTerminos);
        TextView textPrivacidad = findViewById(R.id.textPrivacidad);
        Button btnVolver = findViewById(R.id.btnVolverLogin);


        // ✅ Texto completo para Términos de uso
        String terminos = "Al utilizar la aplicación AquiHayDetailing, aceptas cumplir con nuestras condiciones de servicio. " +
                "No se permite el uso indebido, la suplantación de identidad ni la alteración de datos de otros usuarios. " +
                "Nos reservamos el derecho de suspender cuentas que infrinjan estas normas. El uso de la aplicación implica la aceptación de estas condiciones.";


        // ✅ Texto completo para Política de privacidad
        String privacidad = "La aplicación recopila datos como correo electrónico y nombre para gestionar reservas y personalizar la experiencia. " +
                "Estos datos se almacenan de forma segura y no se comparten con terceros sin tu consentimiento. " +
                "Puedes solicitar la eliminación de tus datos en cualquier momento escribiendo al soporte. " +
                "El uso de la app implica la aceptación de esta política.";


        textTerminos.setText(terminos);
        textPrivacidad.setText(privacidad);


        // ✅ Recogemos el origen desde el Intent
        origin = getIntent().getStringExtra("origin"); // "login" o "principal"


        // ✅ Acción del botón VOLVER
        btnVolver.setOnClickListener(v -> navigateBack());
    }


    private void navigateBack() {
        if ("principal".equals(origin)) {
            Intent intent = new Intent(LegalActivity.this, PantallaPrincipal.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else {
            Intent intent = new Intent(LegalActivity.this, PantallaLogin.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        finish();
    }
}

