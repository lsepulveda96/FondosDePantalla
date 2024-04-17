package com.lsepulveda.fondos_de_pantalla.CategoriasAdmin.VideoJuegosA;

public class VideoJuegos {

    private String imagen;
    private String nombre;
    private int vistas;

    public VideoJuegos(String imagen, String nombre, int vistas) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.vistas = vistas;
    }

    public VideoJuegos() {
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVistas() {
        return vistas;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }
}
