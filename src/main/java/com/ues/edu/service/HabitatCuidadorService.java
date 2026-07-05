/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.HabitatCuidadorDao;
import com.ues.edu.entidades.Habitat;
import java.util.List;
/**
 *
 * @author coc44
 */

public class HabitatCuidadorService {

    private HabitatCuidadorDao dao = new HabitatCuidadorDao();

 
    public void registrarAsignacion(int idHabitat, List<Long> idsEmpleados) {
        dao.guardar(idHabitat, idsEmpleados);
    }

 
    public void modificarAsignacion(int idHabitat, List<Long> idsEmpleados) {
        dao.actualizar(idHabitat, idsEmpleados);
    }

 
    public void removerAsignacion(int idHabitat) {
        dao.eliminar(idHabitat);
    }


    public List<Habitat> obtenerAsignaciones() {
        return dao.listar();
    }


    public Habitat buscarAsignacion(int idHabitat) {
        return dao.buscarPorId(idHabitat);
    }
    
    
}

