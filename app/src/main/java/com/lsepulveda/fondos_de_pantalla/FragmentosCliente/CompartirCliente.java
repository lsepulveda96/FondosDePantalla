package com.lsepulveda.fondos_de_pantalla.FragmentosCliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lsepulveda.fondos_de_pantalla.R;

public class CompartirCliente extends Fragment {

    Button botonCompartir;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compartir_cliente, container, false);

        botonCompartir = view.findViewById(R.id.BotonCompartir);
        botonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //compartirApp(); // falta link con google play store
            }
        });
        return view;
    }

}