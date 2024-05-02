package com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Firebase;

public class CategoriaF {
    String categoria;
    String imagen;

    public CategoriaF() {
    }

    public CategoriaF(String categoria, String imagen) {
        this.categoria = categoria;
        this.imagen = imagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
