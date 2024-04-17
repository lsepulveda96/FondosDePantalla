package com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lsepulveda.fondos_de_pantalla.InicioSesion;
import com.lsepulveda.fondos_de_pantalla.MainActivityAdministrador;
import com.lsepulveda.fondos_de_pantalla.R;

import java.util.HashMap;

public class Cambio_Pass extends AppCompatActivity {

    TextView PassActual;
    EditText ActualPassET, NuevoPassEt;
    Button CAMBIARPASSBTN, IRINICIOBTN;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_pass);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Cambiar Contrasena");

        PassActual = findViewById(R.id.PassActual);
        ActualPassET = findViewById(R.id.ActualPassET);
        NuevoPassEt = findViewById(R.id.NuevoPassEt);
        CAMBIARPASSBTN = findViewById(R.id.CAMBIARPASSBTN);
        IRINICIOBTN = findViewById(R.id.IRINICIOBTN);

        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog(Cambio_Pass.this);

        //CONSULTA PASS EN BD
        Query query = BASE_DE_DATOS_ADMINISTRADORES.orderByChild("CORREO").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    //OBTENER PASS
                    String pass = ""+ds.child("PASSWORD").getValue();
                    PassActual.setText(pass);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CAMBIARPASSBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ACTUAL_PASS = ActualPassET.getText().toString();
                String NUEVO_PASS = NuevoPassEt.getText().toString();

                //CONDICIONES
                if(TextUtils.isEmpty(ACTUAL_PASS)){
                    Toast.makeText(Cambio_Pass.this, "El campo contrasena actual esta vacio", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(NUEVO_PASS)){
                    Toast.makeText(Cambio_Pass.this, "El campo contrasena nueva esta vacio", Toast.LENGTH_SHORT).show();
                }
                if(!ACTUAL_PASS.equals("") && !NUEVO_PASS.equals("") && NUEVO_PASS.length()>=6){
                    System.out.println("entra a cambiar pass: " + ACTUAL_PASS + " - " + NUEVO_PASS);
                    Cambio_Password(ACTUAL_PASS,NUEVO_PASS);
                }else{
                    NuevoPassEt.setError("La contrasena debe ser mayor o igual a 6");
                    NuevoPassEt.setFocusable(true);
                }
            }
        });

        IRINICIOBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Cambio_Pass.this, MainActivityAdministrador.class));
            }
        });
    }

    private void Cambio_Password(String pass_actual, final String nuevo_pass) {
        progressDialog.show();
        progressDialog.setTitle("Actualizando");
        progressDialog.setMessage("Espere por favor");

        // pide datos credeciales del user actual
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),pass_actual);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.updatePassword(nuevo_pass)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                String NUEVO_PASS = NuevoPassEt.getText().toString();
                                HashMap<String,Object> resultado = new HashMap<>(); // para enviar datos que van a ser recuperados en otro lado. el pass
                                resultado.put("PASSWORD", NUEVO_PASS);
                                // ACTUALIZAR EL PASS EN DB
                                BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(resultado)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Cambio_Pass.this, "Contrasena cambiada", Toast.LENGTH_SHORT).show();
                                                //CERRAR SESION
                                                firebaseAuth.signOut();
                                                startActivity(new Intent(Cambio_Pass.this, InicioSesion.class));
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Cambio_Pass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Cambio_Pass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Cambio_Pass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}