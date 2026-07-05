package com.ues.edu.service;
import com.ues.edu.daos.PermisosDao;
import com.ues.edu.entidades.OpcionMenu;
import java.util.List;

public class PermisosService {

    private  PermisosDao dao = new PermisosDao();

    public List<OpcionMenu> listarTodasLasOpciones() {
        return dao.listarTodasLasOpciones();
    }

    public List<Integer> obtenerIdsOpcionesPorRol(int idRol) {
        return dao.obtenerIdsOpcionesPorRol(idRol);
    }

    public boolean guardarPermisos(int idRol, List<Integer> idsOpciones) {
        return dao.actualizarPermisos(idRol, idsOpciones);
    }
}