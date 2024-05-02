package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.MusicaA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderMusica extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderMusica.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
        void onItemLongClick(View view, int position); // admin mantiene presionado item

    }

    public void setOnClickListener(ViewHolderMusica.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderMusica(@NonNull View itemView) {
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
    public void seteoMusica(Context context, String nombre, int vista, String imagen){
        ImageView imagen_Musica;
        TextView nombreImagen_Musica;
        TextView vista_Musica;

        //Conexion con el item
        imagen_Musica = mView.findViewById(R.id.Imagen_Musica);
        nombreImagen_Musica = mView.findViewById(R.id.NombreImagen_Musica);
        vista_Musica = mView.findViewById(R.id.Vista_Musica);

        nombreImagen_Musica.setText(nombre);

        // convierte a string el parametro vista
        String vistaString = String.valueOf(vista);
        vista_Musica.setText(vistaString);

        // controlar posibles errore
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagen_Musica);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagen_Musica);
        }

    }

}
