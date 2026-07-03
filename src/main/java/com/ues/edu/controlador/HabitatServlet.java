/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Habitat;
import com.ues.edu.service.HabitatService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author coc44
 */
@WebServlet(name = "HabitatServlet", urlPatterns = {"/HabitatServlet"})
public class HabitatServlet extends HttpServlet {
    
     private HabitatService habitatService = new HabitatService();

    private Gson gson = new GsonBuilder()
    .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {

        @Override
        public boolean shouldSkipField(com.google.gson.FieldAttributes f) {

           return f.getName().equals("listaAnimales")
                    || f.getName().equals("cuidadores");
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
            out.println("<title>Servlet CategoriaServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategoriaServlet at " + request.getContextPath() + "</h1>");
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

        if (idParam != null && !idParam.isEmpty()) {
            long id = Long.parseLong(idParam);
            Habitat habitat = habitatService.buscarHabitat(id);

            if (habitat == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"mensaje\":\"Habitat no encontrado\"}");
                return;
            }

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(habitat));
            return;
        }

        List<Habitat> habitats = habitatService.obtenerHabitats();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(habitats));
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
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

    response.setContentType("application/json");
    try {
        Habitat habitat = gson.fromJson(request.getReader(), Habitat.class);
        habitatService.crearHabitat(habitat);
        response.getWriter().write("{\"mensaje\":\"Hábitat guardado correctamente\"}");
    } catch (Exception e) {
        e.printStackTrace(); // ver en consola
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"error\":\"Error al guardar hábitat: " + e.getMessage() + "\"}");
    }
}


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Habitat habitat = gson.fromJson(request.getReader(), Habitat.class);

        String error = validarHabitat(habitat);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }

        habitatService.editarHabitat(habitat);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Habitat actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        long id = Long.parseLong(request.getParameter("id"));
        habitatService.eliminarHabitat((int) id);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Habitat eliminado\"}");
    }

    // VALIDACIÓN
    private String validarHabitat(Habitat h) {
        if (h == null) {
            return "Habitat inválido";
        }

        if (h.getTipoTerreno() == null || h.getTipoTerreno().trim().length() < 3) {
            return "Tipo de terreno mínimo 3 caracteres";
        }

        if (h.getCapacidad() == null || h.getCapacidad() <= 0) {
            return "Capacidad debe ser mayor a 0";
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