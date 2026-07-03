<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Panel Principal - Zoológico</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/tabler-icons.min.css">
        <link rel="stylesheet" href="css/index.css">
    </head>

    <body>

        <div class="zoo-header">
            <div>
                <p class="zoo-title">Wild Zoo MK</p>
                <p class="zoo-subtitle">Sistema de gestión del zoológico</p>
            </div>

            <div style="display:flex;align-items:center;gap:15px;">
                <div class="usuario-info">
                    <i class="ti ti-user-circle"></i>
                    <span id="usuarioActivo">Bienvenido, ${usuarioSesion.nombreUsuario}</span>
                </div>

                <a href="LoginServlet?accion=logout" class="btn-logout">
                    <i class="ti ti-logout"></i> Cerrar sesión
                </a>
            </div>
        </div>

        <div class="container mt-4">
            <div class="row g-4">

                <div class="col-md-4 d-flex">
                    <div class="zoo-card w-100">
                        <div class="card-icon-wrap iw-consultas">
                            <i class="ti ti-search" style="font-size:20px;"></i>
                        </div>
                        <p class="card-label">Consultas</p>
                        <p class="card-desc">Visualiza información del sistema</p>
                        <div class="divider div-consultas"></div>

                        <c:choose>
                            <c:when test="${usuarioSesion.rol.id == 1}">
                                <a href="ConsultaAnimalCategoria.jsp" class="btn-action-main btn-consultas"><i class="ti ti-category"></i> Ver animales</a>
                                <a href="ConsultaCuidadorAnimal.jsp" class="btn-ghost gh-consultas"><i class="ti ti-user"></i> Ver cuidador</a>
                                <a href="ConsultaMostrarAlimentos.jsp" class="btn-ghost gh-consultas"><i class="ti ti-salad"></i> Ver alimentos</a>
                                <a href="ConsultaHistorialMedico.jsp" class="btn-ghost gh-consultas"><i class="ti ti-file-text"></i> Ver Historial médico</a>
                                <a href="ConsultaMostrarEmpleados.jsp" class="btn-ghost gh-consultas"><i class="ti ti-user-search"></i> Ver Empleados</a>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="opcion" items="${usuarioSesion.rol.opcionesMenu}" varStatus="status">
                                    <c:if test="${fn:containsIgnoreCase(opcion.url, 'consulta') || fn:containsIgnoreCase(opcion.url, 'mostrar')}">
                                        <a href="${opcion.url}" class="${status.first ? 'btn-action-main btn-consultas' : 'btn-ghost gh-consultas'}">
                                            <i class="ti ${opcion.icono}"></i> ${opcion.nombre}
                                        </a>
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-md-4 d-flex">
                    <div class="zoo-card w-100">
                        <div class="card-icon-wrap iw-crud">
                            <i class="ti ti-settings" style="font-size:20px;"></i>
                        </div>
                        <p class="card-label">CRUD</p>
                        <p class="card-desc">Gestiona registros del sistema</p>
                        <div class="divider div-crud"></div>

                        <c:choose>
                            <c:when test="${usuarioSesion.rol.id == 1}">
                                <a href="Empleado.jsp" class="btn-action-main btn-crud"><i class="ti ti-briefcase"></i> Empleados</a>
                                <a href="Usuarios.jsp" class="btn-ghost gh-crud"><i class="ti ti-users"></i> Usuarios</a>
                                <a href="ticket.jsp" class="btn-ghost gh-crud"><i class="ti ti-ticket"></i> Tickets</a>
                                <a href="detalle_visita.jsp" class="btn-ghost gh-crud"><i class="ti ti-list-details"></i> Visitas</a>
                                <a href="Animal.jsp" class="btn-ghost gh-crud"><i class="ti ti-paw"></i> Animal</a>
                                <a href="Habitat.jsp" class="btn-ghost gh-crud"><i class="ti ti-home-eco"></i> Hábitat</a>
                                <a href="Alimentacion.jsp" class="btn-ghost gh-crud"><i class="ti ti-salad"></i> Alimentación</a>
                                <a href="HistorialMedico.jsp" class="btn-ghost gh-crud"><i class="ti ti-stethoscope"></i> Veterinario</a>
                                <a href="HabitatCuidador.jsp" class="btn-ghost gh-crud"><i class="ti ti-user-heart"></i> Asignar cuidador</a>
                            </c:when>
                            <c:otherwise>
                                <c:set var="isFirstCrud" value="true" />
                                <c:forEach var="opcion" items="${usuarioSesion.rol.opcionesMenu}">
                                    <c:if test="${!fn:containsIgnoreCase(opcion.url, 'consulta') 
                                                  && !fn:containsIgnoreCase(opcion.url, 'mostrar') 
                                                  && !fn:containsIgnoreCase(opcion.url, 'reporte') 
                                                  && !fn:containsIgnoreCase(opcion.url, 'permisos')
                                                  && !fn:containsIgnoreCase(opcion.url, 'index.jsp')}">
                                        <a href="${opcion.url}" class="${isFirstCrud ? 'btn-action-main btn-crud' : 'btn-ghost gh-crud'}">
                                            <i class="ti ${opcion.icono}"></i> ${opcion.nombre}
                                        </a>
                                        <c:set var="isFirstCrud" value="false" />
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-md-4 d-flex">
                    <div class="zoo-card w-100">
                        <div class="card-icon-wrap iw-reportes">
                            <i class="ti ti-chart-bar" style="font-size:20px;"></i>
                        </div>
                        <p class="card-label">Reportes</p>
                        <p class="card-desc">Estadísticas del zoológico</p>
                        <div class="divider div-reportes"></div>

                        <c:choose>
                            <c:when test="${usuarioSesion.rol.id == 1}">
                                <a href="ReporteTicket.jsp" target="_blank" class="btn-action-main btn-reportes"><i class="ti ti-ticket"></i> Ticket más vendido</a>
                                <a href="ReporteAnimalesHabitat.jsp" target="_blank" class="btn-ghost gh-reportes"><i class="ti ti-home-eco"></i> Animales por hábitat</a>
                                <a href="ReporteAtencionesVeterinario.jsp" target="_blank" class="btn-ghost gh-reportes"><i class="ti ti-stethoscope"></i> Atención por veterinario</a>
                                <a href="ReporteVentasEmpleado.jsp" target="_blank" class="btn-ghost gh-reportes"><i class="ti ti-trophy"></i> Más ventas</a>
                                <a href="ReporteVisitasFecha.jsp" target="_blank" class="btn-ghost gh-reportes"><i class="ti ti-calendar-event"></i> Fechas más visitadas</a>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="opcion" items="${usuarioSesion.rol.opcionesMenu}" varStatus="status">
                                    <c:if test="${fn:containsIgnoreCase(opcion.url, 'reporte')}">
                                        <a href="${opcion.url}" target="_blank" class="${status.first ? 'btn-action-main btn-reportes' : 'btn-ghost gh-reportes'}">
                                            <i class="ti ${opcion.icono}"></i> ${opcion.nombre}
                                        </a>
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div> <%-- 🌟 SECCIÓN INFERIOR: Solo para el Administrador (Rol ID 1) --%>
            <c:if test="${usuarioSesion.rol.id == 1}">
                <div class="row mt-4 mb-4 justify-content-center">
                    <div class="col-md-4">
                        <div class="zoo-card text-center" style="padding: 20px;">
                            <div class="card-icon-wrap mx-auto mb-2" style="background: #f1f5f9; color: #3f5b4b; width: 45px; height: 45px;">
                                <i class="ti ti-shield-lock" style="font-size:18px;"></i>
                            </div>
                            <p class="card-label" style="font-size: 16px; margin-bottom: 2px;">Seguridad del Sistema</p>
                            <p class="card-desc" style="font-size: 13px; margin-bottom: 12px;">Configuración de accesos y roles</p>
                            
                            <a href="Permisos.jsp" class="btn-action-main btn-crud w-100 m-0">
                                <i class="ti ti-settings"></i> Administrar Permisos
                            </a>
                        </div>
                    </div>
                </div>
            </c:if>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </body>
</html>