/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

/* * Habitats.js
 */

console.log("JS HABITATS CARGADO");

let paginaActual = 1;
const size = 5; 
let datosCompletos = []; 

// INICIO
document.addEventListener("DOMContentLoaded", function () {
    buscarHabitats();
});


function buscarHabitats(pagina = 1) {
    paginaActual = pagina;

    fetch("HabitatServlet")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error al cargar la lista de hábitats.");
                }
                return response.json();
            })
            .then(data => {
                console.log("Datos recibidos del Servlet:", data);

                // Evaluamos el formato dinámicamente y extraemos el array correspondiente
                if (data.habitats && Array.isArray(data.habitats)) {
                    datosCompletos = data.habitats;
                } else if (Array.isArray(data)) {
                    datosCompletos = data;
                } else {
                    console.error("Formato de datos desconocido:", data);
                    return;
                }

                redibujarTablaLocal();
            })
            .catch(error => {
                console.error("Error:", error);
                mostrarAlertaError("No se pudieron cargar los hábitats");
            });
}


function mostrarHabitats(lista) {
    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    let html = "";
    lista.forEach(h => {
        html += `
            <tr>
                <td>${h.id ?? "—"}</td>
                <td>${h.tipoTerreno ?? "—"}</td>
                <td>${h.capacidad ?? "—"}</td>
                <td class="acciones">
                    <button class="btnEditar" onclick="editarHabitat(${h.id})">
                        <i class="ti ti-edit"></i>
                    </button>

                    <button class="btnEliminar" onclick="eliminarHabitat(${h.id})">
                        <i class="ti ti-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    document.getElementById("tbodyHabitats").innerHTML = html;
}


function renderPaginacion(totalRegistros) {
    const pagContenedor = document.getElementById("paginacionHabitats");
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
    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;
    const registrosSegmentados = datosCompletos.slice(inicio, fin);

    mostrarHabitats(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}

function cambiarPagina(nuevaPagina) {
    buscarHabitats(nuevaPagina);
}


function editarHabitat(id) {
    fetch(`HabitatServlet?id=${id}`)
            .then(response => {
                if (!response.ok)
                    throw new Error("No se pudo obtener el hábitat.");
                return response.json();
            })
            .then(h => {
                document.getElementById("idHabitat").value = h.id;
                document.getElementById("tipoTerreno").value = h.tipoTerreno;
                document.getElementById("capacidad").value = h.capacidad;

                // Cambiar texto del botón principal
                let btnGuardar = document.getElementById("btnGuardarHabitat") || document.getElementById("btnGuardar");
                if (btnGuardar) {
                    btnGuardar.textContent = "Actualizar Hábitat";
                }

                // Subir suavemente al formulario
                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });
            })
            .catch(error => {
                console.error(error);
                mostrarAlertaError("Error al recuperar los datos del hábitat.");
            });
}

// GUARDAR O ACTUALIZAR
document.getElementById("formHabitat").addEventListener("submit", function (event) {
    event.preventDefault();

    let id = document.getElementById("idHabitat").value;

    let habitat = {
        tipoTerreno: document.getElementById("tipoTerreno").value,
        capacidad: parseInt(document.getElementById("capacidad").value)
    };

    if (id) {
        habitat.id = parseInt(id);
    }

    let metodo = id ? "PUT" : "POST";

    fetch("HabitatServlet", {
        method: metodo,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(habitat)
    })
            // 🔽 ¡ESTE ES EL BLOQUE QUE HACE LA MAGIA! 🔽
            .then(async response => {
                const text = await response.text();
                let data;

                try {
                    data = JSON.parse(text);
                } catch (e) {
                    throw new Error(text || "Error interno del servidor");
                }

                if (!response.ok) {
                    // Extrae el mensaje de error que configuraste en tu Servlet
                    throw new Error(data.error || "Error en la operación del hábitat");
                }
                return data; // Si todo está bien, pasa al siguiente .then
            })
            .then(data => {
                console.log("Éxito:", data);
                let msgError = document.getElementById("mensajeErrorHabitat") || document.getElementById("mensajeError");
                if (msgError)
                    msgError.innerHTML = "";

                limpiarFormularioHabitat();
                buscarHabitats(paginaActual);

                Swal.fire({
                    icon: "success",
                    title: id ? "Hábitat Actualizado" : "Hábitat Guardado",
                    text: data.mensaje || "Operación realizada con éxito",
                    confirmButtonColor: "#3f5b4b"
                });
            })
            .catch(error => {
                console.error("Error atrapado:", error);

                // Limpia la barra roja por si acaso quedó pintada de antes
                let msgError = document.getElementById("mensajeErrorHabitat") || document.getElementById("mensajeError");
                if (msgError)
                    msgError.innerHTML = "";

                // Muestra la alerta flotante impecable
                mostrarAlertaError(error.message);
            });
});


function eliminarHabitat(id) {
    Swal.fire({
        title: "¿Eliminar hábitat?",
        text: "Esta acción no se puede deshacer y podría afectar a los animales asignados.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (!result.isConfirmed)
            return;

        fetch(`HabitatServlet?id=${id}`, {
            method: "DELETE"
        })
                .then(async response => {
                    const texto = await response.text();
                    if (!response.ok)
                        throw new Error(texto || "No se pudo eliminar el hábitat");
                    try {
                        return JSON.parse(texto);
                    } catch (e) {
                        return {mensaje: texto};
                    }
                })
                .then(data => {
                    Swal.fire({
                        icon: "success",
                        title: "Eliminado",
                        text: data.mensaje || "El hábitat ha sido removido.",
                        confirmButtonColor: "#3f5b4b"
                    });
                    buscarHabitats(paginaActual);
                })
                .catch(error => {
                    Swal.fire({
                        icon: "error",
                        title: "No se puede eliminar",
                        text: error.message,
                        confirmButtonColor: "#b05d4d"
                    });
                });
    });
}


function limpiarFormularioHabitat() {
    if (document.getElementById("idHabitat"))
        document.getElementById("idHabitat").value = "";
    if (document.getElementById("tipoTerreno"))
        document.getElementById("tipoTerreno").value = "";
    if (document.getElementById("capacidad"))
        document.getElementById("capacidad").value = "";

    // Restaurar texto del botón principal
    let btnGuardar = document.getElementById("btnGuardarHabitat") || document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.textContent = "Guardar Hábitat";
    }

    // Ocultar botón de cancelar edición si aplica
    let btnVolver = document.getElementById("btnVolverHabitat");
    if (btnVolver) {
        btnVolver.style.display = "none";
    }
}


function mostrarAlertaError(mensaje) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: mensaje,
            confirmButtonColor: "#b05d4d"
        });
    } else {
        alert(mensaje);
    }
}