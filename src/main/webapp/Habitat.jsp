<%-- 
    Document   : Habitat
    Created on : 16 may. 2026, 21:31:56
    Author     : coc44
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>WILD ZOO MK - Gestión de Hábitats</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>

<body>

    <div class="contenedor">

        <div class="header">
            <h1>WILD ZOO MK</h1>
            <p>Gestión de Hábitats</p>
        </div>

        <div class="contenido">

            <form id="formHabitat">

                <input type="hidden" id="idHabitat">

                <div class="formulario">

                    <div class="campo">
                        <label for="tipoTerreno">Tipo de Terreno</label>
                        <input type="text"
                               id="tipoTerreno"
                               placeholder="Ingrese tipo de terreno"
                               required>
                    </div>

                    <div class="campo">
                        <label for="capacidad">Capacidad</label>
                        <input type="number"
                               id="capacidad"
                               placeholder="Ingrese capacidad"
                               required>
                    </div>

                    <div class="botones">

                        <button type="submit"
                                 id="btnGuardar"
                                 class="guardar">
                            Guardar Hábitat
                        </button>

                        <button type="button"
                                class="cancelar"
                                onclick="limpiarFormularioHabitat()">
                            Cancelar
                        </button>
                        
                        <button type="button" 
                                id="btnVolverHabitat" 
                                class="cancelar" 
                                style="display: none;" 
                                onclick="limpiarFormularioHabitat()">
                            Volver / Cancelar Edición
                        </button>

                    </div>

                </div>

            </form>

            <div id="mensajeError"></div>

            <table id="tablaHabitats">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tipo Terreno</th>
                        <th>Capacidad</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="tbodyHabitats">
                </tbody>

            </table>

            <div id="paginacionHabitats" class="paginacion"></div>

            <div style="margin-top: 24px; text-align: right;">
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn-back">
                    <i class="ti ti-arrow-left"></i>
                </a>
            </div>

        </div>

    </div>

    <script src="${pageContext.request.contextPath}/js/Habitat.js"></script>

</body>

</html>