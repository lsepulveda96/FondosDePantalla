package com.lsepulveda.fondos_de_pantalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.lsepulveda.fondos_de_pantalla.FragmentosCliente.AcercaDeCliente;
import com.lsepulveda.fondos_de_pantalla.FragmentosCliente.CompartirCliente;
import com.lsepulveda.fondos_de_pantalla.FragmentosCliente.InicioCliente;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Para barra navegacion
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //para cambiar el color de las letras que vienen por defecto
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //fragento por defecto
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier,
                    new InicioCliente()).commit();
            navigationView.setCheckedItem(R.id.InicioCliente);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // todos los fragmentos se van a visualizar dentro del frameLayout containerA, es por eso que cuando se abre cada opcion, se usa el replace, para reemplazar ese fragment
        switch (item.getItemId()){
            case R.id.InicioCliente:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier,
                        new InicioCliente()).commit();
                break;
            case R.id.AcercaDe:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier,
                        new AcercaDeCliente()).commit();
                break;
            case R.id.Compartir:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier,
                        new CompartirCliente()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}