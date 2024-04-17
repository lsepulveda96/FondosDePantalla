package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.VideoJuegosA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderVideoJuegos extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderVideoJuegos.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
        void onItemLongClick(View view, int position); // admin mantiene presionado item

    }

    public void setOnClickListener(ViewHolderVideoJuegos.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderVideoJuegos(@NonNull View itemView) {
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
    public void seteoVideoJuegos(Context context, String nombre, int vista, String imagen){
        ImageView imagen_VideoJuegos;
        TextView nombreImagen_VideoJuegos;
        TextView vista_VideoJuegos;

        //Conexion con el item
        imagen_VideoJuegos = mView.findViewById(R.id.Imagen_VideoJuegos);
        nombreImagen_VideoJuegos = mView.findViewById(R.id.NombreImagen_VideoJuegos);
        vista_VideoJuegos = mView.findViewById(R.id.Vista_VideoJuegos);

        nombreImagen_VideoJuegos.setText(nombre);

        // convierte a string el parametro vista
        String vistaString = String.valueOf(vista);
        vista_VideoJuegos.setText(vistaString);

        // controlar posibles errore
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).into(imagen_VideoJuegos);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
