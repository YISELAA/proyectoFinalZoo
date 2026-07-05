<%-- 
    Document   : ticket
    Created on : 22 may 2026, 22:14:35
    Author     : MINED
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>WILD ZOO MK - Gestión de Tickets</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    </head>

    <body>

        <div class="contenedor">

            <div class="header">
                <h1>WILD ZOO MK</h1>
                <p>Gestión de Tickets</p>
            </div>

            <div class="contenido">

                <form id="formTicket">

                    <input type="hidden" id="idTicket">

                    <div class="formulario">

                        <div class="campo">
                            <label>Tipo Ticket</label>

                            <input type="text"
                                   id="tipoTicket"
                                   placeholder="Ej: Adulto"
                                   required>
                        </div>

                        <div class="campo">
                            <label>Precio</label>

                            <input type="number"
                                   id="precio"
                                   placeholder="0.00"
                                   required>
                        </div>


                        <div class="botones">

                            <button type="submit" class="guardar">
                                Guardar
                            </button>

                            <button type="button"
                                    class="cancelar"
                                    onclick="limpiarFormulario()">
                                Cancelar
                            </button>

                        </div>

                    </div>

                </form>

                <table id="tablaTickets">

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tipo</th>
                            <th>Precio</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>

                    </tbody>

                </table>

                <div id="paginacion" class="paginacion"></div>    

                <div style="margin-top: 24px; text-align: right;">
                    <a href="index.jsp" class="btn-back">
                        <i class="ti ti-arrow-left"></i> 
                    </a>
                </div>

            </div>

        </div>

        <script src="${pageContext.request.contextPath}/js/Ticket.js"></script>
    </body>
</html>
