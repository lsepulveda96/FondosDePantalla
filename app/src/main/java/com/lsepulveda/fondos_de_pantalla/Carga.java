package com.lsepulveda.fondos_de_pantalla;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Carga extends AppCompatActivity {

    TextView app_name, desarrollador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carga);

        app_name = findViewById(R.id.app_name);
        desarrollador = findViewById(R.id.desarrollador);

        // Para cambio de letra
        String ubicacion = "fuentes/present_christmas.otf";
        Typeface tf = Typeface.createFromAsset(Carga.this.getAssets(), ubicacion);

        //3 segundos para pantalla de carga
        final int DURACION = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //codigo que se ejecutara pasados los 3 segundos
                //TODO ojo con esto, hay que cambiarlo por un condicional
                Intent intent = new Intent(Carga.this, MainActivityAdministrador.class);
//                Intent intent = new Intent(Carga.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },DURACION);

        app_name.setTypeface(tf);
        desarrollador.setTypeface(tf);
    }
}