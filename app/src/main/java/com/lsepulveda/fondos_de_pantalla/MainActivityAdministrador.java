package com.lsepulveda.fondos_de_pantalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador.InicioAdmin;
import com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador.ListarAdmin;
import com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador.PerfilAdmin;
import com.lsepulveda.fondos_de_pantalla.FragmentosAdministrador.RegistrarAdmin;

//permite hacer click en el menu que tiene el administrador
public class MainActivityAdministrador extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_administrador);

        // Para barra navegacion
        Toolbar toolbar = findViewById(R.id.toolbarA);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_A);
        NavigationView navigationView = findViewById(R.id.nav_viewA);
        navigationView.setNavigationItemSelectedListener(this);
        //para cambiar el color de las letras que vienen por defecto
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser(); // obtiene el admin actual

        //fragento por defecto
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanierA,
                    new InicioAdmin()).commit();
            navigationView.setCheckedItem(R.id.InicioAdmin);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // todos los fragmentos se van a visualizar dentro del frameLayout containerA, es por eso que cuando se abre cada opcion, se usa el replace, para reemplazar ese fragment
        switch (item.getItemId()){
            case R.id.InicioAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanierA,
                        new InicioAdmin()).commit();
                break;
            case R.id.PerfilAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanierA,
                        new PerfilAdmin()).commit();
                break;
            case R.id.RegistrarAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanierA,
                        new RegistrarAdmin()).commit();
                break;
            case R.id.ListarAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanierA,
                        new ListarAdmin()).commit();
                break;
            case R.id.Salir:
                cerrarSesion();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void comprobandoInicioSesion(){
        if(user!=null){
            //si el admin a iniciado sesion
            Toast.makeText(this, "se ha iniciado sesion", Toast.LENGTH_SHORT).show();
        }else{
            //si no se ha iniciado sesion, es porque es un usuario cliente
            startActivity(new Intent(MainActivityAdministrador.this, MainActivity.class));
            finish();
        }
    }

    private void cerrarSesion(){
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivityAdministrador.this, MainActivity.class));
        Toast.makeText(this, "sesion cerrada con exito", Toast.LENGTH_SHORT).show();
    }

    // cuando se ejecute esta actividad, esto va a ser lo primero que se ejecute
    @Override
    protected void onStart() {
        comprobandoInicioSesion();
        super.onStart();
    }
}