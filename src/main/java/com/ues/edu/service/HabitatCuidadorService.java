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

    // Registrar cuidadores en un hábitat
    public void registrarAsignacion(int idHabitat, List<Long> idsEmpleados) {
        dao.guardar(idHabitat, idsEmpleados);
    }

    // Modificar cuidadores de un hábitat
    public void modificarAsignacion(int idHabitat, List<Long> idsEmpleados) {
        dao.actualizar(idHabitat, idsEmpleados);
    }

    // Remover todos los cuidadores de un hábitat
    public void removerAsignacion(int idHabitat) {
        dao.eliminar(idHabitat);
    }

    // Listar hábitats con sus cuidadores
    public List<Habitat> obtenerAsignaciones() {
        return dao.listar();
    }

    // Buscar un hábitat específico con sus cuidadores
    public Habitat buscarAsignacion(int idHabitat) {
        return dao.buscarPorId(idHabitat);
    }
    
    
}

