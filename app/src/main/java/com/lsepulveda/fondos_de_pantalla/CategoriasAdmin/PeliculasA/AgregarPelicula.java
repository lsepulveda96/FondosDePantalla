package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA.AgregarMusica;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AgregarPelicula extends AppCompatActivity {

    TextView idPelicula, vistaPeliculasTv;
    ImageView imagenAgregarPelicula;
    EditText nombrePeliculasTv;
    Button publicarPeliculaBtn;

    // para crear la imagen dentro de esa carpeta
    String rutaAlmacenamiento = "Pelicula_subida/";
    String rutaDeBaseDeDatos = "PELICULAS";
    Uri rutaArchivoUri;

    StorageReference mStorageReference;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    // recuperar nombre, recuperar vista, recuperar imagen
    String rId, rNombre, rVista, rImagen;

//    int CODIGO_SOLICITUD_IMAGEN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_pelicula);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        idPelicula = findViewById(R.id.idPelicula);
        vistaPeliculasTv = findViewById(R.id.VistaPeliculas);
        nombrePeliculasTv = findViewById(R.id.NombrePeliculas);
        publicarPeliculaBtn = findViewById(R.id.PublicarPelicula);
        imagenAgregarPelicula = findViewById(R.id.ImagenAgregarPelicula);
        mStorageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(rutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarPelicula.this);

        Bundle intent  = getIntent().getExtras();
        if(intent != null){
            // recuperar los datos de la actividad anterior
            rId = intent.getString("IdEnviado");
            rNombre = intent.getString("NombreEnviado");
            rImagen = intent.getString("ImagenEnviada");
            rVista = intent.getString("VistaEnviada");

            // setear en textView y en imagen
            idPelicula.setText(rId);
            nombrePeliculasTv.setText(rNombre);
            vistaPeliculasTv.setText(rVista);
            Picasso.get().load(rImagen).into(imagenAgregarPelicula);

            // cambiar nombre en actionBar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            // cambiar nombre del boton
            publicarPeliculaBtn.setText(actualizar);
        }

        // al hacer click, abre la galeria para seleccionar una imagen. luego de esto abre un uri
        imagenAgregarPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                obtenerImagenGaleria.launch(intent);
            }
        });

        // ejecuta el metodo subir imagen
        publicarPeliculaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(publicarPeliculaBtn.getText().equals("Publicar")){
                    subirImagen();
                }else{
                    actualizarPelicula();
                }
            }
        });

    }

    private void actualizarPelicula() {
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor..");
        progressDialog.show();
        progressDialog.setCancelable(false);
        eliminarImgAnterior();

    }

    private void eliminarImgAnterior() {
        StorageReference imagen = getInstance().getReferenceFromUrl(rImagen);
        imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // si la img se elimino
                Toast.makeText(AgregarPelicula.this, "Imagen anterior ha sido eliminada", Toast.LENGTH_SHORT).show();
                subirNuevaImg();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarPelicula.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void subirNuevaImg() {
        //Declaramos la nueva imagen a actualizar
        String nuevaImg = System.currentTimeMillis()+ ".png";
        // referencia de almacenamiento para que la nueva img se pueda guardar en esa carpeta
        StorageReference mStorageReference2 = mStorageReference.child(rutaAlmacenamiento + nuevaImg); // nombre de la carpeta donde se almacenan las peliculas
        //obtener mapa de bits de la nueva img seleccionada
        Bitmap bitmap = ((BitmapDrawable)imagenAgregarPelicula.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //comprimir imagen
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte [] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = mStorageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarPelicula.this, "Nueva imagen cargada", Toast.LENGTH_SHORT).show();
                // obtener la url de la imagen recien cargada
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri downoladUri = uriTask.getResult();
                // actualizar la base de datos con nuevos datos
                actualizarImgBD(downoladUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AgregarPelicula.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private void actualizarImgBD(final String nuevaImagen) {
        final String nombreActualizar = nombrePeliculasTv.getText().toString(); // por si desea cambiar el nombre
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // el nombre tierne que coincidir con el de la base de datos, en este caso "PELICULAS"
        DatabaseReference databaseReference =  firebaseDatabase.getReference("PELICULAS");

        // CONSULTA
        // es el dato que no se puede repetir dos veces. (esto para mi esta mal, si hay dos nombres iguales dice que se borran). deberia tener un id
        Query query = databaseReference.orderByChild("id").equalTo(rId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // DATOS A ACTUALIZAR. NOMBRE E IMAGEN
                for (DataSnapshot ds : snapshot.getChildren()){
                    ds.getRef().child("nombre").setValue(nombreActualizar);
                    ds.getRef().child("imagen").setValue(nuevaImagen);
                }
                progressDialog.dismiss();
                Toast.makeText(AgregarPelicula.this, "Actualizado con exito", Toast.LENGTH_SHORT).show();
                // vuelve a donde se listan las imagenes
                startActivity(new Intent(AgregarPelicula.this, PeliculasA.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //
    private void subirImagen() {

        //convertir a cadena el nombre de imagen para almacenarlo en la db
        String mNombre = nombrePeliculasTv.getText().toString();

        // validar que el nombre y la imagen no sean nulos
        if(mNombre.equals("") || rutaArchivoUri == null){
            Toast.makeText(this, "Asigne un nombre o una imagen", Toast.LENGTH_SHORT).show();
        }

        //por si se setea correctamente
        else{
            progressDialog.setTitle("Por favor espere");
            progressDialog.setMessage("Subiendo imagen PELICULA ...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            //20230630_1234.PNG
            //dentro de la carpeta storage de FareBase con el siguiente formato de nombre
            StorageReference storageReference2 = mStorageReference.child(rutaAlmacenamiento+System.currentTimeMillis()+"."+obtenerExtensionDelArchivo(rutaArchivoUri));

            //envia imagen dentro del apartado del storage
            storageReference2.putFile(rutaArchivoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // intentar obtener la uri que se subio al firebase storage, para luego subirla en firebase database
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while(!uriTask.isSuccessful());

                    Uri downloadURI = uriTask.getResult();

                    String ID = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss",
                            Locale.getDefault()).format(System.currentTimeMillis());
                    // ejemplo: 2021-06-30/06:03:20
                    idPelicula.setText(ID);

                    String mNombre = nombrePeliculasTv.getText().toString();
                    String mId = idPelicula.getText().toString();


                    // se convierte a string y luego a entero, para poder almacenar la info en la db
                    String mVista = vistaPeliculasTv.getText().toString();
                    int VISTA = Integer.parseInt(mVista);

                    Pelicula pelicula = new Pelicula(mNombre+"/"+mId, downloadURI.toString(),mNombre,VISTA);
                    String ID_IMAGEN = databaseReference.push().getKey();

                    // los lista segun el id del imagen
                    databaseReference.child(ID_IMAGEN).setValue(pelicula);

                    progressDialog.dismiss();
                    Toast.makeText(AgregarPelicula.this, "Subido exitosamente", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AgregarPelicula.this, PeliculasA.class));
                    finish();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AgregarPelicula.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressDialog.setTitle("Publicando");
                    progressDialog.setCancelable(false);
                }
            });
        }
    }

    //obtener extension (formato) de la imagen .jpg / .png
    private String obtenerExtensionDelArchivo(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // obtener imagen de galeria
    private ActivityResultLauncher<Intent> obtenerImagenGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //manejar el resultado de nuestro intent
                    if(result.getResultCode() == Activity.RESULT_OK){
                        //seleccion de imagen
                        Intent data = result.getData();
                        //obtener uri de imagen
                        rutaArchivoUri = data.getData();
                        imagenAgregarPelicula.setImageURI(rutaArchivoUri);
                    }else{
                        Toast.makeText(AgregarPelicula.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}