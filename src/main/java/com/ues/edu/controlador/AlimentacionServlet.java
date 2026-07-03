package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Alimentacion;
import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.Usuario;
import com.ues.edu.service.AlimentacionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AlimentacionServlet", urlPatterns = {"/AlimentacionServlet"})
public class AlimentacionServlet extends HttpServlet {

    private AlimentacionService alimentacionService = new AlimentacionService();

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
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

    // ==========================
    // GET
    // ==========================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String accion = request.getParameter("accion");

            // ================= SESIÓN =================
            if ("obtenerSesion".equals(accion)) {

                HttpSession session = request.getSession(false);

                Object logueado = null;
                if (session != null) {
                    logueado = session.getAttribute("usuarioLogueado");
                    if (logueado == null) logueado = session.getAttribute("empleadoLogueado");
                    if (logueado == null) logueado = session.getAttribute("usuario");
                    if (logueado == null) logueado = session.getAttribute("empleado");
                }

                if (logueado != null) {
                    response.getWriter().write(gson.toJson(logueado));
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"No hay sesión activa\"}");
                }
                return;
            }

            // ================= ID =================
            String idParam = request.getParameter("id");

            if (idParam != null && !idParam.isEmpty()) {

                int id = Integer.parseInt(idParam);
                Alimentacion a = alimentacionService.buscarAlimentacion(id);

                if (a == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"mensaje\":\"No encontrado\"}");
                    return;
                }

                response.getWriter().write(gson.toJson(a));
                return;
            }

            // ================= LISTAR =================
            List<Alimentacion> lista = alimentacionService.obtenerAlimentaciones();
            response.getWriter().write(gson.toJson(lista));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error servidor\"}");
        }
    }

    // ==========================
    // POST
    // ==========================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            Alimentacion a = gson.fromJson(request.getReader(), Alimentacion.class);

            HttpSession session = request.getSession(false);
            Usuario user = (session != null)
                    ? (Usuario) session.getAttribute("usuarioSesion")
                    : null;

            if (user == null || user.getEmpleado() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Sin sesión\"}");
                return;
            }

            Empleado emp = new Empleado();
            emp.setId(user.getEmpleado().getId());
            a.setCuidador(emp);

            alimentacionService.crearAlimentacion(a);

            response.getWriter().write("{\"mensaje\":\"Registrado correctamente\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error al guardar\"}");
        }
    }

    // ==========================
    // PUT
    // ==========================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");

        try {
            Alimentacion a = gson.fromJson(request.getReader(), Alimentacion.class);
            alimentacionService.editarAlimentacion(a);

            response.getWriter().write("{\"mensaje\":\"Actualizado\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error actualizar\"}");
        }
    }

    // ==========================
    // DELETE
    // ==========================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        alimentacionService.eliminarAlimentacion(id);

        response.getWriter().write("{\"mensaje\":\"Eliminado\"}");
    }
}