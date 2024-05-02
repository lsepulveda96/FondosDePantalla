package com.lsepulveda.fondos_de_pantalla.CategoriasClienteFirebase;

public class ImgCatFirebaseElegida {

    String id;
    String imagen;
    String nombre;
    int vistas;

    public ImgCatFirebaseElegida() {
    }

    public ImgCatFirebaseElegida(String id, String imagen, String nombre, int vistas) {
        this.id = id;
        this.imagen = imagen;
        this.nombre = nombre;
        this.vistas = vistas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
