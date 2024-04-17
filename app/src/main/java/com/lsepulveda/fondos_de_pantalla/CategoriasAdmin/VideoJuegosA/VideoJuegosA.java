package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.VideoJuegosA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.AgregarPelicula;
import com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA.PeliculasA;
import com.lsepulveda.fondos_de_pantalla.R;

public class VideoJuegosA extends AppCompatActivity {

    RecyclerView recyclerViewVideoJuegos;
    FirebaseDatabase mFireBaseDataBase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<VideoJuegos, ViewHolderVideoJuegos> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<VideoJuegos> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_juegos);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("VideoJuegos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        recyclerViewVideoJuegos = findViewById(R.id.recyclerViewVideoJuegos);
        recyclerViewVideoJuegos.setHasFixedSize(true); // para que el recycler view se adapte al eliminar o agregar elementos.

        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mRef = mFireBaseDataBase.getReference("VIDEOJUEGOS");

        listarImagenesVideoJuegos();

    }

    private void listarImagenesVideoJuegos() {
        // le pasa mRef para poder leer la base de datos
        options = new FirebaseRecyclerOptions.Builder<VideoJuegos>().setQuery(mRef,VideoJuegos.class).build();
        //contiene el viewHolder que contiene el hacer click y el mantener pulsado al hacer click
        //tambien el seteo de videojuegos para poder pasar por parametro el nombre, imagen y num de vistas
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<VideoJuegos, ViewHolderVideoJuegos>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderVideoJuegos viewHolderVideoJuegos, int i, @NonNull VideoJuegos videoJuegos) {
                viewHolderVideoJuegos.seteoVideoJuegos(
                        getApplicationContext(),
                        videoJuegos.getNombre(),
                        videoJuegos.getVistas(),
                        videoJuegos.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderVideoJuegos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inlfar el item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videojuegos,parent,false);

                ViewHolderVideoJuegos viewHolderVideoJuegos = new ViewHolderVideoJuegos(itemView);
                viewHolderVideoJuegos.setOnClickListener(new ViewHolderVideoJuegos.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(VideoJuegosA.this, "ITEM CLICK", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        final String nombre = getItem(position).getNombre();
                        final String imagen = getItem(position).getImagen();

                        int vista = getItem(position).getVistas();
                        final String vistaString = String.valueOf(vista);

                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoJuegosA.this);

                        String [] opciones = {"Actualizar", "Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Intent intent = new Intent(VideoJuegosA.this, AgregarVideojuegos.class);
                                    intent.putExtra("NombreEnviado", nombre);
                                    intent.putExtra("ImagenEnviada", imagen);
                                    intent.putExtra("VistaEnviada", vistaString);
                                    startActivity(intent);
                                }
                                if(i==1){
                                    eliminarDatos(nombre, imagen);
                                }
                            }
                        });

                        builder.create().show();
                    }
                });
                return viewHolderVideoJuegos;
            }
        };

        // manera en que se va a listar las imagenes, en este caso en dos columnas
        recyclerViewVideoJuegos.setLayoutManager(new GridLayoutManager(VideoJuegosA.this, 2));
        //para que este escuchando el recyclerView, ya sea que estemos agregando o eliminando imagenes
        firebaseRecyclerAdapter.startListening();
        recyclerViewVideoJuegos.setAdapter(firebaseRecyclerAdapter);
    }

    private void eliminarDatos(final String nombreActual, final String imagenActual){
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoJuegosA.this);
        builder.setTitle("Eliminar");
        builder.setMessage("Desea eliminar imagen?");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // eliminar imagen de la base de datos
                Query query = mRef.orderByChild("nombre").equalTo(nombreActual);
                //metodo que escucha si se elimina una img
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recorre todas las img de pelicula y borra esa img con el nombre que elegimos antes
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(VideoJuegosA.this, "la imagen ha sido eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VideoJuegosA.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //eliminacion desde la carpeta de firebase para liberar espacio
                StorageReference imagenSeleccionada = getInstance().getReferenceFromUrl(imagenActual);
                imagenSeleccionada.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    // entra si apreta boton si
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(VideoJuegosA.this, "eliminado", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoJuegosA.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //si presiona boton que no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(VideoJuegosA.this, "cancelado por admin", Toast.LENGTH_SHORT).show();
            }
        });

        // sin esta linea no se va a mostrar el cuadro de dialogo
        builder.create().show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);
        menuInflater.inflate(R.menu.menu_vista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.Agregar:
                startActivity(new Intent(VideoJuegosA.this, AgregarVideojuegos.class));
                finish();
                break;
            case R.id.Vista:
                Toast.makeText(this, "Listar imagenes", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}