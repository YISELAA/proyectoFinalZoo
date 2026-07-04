<%@page import="java.util.List"%>
<%@page import="com.ues.edu.entidades.Ticket"%>
<%@page import="com.ues.edu.service.TicketService"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>WILD ZOO MK - Gestión de detalle de visitas</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ticket.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>


    </head>

    <body>
        <div class="contenedor">

            <div class="header">
                <h1>WILD ZOO MK</h1>
                <p>Gestión de Detalle de Visitas</p>
            </div>

            <div class="contenido">

                <form id="formDetalleVisita">

                    <input type="hidden" id="idDetalleVisita">

                    <div class="formulario">
                        <div class="campo">
                            <label>Nombre Visitante</label>
                            <input type="text" id="nombreVisitante" required>
                        </div>

                        <div class="campo">
                            <label>Teléfono</label>
                            <input type="number" id="telefono" required>
                        </div>

                        <div class="campo">
                            <label>Total</label>
                            <input type="text" id="totalDisplay" readonly>
                        </div>
                    </div>

                    <div class="tickets-section">

                        <div class="tickets-section-title">Agregar tickets</div>

                        <div class="tickets-row">

                            <div class="campo">
                                <label>Tipo de Ticket</label>
                                <select id="idTicket">
                                    <option value="">Seleccione ticket</option>
                                    <%
                                        TicketService service = new TicketService();
                                        List<Ticket> tickets = service.obtenerTickets();
                                        for (Ticket t : tickets) {
                                            if ("Activo".equals(t.getEstado())) {
                                    %>
                                    <option value="<%= t.getTipo()%>"
                                            data-precio="<%= t.getPrecio()%>">
                                        <%= t.getTipo()%> - $<%= t.getPrecio()%>
                                    </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="campo">
                                <label>Cantidad</label>
                                <input type="number" id="cantidad" min="1">
                            </div>

                            <button type="button" class="btn-agregar" id="btnAgregar">
                                + Agregar
                            </button>

                        </div>

                        <div id="errorTicket" class="error-ticket">
                            Seleccione ticket y cantidad válida
                        </div>

                        <div class="ticket-lista" id="ticketLista"></div>

                    </div>

                    <!-- ✅ ESTE DIV ES EL QUE FALTABA -->
                    <div id="mensajeError"></div>

                    <div class="botones">
                        <button type="submit" class="guardar">Guardar</button>
                        <button type="button" class="cancelar" id="btnCancelar">Cancelar</button>
                    </div>

                </form>

                <table id="tablaDetalleVisita">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Teléfono</th>
                            <th>Fecha</th>
                            <th>Tickets</th>
                            <th>Total</th>
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

        <script src="${pageContext.request.contextPath}/js/detalleVisita.js"></script>
    </body>
</html>