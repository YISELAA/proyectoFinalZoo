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

    // CREAR
    public void crearHabitat(Habitat h) {
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

//    // BUSCAR POR TIPO DE TERRENO
//    public List<Habitat> buscarPorTipoTerreno(String tipoTerreno) {
//        return dao.buscarPorTipoTerreno(tipoTerreno);
//    }
//
//    // FILTRAR POR CAPACIDAD MÍNIMA
//    public List<Habitat> filtrarPorCapacidadMinima(int capacidad) {
//        return dao.filtrarPorCapacidadMinima(capacidad);
//    }

    // LISTAR PAGINADO
    public List<Habitat> listarPaginado(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}
