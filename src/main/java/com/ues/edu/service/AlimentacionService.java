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

    // ==========================
    // CREAR
    // ==========================
    public void crearAlimentacion(Alimentacion a) {
        dao.guardar(a);
    }

    // ==========================
    // ACTUALIZAR
    // ==========================
    public void editarAlimentacion(Alimentacion a) {
        dao.actualizar(a);
    }

    // ==========================
    // ELIMINAR
    // ==========================
    public void eliminarAlimentacion(int id) {
        dao.eliminar(id);
    }

    // ==========================
    // LISTAR
    // ==========================
    public List<Alimentacion> obtenerAlimentaciones() {
        return dao.listar();
    }

    // ==========================
    // BUSCAR POR ID
    // ==========================
    public Alimentacion buscarAlimentacion(int id) {
        return dao.buscarPorId(id);
    }
}