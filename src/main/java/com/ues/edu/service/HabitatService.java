/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.HabitatDao;
import com.ues.edu.entidades.Habitat;
import java.util.List;

/**
 *
 * @author MINED
 */
public class HabitatService {

    private HabitatDao dao = new HabitatDao();

    public void crearHabitat(Habitat h){
       dao.guardar(h);
    }

    // ACTUALIZAR
    public void editarHabitat(Habitat h) {
        dao.actualizar(h);
    }

    // ELIMINAR
    public void eliminarHabitat(int id) {
        dao.eliminar(id);
    }

    // LISTAR TODOS
    public List<Habitat> obtenerHabitats() {
        return dao.listar();
    }

    // BUSCAR POR ID
    public Habitat buscarHabitat(long id) {
        return dao.buscarPorId(id);
    }

    // LISTAR PAGINADO
    public List<Habitat> listarPaginado(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}
