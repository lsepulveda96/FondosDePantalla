package com.lsepulveda.fondos_de_pantalla.CategoriasCliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.Pelicula;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.ViewHolderPelicula;
import com.lsepulveda.fondos_de_pantalla.DetalleCliente.DetalleImagen;
import com.lsepulveda.fondos_de_pantalla.R;

import java.util.HashMap;

public class PeliculasCliente extends AppCompatActivity {

    RecyclerView recyclerViewPeliculaC;
    FirebaseDatabase mFireBaseDataBase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Pelicula, ViewHolderPelicula> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Pelicula> options;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Peliculas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewPeliculaC = findViewById(R.id.recyclerViewPeliculaC);
        recyclerViewPeliculaC.setHasFixedSize(true); // para que el recycler view se adapte al eliminar o agregar elementos.

        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mRef = mFireBaseDataBase.getReference("PELICULAS");

        listarImagenesPeliculas();
    }

    private void listarImagenesPeliculas() {
        // le pasa mRef para poder leer la base de datos
        options = new FirebaseRecyclerOptions.Builder<Pelicula>().setQuery(mRef,Pelicula.class).build();
        //contiene el viewHolder que contiene el hacer click y el mantener pulsado al hacer click
        //tambien el seteo de peliculas para poder pasar por parametro el nombre, imagen y num de vistas
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pelicula, ViewHolderPelicula>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderPelicula viewHolderPelicula, int i, @NonNull Pelicula pelicula) {
                viewHolderPelicula.seteoPeliculas(
                        getApplicationContext(),
                        pelicula.getNombre(),
                        pelicula.getVistas(),
                        pelicula.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderPelicula onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inlfar el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula,parent,false);

                ViewHolderPelicula viewHolderPelicula = new ViewHolderPelicula(itemView);
                viewHolderPelicula.setOnClickListener(new ViewHolderPelicula.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // obtener los datos de la imagen
                        String imagen = getItem(position).getImagen();
                        String nombres = getItem(position).getNombre();
                        int vistas = getItem(position).getVistas();
                        // convertir a string la vista
                        String vistasString = String.valueOf(vistas);
                        String id = getItem(position).getId();
                        valueEventListener = mRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    //crear un objeto de la clase peliculas
                                    Pelicula pelicula = ds.getValue(Pelicula.class);

                                    if(pelicula.getId().equals(id)){
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
                        Intent intent = new Intent(PeliculasCliente.this, DetalleImagen.class);

                        // datos a pasar
                        intent.putExtra("Imagen", imagen);
                        intent.putExtra("Nombre", nombres);
                        intent.putExtra("Vista", vistasString);

                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
                return viewHolderPelicula;
            }
        };

        // manera en que se va a listar las imagenes, en este caso en dos columnas
        recyclerViewPeliculaC.setLayoutManager(new GridLayoutManager(PeliculasCliente.this, 2));
        //para que este escuchando el recyclerView, ya sea que estemos agregando o eliminando imagenes
        firebaseRecyclerAdapter.startListening();
        recyclerViewPeliculaC.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // firebaseRA va a estar escuchando si las imagenes fueron traidas con exito
        if(firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        } //sino no muestra ninguna imagen
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mRef != null && valueEventListener!= null){
            mRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_vista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Vista) {
            Toast.makeText(this, "Listar imagenes", Toast.LENGTH_SHORT).show();
            // aca va el metodo para ordenar img en 2 o en 3 columnas
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // dirige a la actividad anterior
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}