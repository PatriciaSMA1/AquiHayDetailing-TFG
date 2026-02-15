package com.example.aquihaydetailing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PantallaServicios extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private ImageView profileImage;
    private TextView textNombreHeader;

    private RecyclerView recyclerServicios;
    private ServiciosAdapter serviciosAdapter;
    private List<Servicio> listaServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicios);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // Header del menú lateral
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);

        cargarDatosUsuario();

        // Header superior
        View headerMenu = findViewById(R.id.headerMenu);
        menuIcon = headerMenu.findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navegación lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();


            if (id == R.id.nav_home) {
                startActivity(new Intent(this, PantallaPrincipal.class));
            } else if (id == R.id.nav_reserva) {
                startActivity(new Intent(this, Servicios.class));
            } else if (id == R.id.nav_servicios) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_productos) {
                startActivity(new Intent(this, PantallaProductos.class));
            } else if (id == R.id.nav_mis_citas) {
                startActivity(new Intent(this, PantallaMisCitas.class));
            } else if (id == R.id.nav_contacto) {
                startActivity(new Intent(this, PantallaContacto.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PantallaPerfil.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // RecyclerView
        recyclerServicios = findViewById(R.id.recyclerServicios);
        recyclerServicios.setLayoutManager(new LinearLayoutManager(this));

        // Lista de servicios
        listaServicios = obtenerServicios();

        // Adapter
        serviciosAdapter = new ServiciosAdapter(this, listaServicios, servicio -> {
            Intent intent = new Intent(PantallaServicios.this, Servicios.class);
            intent.putExtra("servicioSeleccionado", servicio.getNombre());
            startActivity(intent);
        });

        recyclerServicios.setAdapter(serviciosAdapter);
    }

    // Cargar nombre y foto en el menú lateral
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

    // Lista real de servicios (SIN descripción)
    private List<Servicio> obtenerServicios() {
        List<Servicio> servicios = new ArrayList<>();

        servicios.add(new Servicio(
                "Limpieza interior (sin tapicería)",
                Arrays.asList(
                        "Limpieza al detalle de salpicadero y consola central",
                        "Limpieza de puertas + marcos",
                        "Aspirado de alfombrillas, moqueta y maletero",
                        "Aspirado de asientos (tela / cuero / alcántara)",
                        "Limpieza de cristales por dentro y fuera"
                ),
                "2 - 2,5 horas"
        ));

        servicios.add(new Servicio(
                "Limpieza interior + tapicería",
                Arrays.asList(
                        "Limpieza al detalle de salpicadero y consola central",
                        "Limpieza de puertas + marcos",
                        "Aspirado de alfombrillas, moqueta y maletero",
                        "Aspirado de asientos (tela / cuero / alcántara)",
                        "Limpieza de cristales por dentro y fuera",
                        "Limpieza profunda de asientos + hidratado",
                        "Limpieza profunda de alfombrillas y moquetas"
                ),
                "2 - 4 horas"
        ));

        servicios.add(new Servicio(
                "Limpieza exterior",
                Arrays.asList(
                        "Limpieza al detalle de carrocería",
                        "Limpieza y descontaminación de llantas",
                        "Abrillantado de neumáticos",
                        "Limpieza de cristales por dentro y fuera",
                        "Eliminación de resina, alquitrán y mosquitos"
                ),
                "1 hora"
        ));

        servicios.add(new Servicio(
                "Limpieza completa interior + exterior",
                Arrays.asList(
                        "Incluye todo lo del interior + tapicería",
                        "Incluye todo lo de la limpieza exterior"
                ),
                "3 - 5 horas"
        ));
        return servicios;
    }
}
