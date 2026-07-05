package com.ues.edu.service;

import com.ues.edu.daos.RolDao;
import com.ues.edu.entidades.Rol;
import java.util.List;

public class RolService {
    RolDao dao = new RolDao();

    public List<Rol> obtenerRoles() {
        return dao.listar();
    }
}