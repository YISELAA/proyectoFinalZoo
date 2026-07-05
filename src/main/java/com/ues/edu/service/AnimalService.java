/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.AnimalDao;
import com.ues.edu.entidades.Animal;
import java.util.List;

/**
 *
 * @author coc44
 */
public class AnimalService {

    private AnimalDao dao = new AnimalDao();

  
    public void crearAnimal(Animal a) {
        dao.guardar(a);
    }

   
    public void editarAnimal(Animal a) {
        dao.actualizar(a);
    }

   
    public void eliminarAnimal(int id) {
        dao.eliminar(id);
    }

   
    public List<Animal> obtenerAnimales() {
        return dao.listar();
    }

    
    public Animal buscarAnimal(int id) {
        return dao.buscarPorId(id);
    }


    public List<Animal> obtenerAnimalesPaginados(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}
