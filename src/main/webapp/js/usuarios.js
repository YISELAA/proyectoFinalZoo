/* * Usuarios.js - Gestión de Usuarios con Paginación y Paleta Wild Zoo MK */

console.log("JS USUARIOS CARGADO");

let usuarios = [];
let paginaActual = 1;
const size = 5;

// ==========================================
// INICIO Y LOGICA DEL FORMULARIO
// ==========================================
document.addEventListener("DOMContentLoaded", function () {

    buscarUsuarios();

    document.getElementById("formUsuario").addEventListener("submit", function (event) {
        event.preventDefault();

        let id = document.getElementById("idUsuario").value;
        let idRolVal = parseInt(document.getElementById("idRol").value);

        let usuario = {
            nombreUsuario: document.getElementById("nombreUsuario").value.trim(),
            contrasena: document.getElementById("contrasena").value,
            empleado: { id: parseInt(document.getElementById("idEmpleado").value) },
            rol: { id: idRolVal }
        };

        if (id) {
            usuario.id = parseInt(id);
        }

        let metodo = id ? "PUT" : "POST";

        fetch("/ProyectoFinalZoo/UssuarioServlet", {
            method: metodo,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(usuario)
        })
        .then(async response => {
            const text = await response.text();
            let data;
            try {
                data = JSON.parse(text);
            } catch (e) {
                throw new Error("Error interno del servidor");
            }

            if (!response.ok) {
                throw new Error(data.error || "Ocurrió un error en la operación");
            }
            return data;
        })
        .then(data => {
            let msgError = document.getElementById("mensajeError");
            if (msgError) msgError.innerHTML = "";
            
            limpiarFormulario();
            
            if (!id) {
                paginaActual = 1;
            }

            buscarUsuarios();

            Swal.fire({
                icon: "success",
                title: id ? "Actualizado" : "Agregado",
                text: data.mensaje || "Operación realizada con éxito",
                confirmButtonColor: "#3f5b4b"
            });
        })
        .catch(error => {
            Swal.fire({
                icon: "warning",
                title: "No se puede guardar",
                text: error.message,
                confirmButtonColor: "#b05d4d"
            });
            console.error(error);
        });
    }); // 👈 Aquí se cierra correctamente el EventListener del submit
}); // 👈 Aquí se cierra correctamente el DOMContentLoaded

// ==========================================
// FUNCIONES DE CONTROL (GLOBALES)
// ==========================================
function buscarUsuarios() {
    fetch("UssuarioServlet")
        .then(res => res.json())
        .then(data => {
            usuarios = data;
            mostrarUsuarios();
            renderPaginacion();
        })
        .catch(error => console.error("Error al cargar usuarios:", error));
}

function mostrarUsuarios() {
    if (!Array.isArray(usuarios)) {
        console.error("Respuesta inválida:", usuarios);
        return;
    }

    let html = "";
    let inicio = (paginaActual - 1) * size;
    let fin = inicio + size;
    let usuariosPagina = usuarios.slice(inicio, fin);

    if (usuariosPagina.length === 0) {
        html = "<tr><td colspan='6' style='text-align:center;'>No se encontraron usuarios registrados</td></tr>";
        document.querySelector("#tablaUsuarios tbody").innerHTML = html;
        return;
    }

    usuariosPagina.forEach(function (u) {
        var idUsr = u.id || "-";
        var nombreUsr = u.nombreUsuario || "-";
        var nombreEmp = (u.empleado && u.empleado.nombre && u.empleado.apellido)
            ? u.empleado.nombre + " " + u.empleado.apellido
            : "No asignado";
        var nombreRol = (u.rol && u.rol.nombreRol) ? u.rol.nombreRol : "Sin rol";

        html += "<tr>" +
            "<td>" + idUsr + "</td>" +
            "<td>" + nombreUsr + "</td>" +
            "<td>***</td>" +
            "<td>" + nombreEmp + "</td>" +
            "<td>" + nombreRol + "</td>" +
            "<td>" +
            "<button class='btnEditar' onclick='editar(" + idUsr + ")'><i class='ti ti-edit'></i></button> " +
            "<button class='btnEliminar' onclick='deshabilitarUsuario(" + idUsr + ")'> <i class='ti ti-trash'></i></button>" +
            "</td>" +
            "</tr>";
    });
    
    document.querySelector("#tablaUsuarios tbody").innerHTML = html;
}

function editar(id) {
    fetch("UssuarioServlet?id=" + id)
        .then(function (response) {
            return response.json();
        })
        .then(function (usuario) {
            document.getElementById("idUsuario").value = usuario.id;
            document.getElementById("nombreUsuario").value = usuario.nombreUsuario;
            document.getElementById("contrasena").value = "";
            document.getElementById("contrasena").placeholder = "Dejar vacío para conservar contraseña actual";
            
            if (usuario.empleado) {
                document.getElementById("idEmpleado").value = usuario.empleado.id;
            }

            if (usuario.rol) {
                document.getElementById("idRol").value = usuario.rol.id;
            }

            document.getElementById("btnGuardar").textContent = "Actualizar Usuario";
        })
        .catch(function (error) {
            console.error(error);
        });
}

function deshabilitarUsuario(id) {
    Swal.fire({
        title: "¿Eliminar usuario?",
        text: "Esta acción no se puede deshacer",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            fetch("/ProyectoFinalZoo/UssuarioServlet?id=" + id, { method: "DELETE" })
                .then(function (response) {
                    if (!response.ok)
                        throw new Error("No se pudo eliminar el usuario");
                    return response.text();
                })
                .then(function (data) {
                    Swal.fire({
                        icon: "success",
                        title: "Eliminado",
                        text: "El usuario ha sido eliminado correctamente",
                        confirmButtonColor: "#3f5b4b"
                    });
                    buscarUsuarios();
                })
                .catch(function (error) {
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: error.message,
                        confirmButtonColor: "#b05d4d"
                    });
                });
        }
    });
}

function renderPaginacion() {
    var pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor) return;
    
    let totalPaginas = Math.ceil(usuarios.length / size) || 1;
    pagContenedor.innerHTML =
        "<button onclick='anterior()'><i class='ti ti-chevron-left'></i></button>" +
        " Página " + paginaActual + " de " + totalPaginas + " " +
        "<button onclick='siguiente()'><i class='ti ti-chevron-right'></i></button>";
}

function siguiente() {
    var totalPaginas = Math.ceil(usuarios.length / size);
    if (paginaActual < totalPaginas) {
        paginaActual++;
        mostrarUsuarios();
        renderPaginacion();
    }
}

function anterior() {
    if (paginaActual > 1) {
        paginaActual--;
        mostrarUsuarios();
        renderPaginacion();
    }
}

function limpiarFormulario() {
    document.getElementById("formUsuario").reset();
    document.getElementById("idUsuario").value = "";
    let msgError = document.getElementById("mensajeError");
    if (msgError) msgError.innerHTML = "";
    document.getElementById("btnGuardar").textContent = "Guardar Usuario";
    document.getElementById("contrasena").placeholder = "Ingrese contraseña";
    document.getElementById("idRol").value = "";
}