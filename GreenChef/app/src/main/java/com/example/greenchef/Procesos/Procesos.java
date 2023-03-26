
package com.example.greenchef.Procesos;

import com.example.greenchef.Model.Usuarios;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class Procesos {
    private static Procesos instancia;
    private Realm realm;
    private List<Usuarios> listaUsuariosLeer;
    List<Usuarios> listaUsuarios;

    /**
     * Constructor privado para crear una instancia de la clase Procesos.
     * La instancia utiliza una base de datos Realm y dos listas de usuarios, una para leer y otra para escribir.
     */
    public Procesos() {
        realm = Realm.getDefaultInstance();
        listaUsuarios = new ArrayList<>();
        listaUsuariosLeer = new ArrayList<>();
    }

    /**
     * Retorna la instancia única de la clase Procesos.
     * Si la instancia no existe, se crea una nueva instancia.
     *
     * @return la instancia única de la clase Procesos.
     */
    public static synchronized Procesos getInstance() {
        if (instancia == null) {
            instancia = new Procesos();
        }
        return instancia;
    }

    /**
     * Lee los usuarios almacenados en la base de datos Realm y los agrega a la lista de usuarios.
     */
    public void leerUsuarios() {
        listaUsuarios.clear();
        listaUsuariosLeer = realm.where(Usuarios.class).findAll();
        for (Usuarios u : listaUsuariosLeer) {
            listaUsuarios.add(u);
        }
    }

    /**
     * Agrega los usuarios de la lista de usuarios a la base de datos Realm.
     */
    public void subirUsuarios() {
        realm.beginTransaction();
        for (Usuarios u : listaUsuarios) {
            realm.copyToRealm(u);
        }
        realm.commitTransaction();
    }

    /**
     * Agrega los usuarios de la lista de usuarios a la base de datos Realm si no existen ya en la base de datos.
     */
    public void guardarDatos() {
        boolean existe = false;
        realm.beginTransaction();
        for (Usuarios u : listaUsuarios) {
            if (listaUsuariosLeer.size() != 0) {
                for (Usuarios u2 : listaUsuariosLeer) {
                    if (u.getId().equals(u2.getId())) {
                        existe = true;
                    }
                }
                if (!existe) {
                    realm.copyToRealm(u);
                }
            } else {
                realm.copyToRealm(u);
            }
        }
        realm.commitTransaction();
    }

    /**
     * Comprueba si existe los usuarios en la base de datos
     */
    public boolean existeUsuario(String nombreUsuario) {
        // Obtener una instancia de Realm
        realm = Realm.getDefaultInstance();

        // Buscar usuarios con el nombre especificado
        RealmResults<Usuarios> usuariosEncontrados = realm.where(Usuarios.class)
                .equalTo("username", nombreUsuario)
                .findAll();

        // Si se encontró al menos un usuario, entonces existe
        boolean existe = !usuariosEncontrados.isEmpty();

        // Cerrar la instancia de Realm
        realm.close();

        // Devolver el resultado de la búsqueda
        return existe;
    }

    public boolean comprobarPassword(String nombreUsuario, String password){
        boolean pswd;
        // Recuperar el usuario actualmente conectado
        Usuarios user = Realm.getDefaultInstance().where(Usuarios.class)
                .equalTo("username", nombreUsuario)
                .findFirst();

        // Comprobar si la contraseña ingresada es igual a la contraseña almacenada en la base de datos
        if (user.getPassword().equals(password)) {
            pswd = true;
        } else {
            pswd = false;
        }

        return pswd;
    }

    /**
     * Crea un objeto de la clase Usuarios y los guarda en la base de datos
     * @param username
     * @param email
     * @param password
     */
    public void guardarUsuario(String username, String email, String password) {
        realm.beginTransaction();
        Usuarios usuario = realm.createObject(Usuarios.class, UUID.randomUUID().toString());
        usuario.setUsername(username);
        usuario.setCorreo(email);
        usuario.setPassword(password);
        realm.commitTransaction();
    }

    /**
     * Elimina un usuario de la lista de usuarios y de la base de datos Realm.
     *
     * @param usuario el usuario a eliminar.
     */
    public void eliminarUsuario(Usuarios usuario) {
        realm.beginTransaction();
        for (Usuarios u : listaUsuarios) {
            if (u.getId().equals(usuario.getId())) {
                u.deleteFromRealm();
            }
        }
        realm.commitTransaction();
    }

    /**
     * Actualiza un usuario en la lista de usuarios y en la base de datos Realm.
     *
     * @param usuario el usuario a actualizar.
     */
    public void actualizarUsuario(Usuarios usuario) {
        realm.beginTransaction();
        for (Usuarios u : listaUsuarios) {
            if (u.getId().equals(usuario.getId())) {
                u.setUsername(usuario.getUsername());
                u.setCorreo(usuario.getCorreo());
                u.setPassword(usuario.getPassword());
            }
        }
        realm.commitTransaction();
    }

    public boolean isRealmEmpty() {
        return realm.isEmpty();
    }

    //get lista
    public List<Usuarios> getListaUsuarios() {
        System.out.println("Tamaño de la lista: " + listaUsuarios.size());
        return listaUsuarios;
    }

    public void setListaUsuarios(List<Usuarios> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public void close() {
        realm.close();
    }
}
