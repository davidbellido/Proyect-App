package com.example.greenchef.model;

public class Producto {
    private int id;
    private String nombre;
    private int id_usuario;
    private int id_supermercado;
    private double precio;
    private int imagen;

    public Producto(int id,String nombre, int id_usuario, int id_supermercado, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.id_usuario = id_usuario;
        this.id_supermercado = id_supermercado;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_supermercado() {
        return id_supermercado;
    }

    public void setId_supermercado(int id_supermercado) {
        this.id_supermercado = id_supermercado;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getImagen() {
        return imagen;
    }
}

