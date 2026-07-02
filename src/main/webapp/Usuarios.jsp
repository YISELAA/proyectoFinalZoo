<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ues.edu.entidades.Empleado" %>
<%@ page import="com.ues.edu.service.EmpleadosService" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WILD ZOO MK - Gestión de Usuarios</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    </head>

    <body>

        <div class="contenedor">

            <div class="header">
                <h1>WILD ZOO MK</h1>
                <p>Gestión de Usuarios</p>
            </div>

            <div class="contenido">

                <form id="formUsuario">

                    <input type="hidden" id="idUsuario">

                    <div class="formulario">

                        <div class="campo">
                            <label for="nombreUsuario">Nombre Usuario</label>
                            <input type="text" id="nombreUsuario" placeholder="Ingrese usuario" required>
                        </div>

                        <div class="campo">
                            <label for="contrasena">Contraseña</label>
                            <div class="password-container">
                                <input type="password" id="contrasena" placeholder="Ingrese contraseña">
                            </div>  
                        </div>

                        <div class="campo">
                            <label for="idEmpleado">Empleado</label>
                            <select id="idEmpleado" required>
                                <option value="">Seleccione empleado</option>
                                <%
                                    try {
                                        EmpleadosService empleadoService = new EmpleadosService();
                                        List<Empleado> empleados = empleadoService.obtenerEmpleados();
                                        if (empleados != null) {
                                            for (Empleado e : empleados) {
                                %>
                                <option value="<%= e.getId()%>"><%= e.getNombre()%> <%= e.getApellido()%></option>                                <%
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Evita que un error de BD rompa toda la página visualmente
                                        out.println("<option value=''>Error al cargar empleados</option>");
                                    }
                                %>
                            </select>
                        </div>

                        <div class="campo">
                            <label for="idRol">Rol</label>
                            <select id="idRol" required>
                                <option value="">Seleccione rol</option>
                                <%
                                    try {
                                        com.ues.edu.service.RolService rolService = new com.ues.edu.service.RolService();
                                        List<com.ues.edu.entidades.Rol> roles = rolService.obtenerRoles();
                                        if (roles != null) {
                                            for (com.ues.edu.entidades.Rol r : roles) {
                                %>
                                <option value="<%= r.getId()%>"><%= r.getNombreRol()%></option>
                                <%
                                            }
                                        }
                                    } catch (Exception ex) {
                                        out.println("<option value=''>Error al cargar roles</option>");
                                    }
                                %>
                            </select>
                        </div>

                        <div class="botones">
                            <button type="submit" id="btnGuardar" class="guardar">
                                Guardar Usuario
                            </button>

                            <button type="button" class="cancelar" onclick="limpiarFormulario()">
                                Cancelar
                            </button>
                        </div>

                    </div>

                </form>

                <div id="mensajeError"></div>

                <table id="tablaUsuarios">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Usuario</th>
                            <th>Contraseña</th>
                            <th>Empleado</th>
                            <th>Rol</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>

                <div id="paginacion" class="paginacion"></div>                


                <div style="margin-top: 24px; text-align: right;">
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back">
                        <i class="ti ti-arrow-left"></i>
                    </a>
                </div>

            </div>

        </div>

        <script src="${pageContext.request.contextPath}/js/usuarios.js"></script>

    </body>
</html>