package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lsepulveda.fondos_de_pantalla.MainActivityAdministrador;
import com.lsepulveda.fondos_de_pantalla.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class RegistrarAdmin extends Fragment {

    TextView FechaRegistro;
    EditText Correo, Password, Nombres, Apellidos, Edad;
    Button Registrar;

    // parea crear correo electronico
    FirebaseAuth auth;

    // animacion al registrar
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrar_admin, container, false);

        FechaRegistro = view.findViewById(R.id.FechaRegistro);
        Correo = view.findViewById(R.id.Correo);
        Password = view.findViewById(R.id.Password);
        Nombres = view.findViewById(R.id.Nombres);
        Apellidos = view.findViewById(R.id.Apellidos);
        Edad = view.findViewById(R.id.Edad);

        Registrar = view.findViewById(R.id.Registrar);

        auth = FirebaseAuth.getInstance();

        Date date = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("d 'de' MMMM 'del' yyyy"); // 14 de enero del 2023
        String Sfecha = fecha.format(date); // convertimos fecha a string
        FechaRegistro.setText(Sfecha);

        // al hacer click en boton registrar
        Registrar.setOnClickListener(view1 -> {
            //convertimos a string los editText

            String nombre = Nombres.getText().toString();
            String apellidos = Apellidos.getText().toString();
            String edad = Edad.getText().toString();
            String correo = Correo.getText().toString();
            String pass = Password.getText().toString();

            // todos los campos seran obligatorios
            if(correo.equals("")||pass.equals("")||nombre.equals("")||apellidos.equals("")||edad.equals("")){
                Toast.makeText(getActivity(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            }else{
                //validacion correo
                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    Correo.setError("Correo Invalido");
                    Correo.setFocusable(true); // para que se mantenga parpadeando barra y pueda seguir escribiendo
                }
                //firebase pide correo valido, y que la contra sea <= 6
                else if (pass.length()<6) {
                    Password.setError("Contrasena debe ser mayor o igual a 6");
                    Password.setFocusable(true);
                }else{
                    RegistroAdministradores(correo,pass);
                }
            }
        });

        progressDialog = new ProgressDialog(getActivity()); // no estamos en una actividad, el contexto lo obtiene como getActivity
        progressDialog.setMessage("Registrando, espere por favor");
        progressDialog.setCancelable(false); // si no se establece, cuando sale la alerta, haces click en otro lado y se cancela

        return view;
    }

    // Metodo para registrar administradores
    private void RegistroAdministradores(String correo, String pass) {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(correo,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Si el admin fue creado correctamente
                if(task.isSuccessful()) {
                    progressDialog.dismiss(); // cierra el progressDialog
                    FirebaseUser user = auth.getCurrentUser(); // toma en cuenta al usuario actual
                    assert user != null; //afirmar que el admin no es null

                    //convertir a string los datos de los admin
                    String UID = user.getUid();
                    String correo = Correo.getText().toString();
                    String pass = Password.getText().toString();
                    String nombre = Nombres.getText().toString();
                    String apellidos = Apellidos.getText().toString();
                    String edad = Edad.getText().toString();
                    int EdadInt = Integer.parseInt(edad);

                    //permite enviar datos para ser recogidos en servidor de Firebase
                    HashMap<Object, Object> Administradores = new HashMap<>();
                    Administradores.put("UID", UID);
                    Administradores.put("CORREO", correo);
                    Administradores.put("PASSWORD", pass);
                    Administradores.put("NOMBRES", nombre);
                    Administradores.put("APELLIDOS", apellidos);
                    Administradores.put("EDAD", EdadInt);
                    Administradores.put("IMAGEN", "");

                    //Inicializar FirebaseDataBase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("BASE DE DATOS ADMINISTRADORES");
                    reference.child(UID).setValue(Administradores); // se lista a travez de UID. cada admin va a tener su propio UID, correo, etc
                    startActivity(new Intent(getActivity(), MainActivityAdministrador.class)); // nos dirige desde fragemento hacia el main activity administrador
                    Toast.makeText(getActivity(), "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else{
                    progressDialog.dismiss(); // si salio por error
                    Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Para mostar mensaje de error
                Toast.makeText(getActivity(), ""+e.getMessage() , Toast.LENGTH_SHORT);
            }
        });

    }
}