package com.example.aquihaydetailing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Reserva extends AppCompatActivity {

    private TextView textFecha, textTiempoEstimado, textPrecio;
    private RecyclerView recyclerHorarios;
    private Button btnReservar, btnAbrirCalendario;
    private RadioGroup radioGrupoTamaño;

    private CheckBox checkOzono, checkAntilluvia, checkFaros;

    private EditText txtDireccion;

    private LocalDate fechaSeleccionada;
    private String horarioSeleccionado = null;
    private String servicioClave = "";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private ImageView profileImage;
    private TextView textNombreHeader;

    private ActivityResultLauncher<String> pickImageLauncher;

    // DESPLEGABLES
    private LinearLayout headerTamano, contenidoTamano;
    private LinearLayout headerExtras, contenidoExtras;
    private LinearLayout headerHorarios;
    private TextView iconTamano, iconExtras, iconHorarios;

    // ⭐ NUEVO: icono de información
    private ImageView infoTamano;

    // TIEMPOS ESTIMADOS
    private final Map<String, String> tiempos = new HashMap<String, String>() {{
        put("interior_sin_tapiceria", "2 - 2.5 horas");
        put("interior_con_tapiceria", "2 - 4 horas");
        put("exterior", "1 hora");
        put("completa", "2.5 - 4 horas");
    }};

    // PRECIOS POR TAMAÑO
    private final Map<String, Integer[]> precios = new HashMap<String, Integer[]>() {{
        put("interior_sin_tapiceria", new Integer[]{40, 45, 50});
        put("interior_con_tapiceria", new Integer[]{65, 75, 80});
        put("exterior", new Integer[]{15, 20, 25});
        put("completa", new Integer[]{90, 100, 120});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reserva_citas);

        // REFERENCIAS XML
        textFecha = findViewById(R.id.textFecha);
        textTiempoEstimado = findViewById(R.id.textTiempoEstimado);
        textPrecio = findViewById(R.id.textPrecio);
        radioGrupoTamaño = findViewById(R.id.radioGrupoTamaño);

        checkOzono = findViewById(R.id.checkOzono);
        checkAntilluvia = findViewById(R.id.checkAntilluvia);
        checkFaros = findViewById(R.id.checkFaros);

        recyclerHorarios = findViewById(R.id.recyclerHorarios);
        btnReservar = findViewById(R.id.btnReservar);
        btnAbrirCalendario = findViewById(R.id.btnAbrirCalendario);

        txtDireccion = findViewById(R.id.txtDireccion);

        menuIcon = findViewById(R.id.menuIcon);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // DESPLEGABLES
        headerTamano = findViewById(R.id.headerTamano);
        contenidoTamano = findViewById(R.id.contenidoTamano);
        iconTamano = findViewById(R.id.iconTamano);

        headerExtras = findViewById(R.id.headerExtras);
        contenidoExtras = findViewById(R.id.contenidoExtras);
        iconExtras = findViewById(R.id.iconExtras);

        headerHorarios = findViewById(R.id.headerHorarios);
        iconHorarios = findViewById(R.id.iconHorarios);

        // ⭐ NUEVO: referencia al icono de información
        infoTamano = findViewById(R.id.infoTamano);

        // HEADER DEL MENÚ
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profileImage);
        textNombreHeader = headerView.findViewById(R.id.textNombreHeader);

        // OBTENER SERVICIO SELECCIONADO
        servicioClave = getIntent().getStringExtra("servicio");
        if (servicioClave == null) servicioClave = "";

        // MOSTRAR TIEMPO ESTIMADO
        if (tiempos.containsKey(servicioClave)) {
            textTiempoEstimado.setText("Tiempo estimado: " + tiempos.get(servicioClave));
        }

        // ACTUALIZAR PRECIO
        radioGrupoTamaño.setOnCheckedChangeListener((group, checkedId) -> actualizarPrecio());
        checkOzono.setOnCheckedChangeListener((b, c) -> actualizarPrecio());
        checkAntilluvia.setOnCheckedChangeListener((b, c) -> actualizarPrecio());
        checkFaros.setOnCheckedChangeListener((b, c) -> actualizarPrecio());

        // FECHA INICIAL
        fechaSeleccionada = LocalDate.now();
        actualizarTextoFecha(fechaSeleccionada);

        // HORARIOS
        List<String> horariosDinamicos = generarHorariosDinamicos(servicioClave);
        recyclerHorarios.setLayoutManager(new LinearLayoutManager(this));
        HorarioAdapter adapter = new HorarioAdapter(horariosDinamicos, selected -> horarioSeleccionado = selected);
        recyclerHorarios.setAdapter(adapter);

        // CALENDARIO
        btnAbrirCalendario.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona una fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            picker.show(getSupportFragmentManager(), "fechaPicker");

            picker.addOnPositiveButtonClickListener(selection -> {
                fechaSeleccionada = Instant.ofEpochMilli(selection)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                actualizarTextoFecha(fechaSeleccionada);
            });
        });

        // BOTÓN RESERVAR
        btnReservar.setOnClickListener(v -> guardarReserva());

        // MENÚ LATERAL
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) startActivity(new Intent(this, PantallaPrincipal.class));
            else if (id == R.id.nav_reserva) startActivity(new Intent(this, Servicios.class));
            else if (id == R.id.nav_servicios)
                startActivity(new Intent(this, PantallaServicios.class));
            else if (id == R.id.nav_productos)
                startActivity(new Intent(this, PantallaProductos.class));
            else if (id == R.id.nav_mis_citas)
                startActivity(new Intent(this, PantallaMisCitas.class));
            else if (id == R.id.nav_contacto)
                startActivity(new Intent(this, PantallaContacto.class));
            else if (id == R.id.nav_perfil) startActivity(new Intent(this, PantallaPerfil.class));

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        cargarDatosUsuario();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) subirImagenAFirebase(uri);
                });

        profileImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        configurarDesplegables();

        corregirReservasMalGuardadas();

        // ⭐⭐⭐ NUEVO: INFORMACIÓN DEL TAMAÑO DEL VEHÍCULO ⭐⭐⭐
        infoTamano.setOnClickListener(v -> {

            int seleccionado = radioGrupoTamaño.getCheckedRadioButtonId();
            String mensaje;

            if (seleccionado == R.id.radioPequeno) {
                mensaje = "Vehículos pequeños:\n- Fiat 500\n- Seat Ibiza\n- Renault Clio\n- Utilitarios compactos";

            } else if (seleccionado == R.id.radioMediano) {
                mensaje = "Vehículos medianos:\n- Seat León\n- Nissan Qashqai\n- Hyundai Tucson\n- Berlinas y SUV medios";

            } else if (seleccionado == R.id.radioGrande) {
                mensaje = "Vehículos grandes:\n- Kia Sorento\n- VW Sharan\n- Furgonetas tipo Transporter\n- SUV grandes";

            } else {
                mensaje = "Selecciona un tamaño para ver la información.";
            }

            new AlertDialog.Builder(Reserva.this)
                    .setTitle("Información del vehículo")
                    .setMessage(mensaje)
                    .setPositiveButton("Entendido", null)
                    .show();
        });

    }

    // -------------------------------
    // RESTO DEL CÓDIGO SIN CAMBIOS
    // -------------------------------

    private void configurarDesplegables() {
        headerTamano.setOnClickListener(v -> {
            if (contenidoTamano.getVisibility() == View.VISIBLE) {
                contenidoTamano.setVisibility(View.GONE);
                iconTamano.setText("▼");
            } else {
                contenidoTamano.setVisibility(View.VISIBLE);
                iconTamano.setText("▲");
            }
        });

        headerExtras.setOnClickListener(v -> {
            if (contenidoExtras.getVisibility() == View.VISIBLE) {
                contenidoExtras.setVisibility(View.GONE);
                iconExtras.setText("▼");
            } else {
                contenidoExtras.setVisibility(View.VISIBLE);
                iconExtras.setText("▲");
            }
        });

        headerHorarios.setOnClickListener(v -> {
            if (recyclerHorarios.getVisibility() == View.VISIBLE) {
                recyclerHorarios.setVisibility(View.GONE);
                iconHorarios.setText("▼");
            } else {
                recyclerHorarios.setVisibility(View.VISIBLE);
                iconHorarios.setText("▲");
            }
        });
    }

    private List<String> generarHorariosDinamicos(String servicioClave) {
        int duracionHoras = 1;

        switch (servicioClave) {
            case "interior_sin_tapiceria":
                duracionHoras = 2;
                break;
            case "interior_con_tapiceria":
                duracionHoras = 2;
                break;
            case "exterior":
                duracionHoras = 1;
                break;
            case "completa":
                duracionHoras = 3;
                break;
        }

        List<String> lista = new ArrayList<>();

        int horaInicio = 8;
        int minutoInicio = 30;

        while (horaInicio < 22) {

            int finHora = horaInicio + duracionHoras;
            if (finHora > 23) break;

            String inicio = String.format("%02d:%02d", horaInicio, minutoInicio);
            String fin = String.format("%02d:%02d", finHora, minutoInicio);

            lista.add(inicio + " - " + fin);

            horaInicio += duracionHoras;
        }

        return lista;
    }

    private void actualizarPrecio() {
        if (!precios.containsKey(servicioClave)) {
            textPrecio.setText("Precio: -");
            return;
        }

        Integer[] preciosServicio = precios.get(servicioClave);
        int index = 0;

        int checkedId = radioGrupoTamaño.getCheckedRadioButtonId();
        if (checkedId == R.id.radioMediano) index = 1;
        else if (checkedId == R.id.radioGrande) index = 2;

        int precioBase = preciosServicio[index];

        int precioExtras = 0;
        if (checkOzono.isChecked()) precioExtras += 5;
        if (checkAntilluvia.isChecked()) precioExtras += 5;
        if (checkFaros.isChecked()) precioExtras += 20;

        int precioTotal = precioBase + precioExtras;

        textPrecio.setText(String.format(Locale.getDefault(), "Precio: %d €", precioTotal));
    }

    private void guardarReserva() {

        if (horarioSeleccionado == null) {
            Toast.makeText(this, "Selecciona un horario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (radioGrupoTamaño.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona el tamaño del vehículo", Toast.LENGTH_SHORT).show();
            return;
        }

        String direccion = txtDireccion.getText().toString().trim();
        if (direccion.isEmpty()) {
            Toast.makeText(this, "Introduce una dirección para completar la reserva", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        Map<String, Object> extras = new HashMap<>();
        extras.put("servicio", servicioClave);
        extras.put("fecha", fechaSeleccionada.toString());
        extras.put("horario", horarioSeleccionado);
        extras.put("tamaño", obtenerTamañoSeleccionado());
        extras.put("precio", textPrecio.getText().toString());
        extras.put("direccion", direccion);
        extras.put("timestamp", System.currentTimeMillis());

        List<String> extrasOpcionales = new ArrayList<>();
        if (checkOzono.isChecked()) extrasOpcionales.add("Tratamiento de ozono");
        if (checkAntilluvia.isChecked()) extrasOpcionales.add("Tratamiento antilluvia");
        if (checkFaros.isChecked()) extrasOpcionales.add("Pulido de faros");

        Map<String, Object> reserva = new HashMap<>();
        reserva.put("usuarioId", uid);
        reserva.put("estado", "pendiente");
        reserva.put("extras", extras);
        reserva.put("extrasOpcionales", extrasOpcionales);

        FirebaseFirestore.getInstance().collection("reservas")
                .add(reserva)
                .addOnSuccessListener(doc -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Reserva confirmada")
                            .setMessage("Tu cita ha sido guardada correctamente.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar la reserva", Toast.LENGTH_LONG).show();
                });
    }

    private String obtenerTamañoSeleccionado() {
        int id = radioGrupoTamaño.getCheckedRadioButtonId();
        if (id == R.id.radioPequeno) return "Pequeño";
        if (id == R.id.radioMediano) return "Mediano";
        return "Grande";
    }

    private void actualizarTextoFecha(LocalDate fecha) {
        String diaSemana = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String mes = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String texto = "ESPACIOS DISPONIBLES PARA EL " +
                diaSemana.toUpperCase() + " " + fecha.getDayOfMonth() + " " + mes.toUpperCase();
        textFecha.setText(texto);
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombreFirestore = document.getString("nombre");
                        textNombreHeader.setText(nombreFirestore != null ? nombreFirestore : "No disponible");

                        String url = document.getString("imagenPerfil");
                        if (url != null) Glide.with(this).load(url).into(profileImage);
                    }
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
                        .update("imagenPerfil", downloadUri.toString())
                        .addOnSuccessListener(aVoid -> {
                            Glide.with(this).load(downloadUri).into(profileImage);
                            Toast.makeText(this, "Imagen actualizada correctamente", Toast.LENGTH_SHORT).show();
                        });
            });
        });
    }

    private void corregirReservasMalGuardadas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("reservas")
                .whereEqualTo("usuarioId", uid)
                .get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) {
                        Object extrasObj = doc.get("extras");

                        if (!(extrasObj instanceof Map)) continue;

                        Map<String, Object> extras = (Map<String, Object>) extrasObj;
                        List<String> extrasOpcionales = new ArrayList<>();

                        List<String> clavesNumericas = new ArrayList<>();
                        for (String key : extras.keySet()) {
                            if (key.matches("\\d+")) {
                                Object valor = extras.get(key);
                                if (valor instanceof String) extrasOpcionales.add((String) valor);
                                clavesNumericas.add(key);
                            }
                        }

                        // Eliminar claves numéricas incorrectas
                        for (String clave : clavesNumericas) {
                            extras.remove(clave);
                        }

                        // Preparar actualización
                        Map<String, Object> actualizacion = new HashMap<>();
                        actualizacion.put("extras", extras);
                        actualizacion.put("extrasOpcionales", extrasOpcionales);

                        // Guardar corrección
                        db.collection("reservas")
                                .document(doc.getId())
                                .update(actualizacion);
                    }
                });
    }
}