package com.example.aquihaydetailing;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class Servicios extends AppCompatActivity {

    private RecyclerView recyclerServicios;
    private final List<String> servicios = Arrays.asList(
            "Limpieza interior (sin tapicería)",
            "Limpieza interior + tapicería",
            "Limpieza exterior",
            "Limpieza completa"
    );

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private ImageView profileImage;
    private TextView textNombreHeader;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ⭐ NUEVO: Cargar layout según orientación
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.servicios_lista);   // layout horizontal (cajas amarillas)
        } else {
            setContentView(R.layout.servicios);         // layout vertical (normal)
        }

        recyclerServicios = findViewById(R.id.recyclerServicios);
        recyclerServicios.setLayoutManager(new LinearLayoutManager(this));

        ServicioAdapter adapter = new ServicioAdapter(servicios, servicio -> {
            String claveServicio = "";

            switch (servicio) {
                case "Limpieza interior (sin tapicería)":
                    claveServicio = "interior_sin_tapiceria";
                    break;
                case "Limpieza interior + tapicería":
                    claveServicio = "interior_con_tapiceria";
                    break;
                case "Limpieza exterior":
                    claveServicio = "exterior";
                    break;
                case "Limpieza completa":
                    claveServicio = "completa";
                    break;
            }

            Intent intent = new Intent(Servicios.this, Reserva.class);
            intent.putExtra("servicio", claveServicio);
            startActivity(intent);
        });

        recyclerServicios.setAdapter(adapter);

        menuIcon = findViewById(R.id.menuIcon);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) startActivity(new Intent(this, PantallaPrincipal.class));
            else if (id == R.id.nav_reserva) startActivity(new Intent(this, Servicios.class));
            else if (id == R.id.nav_servicios) startActivity(new Intent(this, PantallaServicios.class));
            else if (id == R.id.nav_productos) startActivity(new Intent(this, PantallaProductos.class));
            else if (id == R.id.nav_mis_citas) startActivity(new Intent(this, PantallaMisCitas.class));
            else if (id == R.id.nav_contacto) startActivity(new Intent(this, PantallaContacto.class));
            else if (id == R.id.nav_perfil) startActivity(new Intent(this, PantallaPerfil.class));

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(user.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String nombreFirestore = document.getString("nombre");
                            textNombreHeader.setText(nombreFirestore != null ? nombreFirestore : "No disponible");

                            String url = document.getString("imagenPerfil");
                            if (url != null) Glide.with(this).load(url).into(profileImage);
                        } else {
                            String nombre = user.getDisplayName() != null ? user.getDisplayName() : "No disponible";
                            textNombreHeader.setText(nombre);
                        }
                    })
                    .addOnFailureListener(e -> textNombreHeader.setText("Error"));
        }

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) subirImagenAFirebase(uri);
                });

        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
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
                        .update("imagenPerfil", downloadUri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Glide.with(this).load(downloadUri).into(profileImage);
                            Toast.makeText(this, "Imagen actualizada correctamente", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al guardar URL en Firestore", Toast.LENGTH_SHORT).show();
                        });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
        });
    }
}