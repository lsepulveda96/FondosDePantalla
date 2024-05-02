package com.lsepulveda.fondos_de_pantalla.CategoriasClienteFirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA.Musica;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA.ViewHolderMusica;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.Pelicula;
import com.lsepulveda.fondos_de_pantalla.CategoriasCliente.MusicaCliente;
import com.lsepulveda.fondos_de_pantalla.DetalleCliente.DetalleImagen;
import com.lsepulveda.fondos_de_pantalla.R;

import java.util.HashMap;

public class ListaCategoriaFirebase extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerViewCat_Elegida;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseRecyclerAdapter<ImgCatFirebaseElegida, ViewHolderImgCatFElegida> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<ImgCatFirebaseElegida> options;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categoria_firebase);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Lista Imagenes");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        String BD_CAT_FIREBASE = getIntent().getStringExtra("NOMBRE_CATEGORIA");

        recyclerViewCat_Elegida = findViewById(R.id.recyclerViewCat_Elegida);
        recyclerViewCat_Elegida.setHasFixedSize(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CATEGORIAS_SUBIDAS_FIREBASE").child(BD_CAT_FIREBASE);

        listarCategoriaSeleccionada();
    }

    private void listarCategoriaSeleccionada() {
        // le pasa mRef para poder leer la base de datos
        options = new FirebaseRecyclerOptions.Builder<ImgCatFirebaseElegida>().setQuery(databaseReference,ImgCatFirebaseElegida.class).build();
        //contiene el viewHolder que contiene el hacer click y el mantener pulsado al hacer click
        //tambien el seteo de musica para poder pasar por parametro el nombre, imagen y num de vistas
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ImgCatFirebaseElegida, ViewHolderImgCatFElegida>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderImgCatFElegida viewHolderImgCatFElegida, int i, @NonNull ImgCatFirebaseElegida imgCatFirebaseElegida) {
                viewHolderImgCatFElegida.seteoCategoriaFElegida(
                        getApplicationContext(),
                        imgCatFirebaseElegida.getNombre(),
                        imgCatFirebaseElegida.getVistas(),
                        imgCatFirebaseElegida.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderImgCatFElegida onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inlfar el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_cat_f_elegida,parent,false);

                ViewHolderImgCatFElegida viewHolderImgCatFElegida = new ViewHolderImgCatFElegida(itemView);
                viewHolderImgCatFElegida.setOnClickListener(new ViewHolderImgCatFElegida.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // obtener los datos de la imagen
                        String id = getItem(position).getId();
                        String imagen = getItem(position).getImagen();
                        String nombres = getItem(position).getNombre();
                        int vistas = getItem(position).getVistas();
                        // convertir a string la vista
                        String vistasString = String.valueOf(vistas);

                        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    //crear un objeto de la clase peliculas
                                    ImgCatFirebaseElegida imgCatFirebaseElegida = ds.getValue(ImgCatFirebaseElegida.class);

                                    if(imgCatFirebaseElegida.getId().equals(id)){
                                        int i = 1;
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        // el valor que vamos a actualizar
                                        hashMap.put("vistas", vistas+1);
                                        ds.getRef().updateChildren(hashMap);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //pasamso a la actividad detalle cliente
                        Intent intent = new Intent(ListaCategoriaFirebase.this, DetalleImagen.class);

                        // datos a pasar
                        intent.putExtra("Imagen", imagen);
                        intent.putExtra("Nombre", nombres);
                        intent.putExtra("Vista", vistasString);

                        startActivity(intent);
                    }

                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderImgCatFElegida;
            }
        };

        // manera en que se va a listar las imagenes, en este caso en dos columnas
        recyclerViewCat_Elegida.setLayoutManager(new GridLayoutManager(ListaCategoriaFirebase.this, 2));
        //para que este escuchando el recyclerView, ya sea que estemos agregando o eliminando imagenes
        firebaseRecyclerAdapter.startListening();
        recyclerViewCat_Elegida.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        if(firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(databaseReference != null && valueEventListener!= null){
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}