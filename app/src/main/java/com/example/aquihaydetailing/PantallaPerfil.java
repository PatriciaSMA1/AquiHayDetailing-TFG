package com.example.aquihaydetailing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PantallaPerfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView textNombre, textCorreo, textTelefono, textReservas;
    private Button btnCerrarSesion, btnEditarPerfil;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private TextView textNombreHeader;
    private ImageView profileImage;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        textNombre = findViewById(R.id.textNombre);
        textCorreo = findViewById(R.id.textCorreo);
        textTelefono = findViewById(R.id.textTelefono);
        textReservas = findViewById(R.id.textReservas);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        View headerView = navigationView.getHeaderView(0);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);
        profileImage = headerView.findViewById(R.id.profileImage);

        View headerMenu = findViewById(R.id.headerMenu);
        menuIcon = headerMenu.findViewById(R.id.menuIcon);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            textCorreo.setText("Correo: " + user.getEmail());

            DocumentReference userDoc = db.collection("usuarios").document(user.getUid());
            userDoc.get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {

                            // Nombre
                            String nombreFirestore = document.getString("nombre");
                            String nombreFinal = (nombreFirestore != null ? nombreFirestore : "No disponible");
                            textNombre.setText("Nombre: " + nombreFinal);
                            textNombreHeader.setText(nombreFinal);

                            // Teléfono 📱
                            String telefonoFirestore = document.getString("telefono");
                            textTelefono.setText("Teléfono: " +
                                    (telefonoFirestore != null ? telefonoFirestore : "No disponible"));

                            // Imagen de perfil
                            String url = document.getString("imagenPerfil");
                            if (url != null) {
                                Glide.with(this).load(url).into(profileImage);
                            }

                        } else {
                            // Si el documento NO existe, lo creamos con datos básicos
                            String nombre = user.getDisplayName() != null ? user.getDisplayName() : "No disponible";

                            Map<String, Object> datos = new HashMap<>();
                            datos.put("nombre", nombre);
                            datos.put("correo", user.getEmail());
                            datos.put("telefono", "No disponible");

                            userDoc.set(datos)
                                    .addOnSuccessListener(aVoid -> {
                                        textNombre.setText("Nombre: " + nombre);
                                        textCorreo.setText("Correo: " + user.getEmail());
                                        textTelefono.setText("Teléfono: No disponible");
                                        textNombreHeader.setText(nombre);
                                    })
                                    .addOnFailureListener(e -> {
                                        textNombre.setText("Nombre: error");
                                        textTelefono.setText("Teléfono: error");
                                        textNombreHeader.setText("Error");
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        textNombre.setText("Nombre: error");
                        textTelefono.setText("Teléfono: error");
                        textNombreHeader.setText("Error");
                    });

            // Contador de reservas
            db.collection("reservas")
                    .whereEqualTo("usuarioId", user.getUid())
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            textReservas.setText("Reservas solicitadas: error");
                            return;
                        }
                        if (snapshots != null) {
                            int total = snapshots.size();
                            textReservas.setText("Reservas solicitadas: " + (total > 0 ? total : "ninguna"));
                        }
                    });
        }

        // Selección de imagen
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        subirImagenAFirebase(uri);
                    }
                });

        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Código corregido para Cerrar Sesión
        btnCerrarSesion.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("¿Cerrar sesión?")
                    .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // 1. Se cierra la sesion en Firebase
                        mAuth.signOut();

                        // 2. Creamos el Intent para ir al Login
                        Intent intent = new Intent(this, PantallaLogin.class);

                        // 3. ESTA ES LA CLAVE: Limpiamos toda la pila de actividades
                        // Esto elimina cualquier pantalla que hubiera abierta "detrás"
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                        // 4. Cerramos la actividad actual
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Editar perfil
        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPerfil.this, EditarPerfil.class);
            startActivity(intent);
        });

        // Menú lateral
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) startActivity(new Intent(this, PantallaPrincipal.class));
            else if (id == R.id.nav_reserva) startActivity(new Intent(this, Servicios.class));
            else if (id == R.id.nav_servicios) startActivity(new Intent(this, PantallaServicios.class));
            else if (id == R.id.nav_productos) startActivity(new Intent(this, PantallaProductos.class));
            else if (id == R.id.nav_mis_citas) startActivity(new Intent(this, PantallaMisCitas.class));
            else if (id == R.id.nav_contacto) startActivity(new Intent(this, PantallaContacto.class));
            else if (id == R.id.nav_perfil) Toast.makeText(this, "Ya estás en perfil", Toast.LENGTH_SHORT).show();

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void subirImagenAFirebase(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("profileImages/" + user.getUid() + ".jpg");

        ref.putFile(uri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                FirebaseFirestore.getInstance().collection("usuarios")
                        .document(user.getUid())
                        .update("imagenPerfil", downloadUri.toString());

                Glide.with(this).load(downloadUri).into(profileImage);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
        });
    }
}