package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.SeriesA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderSerie extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderSerie.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
        void onItemLongClick(View view, int position); // admin mantiene presionado item

    }

    public void setOnClickListener(ViewHolderSerie.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderSerie(@NonNull View itemView) {
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
    public void seteoSeries(Context context, String nombre, int vista, String imagen){
        ImageView imagen_Serie;
        TextView nombreImagen_Serie;
        TextView vista_Serie;

        //Conexion con el item
        imagen_Serie = mView.findViewById(R.id.Imagen_Serie);
        nombreImagen_Serie = mView.findViewById(R.id.NombreImagen_Serie);
        vista_Serie = mView.findViewById(R.id.Vista_Serie);

        nombreImagen_Serie.setText(nombre);

        // convierte a string el parametro vista
        String vistaString = String.valueOf(vista);
        vista_Serie.setText(vistaString);

        // controlar posibles errores
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagen_Serie);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagen_Serie);
        }

    }

}
