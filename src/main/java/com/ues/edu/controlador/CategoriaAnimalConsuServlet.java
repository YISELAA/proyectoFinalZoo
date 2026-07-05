package com.ues.edu.controlador;

import com.ues.edu.daos.ConsultaAnimalCategoriaDao;
import com.ues.edu.entidades.Animal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "CategoriaAnimalConsuServlet", urlPatterns = {"/CategoriaAnimalConsuServlet"})
public class CategoriaAnimalConsuServlet extends HttpServlet {

    ConsultaAnimalCategoriaDao dao = new ConsultaAnimalCategoriaDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String filtro = request.getParameter("filtro");

            List<Animal> animales = dao.buscarFiltro(filtro);

            JSONArray jsonArray = new JSONArray();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            
            if (animales != null) {

                for (Animal a : animales) {

                    JSONObject obj = new JSONObject();

                    obj.put("id", a.getId());
                    obj.put("nombre", a.getNombre());
                    obj.put("especie", a.getEspecie());
                    obj.put("sexo", a.getSexo());
                    obj.put("fecha_ingreso",
                            a.getFechaIngreso() != null
                            ? sdf.format(a.getFechaIngreso())
                            : "");

                    obj.put("fecha_nacimiento",
                            a.getFechaNacimiento() != null
                            ? sdf.format(a.getFechaNacimiento())
                            : "");

                    obj.put("edad", a.getEdad());

                    obj.put("tipoTerreno",
                            a.getHabitat() != null
                            ? a.getHabitat().getTipoTerreno()
                            : "");

                    obj.put("capacidad",
                            a.getHabitat() != null
                            ? a.getHabitat().getCapacidad()
                            : "");

                    jsonArray.put(obj);
                }
            }

            out.print(jsonArray);

        } catch (Exception e) {

            e.printStackTrace();

            JSONObject error = new JSONObject();
            error.put("error", e.toString());

            out.print(error);
        }

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "CategoriaAnimalConsuServlet";
    }
}