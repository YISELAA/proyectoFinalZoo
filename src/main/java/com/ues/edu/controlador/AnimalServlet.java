/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ues.edu.entidades.Animal;
import com.ues.edu.service.AnimalService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author coc44
 */
@WebServlet(name = "AnimalServlet", urlPatterns = {"/AnimalServlet"})
public class AnimalServlet extends HttpServlet {

    private AnimalService animalService = new AnimalService();

    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .addSerializationExclusionStrategy(new com.google.gson.ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(com.google.gson.FieldAttributes f) {
                    return f.getName().equals("cuidadores")
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
            out.println("<title>Servlet AnimalServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AnimalServlet at " + request.getContextPath() + "</h1>");
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
            String idParam = request.getParameter("id");

            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Animal animal = animalService.buscarAnimal(id);

                if (animal == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Animal no encontrado\"}");
                    return;
                }

                response.getWriter().write(gson.toJson(animal));
                return;
            }

            List<Animal> animales = animalService.obtenerAnimales();
            response.getWriter().write(gson.toJson(animales));

        } catch (Exception e) {
            // Imprime el error real en la consola de tu NetBeans/IDE
            e.printStackTrace();

            // Envía una respuesta JSON controlada en lugar de una página HTML rota
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno en el servidor: " + e.getMessage() + "\"}");
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

        Animal animal = gson.fromJson(request.getReader(), Animal.class);

        String error = validarAnimal(animal);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }

        animalService.crearAnimal(animal);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Animal guardado\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Animal animal = gson.fromJson(request.getReader(), Animal.class);

        String error = validarAnimal(animal);

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + error + "\"}");
            return;
        }

        animalService.editarAnimal(animal);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Animal actualizado\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        animalService.eliminarAnimal(id);

        response.setContentType("application/json");
        response.getWriter().write("{\"mensaje\":\"Animal eliminado\"}");
    }

   // 🔥 VALIDACIÓN FLEXIBILIZADA PARA COADYUVAR CON HÁBITATS ELIMINADOS
    private String validarAnimal(Animal a) {

        if (a == null) {
            return "Animal inválido";
        }

        if (a.getNombre() == null || a.getNombre().trim().length() < 3) {
            return "Nombre mínimo 3 caracteres";
        }

        if (a.getEspecie() == null || a.getEspecie().trim().length() < 3) {
            return "La especie es requerida";
        }

        if (a.getFechaNacimiento() == null) {
            return "Fecha de nacimiento requerida";
        }

        // 🌟 QUITAMOS LA OBLIGATORIEDAD ESTRICTA DEL HÁBITAT
        // Ahora, si viene un hábitat, validamos que su ID sea correcto; 
        // pero si viene null (animal libre), el Servlet lo dejará pasar sin errores.
        if (a.getHabitat() != null && a.getHabitat().getId() == null) {
            return "El formato del hábitat asignado no es válido";
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
