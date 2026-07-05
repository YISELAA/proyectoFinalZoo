package com.ues.edu.service;

import com.ues.edu.daos.UsuarioDao;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.modelo.EncriptarContrasenia;
import java.util.List;

public class UsuariosService {

    UsuarioDao dao = new UsuarioDao();

    public List<Usuario> mostrarUsuarios() {
        return dao.listar();
    }

    public Usuario buscarUsuario(int id) {
        return dao.buscarPorId(id);
    }

  public void crearUsuario(Usuario u) {

    System.out.println("===============");
    System.out.println("Usuario recibido: " + u.getNombreUsuario());
    System.out.println("Contraseña recibida: " + u.getContrasena());
    System.out.println("===============");

    if (dao.existeNombreUsuario(u.getNombreUsuario())) {
        throw new RuntimeException("El nombre de usuario ya existe");
    }

    if (u.getContrasena() == null) {
        throw new RuntimeException("LA CONTRASEÑA LLEGA NULL");
    }

    EncriptarContrasenia enc = new EncriptarContrasenia();

    u.setContrasena(
            enc.contraseniaencriptar(
                    u.getContrasena()
            )
    );

    dao.guardar(u);
}
   public void eliminarUsuario(int id){
       dao.eliminar(id);
   }
   
public void actualizarUsuario(Usuario u) {

    Usuario existente = dao.buscarPorId(u.getId());

    if (u.getContrasena() == null ||
        u.getContrasena().trim().isEmpty()) {

        // conservar contraseña actual
        u.setContrasena(existente.getContrasena());

    } else {

        EncriptarContrasenia enc =
                new EncriptarContrasenia();

        u.setContrasena(
                enc.contraseniaencriptar(
                        u.getContrasena()
                )
        );
    }

    dao.actualizar(u);
}   
}