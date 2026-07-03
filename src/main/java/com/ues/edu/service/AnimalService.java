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

    // 🔥 CREAR
    public void crearAnimal(Animal a) {
        dao.guardar(a);
    }

    // 🔥 ACTUALIZAR
    public void editarAnimal(Animal a) {
        dao.actualizar(a);
    }

    // 🔥 ELIMINAR
    public void eliminarAnimal(int id) {
        dao.eliminar(id);
    }

    // 🔥 LISTAR TODOS
    public List<Animal> obtenerAnimales() {
        return dao.listar();
    }

    // 🔥 BUSCAR POR ID
    public Animal buscarAnimal(int id) {
        return dao.buscarPorId(id);
    }

    // 🔥 BUSCAR POR NOMBRE
//    public List<Animal> buscarPorNombre(String nombre) {
//        return dao.buscarPorNombre(nombre);
//    }
//
//    // 🔥 FILTRAR POR HÁBITAT
//    public List<Animal> filtrarPorHabitat(int idHabitat) {
//        return dao.filtrarPorHabitat(idHabitat);
//    }

    // 🔥 PAGINACIÓN
    public List<Animal> obtenerAnimalesPaginados(int pagina, int size) {
        return dao.listarPaginado(pagina, size);
    }
}
