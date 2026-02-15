package com.example.aquihaydetailing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.List;

public class PantallaProductos extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private ImageView profileImage;
    private TextView textNombreHeader;

    private RecyclerView recyclerProductos;
    private ProductosAdapter productosAdapter;
    private List<Producto> listaProductos;

    private Spinner spinnerCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_productos);

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
                Intent intent = new Intent(this, PantallaPrincipal.class);
                startActivity(intent);
            } else if (id == R.id.nav_reserva) {
                Intent intent = new Intent(this, Servicios.class);
                startActivity(intent);
            } else if (id == R.id.nav_servicios) {
                Intent intent = new Intent(this, PantallaServicios.class);
                startActivity(intent);
            } else if (id == R.id.nav_productos) {
                Toast.makeText(this, "Ya estás en productos", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_mis_citas) {
                startActivity(new Intent(this, PantallaMisCitas.class));
            } else if (id == R.id.nav_contacto) {
                startActivity(new Intent(this, PantallaContacto.class));
            } else if (id == R.id.nav_perfil) {
                Intent intent = new Intent(this, PantallaPerfil.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Spinner categorías
        spinnerCategorias = findViewById(R.id.spinnerCategorias);

        // 🔥 ADAPTER PERSONALIZADO PARA TEXTO DORADO
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // estilo cerrado
                getResources().getStringArray(R.array.categorias_array)
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown); // estilo desplegado
        spinnerCategorias.setAdapter(adapter);

        // RecyclerView
        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        // Lista de productos
        listaProductos = obtenerProductos();

        // Adapter inicial
        productosAdapter = new ProductosAdapter(this, listaProductos);
        recyclerProductos.setAdapter(productosAdapter);

        // Listener del Spinner
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = parent.getItemAtPosition(position).toString();
                filtrarPorCategoria(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

    // Filtro por categoría
    private void filtrarPorCategoria(String categoria) {
        if (categoria.equals("Todos")) {
            productosAdapter = new ProductosAdapter(this, listaProductos);
        } else {
            List<Producto> filtrados = new ArrayList<>();
            for (Producto p : listaProductos) {
                if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)) {
                    filtrados.add(p);
                }
            }
            productosAdapter = new ProductosAdapter(this, filtrados);
        }
        recyclerProductos.setAdapter(productosAdapter);
    }

    // Lista local de productos con categoría incluida
    private List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();

        productos.add(new Producto(
                "LEATHER CLEANER",
                "Limpiador de tapicerías interior cuero/piel",
                "https://fullcarx.com/3005-large_default/leather-cleaner-limpia-tapicerias-interior-cueropiel.jpg",
                "Limpieza interior + tapicería",
                "https://fullcarx.com/es/tapiceria/leather-cleaner-limpia-tapicerias-interior-cueropiel",
                "Interior"
        ));

        productos.add(new Producto(
                "WHEEL CLEANER",
                "Limpiador de llantas",
                "https://fullcarx.com/3001-large_default/wheel-cleaner-limpia-llantas.jpg",
                "Limpieza exterior, limpieza completa",
                "https://fullcarx.com/es/llantas/wheel-cleaner-limpia-llantas",
                "Exterior"
        ));

        productos.add(new Producto(
                "GLASS CLEANER",
                "Limpia cristales",
                "https://fullcarx.com/3014-large_default/glass-cleaner-limpia-cristales.jpg",
                "Limpieza exterior, limpieza completa, limpieza interior",
                "https://fullcarx.com/es/cristales/glass-cleaner-limpia-cristales",
                "Exterior"
        ));

        productos.add(new Producto(
                "DASHBOARD CLEANER",
                "Limpia salpicaderos",
                "https://fullcarx.com/3002-large_default/dashboard-cleaner-limpia-salpicaderos.jpg",
                "Limpieza interior (sin tapicería), limpieza interior + tapicería",
                "https://fullcarx.com/es/salpicadero/dashboard-cleaner-limpia-salpicaderos",
                "Interior"
        ));

        productos.add(new Producto(
                "CAR VISION Aditivo",
                "Tratamiento Anti-Lluvia Limpiaparabrisas",
                "https://fullcarx.com/3499-large_default/car-vision-aditivo-limpiaparabrisas-anti-lluvia.jpg",
                "Servicios extra, tratamiento antilluvia",
                "https://fullcarx.com/es/cristales/car-vision-aditivo-limpiaparabrisas-anti-lluvia",
                "Servicios extra"
        ));

        productos.add(new Producto(
                "KIT POLISH ",
                "Kit completo de pulido de faros",
                "https://fullcarx.com/3169-large_default/pack-pulido-completo-fcx.jpg",
                "Servicios extra, pulido de faros",
                "https://fullcarx.com/es/kits-pulido/pack-pulido-completo-fcx",
                "Servicios extra"
        ));

        productos.add(new Producto(
                "INSECT REMOVER ",
                "Limpia insectos",
                "https://fullcarx.com/3010-large_default/insect-remover-limpia-insectos.jpg",
                "Limpieza exterior, limpieza completa",
                "https://fullcarx.com/es/limpiadores-especificos/insect-remover-limpia-insectos",
                "Exterior"
        ));

        productos.add(new Producto(
                "SNOW FOAM",
                "Champú Activo para limpieza de carrocería",
                "https://fullcarx.com/3008-large_default/snow-foam-champu-activo-ph-neutro.jpg",
                "Limpieza exterior, limpieza completa",
                "https://fullcarx.com/es/champu/snow-foam-champu-activo-ph-neutro",
                "Exterior"
        ));

        productos.add(new Producto(
                "SCREEN CLEANER",
                "Limpia pantallas 100 ml",
                "https://fullcarx.com/3042-large_default/screen-cleaner-limpiador-pantallas-y-piano-black-100ml.jpg",
                "Limpieza interior (sin tapicería), limpieza interior + tapicería",
                "https://fullcarx.com/es/salpicadero/screen-cleaner-limpiador-pantallas-y-piano-black-100ml",
                "Interior"
        ));

        productos.add(new Producto(
                "TIRE DETAILER ",
                " Recuperador de Neumáticos",
                "https://fullcarx.com/3020-large_default/tire-detailer-recuperador-de-neumaticos.jpg",
                "Limpieza exterior, limpieza completa",
                "https://fullcarx.com/es/",
                "Interior"
        ));

        return productos;
    }
}

