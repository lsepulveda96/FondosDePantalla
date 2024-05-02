package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.PeliculasA;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderPelicula extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderPelicula.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
        void onItemLongClick(View view, int position); // admin mantiene presionado item

    }

    public void setOnClickListener(ViewHolderPelicula.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderPelicula(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view, getBindingAdapterPosition());
                return true;
            }
        });
    }


    //desde firebase a recycler view
    public void seteoPeliculas(Context context, String nombre, int vista, String imagen){
        ImageView imagen_Pelicula;
        TextView nombreImagenPelicula;
        TextView vistaPelicula;

        //Conexion con el item
        imagen_Pelicula = mView.findViewById(R.id.ImagenPelicula);
        nombreImagenPelicula = mView.findViewById(R.id.NombreImagenPelicula);
        vistaPelicula = mView.findViewById(R.id.VistaPeliculas);

        nombreImagenPelicula.setText(nombre);

        // convierte a string el parametro vista
        String vistaString = String.valueOf(vista);
        vistaPelicula.setText(vistaString);

        // controlar posibles errores
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagen_Pelicula);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagen_Pelicula);
        }

    }
}
