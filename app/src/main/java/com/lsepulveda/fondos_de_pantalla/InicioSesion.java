package com.lsepulveda.fondos_de_pantalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioSesion extends AppCompatActivity {

    EditText correo, password;
    Button acceder;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        ActionBar actionBar = getSupportActionBar(); // creacion action bar
        assert actionBar != null;
        actionBar.setTitle("Inicio sesion");  // asigna un titulo
        actionBar.setDisplayHomeAsUpEnabled(true); // habilita boton de retroceso
        actionBar.setDisplayShowHomeEnabled(true); // ayuda al diseno

        correo = findViewById(R.id.Correo);
        password = findViewById(R.id.Password);
        acceder = findViewById(R.id.Acceder);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(InicioSesion.this);
        progressDialog.setMessage("Ingresando, por favor espere");
        progressDialog.setCancelable(false);

        acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String correoStr = correo.getText().toString();
                String passStr = password.getText().toString();

                    //validacion correo
                    if(!Patterns.EMAIL_ADDRESS.matcher(correoStr).matches()){
                        correo.setError("Correo Invalido");
                        correo.setFocusable(true); // para que se mantenga parpadeando barra y pueda seguir escribiendo
                    }
                    //firebase pide correo valido, y que la contra sea <= 6
                    else if (passStr.length()<6) {
                        password.setError("Contrasena debe ser mayor o igual a 6");
                        password.setFocusable(true);
                    }else{
                        LogeoAdministradores(correoStr,passStr);
                    }
                }
        });
    }


        private void LogeoAdministradores(String correo, String pass) {
            progressDialog.show();
            progressDialog.setCancelable(false);
            firebaseAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(InicioSesion.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        startActivity(new Intent(InicioSesion.this, MainActivityAdministrador.class));
                        assert user != null;
                        Toast.makeText(InicioSesion.this, "Bienvenido" + user.getEmail(), Toast.LENGTH_SHORT).show();
                        finish(); // para que la actividad inicio de sesion se destruya
                    } else {
                        progressDialog.dismiss();
                        usuarioInvalido();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    usuarioInvalido();

                }
            });
        }

    private void usuarioInvalido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioSesion.this);
        builder.setCancelable(false);
        builder.setTitle("Ha ocurrido un error");
        builder.setMessage("Verifique si el correo y contrasena son validos").setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    // boton atras
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

