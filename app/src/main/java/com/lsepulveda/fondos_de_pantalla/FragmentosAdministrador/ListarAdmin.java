package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsepulveda.fondos_de_pantalla.Adaptador.Adaptador;
import com.lsepulveda.fondos_de_pantalla.Modelo.Administrador;
import com.lsepulveda.fondos_de_pantalla.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListarAdmin extends Fragment {

    RecyclerView administradores_recyclerView;
    Adaptador adaptador;
    List<Administrador> administradorList;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_admin, container, false);
        administradores_recyclerView = view.findViewById(R.id.administradores_recyclerView);
        administradores_recyclerView.setHasFixedSize(true);
        // ordena de uno en uno
        administradores_recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        administradorList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();

        obtenerLista();

        return view;
    }

    // Obtener toda la lista
    private void obtenerLista() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");
        // ordena por apellidos
        reference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradorList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Administrador administrador = ds.getValue(Administrador.class);
                    //Condicion para que en la lisat se visualicen todos los usaurios, excepoto el que ha iniciado sesion
                    assert administrador != null;
                    assert user != null;
//                    if(!administrador.getUID().equals(user.getUid())){
                        administradorList.add(administrador);
//                    }
                    adaptador = new Adaptador(getActivity(), administradorList);
                    administradores_recyclerView.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscarAdministrador(String consulta) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");
        // ordena por apellidos
        reference.orderByChild("APELLIDOS").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                administradorList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Administrador administrador = ds.getValue(Administrador.class);
                    //Condicion para que en la lisat se visualicen todos los usaurios, excepoto el que ha iniciado sesion
                    assert administrador != null;
                    assert user != null;
                    //if(!administrador.getUID().equals(user.getUid())){

                        // buscar por nombre y correo
                        if(administrador.getNOMBRES().toLowerCase().contains(consulta.toLowerCase()) ||
                                administrador.getCORREO().toLowerCase().contains(consulta.toLowerCase())){
                            administradorList.add(administrador);
                        }
                   // }
                    adaptador = new Adaptador(getActivity(), administradorList);
                    administradores_recyclerView.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //crear menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_buscar, menu);
        MenuItem item = menu.findItem(R.id.buscar_administrador);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String consulta) {
                // se llama cuando el user presiona el boton busqueda desde el teclado
                if(!TextUtils.isEmpty(consulta.trim())){
                    buscarAdministrador(consulta);
                }else{
                    // si la busqueda es vacia muestra toda la lista
                    obtenerLista();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String consulta) {
                // la busqueda se va actualizando conforme vamos escribiendo
                if(!TextUtils.isEmpty(consulta.trim())){
                    buscarAdministrador(consulta);
                }else{
                    // si la busqueda es vacia muestra toda la lista
                    obtenerLista();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    //visualizar el menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //esto lo hacemos xq estamos trabajando desde un fragmento
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}