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


    public void editarHabitat(Habitat h) {
        dao.actualizar(h);
    }


    public void eliminarHabitat(int id) {
        dao.eliminar(id);
    }

  
    public List<Habitat> obtenerHabitats() {
        return dao.listar();
    }

 
    public Habitat buscarHabitat(long id) {
        return dao.buscarPorId(id);
    }


    public List<Habitat> listarPaginado(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}
