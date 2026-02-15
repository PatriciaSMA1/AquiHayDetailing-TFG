package com.example.aquihaydetailing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditarPerfil extends AppCompatActivity {

    private ImageView imgPerfil;
    private EditText editNombre, editCorreo;
    private Button btnCambiarFoto, btnEliminarFoto, btnGuardar, btnCancelar;

    private Uri imagenSeleccionada = null;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private static final int REQUEST_GALERIA = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        imgPerfil = findViewById(R.id.imgPerfil);
        editNombre = findViewById(R.id.editNombre);
        editCorreo = findViewById(R.id.editCorreo);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnEliminarFoto = findViewById(R.id.btnEliminarFoto);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);

        cargarDatosUsuario();

        btnCambiarFoto.setOnClickListener(v -> abrirGaleria());
        btnEliminarFoto.setOnClickListener(v -> eliminarFoto());
        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String nombre = document.getString("nombre");
                        String correo = document.getString("correo");
                        String fotoUrl = document.getString("imagenPerfil");

                        if (nombre != null) editNombre.setText(nombre);
                        if (correo != null) editCorreo.setText(correo);
                        else editCorreo.setText(user.getEmail());

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .circleCrop()
                                    .into(imgPerfil);
                        }
                    }
                });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALERIA && resultCode == Activity.RESULT_OK && data != null) {
            imagenSeleccionada = data.getData();
            Glide.with(this)
                    .load(imagenSeleccionada)
                    .circleCrop()
                    .into(imgPerfil);
        }
    }

    private void guardarCambios() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        String nuevoNombre = editNombre.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios").document(uid)
                .update("nombre", nuevoNombre)
                .addOnSuccessListener(aVoid -> {
                    if (imagenSeleccionada != null) {
                        subirFoto(uid);
                    } else {
                        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void subirFoto(String uid) {
        StorageReference fotoRef = storageRef.child("profileImages/" + uid + ".jpg");


        fotoRef.putFile(imagenSeleccionada)
                .addOnSuccessListener(taskSnapshot ->
                        fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            db.collection("usuarios").document(uid)
                                    .update("imagenPerfil", uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        })
                );
    }

    private void eliminarFoto() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        StorageReference fotoRef = storageRef.child("profileImages/" + uid + ".jpg");

        fotoRef.delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("usuarios").document(uid)
                            .update("imagenPerfil", "")
                            .addOnSuccessListener(unused -> {

                                // Imagen por defecto
                                imgPerfil.setImageResource(R.drawable.circle_background); // o usa setImageDrawable(null)

                                imagenSeleccionada = null;

                                Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "No se encontró la imagen en Storage", Toast.LENGTH_SHORT).show();
                });
    }
}