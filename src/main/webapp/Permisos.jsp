<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ues.edu.entidades.OpcionMenu" %>
<%@ page import="com.ues.edu.entidades.Rol" %>
<%@ page import="com.ues.edu.entidades.Usuario" %>
<%@ page import="com.ues.edu.service.PermisosService" %>
<%@ page import="com.ues.edu.service.RolService" %>

<%
    // ESCUDO DE SEGURIDAD EN EL SERVIDOR:
    //es para evitar que intrusos adivinen la URL escribiéndola en el navegador.
    Usuario usr = (Usuario) session.getAttribute("usuarioSesion");
    if (usr == null || usr.getRol() == null || usr.getRol().getId() != 1) {
        response.sendRedirect("login.jsp");
        return; 
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WILD ZOO MK - Asignación de Permisos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/tabler-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
    <div class="contenedor">
        <div class="header">
            <h1>WILD ZOO MK</h1>
            <p>Configuración de Accesos por Rol</p>
        </div>

       <div class="contenido">
            <form id="formPermisos">
                
                <div class="formulario">
                    <div class="campo">
                        <label for="idRol" style="font-weight: 700; color: #3f5b4b;">Seleccione el Rol a Configurar</label>
                        <select id="idRol" required onchange="cargarPermisosDelRol()">
                            <option value="">Seleccione un rol</option>
                            <%
                                try {
                                    com.ues.edu.service.RolService rolService = new com.ues.edu.service.RolService();
                                    List<com.ues.edu.entidades.Rol> roles = rolService.obtenerRoles();
                                    if (roles != null) {
                                        for (com.ues.edu.entidades.Rol r : roles) {
                            %>
                                <option value="<%= r.getId() %>"><%= r.getNombreRol() %></option>
                            <%
                                        }
                                    }
                                } catch (Exception ex) {
                                    out.println("<option value=''>Error al cargar roles</option>");
                                }
                            %>
                        </select>
                    </div>
                </div>

                <div class="tabla-seccion" style="margin-top: 30px; width: 100%;">
                    
                    <h3 style="color: #3f5b4b; font-size: 16px; font-weight: 700; margin-bottom: 15px; border-bottom: 2px solid #3f5b4b; padding-bottom: 5px;">
                        Opciones del Menú Disponibles
                    </h3>

                    <table id="tablaPermisos" style="width: 100%; border-collapse: collapse; margin-top: 10px;">
                        <thead>
                            <tr>
                                <th style="width: 20%; text-align: center;">Acceso</th>
                                <th style="text-align: left;">Nombre de la Opción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                try {
                                    com.ues.edu.service.PermisosService service = new com.ues.edu.service.PermisosService();
                                    List<OpcionMenu> opciones = service.listarTodasLasOpciones();
                                    for (OpcionMenu op : opciones) {
                            %>
                                <tr>
                                    <td style="text-align: center; vertical-align: middle; padding: 12px;">
                                        <input type="checkbox" name="opcionesMenu" value="<%= op.getId() %>" id="opc_<%= op.getId() %>" style="width: 19px; height: 19px; cursor: pointer;">
                                    </td>
                                    <td style="text-align: left; vertical-align: middle; padding: 12px;">
                                        <label for="opc_<%= op.getId() %>" style="cursor: pointer; display: block; width: 100%; margin: 0; font-weight: 500; color: #333;">
                                            <%= op.getNombre() %>
                                        </label>
                                    </td>
                                </tr>
                            <%
                                    }
                                } catch (Exception e) {
                                    out.println("<tr><td colspan='2' style='color:red; text-align:center;'>Error al cargar las opciones del menú</td></tr>");
                                }
                            %>
                        </tbody>
                    </table>
                </div>

                <div class="botones" style="margin-top: 25px; display: flex; gap: 10px;">
                    <button type="submit" class="guardar">Guardar Asignación</button>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back" style="display: inline-flex; align-items: center; justify-content-center; padding: 10px; border-radius: 8px; background: #6c757d; color: white; text-decoration: none;">
                        <i class="ti ti-arrow-left"></i>
                    </a>
                </div>

            </form>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/Permisos.js"></script>
</body>
</html>