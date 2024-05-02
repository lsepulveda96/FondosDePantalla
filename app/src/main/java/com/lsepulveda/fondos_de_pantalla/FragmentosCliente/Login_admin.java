package com.lsepulveda.fondos_de_pantalla.FragmentosCliente;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lsepulveda.fondos_de_pantalla.InicioSesion;
import com.lsepulveda.fondos_de_pantalla.R;

public class Login_admin extends Fragment {

    Button acceder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_admin, container, false);
        acceder = view.findViewById(R.id.acceder);
        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InicioSesion.class));
            }
        });

        return view;
    }
}