package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lsepulveda.fondos_de_pantalla.MainActivityAdministrador;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;
import static com.google.firebase.storage.FirebaseStorage.getInstance;
import static android.app.Activity.RESULT_OK;

import java.util.HashMap;


public class PerfilAdmin extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;

    StorageReference storageReference;
    //carpeta que se va a crear en la DB
    String rutaDeAlmacenamiento = "Fotos_Perfil_Administradores/";

    private Uri imagen_uri;
    private String imagen_perfil;
    private ProgressDialog progressDialog;

    // vistas
    ImageView FOTOPERFILIMG;
    TextView UIDPERFIL, NOMBRESPERFIL, APELLIDOSPERFIL, CORREOERFIL, PASSWORDPERFIL, EDADPERFIL;
    Button ACTUALIZARPASS, ACTUALIZARDATOS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        FOTOPERFILIMG = view.findViewById(R.id.FOTOPERFILIMG);
        UIDPERFIL = view.findViewById(R.id.UIDPERFIL);
        NOMBRESPERFIL = view.findViewById(R.id.NOMBRESPERFIL);
        APELLIDOSPERFIL = view.findViewById(R.id.APELLIDOSPERFIL);
        CORREOERFIL = view.findViewById(R.id.CORREOPERFIL);
        PASSWORDPERFIL = view.findViewById(R.id.PASSWORDPERFIL);
        EDADPERFIL = view.findViewById(R.id.EDADPERFIL);

        ACTUALIZARPASS = view.findViewById(R.id.ACTUALIZARPASS);
        ACTUALIZARDATOS = view.findViewById(R.id.ACTUALIZARDATOS);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storageReference = getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");

        BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // obtener los datos
                    String uid = ""+snapshot.child("UID").getValue();
                    String nombre = ""+snapshot.child("NOMBRES").getValue();
                    String apellidos = ""+snapshot.child("APELLIDOS").getValue();
                    String correo = ""+snapshot.child("CORREO").getValue();
                    String pass = ""+snapshot.child("PASSWORD").getValue();
                    String edad = ""+snapshot.child("EDAD").getValue();
                    String imagen = ""+snapshot.child("IMAGEN").getValue();

                    UIDPERFIL.setText(uid);
                    NOMBRESPERFIL.setText(nombre);
                    APELLIDOSPERFIL.setText(apellidos);
                    CORREOERFIL.setText(correo);
                    PASSWORDPERFIL.setText(pass);
                    EDADPERFIL.setText(edad);

                    // para gestionar imagen. en un primer momento vacia
                    try{
                        // si existe la imagen en DB administrador
                        Picasso.get().load(imagen).placeholder(R.drawable.perfil).into(FOTOPERFILIMG);
                    }catch(Exception e){
                        // no existe img en DB administrador
                        // si no existe seteara la img por defecto que le seteamos
                        Picasso.get().load(R.drawable.perfil).into(FOTOPERFILIMG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // nos dirige a la actividad cambiar contrasena

        ACTUALIZARPASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), Cambio_Pass.class));
                getActivity().finish();
            }
        });

        FOTOPERFILIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarImagenPerfilAdministrador();
            }
        });
        return view;
    }

    private void cambiarImagenPerfilAdministrador() {
        String [] opcion = {"Cambiar foto de perfil"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Elegir una opcion");
        builder.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // en caso de que haya mas opciones
                if(i == 0){
                    imagen_perfil = "IMAGEN"; // de la DB
                    elegirFoto();
                }
            }
        });
        builder.create().show(); // para visualizar el alert dialog
    }

    // elegir de donde procede la imagen
    private void elegirFoto(){
        String [] opciones = {"Camara", "Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar imagen de : ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( i == 0 ){
                    // si permiso consedido
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED){
                        elegirDeCamara();
                    }else{
                        solicitudPermisoCamara.launch(Manifest.permission.CAMERA);
                    }
                } else if(i == 1){
                    // si permiso consedido
                    if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED){
                        elegirDeGaleria();
                    }else{
                        solicitudPermisoGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
            }
        });
        builder.create().show(); // para que se pueda mostrar el alert dialog
    }

    private void elegirDeGaleria() {
        Intent galeriaIntent = new Intent(Intent.ACTION_PICK);
        galeriaIntent.setType("image/*");
        obtenerImagenGaleria.launch(galeriaIntent);

    }

    private void elegirDeCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Foto Temporal");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion Temporal");
        imagen_uri = (requireActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // actividad para abrir la camara
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagen_uri);
        obtenerImagenCamara.launch(camaraIntent);
    }

    private ActivityResultLauncher<Intent> obtenerImagenCamara = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // codigo para obtener la imagen
                    if(result.getResultCode() == RESULT_OK){
                        actualizarImagenEnDB(imagen_uri);
                        progressDialog.setTitle("Procesando");
                        progressDialog.setMessage("La imagen se esta cambiando, por favor espere");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }else{
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private ActivityResultLauncher<Intent> obtenerImagenGaleria = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // codigo para obtener la imagen
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        imagen_uri = data.getData();
                        // envia img seleccionada desde galeria
                        actualizarImagenEnDB(imagen_uri);
                        progressDialog.setTitle("Procesando");
                        progressDialog.setMessage("La imagen se esta cambiando, por favor espere");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }else{
                        Toast.makeText(getActivity(), "Cancelado por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private ActivityResultLauncher<String> solicitudPermisoCamara = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if(isGranted) {
            elegirDeCamara();
        }else{
            Toast.makeText(getActivity(), "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });

    private ActivityResultLauncher<String> solicitudPermisoGaleria = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if(isGranted) {
            elegirDeGaleria();
        }else{
            Toast.makeText(getActivity(), "Permiso denegado", Toast.LENGTH_SHORT).show();
        }
    });

    private void actualizarImagenEnDB(Uri uri){
        String rutaDeArchivoYNombre = rutaDeAlmacenamiento + "" + imagen_perfil + "" + user.getUid();
        StorageReference storageReference2 = storageReference.child(rutaDeArchivoYNombre);
        storageReference2.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                // obtener la imagen
                Uri downloadUri = uriTask.getResult();

                if(uriTask.isSuccessful()){
                    HashMap<String, Object> results = new HashMap<>();
                    // para poder enviar los datos
                    results.put(imagen_perfil, downloadUri.toString());
                    //usuario actual el cual quiere cambiar la imagen
                    BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // una vez que se cambie la imagen
                            startActivity(new Intent(getActivity(), MainActivityAdministrador.class));
                            getActivity().finish();
                            Toast.makeText(getActivity(), "Imagen cambiada con exito", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




}