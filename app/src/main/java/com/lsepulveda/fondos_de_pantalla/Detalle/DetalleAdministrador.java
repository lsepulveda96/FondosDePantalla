package com.lsepulveda.fondos_de_pantalla.Detalle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleAdministrador extends AppCompatActivity {

    CircleImageView ImagenDetalleAdmin;
    TextView UidDetalleAdmin, NombresDetalleAdmin, ApellidosDetalleAdmin, CorreoDetalleAdmin, EdadDetalleAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_administrador);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        ImagenDetalleAdmin = findViewById(R.id.ImagenDetalleAdmin);
        UidDetalleAdmin = findViewById(R.id.UidDetalleAdmin);
        NombresDetalleAdmin = findViewById(R.id.NombresDetalleAdmin);
        ApellidosDetalleAdmin = findViewById(R.id.ApellidosDetalleAdmin);
        CorreoDetalleAdmin = findViewById(R.id.CorreoDetalleAdmin);
        EdadDetalleAdmin = findViewById(R.id.EdadDetalleAdmin);

        String UIDDetalle = getIntent().getStringExtra("UID");
        String NombresDetalle = getIntent().getStringExtra("NOMBRES");
        String ApellidosDetalle = getIntent().getStringExtra("APELLIDOS");
        String CorreoDetalle = getIntent().getStringExtra("CORREO");
        String EdadDetalle = getIntent().getStringExtra("EDAD");
        String ImagenDetalle = getIntent().getStringExtra("IMAGEN");

        UidDetalleAdmin.setText("UID = " + UIDDetalle);
        NombresDetalleAdmin.setText("NOMBRES = " + NombresDetalle);
        ApellidosDetalleAdmin.setText("APELLIDOS = " + ApellidosDetalle);
        CorreoDetalleAdmin.setText("CORREO = " + CorreoDetalle);
        EdadDetalleAdmin.setText("EDAD = " + EdadDetalle);

        try{
            // por si el admin no tiene imagen
            Picasso.get().load(ImagenDetalle).placeholder(R.drawable.admin_item).into(ImagenDetalleAdmin);
        }catch (Exception e){
            Picasso.get().load(R.drawable.admin_item).into(ImagenDetalleAdmin);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}