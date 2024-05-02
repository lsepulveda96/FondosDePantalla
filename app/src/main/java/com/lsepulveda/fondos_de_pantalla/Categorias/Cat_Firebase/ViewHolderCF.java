package com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Firebase;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

// viewholder categorias firebase
public class ViewHolderCF extends RecyclerView.ViewHolder {
    View mView;
    private ViewHolderCF.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
    }

    public void setOnClickListener(ViewHolderCF.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderCF(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        });
    }

    //desde firebase a recycler view
    public void seteoCategoriaF(Context context, String categoria, String imagen){
        ImageView imagenCategoriaF;
        TextView nombreCategoriaF;

        //Conexion con el item
        imagenCategoriaF = mView.findViewById(R.id.ImagenCategoriaF);
        nombreCategoriaF = mView.findViewById(R.id.NombreCategoriaF);

        nombreCategoriaF.setText(categoria);

        // controlar posibles errores
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagenCategoriaF);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagenCategoriaF);
        }

    }
}
