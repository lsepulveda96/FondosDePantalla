package com.lsepulveda.fondos_de_pantalla.FragmentosCliente;


import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lsepulveda.fondos_de_pantalla.ApartadoInformativo.Informacion;
import com.lsepulveda.fondos_de_pantalla.ApartadoInformativo.ViewHolderInformacion;
import com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Dispositivo.CategoriaD;
import com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Dispositivo.ViewHolderCD;
import com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Firebase.CategoriaF;
import com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Firebase.ViewHolderCF;
import com.lsepulveda.fondos_de_pantalla.Categorias.ControladorCD;
import com.lsepulveda.fondos_de_pantalla.CategoriasClienteFirebase.ListaCategoriaFirebase;
import com.lsepulveda.fondos_de_pantalla.R;
import com.lsepulveda.fondos_de_pantalla.Util.NetworkChangeListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InicioCliente extends Fragment {

    RecyclerView recyclerViewCategoriasD, recyclerViewCategoriasF, recyclerViewInfo;
    FirebaseDatabase firebaseDatabaseD, firebaseDatabaseF, firebaseDatabaseInfo;
    DatabaseReference referenceD, referenceF, referenceInfo;
    LinearLayoutManager linearLayoutManagerD, linearLayoutManagerF, linearLayoutManagerInfo;

    FirebaseRecyclerAdapter<CategoriaD, ViewHolderCD> firebaseRecyclerAdapterD;
    FirebaseRecyclerAdapter<CategoriaF, ViewHolderCF> firebaseRecyclerAdapterF;
    FirebaseRecyclerAdapter<Informacion, ViewHolderInformacion> firebaseRecyclerAdapterInfo;

    FirebaseRecyclerOptions<CategoriaD> optionsD;
    FirebaseRecyclerOptions<CategoriaF> optionsF;
    FirebaseRecyclerOptions<Informacion> optionsInfo;

    TextView fecha;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inicio_cliente, container, false);

        firebaseDatabaseD = FirebaseDatabase.getInstance();
        firebaseDatabaseF = FirebaseDatabase.getInstance();
        firebaseDatabaseInfo = FirebaseDatabase.getInstance();

        referenceD = firebaseDatabaseD.getReference("CATEGORIAS_D");
        referenceF = firebaseDatabaseF.getReference("CATEGORIAS_F");
        referenceInfo = firebaseDatabaseInfo.getReference("INFORMACION");

        linearLayoutManagerD = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // es un fragmento
        linearLayoutManagerF = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // es un fragmento
        linearLayoutManagerInfo = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false); // es un fragmento

        recyclerViewCategoriasD = view.findViewById(R.id.recyclerViewCategoriasD);
        recyclerViewCategoriasD.setHasFixedSize(true);
        recyclerViewCategoriasD.setLayoutManager(linearLayoutManagerD);

        recyclerViewCategoriasF = view.findViewById(R.id.recyclerViewCategoriasF);
        recyclerViewCategoriasF.setHasFixedSize(true);
        recyclerViewCategoriasF.setLayoutManager(linearLayoutManagerF);

        recyclerViewInfo = view.findViewById(R.id.recyclerViewInfo);
        recyclerViewInfo.setHasFixedSize(true);
        recyclerViewInfo.setLayoutManager(linearLayoutManagerInfo);

        fecha = view.findViewById(R.id.fecha);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy");
        String fechaString = simpleDateFormat.format(date);
        fecha.setText(fechaString);

        verCategoriasD(); // para visalizar la vista al visualizar fragmento
        verCategoriasF(); // visualizar categorias firebase
        verApartadoInformativo();

        return view;
    }

    private void verCategoriasF() {
        optionsF = new FirebaseRecyclerOptions.Builder<CategoriaF>().setQuery(referenceF, CategoriaF.class).build();
        firebaseRecyclerAdapterF = new FirebaseRecyclerAdapter<CategoriaF, ViewHolderCF>(optionsF) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCF viewHolderCF, int position, @NonNull CategoriaF categoriaF) {
                viewHolderCF.seteoCategoriaF(getActivity(), categoriaF.getCategoria(), categoriaF.getImagen());
            }

            @NonNull
            @Override
            public ViewHolderCF onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflar el layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorias_firebase,parent,false);
                ViewHolderCF viewHolderCF = new ViewHolderCF(itemView);

                viewHolderCF.setOnClickListener(new ViewHolderCF.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // obtener nombre de la categoria
                        String NOMBRE_CATEGORIA = getItem(position).getCategoria();

                        //pasar categoria a la siguiente actividad
                        Intent intent = new Intent(getActivity(), ListaCategoriaFirebase.class);
                        intent.putExtra("NOMBRE_CATEGORIA", NOMBRE_CATEGORIA);
                        Toast.makeText(getActivity(), "CATEGORIA SELECCIONADA = " + NOMBRE_CATEGORIA, Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                });

                return viewHolderCF; // retorna view holder categoria dispositivo
            }
        };
        recyclerViewCategoriasF.setAdapter(firebaseRecyclerAdapterF); // para que se pueda visualizar cada item en el recycler view
    }

    private void verCategoriasD(){
        optionsD = new FirebaseRecyclerOptions.Builder<CategoriaD>().setQuery(referenceD, CategoriaD.class).build();
        firebaseRecyclerAdapterD = new FirebaseRecyclerAdapter<CategoriaD, ViewHolderCD>(optionsD) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderCD viewHolderCD, int position, @NonNull CategoriaD categoriaD) {
                viewHolderCD.seteoCategoriaD(getActivity(), categoriaD.getCategoria(), categoriaD.getImagen());
            }

            @NonNull
            @Override
            public ViewHolderCD onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflar el layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorias_dispositivos,parent,false);
                ViewHolderCD viewHolderCD = new ViewHolderCD(itemView);

                viewHolderCD.setOnClickListener(new ViewHolderCD.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // obtenemos el nombre de la categoria
                        String categoria = getItem(position).getCategoria();
                        Intent intent = new Intent(view.getContext(), ControladorCD.class); // para comparar que categoria es
                        intent.putExtra("Categoria", categoria);
                        startActivity(intent);
                        Toast.makeText(getActivity(), categoria, Toast.LENGTH_SHORT).show();
                    }
                });

                return viewHolderCD; // retorna view holder categoria dispositivo
            }
        };
        recyclerViewCategoriasD.setAdapter(firebaseRecyclerAdapterD); // para que se pueda visualizar cada item en el recycler view
    }

    private void verApartadoInformativo() {
        optionsInfo = new FirebaseRecyclerOptions.Builder<Informacion>().setQuery(referenceInfo, Informacion.class).build();
        firebaseRecyclerAdapterInfo = new FirebaseRecyclerAdapter<Informacion, ViewHolderInformacion>(optionsInfo) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderInformacion viewHolderInformacion, int position, @NonNull Informacion informacion) {
                viewHolderInformacion.seteoInformacion(getActivity(), informacion.getNombre(), informacion.getImagen());
            }

            @NonNull
            @Override
            public ViewHolderInformacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflar el layout
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.apartado_informativo,parent,false);
                ViewHolderInformacion viewHolderInformacion = new ViewHolderInformacion(itemView);

                viewHolderInformacion.setOnClickListener(new ViewHolderInformacion.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }
                });

                return viewHolderInformacion; // retorna view holder categoria dispositivo
            }
        };
        recyclerViewInfo.setAdapter(firebaseRecyclerAdapterInfo); // para que se pueda visualizar cada item en el recycler view
    }


    @Override
    public void onStart() {

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireActivity().registerReceiver(networkChangeListener, filter);

        super.onStart();
        if(firebaseRecyclerAdapterD != null && firebaseRecyclerAdapterF != null && firebaseRecyclerAdapterInfo != null){
            firebaseRecyclerAdapterD.startListening();
            firebaseRecyclerAdapterF.startListening();
            firebaseRecyclerAdapterInfo.startListening();
        }
    }

    @Override
    public void onStop() {
        requireActivity().unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}
