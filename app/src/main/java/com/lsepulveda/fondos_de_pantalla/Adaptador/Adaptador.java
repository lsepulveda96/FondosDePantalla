package com.lsepulveda.fondos_de_pantalla.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lsepulveda.fondos_de_pantalla.Detalle.DetalleAdministrador;
import com.lsepulveda.fondos_de_pantalla.Modelo.Administrador;
import com.lsepulveda.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyHolder>{

    private Context context;
    private List<Administrador> administradores;

    public Adaptador(Context context, List<Administrador> administradores) {
        this.context = context;
        this.administradores = administradores;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // para inflar el layout, en este caso el admin_item
        View view = LayoutInflater.from(context).inflate(R.layout.admin_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // para obtener los datos del modelo, en este caso administrador
        String UID = administradores.get(position).getUID();
        String IMAGEN = administradores.get(position).getIMAGEN();
        String NOMBRES = administradores.get(position).getNOMBRES();
        String APELLIDOS = administradores.get(position).getAPELLIDOS();
        String CORREO = administradores.get(position).getCORREO();
        int EDAD = administradores.get(position).getEDAD();
        String edadString = String.valueOf(EDAD);

        // SETEO DE DATOS
        holder.NombreADMIN.setText(NOMBRES);
        holder.CorreoADMIN.setText(CORREO);

        try{
            // Si existe imagen en db, se carga dentro de la img circular que ya teniamos
            Picasso.get().load(IMAGEN).placeholder(R.drawable.perfil_item).into(holder.PerfilADMIN);
        }catch(Exception e ){
            // Si no existe imagen en db
            Picasso.get().load(R.drawable.perfil_item).into(holder.PerfilADMIN);
        }

        // al hacer click en un administrador
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ver detalle
                Intent intent = new Intent(context, DetalleAdministrador.class);
                // pasar los datos a la siguiente administrador
                intent.putExtra("UID", UID);
                intent.putExtra("NOMBRES", NOMBRES);
                intent.putExtra("APELLIDOS", APELLIDOS);
                intent.putExtra("CORREO", CORREO);
                intent.putExtra("EDAD", edadString);
                intent.putExtra("IMAGEN", IMAGEN);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        // recorre la lista de admin registrados en la db y devuleve el tamano de la lista
        return administradores.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        // declaramos vistas
        CircleImageView PerfilADMIN;
        TextView NombreADMIN, CorreoADMIN;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            PerfilADMIN = itemView.findViewById(R.id.PerfilAdmin);
            NombreADMIN = itemView.findViewById(R.id.NombresADMIN);
            CorreoADMIN = itemView.findViewById(R.id.CorreoADMIN);
        }
    }
}
