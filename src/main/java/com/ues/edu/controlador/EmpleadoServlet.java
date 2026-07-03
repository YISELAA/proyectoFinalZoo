/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Empleado;
import com.ues.edu.service.EmpleadosService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

;

/**
 *
 * @author MINED
 */
@WebServlet(name = "EmpleadoServlet", urlPatterns = {"/EmpleadoServlet"})
public class EmpleadoServlet extends HttpServlet {

    private EmpleadosService empleadoService = new EmpleadosService();

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                    return f.getName().equals("habitatsAsignados")
                            || f.getName().equals("cuidadores")
                            || f.getName().equals("historiales")
                            || f.getName().equals("usuario")
                            || f.getName().equals("listaAnimales");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EmpleadoServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EmpleadoServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String tipo = request.getParameter("tipo");

        response.setContentType("application/json");

        if (idParam != null && !idParam.isEmpty()) {

            int id = Integer.parseInt(idParam);
            Empleado empleado = empleadoService.buscarEmpleado(id);

            if (empleado == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"mensaje\":\"Empleado no encontrado\"}");
                return;
            }

            response.getWriter().write(gson.toJson(empleado));
            return;
        }

        List<Empleado> empleados;

        if ("cuidadores".equalsIgnoreCase(tipo)) {
            empleados = empleadoService.obtenerSoloCuidadores();

        } else if ("veterinarios".equalsIgnoreCase(tipo)) {
            empleados = empleadoService.obtenerSoloVeterinarios();

        } else {
            empleados = empleadoService.obtenerEmpleados();
        }

        response.getWriter().write(gson.toJson(empleados));
    }
    
    
    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {

            Empleado empleado
                    = gson.fromJson(request.getReader(), Empleado.class);
            System.out.println("DUI RECIBIDO: " + empleado.getDui());
            String error = validarEmpleado(empleado);

            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                        "{\"error\":\"" + error + "\"}"
                );
                return;
            }

            empleadoService.crearEmpleado(empleado);

            response.getWriter().write(
                    "{\"mensaje\":\"Empleado guardado correctamente\"}"
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"" + e.getMessage() + "\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            response.getWriter().write(
                    "{\"error\":\"Error interno del servidor\"}"
            );
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");

        Empleado empleado = gson.fromJson(request.getReader(), Empleado.class);

        String error = validarEmpleado(empleado);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }

        empleadoService.editarEmpleado(empleado);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Empleado actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        try {

            int id = Integer.parseInt(request.getParameter("id"));

            empleadoService.eliminarEmpleado(id);

            response.getWriter().write(
                    "{\"mensaje\":\"Empleado eliminado correctamente\"}"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            response.getWriter().write(
                    "{\"error\":\"" + e.getMessage() + "\"}"
            );
        }
    }

    private String validarEmpleado(Empleado e) {

        if (e == null) {
            return "Empleado inválido";
        }

        if (e.getNombre() == null || e.getNombre().trim().length() < 3) {
            return "Nombre mínimo 3 caracteres";
        }

        if (!e.getNombre().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            return "El nombre no debe contener números ni caracteres especiales";
        }

        if (e.getApellido() == null || e.getApellido().trim().length() < 3) {
            return "Apellido mínimo 3 caracteres";
        }

        if (!e.getApellido().matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            return "El apellido no debe contener números ni caracteres especiales";
        }

        if (e.getDui() == null || !e.getDui().matches("^\\d{8}-\\d$")) {
            return "El DUI debe tener el formato ########-#";
        }

        return null;
    }
}
