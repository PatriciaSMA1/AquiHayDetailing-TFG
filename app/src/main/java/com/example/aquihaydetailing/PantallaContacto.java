package com.example.aquihaydetailing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PantallaContacto extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private ImageView profileImage;
    private TextView textNombreHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_contacto);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // ✅ Referencias al header del menú lateral
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);

        // ✅ Cargar datos del usuario
        cargarDatosUsuario();

        // Menú superior
        View headerMenu = findViewById(R.id.headerMenu);
        menuIcon = headerMenu.findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navegación lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();


            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, PantallaPrincipal.class);
                startActivity(intent);
            } else if (id == R.id.nav_reserva) {
                Intent intent = new Intent(this, Servicios.class);
                startActivity(intent);
            } else if (id == R.id.nav_servicios) {
                Intent intent = new Intent(this, PantallaServicios.class);
                startActivity(intent);
            } else if (id == R.id.nav_productos) {
                Intent intent = new Intent(this, PantallaProductos.class);
                startActivity(intent);
            } else if (id == R.id.nav_mis_citas) {
                startActivity(new Intent(this, PantallaMisCitas.class));
            } else if (id == R.id.nav_contacto) {
                Toast.makeText(this, "Ya estás en contacto", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_perfil) {
                Intent intent = new Intent(this, PantallaPerfil.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Acciones de contacto
        findViewById(R.id.cardTelefono).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+34674629155"));
            startActivity(intent);
        });

        findViewById(R.id.cardWhatsapp).setOnClickListener(v -> {
            String url = "https://wa.me/34674629155?text=Hola,%20quiero%20información%20sobre%20detailing";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });

        findViewById(R.id.cardEmail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:info@aquihaydetailing.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app");
            startActivity(intent);
        });

        findViewById(R.id.cardInstagram).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/aquihaydetailing")));
        });

        findViewById(R.id.cardTiktok).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@aquihaydetailing")));
        });
    }

    // ✅ Método para cargar nombre y foto en el menú lateral
    private void cargarDatosUsuario() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        String fotoUrl = document.getString("imagenPerfil");


                        if (nombre != null) {
                            textNombreHeader.setText(nombre);
                        }


                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .circleCrop()
                                    .into(profileImage);
                        }
                    }
                });
    }
}





