package com.example.aquihaydetailing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;


import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Arrays;
import java.util.List;

public class PantallaPrincipal extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;

    private ImageView profileImage;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal);

        // Carrusel
        viewPager = findViewById(R.id.viewPager);
        ImageButton flechaIzquierda = findViewById(R.id.flechaIzquierda);
        ImageButton flechaDerecha = findViewById(R.id.flechaDerecha);

        // Redes sociales
        ImageView Instagram = findViewById(R.id.iconInstagram);
        ImageView TikTok = findViewById(R.id.iconTikTok);
        ImageView Whatsapp = findViewById(R.id.iconWhatsapp);

        Instagram.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.instagram.com/aquihaydetailing"))));

        TikTok.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.tiktok.com/@aquihaydetailing"))));

        Whatsapp.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://wa.me/34643320918"))));

        List<Integer> imagenes = Arrays.asList(
                R.drawable.servicio1,
                R.drawable.servicio2,
                R.drawable.servicio3,
                R.drawable.servicio4,
                R.drawable.servicio5
        );

        List<String> titulos = Arrays.asList(
                "Limpieza interior (sin tapicería)",
                "Limpieza interior + tapicería",
                "Limpieza exterior",
                "Limpieza completa",
                "Servicios extra"
        );

        List<String> descripciones = Arrays.asList(
                "Detalle profundo en salpicadero, rejillas y zonas visibles.",
                "Limpieza completa con tratamiento en asientos y textiles.",
                "Lavado profesional con acabado brillante y sin marcas.",
                "Interior y exterior con enfoque integral y resultados premium.",
                "Añade protección cerámica, desinfección o pulido personalizado."
        );

        Carrusel adapter = new Carrusel(imagenes, titulos, descripciones);
        viewPager.setAdapter(adapter);

        handler = new Handler(Looper.getMainLooper());
        runnable = () -> {
            currentPage = (currentPage + 1) % imagenes.size();
            viewPager.setCurrentItem(currentPage, true);
            handler.postDelayed(runnable, 3000);
        };
        handler.postDelayed(runnable, 3000);

        flechaIzquierda.setOnClickListener(v -> {
            int prev = viewPager.getCurrentItem() - 1;
            if (prev >= 0) viewPager.setCurrentItem(prev, true);
        });

        flechaDerecha.setOnClickListener(v -> {
            int next = viewPager.getCurrentItem() + 1;
            if (next < imagenes.size()) viewPager.setCurrentItem(next, true);
        });

        // Imagen de perfil personalizada
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);

        // ✅ Nombre del usuario en el menú lateral
        TextView textNombreHeader = headerView.findViewById(R.id.textNombreHeader);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(user.getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String nombreFirestore = document.getString("nombre");
                            textNombreHeader.setText(nombreFirestore != null ? nombreFirestore : "No disponible");


                            // ✅ Cargar imagen desde Firestore
                            String url = document.getString("imagenPerfil");
                            if (url != null) {
                                Glide.with(this).load(url).into(profileImage);
                            }
                        } else {
                            String nombre = user.getDisplayName() != null ? user.getDisplayName() : "No disponible";
                            textNombreHeader.setText(nombre);
                        }
                    })
                    .addOnFailureListener(e -> textNombreHeader.setText("Error"));
        }

        // ✅ Selector de imagen y subida a Firebase Storage
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        subirImagenAFirebase(uri);
                    }
                });

        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Abrir menú lateral desde icono
        ImageView menuIcon = findViewById(R.id.menuIcon);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Manejo de clics en el menú lateral
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(this, PantallaContacto.class);
                startActivity(intent);
            } else if (id == R.id.nav_perfil) {
                Intent intent = new Intent(this, PantallaPerfil.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ✅ Enlaces legales
        TextView textTerminos = findViewById(R.id.textTerminos);
        TextView textPolitica = findViewById(R.id.textPolitica);

        textTerminos.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, LegalActivity.class);
            intent.putExtra("seccion", "terminos");
            intent.putExtra("origin", "principal");
            startActivity(intent);
        });

        textPolitica.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaPrincipal.this, LegalActivity.class);
            intent.putExtra("seccion", "privacidad");
            intent.putExtra("origin", "principal");
            startActivity(intent);
        });
    }

    // ✅ Subir imagen a Firebase Storage y guardar URL en Firestore
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


                // ✅ Mostrar imagen en pantalla
                Glide.with(this).load(downloadUri).into(profileImage);
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
            Log.e("ImagenPerfil", "Error al subir: " + e.getMessage());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}