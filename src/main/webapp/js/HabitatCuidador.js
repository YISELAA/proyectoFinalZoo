/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

let paginaActual = 1;
const size = 5; // 🌟 Límite estricto de 5 registros por página
let datosCompletos = []; // Almacén global para segmentar las asignaciones de la BD

// Asegurar que todo cargue al iniciar la página
document.addEventListener("DOMContentLoaded", function () {
    cargarComponentes();
    listarAsignaciones();



    // Escuchar el evento del formulario para Guardar/Editar
    document.getElementById("formCategoriaCuidador").addEventListener("submit", guardarOEditar);

    $('#idEmpleadoSelect').select2({
        placeholder: "Seleccione cuidadores",
        width: '100%',
        closeOnSelect: false
    });
});

// ==========================================
// CARGAR SELECTORES DESDE LA BD
// ==========================================
function cargarComponentes() {
    fetch("EmpleadoServlet?tipo=cuidadores")
            .then(res => res.json())
            .then(empleados => {

                let html = "";

                empleados.forEach(emp => {
                    html += `<option value="${emp.id}">
                            ${emp.nombre} ${emp.apellido}
                         </option>`;
                });

                const select = document.getElementById("idEmpleadoSelect");
                select.innerHTML = html;

                $('#idEmpleadoSelect').val(null).trigger('change');

            })
            .catch(err => console.error("Error cargando empleados:", err));

    fetch("HabitatServlet")
            .then(res => {
                if (!res.ok)
                    throw new Error();
                return res.json();
            })
            .then(habitats => {
                let html = `<option value="">Seleccione hábitat...</option>`;
                if (Array.isArray(habitats)) {
                    habitats.forEach(h => {
                        let terreno = h.tipo_terreno || h.tipoTerreno || "Hábitat";
                        let cap = h.capacidad !== undefined ? h.capacidad : 0;
                        html += `<option value="${h.id}">${terreno} (Capacidad: ${cap})</option>`;
                    });
                }
                document.getElementById("idHabitatSelect").innerHTML = html;
            })
            .catch(err => console.error("Error cargando hábitats:", err));
}

// ==========================================
// TRAER DATOS DEL SERVLET (CORREGIDO)
// ==========================================
function listarAsignaciones(pagina = 1) {
    paginaActual = pagina;

    fetch("HabitatCuidadorServlet")
            .then(res => {
                if (!res.ok)
                    throw new Error("Error al consultar las asignaciones.");
                return res.json();
            })
            .then(data => {
                console.log("Datos de asignaciones recibidos:", data);
                datosCompletos = data; // Almacenamos el array completo de la BD

                if (Array.isArray(datosCompletos)) {
                    // Forzamos el renderizado segmentado
                    redibujarTablaLocal();
                }
            })
            .catch(err => {
                console.error("Error al listar la tabla:", err);
                mostrarAlertaError("No se pudo cargar la lista de asignaciones.");
            });
}

// ==========================================
// MOSTRAR ASIGNACIONES EN TABLA
// ==========================================
function mostrarAsignacionesEnTabla(lista) {
    let html = "";
    lista.forEach(h => {
        let nombresCuidadores = "Sin cuidadores asignados";
        if (h.cuidadores && h.cuidadores.length > 0) {
            nombresCuidadores = h.cuidadores
                    .map(c => `${c.nombre} ${c.apellido}`)
                    .join(", ");
        }

        html += `
            <tr>
                <td>${h.id}</td>
                <td>${h.tipo_terreno || h.tipoTerreno || "—"} (Capacidad: ${h.capacidad ?? 0})</td>
                <td>${nombresCuidadores}</td>
                <td class="acciones">
                    <button class="btnEditar" onclick="cargarParaEditar(${h.id})">
                        <i class="ti ti-edit"></i>
                    </button>
                    <button class="btnEliminar" onclick="eliminarAsignacion(${h.id})">
                        <i class="ti ti-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });
    document.getElementById("tbodyCategoriaCuidador").innerHTML = html;
}

// ==========================================
// RENDERIZAR CONTROLES DE PAGINACIÓN (UNIFICADO)
// ==========================================
function renderPaginacion(totalRegistros) {
    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor)
        return;

    const totalPaginas = Math.ceil(totalRegistros / size) || 1;

    pagContenedor.innerHTML = `
        <button onclick="anterior()" ${paginaActual === 1 ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
            <i class="ti ti-chevron-left"></i>
        </button>
        <span style="margin: 0 10px; font-weight: bold;">Página ${paginaActual} de ${totalPaginas}</span>
        <button onclick="siguiente()" ${paginaActual === totalPaginas ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
            <i class="ti ti-chevron-right"></i>
        </button>
    `;
}

// ==========================================
// LÓGICAS DE NAVEGACIÓN LOCAL (EL MOTOR DEL RECORTE)
// ==========================================
function anterior() {
    if (paginaActual > 1) {
        paginaActual--;
        redibujarTablaLocal();
    }
}

function siguiente() {
    const totalPaginas = Math.ceil(datosCompletos.length / size);
    if (paginaActual < totalPaginas) {
        paginaActual++;
        redibujarTablaLocal();
    }
}

function redibujarTablaLocal() {
    // 🧠 Matemática para recortar la lista en bloques de 5
    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;
    const registrosSegmentados = datosCompletos.slice(inicio, fin);

    mostrarAsignacionesEnTabla(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}

function cambiarPagina(nuevaPagina) {
    listarAsignaciones(nuevaPagina);
}

// ==========================================
// ACCIÓN: GUARDAR O MODIFICAR ASIGNACIÓN
// ==========================================
function guardarOEditar(e) {
    e.preventDefault();

    const idHabitat = document.getElementById("idHabitatSelect").value;
    const selectEmpleado = document.getElementById("idEmpleadoSelect");

    if (!idHabitat) {
        mostrarAlertaError("Por favor, seleccione un hábitat.");
        return;
    }

    const idsEmpleados = Array.from(selectEmpleado.selectedOptions)
            .map(option => parseInt(option.value))
            .filter(val => !isNaN(val));

    if (idsEmpleados.length === 0) {
        mostrarAlertaError("Por favor, seleccione al menos un cuidador.");
        return;
    }

    const esEdicion = document.getElementById("idAsignacionOculta").value !== "";
    const url = "HabitatCuidadorServlet";
    const metodo = esEdicion ? "PUT" : "POST";

    const payload = {
        idHabitat: parseInt(idHabitat),
        idsEmpleados: idsEmpleados
    };

    fetch(url, {
        method: metodo,
        headers: {"Content-Type": "application/json;charset=UTF-8"},
        body: JSON.stringify(payload)
    })
            .then(async res => {
                const text = await res.text();
                let data;
                try {
                    data = JSON.parse(text);
                } catch (e) {
                    throw new Error(text || "Error interno del servidor");
                }

                if (!res.ok) {
                    throw new Error(data.error || "Error al procesar la asignación.");
                }
                return data;
            })
            .then(res => {
                limpiarFormulario();
                listarAsignaciones(paginaActual);

                Swal.fire({
                    icon: "success",
                    title: esEdicion ? "Asignación Actualizada" : "Asignación Exitosa",
                    text: res.mensaje || "Operación realizada con éxito.",
                    confirmButtonColor: "#3f5b4b"
                });
            })
            .catch(err => {
                console.error("Error al procesar asignación:", err);
                mostrarAlertaError(err.message);
            });
}

// ==========================================
// ACCIÓN: CARGAR DATOS EN EL FORMULARIO (EDITAR)
// ==========================================
function cargarParaEditar(idHabitat) {
    fetch(`HabitatCuidadorServlet?id=${idHabitat}`)
            .then(res => {
                if (!res.ok)
                    throw new Error("No se pudo obtener la información de la asignación.");
                return res.json();
            })
            .then(habitat => {
                document.getElementById("idHabitatSelect").value = habitat.id;
                document.getElementById("idAsignacionOculta").value = habitat.id;

                const selectEmpleado = document.getElementById("idEmpleadoSelect");
                const idsAsignados = habitat.cuidadores.map(c => c.id.toString());

                for (let i = 0; i < selectEmpleado.options.length; i++) {
                    let option = selectEmpleado.options[i];
                    option.selected = idsAsignados.includes(option.value);
                }

                let btnGuardar = document.getElementById("btnGuardarAsignacion") || document.getElementById("btnGuardar");
                if (btnGuardar) {
                    btnGuardar.textContent = "Actualizar Asignación";
                }
//
//                window.scrollTo({
//                    top: 0,
//                    behavior: "smooth"
//                });
            })
            .catch(err => {
                console.error("Error al buscar asignación:", err);
                mostrarAlertaError("Error al recuperar los datos para edición.");
            });
}

// ==========================================
// ACCIÓN: ELIMINAR ASIGNACIÓN
// ==========================================
function eliminarAsignacion(idHabitat) {
    Swal.fire({
        title: "¿Está seguro de eliminar los cuidadores de este hábitat?",
        text: "El hábitat se quedará temporalmente sin personal asignado.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, desasignar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (!result.isConfirmed) {
            return;
        }

        fetch(`HabitatCuidadorServlet?id=${idHabitat}`, {
            method: "DELETE"
        })
                .then(async res => {
                    const text = await res.text();
                    if (!res.ok) {
                        throw new Error(text || "No se pudo remover la asignación.");
                    }
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        return {mensaje: text};
                    }
                })
                .then(res => {
                    Swal.fire({
                        icon: "success",
                        title: "Asignación Eliminada",
                        text: res.mensaje || "Los cuidadores fueron removidos de este hábitat.",
                        confirmButtonColor: "#3f5b4b"
                    });
                    listarAsignaciones(paginaActual);
                    limpiarFormulario();
                })
                .catch(err => {
                    console.error("Error al eliminar:", err);
                    Swal.fire({
                        icon: "error",
                        title: "Error al borrar",
                        text: err.message,
                        confirmButtonColor: "#b05d4d"
                    });
                });
    });
}

// ==========================================
// LIMPIAR FORMULARIO
// ==========================================
function limpiarFormulario() {
    document.getElementById("formCategoriaCuidador").reset();
    document.getElementById("idAsignacionOculta").value = "";

    let btnGuardar = document.getElementById("btnGuardarAsignacion") || document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.textContent = "Guardar Asignación";
    }
}

function mostrarAlertaError(mensaje) {
    Swal.fire({
        icon: "error",
        title: "Error detectado",
        text: mensaje,
        confirmButtonColor: "#b05d4d"
    });
}
