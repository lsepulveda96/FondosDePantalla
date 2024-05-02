package com.lsepulveda.fondos_de_pantalla.CategoriasCliente;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

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
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA.Serie;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA.ViewHolderSerie;
import com.lsepulveda.fondos_de_pantalla.DetalleCliente.DetalleImagen;
import com.lsepulveda.fondos_de_pantalla.R;

import java.util.HashMap;

public class SeriesCliente extends AppCompatActivity {

    RecyclerView recyclerViewSerieC;
    FirebaseDatabase mFireBaseDataBase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Serie, ViewHolderSerie> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Serie> options;

    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Series");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewSerieC = findViewById(R.id.recyclerViewSerieC);
        recyclerViewSerieC.setHasFixedSize(true); // para que el recycler view se adapte al eliminar o agregar elementos.

        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mRef = mFireBaseDataBase.getReference("SERIE");

        listarImagenesSerie();
    }

    private void listarImagenesSerie() {
        // le pasa mRef para poder leer la base de datos
        options = new FirebaseRecyclerOptions.Builder<Serie>().setQuery(mRef,Serie.class).build();
        //contiene el viewHolder que contiene el hacer click y el mantener pulsado al hacer click
        //tambien el seteo de series para poder pasar por parametro el nombre, imagen y num de vistas
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Serie, ViewHolderSerie>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderSerie viewHolderSerie, int i, @NonNull Serie serie) {
                viewHolderSerie.seteoSeries(
                        getApplicationContext(),
                        serie.getNombre(),
                        serie.getVistas(),
                        serie.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderSerie onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inlfar el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie,parent,false);

                ViewHolderSerie viewHolderSerie = new ViewHolderSerie(itemView);
                viewHolderSerie.setOnClickListener(new ViewHolderSerie.ClickListener() {
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
                                    Serie serie = ds.getValue(Serie.class);

                                    if(serie.getId().equals(id)){
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
                        Intent intent = new Intent(SeriesCliente.this, DetalleImagen.class);

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
                return viewHolderSerie;
            }
        };

        // manera en que se va a listar las imagenes, en este caso en dos columnas
        recyclerViewSerieC.setLayoutManager(new GridLayoutManager(SeriesCliente.this, 2));
        //para que este escuchando el recyclerView, ya sea que estemos agregando o eliminando imagenes
        firebaseRecyclerAdapter.startListening();
        recyclerViewSerieC.setAdapter(firebaseRecyclerAdapter);
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
            // aca iria el metodo ordenar en 2 o 3 columnas
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}