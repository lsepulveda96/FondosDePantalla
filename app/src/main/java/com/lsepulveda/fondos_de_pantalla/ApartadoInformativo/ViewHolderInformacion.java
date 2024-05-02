package com.lsepulveda.fondos_de_pantalla.ApartadoInformativo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Dispositivo.ViewHolderCD;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderInformacion extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderInformacion.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
    }

    public void setOnClickListener(ViewHolderInformacion.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderInformacion(@NonNull View itemView) {
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
    public void seteoInformacion(Context context, String nombre, String imagen){
        ImageView imagenInformativa;
        TextView tituloInformativoTXT;

        //Conexion con el item
        imagenInformativa = mView.findViewById(R.id.imagenInformativa);
        tituloInformativoTXT = mView.findViewById(R.id.tituloInformativoTXT);

        tituloInformativoTXT.setText(nombre);

        // controlar posibles errore
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagenInformativa);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagenInformativa);
        }

    }
}
