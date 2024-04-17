package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA.MusicaA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.PeliculasA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA.SeriesA;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.VideoJuegosA.VideoJuegosA;
import com.lsepulveda.fondos_de_pantalla.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InicioAdmin extends Fragment {

    Button peliculas, series, musica, videoJuegos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inicio_admin, container, false);

        peliculas = view.findViewById(R.id.Peliculas);
        series = view.findViewById(R.id.Series);
        musica = view.findViewById(R.id.Musica);
        videoJuegos = view.findViewById(R.id.VideoJuegos);


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
}