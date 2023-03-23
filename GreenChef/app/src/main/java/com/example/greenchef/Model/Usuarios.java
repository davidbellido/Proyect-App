package com.example.greenchef.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Usuarios extends RealmObject {
    private String username;
    private String correo;
    private String password;

    @PrimaryKey
    private String id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
