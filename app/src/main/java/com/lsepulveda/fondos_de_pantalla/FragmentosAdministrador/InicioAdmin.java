package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA.MusicaA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.PeliculasA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA.SeriesA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.VideoJuegosA.VideoJuegosA;
import com.lsepulveda.fondos_de_pantalla.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InicioAdmin extends Fragment {

    Button peliculas, series, musica, videoJuegos;

    TextView fechaAdmin, nombreTxt;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_admin, container, false);

        peliculas = view.findViewById(R.id.Peliculas);
        series = view.findViewById(R.id.Series);
        musica = view.findViewById(R.id.Musica);
        videoJuegos = view.findViewById(R.id.VideoJuegos);

        fechaAdmin = view.findViewById(R.id.fechaAdmin);
        nombreTxt = view.findViewById(R.id.nombreTXT);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");

        // fecha actual
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy");
        String fechaString = simpleDateFormat.format(date);
        fechaAdmin.setText("Hoy es : " + fechaString);



        peliculas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PeliculasA.class));
            }
        });

        series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SeriesA.class));
            }
        });

        musica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MusicaA.class));
            }
        });

        videoJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), VideoJuegosA.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        comprobarUsuarioActivo();
    }

    private void comprobarUsuarioActivo(){
        if(user!=null)
            cargaDeDatos();
    }

    private void cargaDeDatos(){
        BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // si el usuario existe
                if (snapshot.exists()) {
                    //obtener el dato nombre
                    String nombre = ""+ snapshot.child("NOMBRES").getValue();
                    nombreTxt.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}