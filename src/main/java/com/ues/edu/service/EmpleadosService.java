/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ues.edu.service;

import com.ues.edu.daos.EmpleadoDao;
import com.ues.edu.entidades.Empleado;
import java.util.List;

/**
 *
 * @author MINED
 */
public class EmpleadosService {
    
   private EmpleadoDao dao = new EmpleadoDao();

    public void crearEmpleado(Empleado e) {

    if (dao.existeDui(e.getDui())) {
        throw new RuntimeException("Ya existe un empleado con ese DUI");
    }

    dao.guardar(e);
}

    public void editarEmpleado(Empleado e) {
        dao.actualizar(e);
    }

     
    public void eliminarEmpleado(int id) {
        dao.eliminar(id);
    }

   
    public List<Empleado> obtenerEmpleados() {
        return dao.listar();
    }

    public Empleado buscarEmpleado(int id) {
        return dao.buscarPorId(id);
    }
    
    public List<Empleado> obtenerSoloVeterinarios() {
        return dao.obtenerSoloVeterinarios(); 
    }
    
    public List<Empleado> obtenerSoloCuidadores() {
    return dao.obtenerSoloCuidadores();
}
}