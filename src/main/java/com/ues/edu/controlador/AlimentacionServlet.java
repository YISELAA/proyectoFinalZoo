/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Alimentacion;
import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.service.AlimentacionService;
import com.ues.edu.service.EmpleadosService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author coc44
 */
@WebServlet(name = "AlimentacionServlet", urlPatterns = {"/AlimentacionServlet"})
public class AlimentacionServlet extends HttpServlet {

    private AlimentacionService alimentacionService
            = new AlimentacionService();

    private EmpleadosService empleadosService = new EmpleadosService();

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(
                    java.lang.reflect.Modifier.TRANSIENT)
            .addSerializationExclusionStrategy(
                    new com.google.gson.ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(
                        com.google.gson.FieldAttributes f) {

                    return f.getName().equals("alimentaciones")
                            || f.getName().equals("historiales")
                            || f.getName().equals("cuidadores")
                            || f.getName().equals("habitatsAsignados")
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
            out.println("<title>Servlet AlimentacionServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AlimentacionServlet at " + request.getContextPath() + "</h1>");
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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 🌟 NUEVO: CAPTURAR EL PARÁMETRO DE ACCIÓN PARA LA SESIÓN (MEJORADO)
            String accion = request.getParameter("accion");
            if ("obtenerSesion".equals(accion)) {
                jakarta.servlet.http.HttpSession session = request.getSession(false);

                Object logueado = null;
                if (session != null) {
                    // Probamos los nombres de atributos más comunes que podrías haber usado en tu LoginServlet:
                    logueado = session.getAttribute("usuarioLogueado");
                    if (logueado == null) {
                        logueado = session.getAttribute("empleadoLogueado");
                    }
                    if (logueado == null) {
                        logueado = session.getAttribute("usuario");
                    }
                    if (logueado == null) {
                        logueado = session.getAttribute("empleado");
                    }
                }

                if (logueado != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(this.gson.toJson(logueado));
                } else {
                    // Si sigue saliendo null, es porque no hay sesión o el nombre del atributo en el login es diferente
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"No hay sesión activa en el servidor.\"}");
                }
                return; // Detiene la ejecución
            }

            String idParam = request.getParameter("id");

            // 🔥 BUSCAR POR ID
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Alimentacion alimentacion = alimentacionService.buscarAlimentacion(id);

                if (alimentacion == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"mensaje\":\"Registro no encontrado\"}");
                    return;
                }

                response.getWriter().write(this.gson.toJson(alimentacion));
                return;
            }

            // 🔥 LISTAR TODOS
            List<Alimentacion> lista = alimentacionService.obtenerAlimentaciones();
            response.getWriter().write(this.gson.toJson(lista));

        } catch (Exception e) {
            System.out.println("ERROR EN DOGET DE ALIMENTACION: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error en el servidor: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // 1. Leer JSON del frontend
            Alimentacion alimentacion = gson.fromJson(request.getReader(), Alimentacion.class);

            // 2. Validar campos
            String error = validarAlimentacion(alimentacion);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            // 3. Obtener sesión
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("usuarioSesion") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Sesión expirada. Por favor inicie sesión nuevamente.\"}");
                return;
            }

            // 4. Obtener usuario logueado
            Usuario usuarioLogueado
                    = (Usuario) session.getAttribute("usuarioSesion");

            // 5. USAR RELACIÓN CORRECTA (NO BUSCAR POR ID)
            Empleado empReal = usuarioLogueado.getEmpleado();

            if (empReal == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Usuario sin empleado asociado.\"}");
                return;
            }

            // 6. Asignar cuidador correcto
            alimentacion.setCuidador(empReal);

            // 7. Guardar
            alimentacionService.crearAlimentacion(alimentacion);

            // 8. Respuesta exitosa
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensaje\":\"Alimentación registrada correctamente.\"}");

        } catch (Throwable e) {
            System.out.println("=== ERROR REAL AL GUARDAR ALIMENTACION ===");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error en el servidor: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Alimentacion alimentacion = gson.fromJson(request.getReader(), Alimentacion.class);

            String error = validarAlimentacion(alimentacion);
            if (error != null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"" + error + "\"}");
                return;
            }

            // 🌟 RUTA DIRECTA E INFALIBLE PARA EL PUT
            if (alimentacion.getCuidador() != null && alimentacion.getCuidador().getId() != null) {
                int idEmpleado = alimentacion.getCuidador().getId();

                // Instanciamos el servicio con su ruta completa aquí también
                com.ues.edu.service.EmpleadosService servicioTemp = new com.ues.edu.service.EmpleadosService();
                Empleado empReal = servicioTemp.buscarEmpleado((int) idEmpleado);

                alimentacion.setCuidador(empReal);
            }

            alimentacionService.editarAlimentacion(alimentacion);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"mensaje\":\"Alimentación actualizada correctamente\"}");

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al actualizar: " + e.getMessage() + "\"}");
        }
    }

    // ==========================
    // DELETE
    // ==========================
    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(
                request.getParameter("id"));

        alimentacionService.eliminarAlimentacion(id);

        response.setContentType("application/json");

        response.getWriter().write(
                "{\"mensaje\":\"Alimentación eliminada\"}"
        );
    }

    // ==========================
    // VALIDAR
    // ==========================
    private String validarAlimentacion(Alimentacion a) {

        if (a == null) {
            return "Registro inválido";
        }

        if (a.getTipoAlimento() == null
                || a.getTipoAlimento().trim().length() < 3) {

            return "Tipo alimento mínimo 3 caracteres";
        }

        if (a.getHorario() == null
                || a.getHorario().trim().length() < 3) {

            return "Horario inválido";
        }

        if (a.getCantidad() <= 0) {
            return "Cantidad inválida";
        }

        if (a.getAnimal() == null) {
            return "Debe seleccionar un animal";
        }

        return null;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
