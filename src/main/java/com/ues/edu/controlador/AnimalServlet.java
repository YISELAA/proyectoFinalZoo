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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
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
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno en el servidor: " + e.getMessage() + "\"}");
        }
    }

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
        response.getWriter().write("{\"mensaje\":\"Animal guardado exitosamente\"}");
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
        response.getWriter().write("{\"mensaje\":\"Animal actualizado exitosamente\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            animalService.eliminarAnimal(id);
            response.setContentType("application/json");
            response.getWriter().write("{\"mensaje\":\"Animal eliminado correctamente\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"No se pudo eliminar el animal: " + e.getMessage() + "\"}");
        }
    }
    
    private String validarAnimal(Animal a) {
        if (a == null) {
            return "Animal inválido";
        }

        if (a.getNombre() == null || a.getNombre().trim().length() < 3) {
            return "El nombre debe tener mínimo 3 caracteres";
        }
        if (!a.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return "El nombre solo debe contener letras";
        }

        if (a.getEspecie() == null || a.getEspecie().trim().length() < 3) {
            return "La especie es requerida (mínimo 3 caracteres)";
        }
        if (!a.getEspecie().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return "La especie solo debe contener letras";
        }

        if (a.getSexo() == null || a.getSexo().trim().isEmpty()) {
            return "El sexo del animal es requerido";
        }
        if (!a.getSexo().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return "El sexo solo debe contener letras";
        }

        if (a.getFechaNacimiento() == null) {
            return "Fecha de nacimiento requerida";
        }

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.sql.Date fechaHoy = java.sql.Date.valueOf(hoy);
        
        if (a.getFechaNacimiento().after(fechaHoy)) {
            return "La fecha de nacimiento no puede ser del futuro";
        }
        
        if (a.getFechaIngreso() != null && a.getFechaIngreso().after(fechaHoy)) {
            return "La fecha de ingreso no puede ser del futuro";
        }

        if (a.getHabitat() == null || a.getHabitat().getId() == null) {
            return "Debe asignar un hábitat válido";
        }

        return null;
    }
}