<%-- 
    Document   : Cuidador
    Created on : 31 may. 2026, 16:18:47
    Author     : coc44
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>WILD ZOO MK - Gestión de Hábitats y Cuidadores</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Crud.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.19.0/dist/tabler-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>

    <body>

        <div class="contenedor">

            <div class="header">
                <h1>WILD ZOO MK</h1>
                <p>Gestión de Hábitats y Cuidadores</p>
            </div>

            <div class="contenido">

                <form id="formCategoriaCuidador">

                    <input type="hidden" id="idAsignacionOculta">

                    <div class="formulario">

                        <div class="campo">
                            <label for="idHabitatSelect">Hábitat</label>
                            <select id="idHabitatSelect" required>
                                <option value="">
                                    Seleccione un hábitat
                                </option>
                            </select>
                        </div>

                        <div class="campo">
                            <label for="idEmpleadoSelect">Cuidador</label>

                            <select id="idEmpleadoSelect"
                                    name="cuidadores"
                                    multiple
                                    class="form-select"
                                    size="6">
                            </select>
                        </div>

                        <div class="botones">

                            <button type="submit"
                                    id="btnGuardar"
                                    class="guardar">
                                Guardar Asignación
                            </button>

                            <button type="button"
                                    class="cancelar"
                                    onclick="limpiarFormulario()">
                                Cancelar
                            </button>

                        </div>

                    </div>

                </form>

                <div id="mensajeError"></div>

                <table id="tablaCategoriaCuidador">

                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Hábitat</th>
                            <th>Cuidadores Responsables</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody id="tbodyCategoriaCuidador">
                    </tbody>

                </table>

                <div id="paginacion" class="paginacion"></div>

                <div style="margin-top: 24px; text-align: right;">
                    <a href="${pageContext.request.contextPath}/index.jsp"
                       class="btn-back">
                        <i class="ti ti-arrow-left"></i>
                    </a>
                </div>

            </div>

        </div>

        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />

        <script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>

        <script src="${pageContext.request.contextPath}/js/HabitatCuidador.js"></script>


    </body>

</html>