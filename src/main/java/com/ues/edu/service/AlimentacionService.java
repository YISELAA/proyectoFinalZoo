/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.AlimentacionDao;
import com.ues.edu.entidades.Alimentacion;
import java.util.List;


/**
 *
 * @author coc44
 */
public class AlimentacionService {

    private AlimentacionDao dao =
            new AlimentacionDao();

   
    public void crearAlimentacion(Alimentacion a) {
        dao.guardar(a);
    }

   
    public void editarAlimentacion(Alimentacion a) {
        dao.actualizar(a);
    }

    
    public void eliminarAlimentacion(int id) {
        dao.eliminar(id);
    }

    
    public List<Alimentacion> obtenerAlimentaciones() {
        return dao.listar();
    }

    
    public Alimentacion buscarAlimentacion(int id) {
        return dao.buscarPorId(id);
    }
}