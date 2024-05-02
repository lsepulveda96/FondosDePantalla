package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgregarMusica extends AppCompatActivity {

    TextView IdMusica, VistaMusica;
    EditText NombreMusica;
    ImageView ImagenAgregarMusica;
    Button PublicarMusica;

    String RutaDeAlmacenamiento = "Musica_Subida/";
    String RutaDeBaseDeDatos = "MUSICA";
    Uri RutaArchivoUri;

    StorageReference mStorageReference;
    DatabaseReference DatabaseReference;

    ProgressDialog progressDialog;

    String /*Se añadió en video anterior*/rId, rNombre , rImagen, rVista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_musica);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        IdMusica = findViewById(R.id.IdMusica);
        VistaMusica = findViewById(R.id.VistaMusica);
        NombreMusica = findViewById(R.id.NombreMusica);
        ImagenAgregarMusica = findViewById(R.id.ImagenAgregarMusica);
        PublicarMusica= findViewById(R.id.PublicarMusica);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarMusica.this);

        Bundle intent = getIntent().getExtras();
        if (intent != null){

            //Recuperar los datos de la actividad anterior
            /*Se añadió en video anterior*/
            rId = intent.getString("IdEnviado");
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImagenEnviada");
            rVista = intent.getString("VistaEnviada");

            //Setear
            /*Se añadió en video anterior*/
            IdMusica.setText(rId);
            NombreMusica.setText(rNombre);
            VistaMusica.setText(rVista);
            Picasso.get().load(rImagen).into(ImagenAgregarMusica);

            //cambiar el nombre actionbar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            //cambiar el nombre del botón
            PublicarMusica.setText(actualizar);

        }

        ImagenAgregarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                ObtenerImagenGaleria.launch(intent);

            }
        });

        PublicarMusica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PublicarMusica.getText().equals("Publicar")){
                    /*Método para subir una imagen*/
                    SubirImagen();
                }else {
                    EmpezarActualizacion();
                }
            }
        });
    }

    private void EmpezarActualizacion() {
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor ...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        EliminarImagenAnterior();
    }

    private void EliminarImagenAnterior() {
        StorageReference Imagen = getInstance().getReferenceFromUrl(rImagen);
        Imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //si la imagen se eliminó
                Toast.makeText(AgregarMusica.this, "La imagen anterior a sido eliminada", Toast.LENGTH_SHORT).show();
                SubirNuevaImagen();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //GESTIONAR POSIBLE ERROR
                Toast.makeText(AgregarMusica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void SubirNuevaImagen() {
        //DECLARAMOS LA NUEVA IMAGEN A ACTUALIZAR
        String nuevaImagen = System.currentTimeMillis()+".png";
        /*referencia de almacenamiento, para que la nueva imagen se pueda guardar en esa carpeta*/
        StorageReference mStorageReference2 = mStorageReference.child(RutaDeAlmacenamiento + nuevaImagen);
        /*obtener mapa de bits de la nueva imagen seleccionada*/
        Bitmap bitmap = ((BitmapDrawable)ImagenAgregarMusica.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        /*comprimir imagen*/
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte [] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask  = mStorageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AgregarMusica.this, "Nueva imagen cargada", Toast.LENGTH_SHORT).show();
                /*obtener la URL de la imagen recién cargada*/
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                /*Actualizar la base de datos con nuevos datos*/
                ActualizarImagenBD(downloadUri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarMusica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void ActualizarImagenBD(final String NuevaImagen) {
        final String nombreActualizar = NombreMusica.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("MUSICA");

        //CONSULTA
        /*Se añadió en video anterior*/
        Query query = databaseReference.orderByChild("id").equalTo(rId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //DATOS A ACTUALIZAR
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(NuevaImagen);
                }
                progressDialog.dismiss();
                //Actualización de base de datos
                Toast.makeText(AgregarMusica.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AgregarMusica.this, MusicaA.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SubirImagen() {
        final String mNombre = NombreMusica.getText().toString();

        /*Validar que el nombre y la imagen no sean nulos*/
        if (mNombre.equals("") || RutaArchivoUri==null){
            Toast.makeText(this, "Asigne un nombre o una imagen", Toast.LENGTH_SHORT).show();
        }

        else {
            progressDialog.setTitle("Espere por favor");
            progressDialog.setMessage("Subiendo Imagen MÚSICA ...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StorageReference storageReference2 = mStorageReference.child(RutaDeAlmacenamiento+System.currentTimeMillis()+"."+ObtenerExtensionDelArchivo(RutaArchivoUri));
            storageReference2.putFile(RutaArchivoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            Uri downloadURI = uriTask.getResult();

                            String ID = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss",
                                    Locale.getDefault()).format(System.currentTimeMillis());
                            IdMusica.setText(ID);

                            String mId = IdMusica.getText().toString();
                            String mVista = VistaMusica.getText().toString();
                            int VISTA = Integer.parseInt(mVista);

                            Musica musica = new Musica(mNombre+"/"+mId,downloadURI.toString(),mNombre,VISTA);
                            //MUSICA (ID, IMAGEN, NOMBRE, VISTAS)
                            String ID_IMAGEN = DatabaseReference.push().getKey();

                            DatabaseReference.child(ID_IMAGEN).setValue(musica);

                            progressDialog.dismiss();
                            Toast.makeText(AgregarMusica.this, "Subido Exitosamente", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(AgregarMusica.this, MusicaA.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AgregarMusica.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.setTitle("Publicando");
                            progressDialog.setCancelable(false);
                        }
                    });

        }

    }

    /*Obtenemos la extensión de una image .jpg / .png */
    private String ObtenerExtensionDelArchivo(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /*Obtener imagen de galería*/
    private ActivityResultLauncher<Intent> ObtenerImagenGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    /*Manejar el resultado de nuestro intent*/
                    if (result.getResultCode() == Activity.RESULT_OK){
                        //Selección de imagen
                        Intent data = result.getData();
                        //Obtener uri de imagen
                        RutaArchivoUri = data.getData();
                        ImagenAgregarMusica.setImageURI(RutaArchivoUri);
                    }
                    else {
                        Toast.makeText(AgregarMusica.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}