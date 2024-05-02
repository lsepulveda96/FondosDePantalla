package com.lsepulveda.fondos_de_pantalla.Categorias.Cat_Dispositivo;

public class CategoriaD {

    String categoria;
    String imagen;

    public CategoriaD() {
    }

    public CategoriaD(String categoria, String imagen) {
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
