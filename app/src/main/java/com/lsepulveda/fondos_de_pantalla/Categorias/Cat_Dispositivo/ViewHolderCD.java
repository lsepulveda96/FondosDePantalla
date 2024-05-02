package com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Dispositivo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

// categorias del dispositivo
public class ViewHolderCD extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderCD.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
    }

    public void setOnClickListener(ViewHolderCD.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderCD(@NonNull View itemView) {
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
    public void seteoCategoriaD(Context context, String categoria, String imagen){
        ImageView imagenCategoriaD;
        TextView nombreCategoriaD;

        //Conexion con el item
        imagenCategoriaD = mView.findViewById(R.id.ImagenCategoriaD);
        nombreCategoriaD = mView.findViewById(R.id.NombreCategoriaD);

        nombreCategoriaD.setText(categoria);

        // controlar posibles errore
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imagenCategoriaD);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imagenCategoriaD);
        }

    }
}
