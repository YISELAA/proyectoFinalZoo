
=======
<%-- 
    Document   : Animal
    Created on : 16 may. 2026, 23:24:02
    Author     : coc44
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
            <p>Gestión de Animales</p>
        </div>

        <div class="contenido">

            <form id="formAnimal">

                <input type="hidden" id="idAnimal">

                <div class="formulario">

                    <div class="campo">
                        <label for="nombreAnimal">Nombre</label>
                        <input type="text"
                               id="nombreAnimal"
                               placeholder="Ingrese nombre"
                               required>
                    </div>

                    <div class="campo">
                        <label for="especie">Especie</label>
                        <input type="text"
                               id="especie"
                               placeholder="Ingrese especie"
                               required>
                    </div>

                    <div class="campo">
                        <label for="fechaNacimiento">Fecha Nacimiento</label>
                        <input type="date"
                               id="fechaNacimiento"
                               required>
                    </div>

                    <div class="campo">
                        <label for="fechaIngreso">Fecha Ingreso</label>
                        <input type="date"
                               id="fechaIngreso">
                    </div>

                    <div class="campo">
                        <label for="habitat">Habitat</label>
                        <select id="habitat" required>
                            <option value="">Seleccione habitat</option>
                        </select>
                    </div>

                    <div class="botones">

                        <button type="submit"
                                id="btnGuardar"
                                class="guardar">
                            Guardar Animal
                        </button>

                        <button type="button"
                                class="cancelar"
                                onclick="limpiarFormularioAnimal()">
                            Cancelar
                        </button>

                    </div>

                </div>

            </form>

            <div id="mensajeError"></div>

            <table id="tablaAnimales">

                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Especie</th>
                        <th>Fecha Nacimiento</th>
                        <th>Edad</th>
                        <th>Fecha Ingreso</th>
                        <th>Habitat</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody id="tbodyAnimales">
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

    <script src="${pageContext.request.contextPath}/js/Animal.js"></script>

</body>
</html>