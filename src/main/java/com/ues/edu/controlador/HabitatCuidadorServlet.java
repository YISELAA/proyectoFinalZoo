/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.ues.edu.controlador;

import com.google.gson.Gson;
import com.ues.edu.entidades.Empleado;
import com.ues.edu.entidades.Habitat;
import com.ues.edu.service.HabitatCuidadorService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author coc44
 */
@WebServlet(name = "HabitatCuidadorServlet", urlPatterns = {"/HabitatCuidadorServlet"})
public class HabitatCuidadorServlet extends HttpServlet {


    private HabitatCuidadorService service = new HabitatCuidadorService();
    private Gson gson = new Gson();

    // === BORRA EL VIEJO Y PEGA ESTE AQUÍ ===
    private Map<String, Object> transformarAMap(Habitat habitat) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", habitat.getId());
        map.put("capacidad", habitat.getCapacidad());
        map.put("tipo_terreno", habitat.getTipoTerreno()); 

        List<Map<String, Object>> cuidadoresJSON = new ArrayList<>();
        if (habitat.getCuidadores() != null) { 
            for (Empleado emp : habitat.getCuidadores()) {
                Map<String, Object> empMap = new HashMap<>();
                empMap.put("id", emp.getId());
                empMap.put("nombre", emp.getNombre()); 
                empMap.put("apellido", emp.getApellido());
//                empMap.put("rol", emp.getRol());
                cuidadoresJSON.add(empMap);
            }
        }
        map.put("cuidadores", cuidadoresJSON);
        return map;
    }

    // Esto ya lo tienes abajo, déjalo tal cual:
    private static class HabitatCuidadorDTO {
        int idHabitat;
        List<Long> idsEmpleados;
    }

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
            out.println("<title>Servlet HabitatCuidadorServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HabitatCuidadorServlet at " + request.getContextPath() + "</h1>");
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
          response.setContentType("application/json;charset=UTF-8");
        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.isEmpty()) {
                int id = Integer.parseInt(idParam);
                Habitat habitat = service.buscarAsignacion(id);
                if (habitat == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"No se encontró el hábitat\"}");
                    return;
                }
                response.getWriter().write(gson.toJson(transformarAMap(habitat)));
                return;
            }

            // === ESTA ES LA PARTE QUE MODIFICAMOS ===
            List<Habitat> lista = service.obtenerAsignaciones();
            List<Map<String, Object>> jsonListo = new ArrayList<>();
            for (Habitat h : lista) {
                jsonListo.add(transformarAMap(h)); // <--- Se quitó el (Map<String, Object>) problemático
            }
            response.getWriter().write(gson.toJson(jsonListo));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error en servidor: " + e.getMessage() + "\"}");
            e.printStackTrace();
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
     

     response.setContentType("application/json;charset=UTF-8");
        try {
            HabitatCuidadorDTO dto = gson.fromJson(request.getReader(), HabitatCuidadorDTO.class);
            service.registrarAsignacion(dto.idHabitat, dto.idsEmpleados);
            response.getWriter().write("{\"mensaje\":\"Asignación guardada\"}");
        } catch(Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\""+e.getMessage()+"\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            HabitatCuidadorDTO dto = gson.fromJson(request.getReader(), HabitatCuidadorDTO.class);
            service.modificarAsignacion(dto.idHabitat, dto.idsEmpleados);
            response.getWriter().write("{\"mensaje\":\"Asignación actualizada\"}");
        } catch(Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\""+e.getMessage()+"\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        service.removerAsignacion(id);
        response.getWriter().write("{\"mensaje\":\"Asignación eliminada\"}");
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
