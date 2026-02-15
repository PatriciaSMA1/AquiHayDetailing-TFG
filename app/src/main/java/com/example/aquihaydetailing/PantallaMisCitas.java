package com.example.aquihaydetailing;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.view.View;


public class PantallaMisCitas extends AppCompatActivity {


    private RecyclerView recyclerCitas;
    private CitasAdapter citasAdapter;
    private List<Cita> listaCitas;


    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageView menuIcon;


    // ✅ Referencias al header del menú
    private ImageView profileImage;
    private TextView textNombreHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_citas);


        // REFERENCIAS DEL MENÚ LATERAL
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuIcon = findViewById(R.id.menuIcon);


        // ✅ OBTENER HEADER DEL MENÚ
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);


        // ✅ CARGAR DATOS DEL USUARIO (foto + nombre)
        cargarDatosUsuario();


        // Abrir menú lateral desde icono
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));


        // Manejo de clics en el menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();


            if (id == R.id.nav_home) {
                startActivity(new Intent(this, PantallaPrincipal.class));
            } else if (id == R.id.nav_reserva) {
                startActivity(new Intent(this, Servicios.class));
            } else if (id == R.id.nav_servicios) {
                startActivity(new Intent(this, PantallaServicios.class));
            } else if (id == R.id.nav_productos) {
                startActivity(new Intent(this, PantallaProductos.class));
            } else if (id == R.id.nav_mis_citas) {
                Toast.makeText(this, "Ya estás en mis citas", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_contacto) {
                startActivity(new Intent(this, PantallaContacto.class));
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PantallaPerfil.class));
            }


            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // RECYCLER VIEW
        recyclerCitas = findViewById(R.id.recyclerCitas);
        recyclerCitas.setLayoutManager(new LinearLayoutManager(this));


        cargarCitas();
    }


    // ✅ MÉTODO PARA CARGAR FOTO Y NOMBRE EN EL MENÚ
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


    private void cargarCitas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("CITA_DEBUG", "UID actual del usuario logueado: " + uid);


        FirebaseFirestore.getInstance()
                .collection("reservas")
                .whereEqualTo("usuarioId", uid)
                .get()
                .addOnSuccessListener(query -> {


                    listaCitas = new ArrayList<>();


                    for (DocumentSnapshot doc : query) {


                        Object extrasObj = doc.get("extras");
                        if (!(extrasObj instanceof Map)) continue;


                        Map<String, Object> extras = (Map<String, Object>) extrasObj;


                        String servicio = (String) extras.get("servicio");
                        String fecha = (String) extras.get("fecha");
                        String horario = (String) extras.get("horario");
                        String precio = (String) extras.get("precio");
                        String tamaño = (String) extras.get("tamaño");
                        String estado = (String) doc.get("estado");


                        if (servicio != null && fecha != null && horario != null) {
                            listaCitas.add(new Cita(servicio, fecha, horario, precio, tamaño, estado));
                        }
                    }


                    citasAdapter = new CitasAdapter(this, listaCitas);
                    recyclerCitas.setAdapter(citasAdapter);


                    if (listaCitas.isEmpty()) {
                        Toast.makeText(this, "No tienes citas reservadas", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar las citas", Toast.LENGTH_SHORT).show();
                    Log.e("CITA_DEBUG", "Error al cargar: ", e);
                });
    }
}
