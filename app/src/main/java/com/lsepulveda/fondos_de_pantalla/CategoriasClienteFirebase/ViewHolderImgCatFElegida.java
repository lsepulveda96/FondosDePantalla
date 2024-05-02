package com.lsepulveda.fondos_de_pantalla.CategoriasClienteFirebase;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderImgCatFElegida extends RecyclerView.ViewHolder {

    View mView;
    private ViewHolderImgCatFElegida.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, int position); // admin presiona normal el item
    }

    public void setOnClickListener(ViewHolderImgCatFElegida.ClickListener clickListener){
        mClickListener = clickListener;
    }

    public ViewHolderImgCatFElegida(@NonNull View itemView) {
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
    public void seteoCategoriaFElegida(Context context, String nombre, int vista, String imagen){
        ImageView imgCatFElegida;
        TextView nombreImg_Cat_Elegida, vista_Img_Cat_Elegida;

        //Conexion con el item
        imgCatFElegida = mView.findViewById(R.id.ImgCatFElegida);
        nombreImg_Cat_Elegida = mView.findViewById(R.id.NombreImg_Cat_Elegida);
        vista_Img_Cat_Elegida = mView.findViewById(R.id.Vista_Img_Cat_Elegida);


        nombreImg_Cat_Elegida.setText(nombre);
        String vistaString = String.valueOf(vista);
        vista_Img_Cat_Elegida.setText(vistaString);

        // controlar posibles errores
        try{
            //si la img fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(imgCatFElegida);
        }
        catch (Exception e){
            //si la img no se pudo traer
            Picasso.get().load(R.drawable.categoria).into(imgCatFElegida);
        }

    }

}
