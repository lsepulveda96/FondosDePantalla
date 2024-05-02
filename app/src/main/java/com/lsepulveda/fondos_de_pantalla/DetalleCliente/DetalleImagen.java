package com.lsepulveda.fondos_de_pantalla.DetalleCliente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;


import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DetalleImagen extends AppCompatActivity {

    ImageView ImagenDetalle;
    TextView NombreImagenDetalle;
    TextView VistaDetalle;

    FloatingActionButton fabDescargar, fabCompartir, fabEstablecer;

    Bitmap bitmap; //MAPA DE BITS, SON LA ESTRUCTURA DONDE SE ALMACENAN LOS PIXELES QUE CONFORMAN UN GRÁFICO

    private Uri imageUri = null;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_imagen);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImagenDetalle = findViewById(R.id.imagenDetalle);
        NombreImagenDetalle = findViewById(R.id.nombreImagenDetalle);
        VistaDetalle = findViewById(R.id.vistaDetalle);

        fabDescargar = findViewById(R.id.fabDescargar);
        fabCompartir = findViewById(R.id.fabCompartir);
        fabEstablecer = findViewById(R.id.fabEstablecer);

        dialog = new Dialog(DetalleImagen.this);

        String imagen = getIntent().getStringExtra("Imagen");
        String Nombre = getIntent().getStringExtra("Nombre");
        String Vista = getIntent().getStringExtra("Vista");

        try {
            //SI LA IMAGEN FUE TRAIDA
            Picasso.get().load(imagen).placeholder(R.drawable.detalle_imagen).into(ImagenDetalle);
        }
        catch (Exception e){
            //SI LA IMAGEN NO FUE TRAIDA
            Picasso.get().load(R.drawable.detalle_imagen).into(ImagenDetalle);
        }

        NombreImagenDetalle.setText(Nombre);
        VistaDetalle.setText(Vista);

        fabDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SI LA VERSIÓN DE ANDROID ES MAYOR O IGUAL A ANDROID 11*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    if (ContextCompat.checkSelfPermission(
                            DetalleImagen.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED){
                        DescargarImagenAndroid_11();
                    }
                    /*Si el permiso no es condedido, que aparezca nuevamente el cuadro del permiso*/
                    else {
                        SolicitudPermisoDescargaAndroid_11_o_Superior.launch(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                /*SI LA VERSIÓN DE ANDROID ES MAYOR O IGUAL A 6*/
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    /*si el permiso es condedido, se descarga la imagen*/
                    if (ContextCompat.checkSelfPermission(DetalleImagen.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED){
                        DescargarImagen();
                    }
                    /*Si el permiso no es condedido, que aparezca nuevamente el cuadro del permiso*/
                    else {
                        SolicitudPermisoDescarga.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
                /*SI LA VERSIÓN DE ANDROID ES MENOR A 6*/
                else {
                    DescargarImagen();
                }
            }
        });

        fabCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompartirImagen_Actualizado();
            }
        });

        fabEstablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Método para establecer como fondo de pantalla una imagen seleccionada*/
                EstablecerImagen();
            }
        });

        //EN CASO NO DESCARGUE LA IMAGEN
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private void DescargarImagen(){
        //OBTENER EL MAPA DE BITS DE LA IMAGEN
        bitmap = ((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();
        //OBTENER LA FECHA DE DESCARGA

        String FechaDescarga = new SimpleDateFormat(
                "'Fecha Descarga: ' yyyy_MM_dd 'Hora: ' HH:mm:ss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //DEFINIR LA RUTA DE ALMACENAMIENTO
        //File ruta = Environment.getExternalStorageDirectory();
        File ruta = Environment.getExternalStorageDirectory();

        //DEFINIR EL NOMBRE DE LA CARPETA
        File NombreCarpeta = new File(ruta+"/MI APLICACIÓN/");
        NombreCarpeta.mkdir();
        //DEFINIR EL NOMBRE DE LA IMAGEN DESCARGADA
        String ObtenerNombreImagen = NombreImagenDetalle.getText().toString();
        String NombreImagen = ObtenerNombreImagen+" "+FechaDescarga+ ".JPEG";
        File file = new File(NombreCarpeta,NombreImagen);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
//            Toast.makeText(this, "La imagen se ha descargado con éxito", Toast.LENGTH_SHORT).show();
            animacionDescargaExitosa();

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void DescargarImagenAndroid_11() {
        bitmap = ((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();
        OutputStream fos;
        String NombreImagen = NombreImagenDetalle.getText().toString();

        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, NombreImagen);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"Image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"/MI APLICACIÓN/");
            Uri imageUri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos);
//            Toast.makeText(this, "La imagen se ha descargado con éxito", Toast.LENGTH_SHORT).show();
            animacionDescargaExitosa();

        }catch (Exception e){
            Toast.makeText(this, "No guardado", Toast.LENGTH_SHORT).show();
        }
    }

    /*Código añadido en actualización*/
    private void CompartirImagen_Actualizado(){
        Uri contentUri = getContentUri();

        Intent sharedIntent = new Intent(Intent.ACTION_SEND);

        sharedIntent.setType("image/jpeg");
        sharedIntent.putExtra(Intent.EXTRA_SUBJECT,"Asunto");
        sharedIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        sharedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sharedIntent);
    }

    /*Código añadido en actualización*/
    private Uri getContentUri(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable)ImagenDetalle.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
        }

        catch (Exception e){

        }

        File imageFolder = new File(getCacheDir(), "images");
        Uri contentUri = null;

        try {
            String NombreImagen = NombreImagenDetalle.getText().toString();
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
            stream.flush();
            stream.close();
            contentUri = FileProvider.getUriForFile(
                    this, "com.lsepulveda.fondos_de_pantalla.fileprovider",file);
        }
        catch (Exception e){

        }

        return contentUri;

    };

    private void EstablecerImagen(){
        //OBTENER EL MAPA DE BITS
        bitmap = ((BitmapDrawable) ImagenDetalle.getDrawable()).getBitmap();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        try {
            wallpaperManager.setBitmap(bitmap);
            //Toast.makeText(this, "Establecido con éxito", Toast.LENGTH_SHORT).show();
            animacionEstablecido();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /*Solicitud para descargar imagen con Android menor a 11*/
    private ActivityResultLauncher<String> SolicitudPermisoDescarga =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

                //Si el permiso para descargar la imagen es exitosa
                if (isGranted){
                    DescargarImagen();
                }else {
                    //Si el permiso fue denegado
//                    Toast.makeText(this, "El permiso a sido denegado", Toast.LENGTH_SHORT).show();
                    animacionActivePermisos();
                }

            });


    /*Solicitud para descargar imagen con Android superior a 11*/
    private ActivityResultLauncher<String> SolicitudPermisoDescargaAndroid_11_o_Superior =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{

                //Si el permiso para descargar la imagen es exitosa
                if (isGranted){
                    DescargarImagenAndroid_11();
                }else {
                    //Si el permiso fue denegado
//                    Toast.makeText(this, "El permiso a sido denegado", Toast.LENGTH_SHORT).show();
                    animacionActivePermisos();
                }
            });;


    private void animacionActivePermisos(){
        Button okPermisos;

        // conexxion con el cuadro de dialogo
        dialog.setContentView(R.layout.animacion_permiso);

        okPermisos = dialog.findViewById(R.id.OkPermisos);

        okPermisos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    private void animacionDescargaExitosa(){
        Button okDescarga;

        dialog.setContentView(R.layout.animacion_descarga_exitosa);

        okDescarga = dialog.findViewById(R.id.OkDescarga);

        okDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCancelable(false);
    }

    private void animacionEstablecido(){
        Button okEstablecido;
        dialog.setContentView(R.layout.animacion_establecido);
        okEstablecido = dialog.findViewById(R.id.OkEstablecido);
        okEstablecido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
