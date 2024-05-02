package com.lsepulveda.fondos_de_pantalla.Categorias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA.Serie;
import com.lsepulveda.fondos_de_pantalla.CategoriasCliente.MusicaCliente;
import com.lsepulveda.fondos_de_pantalla.CategoriasCliente.PeliculasCliente;
import com.lsepulveda.fondos_de_pantalla.CategoriasCliente.SeriesCliente;
import com.lsepulveda.fondos_de_pantalla.CategoriasCliente.VideoJuegosCliente;
import com.lsepulveda.fondos_de_pantalla.R;

public class ControladorCD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlador_cd);

        String categoriaRecuperada = getIntent().getStringExtra("Categoria");

        if(categoriaRecuperada.equals("Peliculas")){
            startActivity(new Intent(ControladorCD.this, PeliculasCliente.class));
            finish(); // para cerrar esta clase
        }

        if(categoriaRecuperada.equals("Series")){
            startActivity(new Intent(ControladorCD.this, SeriesCliente.class));
            finish(); // para cerrar esta clase
        }

        if(categoriaRecuperada.equals("Musica")){
            startActivity(new Intent(ControladorCD.this, MusicaCliente.class));
            finish(); // para cerrar esta clase
        }

        if(categoriaRecuperada.equals("VideoJuegos")){
            startActivity(new Intent(ControladorCD.this, VideoJuegosCliente.class));
            finish(); // para cerrar esta clase
        }
    }
}